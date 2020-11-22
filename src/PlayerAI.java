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

    public PlayerAI(String name, Color color, int armiesToAllocate) {
        super(name, color, true);
        this.armiesToAllocate = armiesToAllocate;
    }
    public boolean isItOptimalContinent(Continent[] continents,  Country sourceCountry){
        int[] continentValue = new int[continents.length];
        int maxContinentIndex = 0;
        for(int value: continentValue){
            value = 0;
        }
        for(Country count: this.countries){
            continentValue[count.getContinentId()] ++;
        }
        for(int i = 0; i < continentValue.length; i++){
            if(continentValue[i] > continentValue[maxContinentIndex]){
                maxContinentIndex = i;
            }
        }
        if (sourceCountry.getContinentId() == maxContinentIndex){
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
                break;
            case MEDIUM:
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
                break;
            case MEDIUM:
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
                break;
            case MEDIUM:
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
