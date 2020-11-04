/**
 * Information container for the model to pass to the view during the Attack phases
 *
 * @author Omar Hashmi
 * @version 11.04.2020
 */

public class AttackContext extends ActionContext {
    /** Source country **/
    public final Country srcCountry;
    /** Destination country **/
    public final Country dstCountry;
    /** Source army size **/
    public final int srcArmy;
    /** Destination army size **/
    public final int dstArmy;
    /** Source army lost troops **/
    public final int srcArmyDead;
    /** Destination army lost troops **/
    public final int dstArmyDead;
    /** Whether or not the attacker won **/
    public final boolean attackerVictory;
    /** The dice roll information **/
    public final int diceRolls[][];

    /** Constructor for AttackContext **/
    AttackContext(Phase phase,
                  Player player,
                  Country[] highlightedCountries,
                  Country srcCountry,
                  Country dstCountry,
                  int srcArmy,
                  int dstArmy,
                  int srcArmyDead,
                  int dstArmyDead,
                  boolean attackerVictory,
                  int[][] diceRolls){
        super(phase, player, highlightedCountries);

        this.srcCountry=srcCountry;
        this.dstCountry=dstCountry;
        this.srcArmy=srcArmy;
        this.dstArmy=dstArmy;
        this.srcArmyDead=srcArmyDead;
        this.dstArmyDead=dstArmyDead;
        this.attackerVictory=attackerVictory;
        this.diceRolls=diceRolls;
    }
}