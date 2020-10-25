public class BattleObject {
    private Country attackingCountry;
    private Country defendingCountry;
    private int attackingArmy;
    private int defendingArmy;
    private boolean didAttackerWin;


    public BattleObject(Country attacker, Country defender, int attackingArmy, int defendingArmy, boolean didAttackerWin){
        this.attackingCountry = attacker;
        this.defendingCountry = defender;
        this.attackingArmy = attackingArmy;
        this.defendingArmy = defendingArmy;
        this.didAttackerWin = didAttackerWin;
    }

    public Country getAttackingCountry() {
        return attackingCountry;
    }

    public Country getDefendingCountry() {
        return defendingCountry;
    }

    public int getAttackingArmy() {
        return attackingArmy;
    }

    public int getDefendingArmy() {
        return defendingArmy;
    }

    public boolean didAttackerWin() {
        return didAttackerWin;
    }
}
