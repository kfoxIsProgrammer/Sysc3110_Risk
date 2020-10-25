public class BattleObject {
    private Country attackingCountry;
    private Country defendingCountry;
    private int initialAttackingArmy;
    private int initialDefendingArmy;
    private int finalAttackingArmy;
    private int finalDefendingArmy;
    private boolean didAttackerWin;


    public BattleObject(Country attacker, Country defender, int initialAttackingArmy, int initialDefendingArmy,int finalAttackingArmy,int finalDefendingArmy, boolean didAttackerWin){
        this.attackingCountry = attacker;
        this.defendingCountry = defender;
        this.initialAttackingArmy = initialAttackingArmy;
        this.initialDefendingArmy = initialDefendingArmy;
        this.finalAttackingArmy = finalAttackingArmy;
        this.finalDefendingArmy = finalDefendingArmy;
        this.didAttackerWin = didAttackerWin;
    }

    public Country getAttackingCountry() {
        return attackingCountry;
    }

    public Country getDefendingCountry() {
        return defendingCountry;
    }

    public int getInitialAttackers() {
        return initialAttackingArmy;
    }

    public int getInitialDefenders() {
        return initialDefendingArmy;
    }

    public int getFinalAttackingArmy() {
        return finalAttackingArmy;
    }

    public int getFinalDefendingArmy() {
        return finalDefendingArmy;
    }

    public boolean didAttackerWin() {
        return didAttackerWin;
    }
}
