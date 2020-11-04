/**
 * Information container for the model to pass to the view during the Fortify phases
 *
 * @author Omar Hashmi
 * @version 11.04.2020
 */

public class FortifyContext extends  ActionContext{
    /** Source country **/
    public final Country srcCountry;
    /** Destination country **/
    public final Country dstCountry;
    /** Source army size **/
    public final int srcArmy;
    /** Destination army size **/
    public final int dstArmy;

    /** Constructor for FortifyContext **/
    FortifyContext(Phase phase,
                  Player player,
                  Country[] highlightedCountries,
                  Country srcCountry,
                  Country dstCountry,
                  int srcArmy,
                  int dstArmy){
        super(phase, player, highlightedCountries);

        this.srcCountry=srcCountry;
        this.dstCountry=dstCountry;
        this.srcArmy=srcArmy;
        this.dstArmy=dstArmy;
    }
}