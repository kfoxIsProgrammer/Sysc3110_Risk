/**Describing a Continent on the map in a game of RISK
 * @author  Kshitij Sawhney
 * @version  10 / 22 / 2020
 */
public class Continent {
    /** Name of the continent **/
    private String name;
    /** List of countries within the continent **/
    private Country[] countries;
    /** List of countries within the continent **/
    private int[] countryIDs;
    /** Number of troops received as bonus for full ownership **/
    private int bonusTroops;

    /**
     * Convert array of countries to their IDs
     * @param allCountries all Countries
     */
    public void IDsToCountries(Country[] allCountries){
        countries=new Country[countryIDs.length];

        for(int i=0;i<countryIDs.length;i++){
            countries[i]=allCountries[countryIDs[i]];
        }
    }

    /**
     * Tests if a player owns the all the countries in the continent
     * @param player the player to test
     * @return True = the player owns the continent, False = the player does not own the continent
     */
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

    public String toString(){
        return name;
    }
}
