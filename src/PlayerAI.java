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

    public PlayerAI(String name, Color color, int armiesToAllocate, int playerId, Continent[] continents) {
        super(name, color, true, playerId);
        this.continents = continents;
        this.troopsToDeploy = armiesToAllocate;
        this.difficulty=Difficulty.BABY;
    }

    public ActionContext getMove(ActionContext ac) {
        utilities=new ArrayList<>();
        actions=new ArrayList<>();

        switch (ac.getPhase()){
            case DEPLOY_CONFIRM:
                return getDeploy(ac);
            case ATTACK_SRC_CONFIRM:
                return getAttack(ac);
            case ATTACK_DST_CONFIRM:
                return getDefend(ac);
            case RETREAT_CONFIRM:
                return getRetreat(ac);
            case FORTIFY_CONFIRM:
                return getFortify(ac);
            default:
                return null;
        }
    }
    public ActionContext getDeploy(ActionContext ac){
        for(Country country: this.getCountries()){
            int allyNeighbors=country.getArmy();
            int enemyNeighbors=0;

            for(Country neighbor: country.getAdjacentOwnedCountries(this)){
                allyNeighbors+=neighbor.getArmy();
            }
            for(Country neighbor: country.getAdjacentOwnedCountries(this)){
                enemyNeighbors+=neighbor.getArmy();
            }

            utilities.add(enemyNeighbors-allyNeighbors);
            ActionContext actionContext=new ActionContext(Phase.DEPLOY_CONFIRM,this);
            actionContext.setDstCountry(country);
            actionContext.setDstArmy((int) Math.ceil(troopsToDeploy/5));
            actions.add(actionContext);
        }

        return bestAction();
    }
    public ActionContext getAttack(ActionContext ac){
        for(Country srcCountry: this.getCountries()){
            for(Country dstCountry: srcCountry.getAdjacentUnownedCountries(this)){
                int srcPower=0;
                int dstPower=0;

                for(Country srcAlly: srcCountry.getAdjacentOwnedCountries(this)){
                    srcPower+=srcAlly.getArmy();
                }
                for(Country dstAlly: dstCountry.getAdjacentOwnedCountries(dstCountry.getOwner())){
                    dstPower+=dstAlly.getArmy();
                }

                utilities.add(dstPower-srcPower);
                ActionContext actionContext=new ActionContext(Phase.DEPLOY_CONFIRM,this);
                actionContext.setSrcCountry(srcCountry);
                actionContext.setDstCountry(dstCountry);
                actionContext.setDstArmy((int) Math.min(3,srcCountry.getArmy()));
                actions.add(actionContext);
            }
        }


        return ac;
    }
    public ActionContext getRetreat(ActionContext ac){

        return ac;
    }
    public ActionContext getFortify(ActionContext ac){

        return ac;
    }
    public ActionContext getDefend(ActionContext ac){

        return ac;
    }

    public ActionContext bestAction(){
        int maxId=0;
        int maxUtility=Integer.MIN_VALUE;

        for(int i=0;i<utilities.size();i++){
            if(utilities.get(i)>maxUtility){
                maxId=i;
                maxUtility=utilities.get(i);
            }
        }
        return actions.get(maxId);
    }

    public boolean canDeploy(){
        return this.troopsToDeploy>0;
    }
    public boolean canAttack(){
        for(Country country: countries){
            if(country.getArmy()>1){
                return true;
            }
        }
        return false;
    }
    public boolean canFortify(){
        for(Country country: countries){
            if(country.getArmy()>1){
                return false;
            }
        }
        return false;
    }
}
