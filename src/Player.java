import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Player class used to represent a player or AI, playing the game of risk
 * Abstract for the purposed of splitting AI and Humans
 * @author Kevin
 * @version 12-09-2020
 */
public abstract class Player{
    /** Boolean describing if this player is an ai **/
    protected final Boolean isAI;
    /** Index of this player **/
    protected final int playerId;
    /** Name of this player **/
    protected final String name;
    /** The color the player is **/
    protected transient final  Color playerColor;
    /** Map that will help determine which country the player owns */
    protected transient Map map;
    /** Number of armies that can be allocated to a country **/
    protected int troopsToDeploy;
    /** Hashmap of owned Countries **/
    protected ArrayList<Integer> countryIndexes;
    /** This is used for lose condition **/
    protected boolean hasLost = false;

    /**
     * 5 Param constructor
     * @param name the name of the player
     * @param color the color of the player
     * @param isAI if the player is an AI
     * @param playerId the id of the player
     * @param map the map associated with the player
     */
    protected Player(String name, Color color, boolean isAI, int playerId, Map map){
        this.isAI = isAI;
        this.playerId=playerId;
        this.name = name;
        this.playerColor = color;
        this.map = map;
        countryIndexes = new ArrayList<>();
    }
    /**
     * Used to decrement armiesToAllocate
     * @param troops the number of troops to remove
     */
    public void removeTroops(int troops){
        if(this.troopsToDeploy - troops < 0)
            this.troopsToDeploy = 0;
        else
            this.troopsToDeploy -= troops;
    }
    /**
     * Add an owned country to this player
     * @param country the owned country to add
     */

    public void addCountry(Country country){
       countryIndexes.add(map.getIndexOfCountry(country));
       country.setOwner(this);
    }
    /**
     * Remove an owned country to this player
     * @param country the owned country to remove
     */
    public void removeCountry(Country country){
        countryIndexes.removeAll(Collections.singleton(map.getIndexOfCountry(country)));

    }

    /**
     * Player has lost and boolean is set to true
     */
    public void setHasLost(boolean b){
     hasLost = b;
    }

    /**
     * Add the owned country indexes to the player's CountryIndex list
     * @param countries the countries that the players owns
     */
    public void setCountries(Country[] countries){
        for(Country country: countries){
            countryIndexes.add(map.getIndexOfCountry(country));
        }
    }

    /**
     * Getter for name
     * @return this player's name
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for the number of troops to deploy
     * @return number troops that can be deployed
     */
    public int getTroopsToDeploy() {
        return troopsToDeploy;
    }

    /**
     * Gets all of the owned countries by their index and return an array
     * @return an array of owned countries
     */
    public Country[] getCountries() {
        Country[] tmpCountries=new Country[countryIndexes.size()];
        int i = 0;
        for (int x : countryIndexes){
            tmpCountries[i] = map.getCountries()[x];
            i++;
        }

        return tmpCountries;
    }

    /**
     * Returns a boolean if this player has Lost
     * @return boolean
     */
    public boolean getHasLost() {
        return hasLost;
    }

    /**
     * Getter for the players Color attribute
     * @return the player's Color
     */
    public Color getColor() {
        return playerColor;
    }

    /**
     * ToString for printing players name
     * @return the name of the player
     */
    public String toString(){
        return name;
    }
}
