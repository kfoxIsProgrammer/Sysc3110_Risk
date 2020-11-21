import java.awt.*;
import java.util.ArrayList;
import java.util.Map;
import java.util.PriorityQueue;

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
    private ArrayList<ActionContext> lowerPriority = new ArrayList<>();
    private ArrayList<ActionContext> higherPriority = new ArrayList<>();

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
                ActionContext priorityContext = new ActionContext(Phase.DEPLOY_CONFIRM, this);
                //If you are in a potential losing state
                if(srcCountry.getArmy() < dstCountry.getArmy()) {
                    if (dstCountry.getArmy() - srcCountry.getArmy() >= this.getArmiesToAllocate()) {
                        priorityContext.setSrcArmy(dstCountry.getArmy() - srcCountry.getArmy() + 1);
                        priorityContext.setDstCountry(dstCountry);
                        this.higherPriority.add(priorityContext);
                    }
                }
                    else
                    //You are in a slightly less potential of a losing state
                        if(dstCountry.getArmy() - srcCountry.getArmy() >= this.getArmiesToAllocate()){
                            priorityContext.setSrcArmy(dstCountry.getArmy()-srcCountry.getArmy()+1);
                            priorityContext.setDstCountry(dstCountry);
                            this.lowerPriority.add(priorityContext);
                }
                break;
            case HARD:
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
                ActionContext priorityContext = new ActionContext(Phase.ATTACK_CONFIRM, this);
                        //Advantage state add to high priority
                        if(srcCountry.getArmy() > dstCountry.getArmy()){
                            priorityContext.setSrcCountry(srcCountry);
                            priorityContext.setSrcArmy(srcCountry.getArmy()>2? 3 : srcCountry.getArmy());
                            priorityContext.setDstCountry(dstCountry);
                            higherPriority.add(priorityContext);
                        }
                        else
                        {
                            priorityContext.setSrcCountry(srcCountry);
                            priorityContext.setSrcArmy(srcCountry.getArmy()>2? 3 : srcCountry.getArmy());
                            priorityContext.setDstCountry(dstCountry);
                            lowerPriority.add(priorityContext);
                        }
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
