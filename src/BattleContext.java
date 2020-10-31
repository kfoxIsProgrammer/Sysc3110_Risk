import java.util.ArrayList;

/**
 * Model of outcome of a battle
 * @author kevin
 * @version 10-25-2020
 */
public class BattleContext {
    /**
     * Attack country
     */
    private Country attackingCountry;
    /**
     * Defending country
     */
    private Country defendingCountry;
    /**
     * initial army of attacking country
     */
    private int initialAttackingArmy;
    /**
     * initial army of defending country
     */
    private int initialDefendingArmy;
    /**
     * final army of attacking country
     */
    private int finalAttackingArmy;
    /**
     * final army of defending country
     */
    private int finalDefendingArmy;
    /**
     * Data of individual dice rolls
     */
    private ArrayList<Integer[]> battleOutCome;
    /**
     * boolean did the attack win
     */
    private boolean didAttackerWin;

    public BattleContext(){

    }

    /**
     * 7 param constructor
     * @param attacker the attacking country
     * @param defender the defending country
     * @param initialAttackingArmy inital attacking army
     * @param initialDefendingArmy initial defending army
     * @param finalAttackingArmy final attacking army
     * @param finalDefendingArmy final defending army
     * @param didAttackerWin did the attacker win
     */
    public BattleContext(Country attacker, Country defender, int initialAttackingArmy, int initialDefendingArmy, int finalAttackingArmy, int finalDefendingArmy, boolean didAttackerWin){
        this.attackingCountry = attacker;
        this.defendingCountry = defender;
        this.initialAttackingArmy = initialAttackingArmy;
        this.initialDefendingArmy = initialDefendingArmy;
        this.finalAttackingArmy = finalAttackingArmy;
        this.finalDefendingArmy = finalDefendingArmy;
        this.battleOutCome = new ArrayList<>();
        this.didAttackerWin = didAttackerWin;
    }

    /**
     * getting for attacking country
     * @return the attacking country object
     */
    public Country getAttackingCountry() {
        return attackingCountry;
    }

    /**
     * getter for defending country
     * @return the defending country object
     */
    public Country getDefendingCountry() {
        return defendingCountry;
    }

    /**
     * getter for initial attacker army
     * @return intial attacker int value
     */
    public int getInitialAttackers() {
        return initialAttackingArmy;
    }

    /**
     * getter for initial defeding army
     * @return intial defender int value
     */
    public int getInitialDefenders() {
        return initialDefendingArmy;
    }

    /**
     * getter for final attacker army
     * @return final attacker int value
     */
    public int getFinalAttackingArmy() {
        return finalAttackingArmy;
    }

    /**
     * getter for final defender army
     * @return final defender int value
     */
    public int getFinalDefendingArmy() {
        return finalDefendingArmy;
    }

    /**
     * getter for boolean ifTheAttackerWon
     * @return boolean value if an attacker won
     */
    public boolean didAttackerWin() {
        return didAttackerWin;
    }

    public void setAttackingCountry(Country attackingCountry) {
        this.attackingCountry = attackingCountry;
    }

    public void setDefendingCountry(Country defendingCountry) {
        this.defendingCountry = defendingCountry;
    }

    public void setInitialAttackingArmy(int initialAttackingArmy) {
        this.initialAttackingArmy = initialAttackingArmy;
    }

    public void setInitialDefendingArmy(int initialDefendingArmy) {
        this.initialDefendingArmy = initialDefendingArmy;
    }

    public void setFinalAttackingArmy(int finalAttackingArmy) {
        this.finalAttackingArmy = finalAttackingArmy;
    }

    public void setFinalDefendingArmy(int finalDefendingArmy) {
        this.finalDefendingArmy = finalDefendingArmy;
    }

    public void setDidAttackerWin(boolean didAttackerWin) {
        this.didAttackerWin = didAttackerWin;
    }

    public void addDiceRollBattle(Integer[] battle){
        this.battleOutCome.add(battle);
    }
}
