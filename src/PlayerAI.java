import java.awt.*;
import java.util.ArrayList;

public class PlayerAI extends Player {
    private enum Difficulty {
        BABY,
        EASY,
        MEDIUM,
        HARD
    }

    private Difficulty difficulty;
    private ArrayList<ActionContext> actions;
    private ArrayList<Integer> utilities;
    private int maxUtilityIndex;
    private int continentIndexToFocus;
    private Continent[] continents;

    public PlayerAI(String name, Color color, int armiesToAllocate, int playerId, Continent[] continents) {
        super(name, color, true, playerId);
        this.continents = continents;
        this.armiesToAllocate = armiesToAllocate;
    }
    public boolean isItOptimalContinent(Country focalCountry){
        int[] continentValue = new int[continents.length];
        int maxContinentIndex = 0;
        for(int value: continentValue){
            value = 0;
        }
        for(Country count: this.countries){
            continentValue[count.getContinentId()] ++;
        }

        for(int i = 0; i < continentValue.length; i++){
            if(continentValue[i] > continentValue[maxContinentIndex] && continentValue[i] != continents[i].getCountryList().length ){
                maxContinentIndex = i;
            }

        }

        if (focalCountry.getContinentId() == maxContinentIndex){
            return true;
        }
        else {return false;}
    }
    public ActionContext getMove(ActionContext actionContext) {
        this.actions = new ArrayList<>();
        this.utilities = new ArrayList<>();
        this.maxUtilityIndex = 0;
        int maxUtility;

        switch (actionContext.getPhase()) {
            case DEPLOY_DST:
            case DEPLOY_ARMY:
            case DEPLOY_CONFIRM:
                maxUtility = Integer.MIN_VALUE;

                for (int i = 0; i < countries.size(); i++) {
                    Country[] dstCountries = countries.get(i).getAdjacentOwnedCountries(this);
                    for (int j = 0; j < dstCountries.length; j++) {
                        deployUtility(countries.get(i), dstCountries[j]);

                        if (utilities.get(utilities.size() - 1) > maxUtility) {
                            this.maxUtilityIndex = utilities.size() - 1;
                        }
                    }
                }
                return actions.get(maxUtilityIndex);
            case ATTACK_SRC:
            case ATTACK_DST:
            case ATTACK_ARMY:
            case ATTACK_CONFIRM:
                maxUtility = Integer.MIN_VALUE;

                for (int i = 0; i < countries.size(); i++) {
                    Country[] dstCountries = countries.get(i).getAdjacentUnownedCountries(this);
                    for (int j = 0; j < dstCountries.length; j++) {
                        attackUtility(countries.get(i), dstCountries[j]);

                        if (utilities.get(utilities.size() - 1) > maxUtility) {
                            this.maxUtilityIndex = utilities.size() - 1;
                        }
                    }
                }
                return actions.get(maxUtilityIndex);
            case RETREAT_ARMY:
            case RETREAT_CONFIRM:
                //TODO This
                break;
            case FORTIFY_SRC:
            case FORTIFY_DST:
            case FORTIFY_ARMY:
            case FORTIFY_CONFIRM:
                maxUtility = Integer.MIN_VALUE;

                for (int i = 0; i < countries.size(); i++) {
                    ArrayList<Country> dstCountries = countries;
                    for (int j = 0; j < dstCountries.size(); j++) {
                        if((countries.get(i).getOwner().equals(dstCountries.get(j).getOwner())) && (countries.get(i).isConnected(dstCountries.get(j)))){
                            fortifyUtility(countries.get(i), dstCountries.get(j));
                        }
                        if (utilities.get(utilities.size() - 1) > maxUtility) {
                            this.maxUtilityIndex = utilities.size() - 1;
                        }
                    }
                }
                return actions.get(maxUtilityIndex);
            case NEW_GAME:
            case GAME_OVER:
            default:
                return null;
        }

        return actions.get(maxUtilityIndex);
    }

    private void deployUtility(Country srcCountry, Country dstCountry) {
        int utility;
        ActionContext actionContext = new ActionContext(Phase.DEPLOY_CONFIRM, this);
        switch (this.difficulty) {
            case BABY:
                break;
            case EASY:
                utility = 0;
                if(isItOptimalContinent(dstCountry)){utility+= 10;}
                for(Country count: dstCountry.getAdjacentCountries()){
                    if(count.getOwner() != this){utility += 1; }
                }
                actionContext.setSrcCountry(srcCountry);
                actionContext.setDstCountry(dstCountry);
                utilities.add(utility);
                actions.add(actionContext);
                break;
            case MEDIUM:
                ActionContext deployContext = new ActionContext(Phase.DEPLOY_CONFIRM, this);
                deployContext.setDstCountry(srcCountry);

                int srcCountryAdjacentValue = 0;
                for(Country c: srcCountry.getAdjacentCountries()){
                    if(!c.getOwner().equals(srcCountry.getOwner())){
                        srcCountryAdjacentValue+= c.getArmy();
                    }
                }
                if(this.getArmiesToAllocate()>0){
                    Random rng = new Random();

                    deployContext.setSrcArmy(rng.nextInt(this.getArmiesToAllocate())-1);
                    utility = srcCountryAdjacentValue - srcCountry.getAdjacentOwnedCountries(this).length-1;
                    utilities.add(utility);
                    actions.add(deployContext);
                }
                break;
            case HARD:
                actionContext.setDstCountry(srcCountry);
                int troopsDiff = dstCountry.getArmy() - srcCountry.getArmy();
                if (troopsDiff < armiesToAllocate && troopsDiff>0) {
                    actionContext.setSrcArmy(troopsDiff);
                    utility = troopsDiff+ dstCountry.getAdjacentOwnedCountries(this).length - 1;
                    utilities.add(utility);
                    actions.add(actionContext);
                }
                break;
        }
    }

    private void attackUtility(Country srcCountry, Country dstCountry) {
        int utility;
        ActionContext actionContext = new ActionContext(Phase.ATTACK_CONFIRM, this);
        switch (this.difficulty) {
            case BABY:
                break;
            case EASY:
                utility = 0;
                if(isItOptimalContinent(dstCountry)){utility+= 10;}
                for(Country count: srcCountry.getAdjacentCountries()){
                    if(count.getOwner() == this){utility += 1; }
                }
                for(Country count: dstCountry.getAdjacentCountries()){
                    if(count.getOwner() == this){utility += 1; }
                }
                if((srcCountry.getArmy() - dstCountry.getArmy()) > 1){utility+=5;}
                utility+= srcCountry.getArmy();
                actionContext.setSrcCountry(srcCountry);
                actionContext.setDstCountry(dstCountry);
                utilities.add(utility);
                actions.add(actionContext);
                break;
            case MEDIUM:
                ActionContext attackContext = new ActionContext(Phase.ATTACK_CONFIRM, this);
                attackContext.setSrcCountry(srcCountry);
                attackContext.setDstCountry(dstCountry);
                //Will only attack high priority

                if(srcCountry.getArmy()-1 > dstCountry.getArmy()) {
                    int srcCountryAdjacentValue = 0;
                    int dstCountryAdjacentValue=0;
                    for(Country c: srcCountry.getAdjacentCountries()){
                        if(!c.getOwner().equals(srcCountry.getOwner())){
                            srcCountryAdjacentValue+= c.getArmy();
                        }
                    }
                    for(Country c: dstCountry.getAdjacentCountries()){
                        if(!c.getOwner().equals(srcCountry.getOwner()) && !c.equals(srcCountry)){
                            dstCountryAdjacentValue+=c.getArmy();
                        }
                    }
                    attackContext.setSrcArmy(srcCountry.getArmy()-1);
                    utilities.add(srcCountryAdjacentValue+dstCountryAdjacentValue/2);
                    actions.add(attackContext);
                }
                break;
            case HARD:
                if(srcCountry.getArmy()>=dstCountry.getArmy() && srcCountry.getArmy()!=1){
                    int advantage = srcCountry.getArmy()-dstCountry.getArmy();
                    utility = advantage+dstCountry.getAdjacentOwnedCountries(this).length-1;
                    if (utility>=2){
                        actionContext.setSrcCountry(srcCountry);
                        actionContext.setSrcArmy(srcCountry.getArmy()-1);
                        actionContext.setDstCountry(dstCountry);
                        utilities.add(utility);
                        actions.add(actionContext);
                    }
                }
                break;
        }
    }

    private void retreatUtility(Country srcCountry, Country dstCountry) {
        switch (this.difficulty) {
            case BABY:
                break;
            case EASY:
                break;
            case MEDIUM:
                break;
            case HARD:
                break;
        }
    }

    private void fortifyUtility(Country srcCountry, Country dstCountry) {
        int utility;
        ActionContext actionContext = new ActionContext(Phase.FORTIFY_CONFIRM, this);
        switch (this.difficulty) {
            case BABY:
                break;
            case EASY:
                utility = 0;
                if(isItOptimalContinent(dstCountry)){utility+= 10;}
                for(Country count: srcCountry.getAdjacentCountries()){
                    if(count.getOwner() == this){utility += 1; }
                }
                for(Country count: dstCountry.getAdjacentCountries()){
                    if(count.getOwner() != this){utility += 1; }
                }
                actionContext.setSrcCountry(srcCountry);
                actionContext.setDstCountry(dstCountry);
                utilities.add(utility);
                actions.add(actionContext);
                break;
            case MEDIUM:
                fortifyContext.setDstCountry(dstCountry);
                fortifyContext.setSrcCountry(srcCountry);
                fortifyContext.setSrcArmy(srcCountry.getArmy()-1);
                int dstAdjacentValue= 0;
                int srcAdjacentValue=0;

                for(Country c: srcCountry.getAdjacentCountries()){
                    if(!c.getOwner().equals(srcCountry.getOwner())){
                        srcAdjacentValue+=c.getArmy();
                    }
                }
                for(Country c: dstCountry.getAdjacentCountries()){
                    if(!c.getOwner().equals(dstCountry.getOwner())){
                        dstAdjacentValue+=c.getArmy();
                    }
                }
                int differential = dstAdjacentValue-srcAdjacentValue;

                utilities.add(differential);
                actions.add(fortifyContext);
                break;
            case HARD:
                if (srcCountry.getAdjacentOwnedCountries(this).length == srcCountry.getAdjacentCountries().length && dstCountry.getAdjacentCountries().length!=dstCountry.getAdjacentOwnedCountries(this).length &&srcCountry.getArmy()>3){
                    utility = srcCountry.getArmy()-dstCountry.getArmy()-3;
                    actionContext.setSrcCountry(srcCountry);
                    actionContext.setDstCountry(dstCountry);
                    actionContext.setSrcArmy(utility);
                    utilities.add(utility);
                    actions.add(actionContext);
                }
                break;
        }
    }
}
