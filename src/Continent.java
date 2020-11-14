/**Describing a Continent on the map in a game of RISK
 * @author  Kshitij Sawhney
 * @version  10 / 22 / 2020
 */
public class Continent {
    /**Name of the continent*/
    public final String name;
    /**List of countries within the continent*/
    public final Country[] countryList;
    /**Number of troops received as bonus for full ownership*/
    public final int bonusArmyValue;

    /**
     *3 param Constructor for Continent
     * @param name name of the continent
     * @param countryList list of contained countries
     * @param bonusArmyValue troop bonus
     */
    public Continent(String name, Country[] countryList,int bonusArmyValue){
        this.name=name;
        this.countryList=countryList;
        this.bonusArmyValue=bonusArmyValue;
    }

    /**Getter for name
     * @return name of the continent*/
    public String getName() {
        return name;
    }
    /**Getter for bonusArmyValue
     * @return number of bonus troops*/
    public int getBonusArmyValue() {
        return bonusArmyValue;
    }
    /**Getter for countryList
     * @return list of countries contained within the continent*/
    public Country[] getCountryList() {
        return countryList;
    }
}
