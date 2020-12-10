import java.awt.*;
import java.util.ArrayList;

/**
 * PlayerAI is a Risk PLayer that plays autonomously
 * Extends Player
 * @author Omar Hashmi
 * @version 12-09-2020
 */
public class PlayerAI extends Player {
    /** List of possible Action Contexts that the AI will use  */
    private ArrayList<ActionContext> actions;
    /** List of value each action context has */
    private ArrayList<Integer> utilities;
    /** The index of the action context with the largest utility */
    private int maxUtilityIndex;
    /** The value of the maximum utility Action Context*/
    private int maxUtility;

    /**
     * 5 param constructor
     * @param name name of the AI
     * @param color color of the AI
     * @param armiesToAllocate the number of armies to deploy
     * @param playerId the id of the AI
     * @param map the current game map
     */
    public PlayerAI(String name, Color color, int armiesToAllocate, int playerId, Map map) {
        super(name, color, true, playerId, map);
        this.troopsToDeploy = armiesToAllocate;
    }

    /**
     * Function to initialize the Action Contexts and returning the largest valued Action Context
     * @param actionContext the current Action Context to determine next Action Context
     * @return the highest utility Action Context
     */
    public ActionContext getMove(ActionContext actionContext) {
        this.actions = new ArrayList<>();
        this.utilities = new ArrayList<>();
        this.maxUtilityIndex = 0;

        switch (actionContext.getPhase()) {
            case CLAIM_COUNTRY:
                maxUtility=Integer.MIN_VALUE;
                for(Country country: map.getCountries()){
                    if(country.getOwner()==null){
                        claimUtility(country);
                        updateMaxUtility();
                    }
                }
                break;
            case INITIAL_DEPLOY_DST:
                maxUtility = Integer.MIN_VALUE;
                for (Country country: this.getCountries()) {
                    initialDeployUtility(country);
                    updateMaxUtility();
                }
                break;
            case DEPLOY_DST:
                maxUtility = Integer.MIN_VALUE;
                for (Country country: this.getCountries()) {
                    deployUtility(country);
                    updateMaxUtility();
                }
                break;
            case ATTACK_SRC:
                maxUtility = Integer.MIN_VALUE;
                for(Country srcCountry: this.getCountries()){
                    if(srcCountry.getArmy()>=2) {
                        for(Country dstCountry: srcCountry.getAdjacentUnownedCountries(this)){
                            attackUtility(srcCountry,dstCountry);
                            updateMaxUtility();
                        }
                    }
                }
                break;
            case RETREAT_NUM_TROOPS:
                return actionContext;
            case FORTIFY_SRC:
                maxUtility = Integer.MIN_VALUE;
                for(Country srcCountry: this.getCountries()){
                    if(srcCountry.getArmy()>=2) {
                        for(Country dstCountry: srcCountry.getConnectedOwnedCountries(this)){
                            fortifyUtility(srcCountry,dstCountry);
                            updateMaxUtility();
                        }
                    }
                }
                break;
            default:
                return null;
        }

        if(actions.size()>0) {
            return actions.get(maxUtilityIndex);
        }else{
            return null;
        }
    }

    /**
     * Updates the max utility
     */
    private void updateMaxUtility(){
        if (utilities.get(utilities.size() - 1) > maxUtility) {
            maxUtility = utilities.get(utilities.size()-1);
            this.maxUtilityIndex = utilities.size() - 1;
        }
    }

    /**
     * Utility to help AI choose the best Country
     * @param country Country to test
     */
    private void claimUtility(Country country){
        int utility=0;

        for(Country neighbor: country.getAdjacentCountries()){
            if(neighbor.getOwner()==null){
                utility+=1;
            }else if(neighbor.getOwner()==this){
                utility+=2;
            }else{
                utility-=1;
            }
        }

        ActionContext ac=new ActionContext(Phase.CLAIM_COUNTRY,this);
        ac.setDstCountry(country);
        actions.add(ac);
        utilities.add(utility);

    }

    /**
     * Utility to help deploy initial Armies
     * @param dstCountry the country to test
     */
    private void initialDeployUtility(Country dstCountry) {
        int utility = 0;

        for(Country country: dstCountry.getAdjacentCountries()){
            if(country.getOwner()!=this){
                utility += country.getArmy();
            }
            else{
                utility-=(country.getArmy())/2;
            }
        }
        utility-=dstCountry.getArmy();

        ActionContext actionContext = new ActionContext(Phase.INITIAL_DEPLOY_DST, this);
        actionContext.setDstCountry(dstCountry);
        actionContext.setDstArmy(1);

        utilities.add(utility);
        actions.add(actionContext);
    }

    /**
     * Utility to help during the Deploy Phase
     * @param dstCountry the Country to test
     */
    private void deployUtility(Country dstCountry) {
        int utility = 0;

        for(Country country: dstCountry.getAdjacentCountries()){
            if(country.getOwner()!=this&& country.getAdjacentUnownedCountries(this).length!=0){
                utility += country.getArmy();
            }
            else{
                utility-=(country.getArmy()*0.6);
            }
        }
        utility-=dstCountry.getArmy();

        ActionContext actionContext = new ActionContext(Phase.DEPLOY_DST, this);
        actionContext.setDstCountry(dstCountry);
        actionContext.setDstArmy(1);

        utilities.add(utility);
        actions.add(actionContext);
    }

    /**
     * Utility to help during the Attack Phase
     * @param srcCountry the source country to attack from
     * @param dstCountry the destination country to attack
     */
    private void attackUtility(Country srcCountry, Country dstCountry){
        int utility;
        ActionContext actionContext = new ActionContext(Phase.ATTACK_SRC, this);
        utility = 0;
        for(Country count: srcCountry.getAdjacentCountries()){
            if(count.getOwner()==this){
                utility += 1;
            }
        }
        for(Country count: dstCountry.getAdjacentCountries()){
            if(count.getOwner()==this){
                utility+=1;
            }
        }
        if((srcCountry.getArmy()-dstCountry.getArmy()) > 1){
            utility+=5;
        }
        if(!srcCountry.getOwner().equals(dstCountry.getOwner())) {
            utility += srcCountry.getArmy();
            actionContext.setSrcCountry(srcCountry);
            actionContext.setDstCountry(dstCountry);
            actionContext.setSrcArmy(Math.min(3,srcCountry.getArmy()-1));
            utilities.add(utility);
            actions.add(actionContext);
        }
    }

    /**
     * Utility to help during Fortify Phase
     * @param srcCountry the Source Country to fortify from
     * @param dstCountry the destination Country to fortify to
     */
    private void fortifyUtility(Country srcCountry, Country dstCountry) {
        int srcPower=0;
        int dstPower=0;

        for(Country srcNeighbor: srcCountry.getAdjacentCountries()){
            if(srcNeighbor.getOwner()==this){
                srcPower+=srcNeighbor.getArmy();
            }else{
                srcPower-=srcNeighbor.getArmy();
            }
        }
        for(Country dstNeighbor: srcCountry.getAdjacentCountries()){
            if(dstNeighbor.getOwner()==this){
                dstPower+=dstNeighbor.getArmy();
            }else{
                dstPower-=dstNeighbor.getArmy();
            }
        }

        utilities.add(srcPower-dstPower);
        ActionContext actionContext=new ActionContext(Phase.FORTIFY_SRC,this);
        actionContext.setSrcCountry(srcCountry);
        actionContext.setDstCountry(dstCountry);
        actionContext.setDstArmy(1);
        actions.add(actionContext);
    }
}
