/**
 * Information container for the model to pass to the view
 * public instance variables are used since the data is designed
 * to be public
 * @author Omar Hashmi, Kevin Fox (Documentation)
 * @version 11.07.2020
 */

public class ActionContext {
    /** The current phase **/
    public  Phase phase;
    /** The current player **/
    public  Player player;
    /** The countries on the map that should be highlighted **/
    public  Country[] highlightedCountries;
    /** Source country **/
    public  Country srcCountry;
    /** Destination country **/
    public  Country dstCountry;
    /** Source army size **/
    public  int srcArmy;
    /** Destination army size **/
    public  int dstArmy;
    /** Source army lost troops **/
    public  int srcArmyDead;
    /** Destination army lost troops **/
    public  int dstArmyDead;
    /** Whether or not the attacker won **/
    public  boolean attackerVictory;
    /** The dice roll information **/
    public  Integer diceRolls[][];

    /** Constructor for ActionContext **/
    ActionContext(Phase phase, Player player){
        this.phase=phase;
        this.player=player;
    }
    /** Setters for ActionContext instance variables **/

    /**
     * Setter for phase
     * @param phase the current phase of the risk game
     */
    public void setPhase(Phase phase) {
        this.phase = phase;
    }

    /**
     * Setter for current Player
     * @param player the player that made a move
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * Setter for countries for the view to highlight
     * @param highlightedCountries array of countries to highlight on the view
     */
    public void setHighlightedCountries(Country[] highlightedCountries) {
        this.highlightedCountries = highlightedCountries;
    }

    /**
     * Setter for the country thats making an action
     * @param srcCountry the country making an action
     */
    public void setSrcCountry(Country srcCountry) {
        this.srcCountry = srcCountry;
    }

    /**
     * Setter for the country that an action is towards
     * @param dstCountry the country getting an action
     */
    public void setDstCountry(Country dstCountry) {
        this.dstCountry = dstCountry;
    }

    /**
     * Setter for source country's army value
     * @param srcArmy the army coming from the source country
     */
    public void setSrcArmy(int srcArmy) {
        this.srcArmy = srcArmy;
    }

    /**
     * Setter for destination country's army value
     * @param dstArmy the army at the destination country
     */
    public void setDstArmy(int dstArmy) {
        this.dstArmy = dstArmy;
    }

    /**
     * Setter for number of lost units for the source country during an attack
     * @param srcArmyDead army value that was lost for the source country during an attack
     */
    public void setSrcArmyDead(int srcArmyDead) {
        this.srcArmyDead = srcArmyDead;
    }
    /**
     * Setter for number of lost units for the destination country during an attack
     * @param dstArmyDead army value that was lost for the destination country during an attack
     */
    public void setDstArmyDead(int dstArmyDead) {
        this.dstArmyDead = dstArmyDead;
    }

    /**
     * Setter for if the source country of an attack was successful
     * @param attackerVictory boolean value to represent if the source
     *                        country was victorious during an attack
     */
    public void setAttackerVictory(boolean attackerVictory) {
        this.attackerVictory = attackerVictory;
    }

    /**
     * Setter for the dice rolls during an attack, to be displayed later
     * @param diceRolls the 2d array representing dice rolls
     */
    public void setDiceRolls(Integer[][] diceRolls) {
        this.diceRolls = diceRolls;
    }
}