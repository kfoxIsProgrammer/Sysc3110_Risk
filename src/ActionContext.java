/**
 * Information container for the model to pass to the view
 * public instance variables are used since the data is designed
 * to be public
 * @author Omar Hashmi, Kevin Fox (Documentation)
 * @version 11.07.2020
 */

public class ActionContext {
    /** The current phase **/
    private Phase phase;
    /** The current player **/
    private Player player;
    /** The index of the current player **/
    private int playerId;
    /** The countries on the map that should be highlighted **/
    private Country[] highlightedCountries;
    /** Source country **/
    private Country srcCountry;
    /** Destination country **/
    private Country dstCountry;
    /** Source army size **/
    private int srcArmy;
    /** Destination army size **/
    private int dstArmy;
    /** Source army lost troops **/
    private int srcArmyDead;
    /** Destination army lost troops **/
    private int dstArmyDead;
    /** Whether or not the attacker won **/
    private boolean attackerVictory;
    /** The dice roll information **/
    private Integer[][] diceRolls;

    /** Constructor for ActionContext **/
    public ActionContext(Phase phase, Player player){
        this.phase=phase;
        this.player=player;
    }
    public ActionContext(){
        this.phase=Phase.NEW_GAME;
    }

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
    /** Setter for current Player **/
    public void setPlayerId(int playerId) {
        this.playerId = playerId;
    }
    /**
     * Setter for countries for the view to highlight
     * @param highlightedCountries array of countries to highlight on the view
     */
    public void setHighlightedCountries(Country[] highlightedCountries) {
        this.highlightedCountries = highlightedCountries;
    }
    /**
     * Setter for the country that is making an action
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

    public Phase getPhase() {
        return phase;
    }
    public Player getPlayer() {
        return player;
    }
    public int getPlayerId() {
        return playerId;
    }
    public Country[] getHighlightedCountries() {
        return highlightedCountries;
    }
    public Country getSrcCountry() {
        return srcCountry;
    }
    public Country getDstCountry() {
        return dstCountry;
    }
    public int getSrcArmy() {
        return srcArmy;
    }
    public int getDstArmy() {
        return dstArmy;
    }
    public int getSrcArmyDead() {
        return srcArmyDead;
    }
    public int getDstArmyDead() {
        return dstArmyDead;
    }
    public boolean isAttackerVictory() {
        return attackerVictory;
    }
    public Integer[][] getDiceRolls() {
        return diceRolls;
    }
}