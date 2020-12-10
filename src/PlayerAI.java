import java.awt.*;
import java.util.ArrayList;

public class PlayerAI extends Player {
    private ArrayList<ActionContext> actions;
    private ArrayList<Integer> utilities;
    private int maxUtilityIndex;
    private int maxUtility;

    public PlayerAI(String name, Color color, int armiesToAllocate, int playerId, Map map) {
        super(name, color, true, playerId, map);
        this.troopsToDeploy = armiesToAllocate;
    }
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

    private void updateMaxUtility(){
        if (utilities.get(utilities.size() - 1) > maxUtility) {
            maxUtility = utilities.get(utilities.size()-1);
            this.maxUtilityIndex = utilities.size() - 1;
        }
    }

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
    private void deployUtility(Country dstCountry) {
        int utility = 0;

        for(Country country: dstCountry.getAdjacentCountries()){
            if(country.getOwner()!=this&& country.getAdjacentUnownedCountries(this).length!=0){
                utility += country.getArmy();
            }
            else{
                utility-=(country.getArmy())/3;
            }
        }
        utility-=dstCountry.getArmy();

        ActionContext actionContext = new ActionContext(Phase.DEPLOY_DST, this);
        actionContext.setDstCountry(dstCountry);
        actionContext.setDstArmy(1);

        utilities.add(utility);
        actions.add(actionContext);
    }
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
