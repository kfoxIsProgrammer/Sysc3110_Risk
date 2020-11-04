/**
 * Information container for the model to pass to the view during the Deploy phases
 *
 * @author Omar Hashmi
 * @version 11.04.2020
 */

public class DeployContext extends  ActionContext{
    /** Destination country **/
    public final Country dstCountry;
    /** Destination army size **/
    public final int dstArmy;

    /** Constructor for DeployContext **/
    DeployContext(Phase phase,
                  Player player,
                  Country[] highlightedCountries,
                  Country dstCountry,
                  int dstArmy){
        super(phase, player, highlightedCountries);

        this.dstCountry=dstCountry;
        this.dstArmy=dstArmy;
    }
}