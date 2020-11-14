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
        this.actions = new ArrayList<>();
        this.utilities = new ArrayList<>();
        this.maxUtilityIndex = 0;
        int maxUtility=0;

        for (int i = 0; i < this.countries.size(); i++) {
            Country country = this.countries.get(i);

            switch (actionContext.getPhase()) {
                case DEPLOY_DST:
                case DEPLOY_ARMY:
                case DEPLOY_CONFIRM:
                    deployUtility(actionContext, country);
                    break;
                case ATTACK_SRC:
                case ATTACK_DST:
                case ATTACK_ARMY:
                case ATTACK_CONFIRM:
                    attackUtility(actionContext, country);
                    break;
                case RETREAT_ARMY:
                case RETREAT_CONFIRM:
                    retreatUtility(actionContext, country);
                    break;
                case FORTIFY_SRC:
                case FORTIFY_DST:
                case FORTIFY_ARMY:
                case FORTIFY_CONFIRM:
                    fortifyUtility(actionContext, country);
                    break;
                case NEW_GAME:
                case GAME_OVER:
                default:
                    return null;
            }

            if (utilities.get(i)>maxUtility) {
                maxUtility=utilities.get(i);
                maxUtilityIndex=i;
            }
        }
        return actions.get(maxUtilityIndex);
    }

    private void deployUtility(ActionContext actionContext, Country country){
        int utility;

        switch (this.difficulty){
            case BABY:
                utility=country.getArmy();
                utility+=country.getAdjacentOwnedCountries(actionContext.getPlayer()).length;
                this.utilities.add(utility);
                break;
            case EASY:
                break;
            case MEDIUM:
                break;
            case HARD:
                break;
        }
    }
    private void attackUtility(ActionContext actionContext, Country country){
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
    private void retreatUtility(ActionContext actionContext, Country country){
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
    private void fortifyUtility(ActionContext actionContext, Country country){
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
