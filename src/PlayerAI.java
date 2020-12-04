import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

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

    public PlayerAI(String name, Color color, int armiesToAllocate, int playerId, Map map) {
        super(name, color, true, playerId, map);
        this.troopsToDeploy = armiesToAllocate;
        this.difficulty=Difficulty.EASY;
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
            if(continentValue[i] > continentValue[maxContinentIndex] && continentValue[i] != continents[i].getCountries().length ){
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

                for (int index : countryIndexes) {
                    Country[] dstCountries = map.getCountries()[index].getAdjacentOwnedCountries(this);
                    for (int j = 0; j < dstCountries.length; j++) {
                        deployUtility(map.getCountries()[index], dstCountries[j]);

                        if (utilities.get(utilities.size() - 1) > maxUtility) {
                            this.maxUtilityIndex = utilities.size() - 1;
                        }
                    }
                }
                return actions.get(maxUtilityIndex);
            case ATTACK_SRC:
            case ATTACK_DST:
            case ATTACK_SRC_ARMY:
            case ATTACK_SRC_CONFIRM:
            case ATTACK_DST_ARMY:
            case ATTACK_DST_CONFIRM:
                maxUtility = Integer.MIN_VALUE;

                for (int index : countryIndexes) {
                    Country[] dstCountries = map.getCountries()[index].getAdjacentUnownedCountries(this);
                    for (int j = 0; j < dstCountries.length; j++) {
                        attackUtility(map.getCountries()[index], dstCountries[j]);

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

                for (int index : countryIndexes) {
                    ArrayList<Integer> dstCountries = countryIndexes;
                    for (int indexDst : dstCountries) {
                        fortifyUtility(map.getCountries()[index], map.getCountries()[indexDst]);
                        if (utilities.get(utilities.size()) > maxUtility){
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

    public boolean canDeploy(){
        return this.troopsToDeploy >0;
    }
    public boolean canAttack(){
        for(int index: countryIndexes){
            if(map.getCountries()[index].getArmy()>1){
                return true;
            }
        }
        return false;
    }
    public boolean canFortify(){
        for(int index: countryIndexes){
            if(map.getCountries()[index].getArmy()>1){
                return false;
            }
        }
        return false;
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

                    actionContext.setDstArmy(1);
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
                if(this.getTroopsToDeploy()>0){
                    Random rng = new Random();

                    deployContext.setSrcArmy(rng.nextInt(this.getTroopsToDeploy())-1);
                    utility = srcCountryAdjacentValue - srcCountry.getAdjacentOwnedCountries(this).length-1;
                    utilities.add(utility);
                    actions.add(deployContext);
                }
                break;
            case HARD:
                actionContext.setDstCountry(srcCountry);
                int troopsDiff = dstCountry.getArmy() - srcCountry.getArmy();
                if (troopsDiff < troopsToDeploy && troopsDiff>0) {
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
        ActionContext actionContext = new ActionContext(Phase.ATTACK_SRC_CONFIRM, this);
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
                actionContext.setSrcArmy(actionContext.getSrcCountry().getArmy()-1);
                utilities.add(utility);
                actions.add(actionContext);
                break;
            case MEDIUM:
                ActionContext attackContext = new ActionContext(Phase.ATTACK_SRC_CONFIRM, this);
                attackContext.setSrcCountry(srcCountry);
                attackContext.setDstCountry(dstCountry);
                //Will only attack high priority

               // if(srcCountry.getArmy()-1 > dstCountry.getArmy()) {
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


                  //  utilities.add(Integer.MIN_VALUE);


                actions.add(attackContext);
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
                Country[] counts= srcCountry.getAdjacentCountries();
                if(counts!= null) {
                    boolean flag = false;
                    for(Country C: counts){
                        if(C.equals(dstCountry)){
                            flag = true;
                            break;
                        }
                    }
                    if (srcCountry.getOwner().equals(dstCountry.getOwner()) && flag) {
                        actionContext.setSrcArmy(srcCountry.getArmy() - 1);
                        actionContext.setSrcCountry(srcCountry);
                        actionContext.setDstCountry(dstCountry);
                        utilities.add((int) Math.random());
                        actions.add(actionContext);
                    }
                }
                break;
            case MEDIUM:
                actionContext.setDstCountry(dstCountry);
                actionContext.setSrcCountry(srcCountry);
                actionContext.setSrcArmy(srcCountry.getArmy()-1);
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
                actions.add(actionContext);
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
