/**
 * Information container for the model to pass to the view
 *
 * @author Omar Hashmi
 * @version 11.04.2020
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
    public  int diceRolls[][];

    /** Constructor for ActionContext **/
    ActionContext(Phase phase, Player player){
        this.phase=phase;
        this.player=player;
    }

    public void setPhase(Phase phase) {
        this.phase = phase;
    }
    public void setPlayer(Player player) {
        this.player = player;
    }
    public void setHighlightedCountries(Country[] highlightedCountries) {
        this.highlightedCountries = highlightedCountries;
    }
    public void setSrcCountry(Country srcCountry) {
        this.srcCountry = srcCountry;
    }
    public void setDstCountry(Country dstCountry) {
        this.dstCountry = dstCountry;
    }
    public void setSrcArmy(int srcArmy) {
        this.srcArmy = srcArmy;
    }
    public void setDstArmy(int dstArmy) {
        this.dstArmy = dstArmy;
    }
    public void setSrcArmyDead(int srcArmyDead) {
        this.srcArmyDead = srcArmyDead;
    }
    public void setDstArmyDead(int dstArmyDead) {
        this.dstArmyDead = dstArmyDead;
    }
    public void setAttackerVictory(boolean attackerVictory) {
        this.attackerVictory = attackerVictory;
    }
    public void setDiceRolls(int[][] diceRolls) {
        this.diceRolls = diceRolls;
    }
}