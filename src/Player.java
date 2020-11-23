import java.awt.*;
import java.util.ArrayList;

/**
 * Player class used to represent a player or AI, playing the game of risk
 * @author Kevin
 * @version 10-25-2020
 */
public abstract class Player {
    /** Boolean describing if this player is an ai **/
    protected final Boolean isAI;
    /** Index of this player **/
    protected final int playerId;
    /** Name of this player **/
    protected final String name;
    /** The color the player is **/
    protected final Color playerColor;
    /** Number of armies that can be allocated to a country **/
    protected int armiesToAllocate;
    /** Hashmap of owned Countries **/
    protected ArrayList<Country> countries;
    /** Hashmap of Owned Continents **/
    protected ArrayList<Continent> continents;
    /** This is used for lose condition **/
    protected boolean hasLost = false;

    protected Player(String name, Color color, boolean isAI, int playerId){
        this.isAI = isAI;
        this.playerId=playerId;
        this.name = name;
        this.playerColor = color;
        this.countries=new ArrayList<>();
        this.continents=new ArrayList<>();
    }

    /**
     * Add more army to allocate for army allocation phase
     * @param newArmy the new army to add
     */
    public void addArmyToAllocate(int newArmy){
        this.armiesToAllocate += newArmy;
    }
    /**
     * Used to decrement armiesToAllocate
     * @param removeArmy the number of army to remove
     */
    public void removeArmy(int removeArmy){
        if(this.armiesToAllocate - removeArmy < 0)
            this.armiesToAllocate = 0;
        else
            this.armiesToAllocate -= removeArmy;
    }
    /**
     * Add an owned country to this player
     * @param countryToAdd the owned country to add
     */
    public void addCountry(Country countryToAdd){
        this.countries.add(countryToAdd);
    }
    /**
     * Remove an owned country to this player
     * @param countryToRemove the owned country to remove
     */
    public void removeCountry(Country countryToRemove){
        this.countries.remove(countryToRemove);
    }
    /**
     * Add an owned continent to this player
     * @param continentToAdd the owned continent to add
     */
    public void addContinent(Continent continentToAdd){
        this.continents.add(continentToAdd);
    }
    /**
     * Remove an owned continent to this player
     * @param continentToRemove the owned continent to remove
     */
    public void removeContinent(Continent continentToRemove){
        this.continents.remove(continentToRemove);
    }

    /**
     * Player has lost and boolean is set to true
     */
    public void setHasLost(){
     this.hasLost = true;
    }

    public Boolean getAI() {
        return isAI;
    }
    public String getName() {
        return name;
    }
    public int getArmiesToAllocate() {
        return armiesToAllocate;
    }
    public ArrayList<Country> getCountries() {
        return countries;
    }
    public ArrayList<Continent> getContinents() {
        return continents;
    }
    public boolean getHasLost() {
        return hasLost;
    }
    public Color getColor() {
        return playerColor;
    }
}
