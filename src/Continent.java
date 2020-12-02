/**Describing a Continent on the map in a game of RISK
 * @author  Kshitij Sawhney
 * @version  10 / 22 / 2020
 */
public class Continent {
    /** Name of the continent **/
    private String name;
    /** List of countries within the continent **/
    private transient Country[] countries;
    /** List of countries within the continent **/
    private int[] countryIDs;
    /** Number of troops received as bonus for full ownership **/
    private int bonusTroops;

    public void IDsToCountries(Country[] allCountries){
        countries=new Country[countryIDs.length];

        for(int i=0;i<countryIDs.length;i++){
            countries[i]=allCountries[countryIDs[i]];
        }
    }

    public boolean isOwnedBy(Player player){
        for(Country country: countries){
            if(country.getOwner()!=player){
                return false;
            }
        }
        return true;
    }
    /**
     * @return The continent name
     */
    public String getName() {
        return name;
    }
    /**
     * @return The array of contained countries
     */
    public Country[] getCountries() {
        return countries;
    }
    /**
     * @return The bonus army value
     */
    public int getBonusTroops() {
        return bonusTroops;
    }
}
