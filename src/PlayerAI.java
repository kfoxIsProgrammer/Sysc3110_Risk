import java.awt.*;
import java.util.ArrayList;

public class PlayerAI extends Player{
    private enum Difficulty{
        BABY,
        EASY,
        MEDIUM,
        HARD
    }
    private Difficulty difficulty;
    private ArrayList<ActionContext> actions;
    private ArrayList<Integer> utilities;
    private int maxUtilityIndex;

    public PlayerAI(String name, Color color, int armiesToAllocate) {
        super(name,color,true);
        this.armiesToAllocate=armiesToAllocate;
    }

    public ActionContext getMove(ActionContext actionContext) {
        this.actions=new ArrayList<>();
        this.utilities=new ArrayList<>();
        this.maxUtilityIndex=0;
        int maxUtility;

        switch (actionContext.getPhase()) {
            case DEPLOY_DST:
            case DEPLOY_ARMY:
            case DEPLOY_CONFIRM:
                maxUtility=Integer.MIN_VALUE;

                for(int i=0;i<countries.size();i++){
                    Country[] dstCountries=countries.get(i).getAdjacentOwnedCountries(this);
                    for(int j=0;j<dstCountries.length;j++){
                        deployUtility(countries.get(i), dstCountries[j]);

                        if(utilities.get(utilities.size()-1)>maxUtility){
                            this.maxUtilityIndex=utilities.size()-1;
                        }
                    }
                }
                return actions.get(maxUtilityIndex);
            case ATTACK_SRC:
            case ATTACK_DST:
            case ATTACK_ARMY:
            case ATTACK_CONFIRM:
                maxUtility=Integer.MIN_VALUE;

                for(int i=0;i<countries.size();i++){
                    Country[] dstCountries=countries.get(i).getAdjacentUnownedCountries(this);
                    for(int j=0;j<dstCountries.length;j++){
                        attackUtility(countries.get(i), dstCountries[j]);

                        if(utilities.get(utilities.size()-1)>maxUtility){
                            this.maxUtilityIndex=utilities.size()-1;
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
                maxUtility=Integer.MIN_VALUE;

                for(int i=0;i<countries.size();i++){
                    ArrayList<Country> dstCountries=countries;
                    for(int j=0;j<dstCountries.size();j++){
                        //TODO Check if the countries are connected
                        fortifyUtility(countries.get(i), dstCountries.get(j));

                        if(utilities.get(utilities.size()-1)>maxUtility){
                            this.maxUtilityIndex=utilities.size()-1;
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

    private void deployUtility(Country srcCountry, Country dstCountry){
        int utility;
        ActionContext actionContext=new ActionContext(Phase.DEPLOY_CONFIRM, this);

        switch (this.difficulty){
            case BABY:
                break;
            case EASY:
                break;
            case MEDIUM:
                break;
            case HARD:
                for(Country ownedCountry:this.getCountries()){
                    for(Country adjacentCountry: ownedCountry.getAdjacentUnownedCountries(this)){
                        actionContext.setDstCountry(ownedCountry);
                        int troopsDiff=adjacentCountry.getArmy()-ownedCountry.getArmy()+adjacentCountry.getAdjacentOwnedCountries(this).length-1;
                        if(troopsDiff<armiesToAllocate) {
                            actionContext.setSrcArmy(troopsDiff);
                            utility = troopsDiff;
                            utilities.add(utility);
                            actions.add(actionContext);
                        }else{
                            actionContext.setPhase(Phase.ATTACK_SRC);
                        }
                    }
                }
                break;
        }
    }
    private void attackUtility(Country srcCountry, Country dstCountry){
        switch (this.difficulty){
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
    private void retreatUtility(Country srcCountry, Country dstCountry){
        switch (this.difficulty){
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
    private void fortifyUtility(Country srcCountry, Country dstCountry){
        switch (this.difficulty){
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
}
