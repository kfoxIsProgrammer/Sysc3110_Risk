import java.awt.*;
import java.util.ArrayList;

public class PlayerAI extends Player {
    private ArrayList<ActionContext> actions;
    private ArrayList<Integer> utilities;
    private int maxUtilityIndex;

    public PlayerAI(String name, Color color, int armiesToAllocate, int playerId, Map map) {
        super(name, color, true, playerId, map);
        this.troopsToDeploy = armiesToAllocate;
    }
    public ActionContext getMove(ActionContext actionContext) {
        this.actions = new ArrayList<>();
        this.utilities = new ArrayList<>();
        this.maxUtilityIndex = 0;
        int maxUtility;

        switch (actionContext.getPhase()) {
            case CLAIM_COUNTRY:
                maxUtility=Integer.MIN_VALUE;

                for(Country country: map.getCountries()){
                    if(country.getOwner()==null){
                        claimUtility(country);

                        if (utilities.get(utilities.size() - 1) > maxUtility) {
                            maxUtility = utilities.get(utilities.size()-1);
                            this.maxUtilityIndex = utilities.size() - 1;
                        }
                    }
                }
                return actions.get(maxUtilityIndex);

            case INITIAL_DEPLOY_DST:
            case INITIAL_DEPLOY_NUM_TROOPS:
            case INITIAL_DEPLOY_CONFIRM:
            case DEPLOY_DST:
            case DEPLOY_NUM_TROOPS:
            case DEPLOY_CONFIRM:
                maxUtility = Integer.MIN_VALUE;

                for (Country country: this.getCountries()) {
                    deployUtility(country);

                    if (utilities.get(utilities.size() - 1) > maxUtility) {
                        maxUtility = utilities.get(utilities.size()-1);
                        this.maxUtilityIndex = utilities.size() - 1;
                    }
                }
                return actions.get(maxUtilityIndex);
            case ATTACK_SRC:
            case ATTACK_DST:
            case ATTACK_NUM_TROOPS:
            case ATTACK_CONFIRM:
                maxUtility = Integer.MIN_VALUE;
                for (int i = 0; i < countryIndexes.size(); i++) {
                    Country[] dstCountries = map.getCountries()[countryIndexes.get(i)].getAdjacentUnownedCountries(this);
                    for (int j = 0; j < dstCountries.length; j++) {
                        attackUtility(map.getCountries()[countryIndexes.get(i)], dstCountries[j]);

                        if (utilities.get(utilities.size() - 1) > maxUtility) {
                            maxUtility = utilities.get(utilities.size()-1);
                            this.maxUtilityIndex = utilities.size() - 1;
                        }
                    }
                }
            case DEFEND_NUM_TROOPS:
            case DEFEND_CONFIRM:

            case RETREAT_NUM_TROOPS:
            case RETREAT_CONFIRM:
                break;
            case FORTIFY_SRC:
            case FORTIFY_DST:
            case FORTIFY_NUM_TROOPS:
            case FORTIFY_CONFIRM:
                maxUtility = Integer.MIN_VALUE;

                for (int index : countryIndexes) {
                    ArrayList<Integer> dstCountries = countryIndexes;
                    for (int dstIndex : dstCountries) {
                        fortifyUtility(map.getCountries()[index], map.getCountries()[dstIndex]);
                        if (utilities.size() > 0) {
                            if (utilities.get(utilities.size() - 1) > maxUtility) {
                                maxUtility = utilities.get(utilities.size() - 1);
                                this.maxUtilityIndex = utilities.size() - 1;
                            }
                        }
                        else{
                            return new ActionContext(Phase.DEPLOY_DST,this);
                        }
                    }
                }
                return actions.get(maxUtilityIndex);
            case GAME_OVER:
            default:
                return null;
        }

        return actions.get(maxUtilityIndex);
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
    private void deployUtility(Country dstCountry) {
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

        ActionContext actionContext = new ActionContext(Phase.DEPLOY_CONFIRM, this);
        actionContext.setDstCountry(dstCountry);
        actionContext.setDstArmy(1);

        utilities.add(utility);
        actions.add(actionContext);
    }
    private void attackUtility(Country srcCountry, Country dstCountry) {
        int utility;
        ActionContext actionContext = new ActionContext(Phase.ATTACK_CONFIRM, this);
        utility = 0;
        for(Country count: srcCountry.getAdjacentCountries()){
            if(count.getOwner() == this){utility += 1; }
        }
        for(Country count: dstCountry.getAdjacentCountries()){
            if(count.getOwner() == this){utility += 1; }
        }
        if((srcCountry.getArmy() - dstCountry.getArmy()) > 1){utility+=5;}
        if(!srcCountry.getOwner().equals(dstCountry.getOwner())) {
            utility += srcCountry.getArmy();
            actionContext.setSrcCountry(srcCountry);
            actionContext.setDstCountry(dstCountry);
            actionContext.setSrcArmy(actionContext.getSrcCountry().getArmy() - 1);
            utilities.add(utility);
            actions.add(actionContext);
        }
    }
    private void retreatUtility(Country srcCountry, Country dstCountry) {

    }
    private void fortifyUtility(Country srcCountry, Country dstCountry) {
        int utility;
        ActionContext actionContext = new ActionContext(Phase.FORTIFY_CONFIRM, this);
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
    }

}
