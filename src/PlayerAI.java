import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

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
                ActionContext fortityContext = new ActionContext(Phase.FORTIFY_CONFIRM,this);
                fortityContext.setDstCountry(dstCountry);
                fortityContext.setSrcCountry(srcCountry);
                fortityContext.setSrcArmy(srcCountry.getArmy()-1);
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
                actions.add(fortityContext);
                break;
            case HARD:
                break;
        }
    }
}
