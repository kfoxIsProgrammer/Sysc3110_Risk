/**
 * Information container for the model to pass to the view
 *
 * @author Omar Hashmi
 * @version 11.04.2020
 */

public abstract class ActionContext {
    /** The current phase **/
    public final Phase phase;
    /** The current player **/
    public final Player player;
    /** The countries on the map that should be highlighted **/
    public final Country[] highlightedCountries;

    /** Constructor for ActionContext **/
    ActionContext(Phase phase, Player player, Country[] highlightedCountries){
        this.phase=phase;
        this.player=player;
        this.highlightedCountries=highlightedCountries;
    }
}