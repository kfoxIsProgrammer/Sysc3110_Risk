import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;

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
    protected int troopsToDeploy;
    /** Hashmap of owned Countries **/
    protected ArrayList<Country> countries;
    /** This is used for lose condition **/
    protected boolean hasLost = false;

    protected Player(String name, Color color, boolean isAI, int playerId){
        this.isAI = isAI;
        this.playerId=playerId;
        this.name = name;
        this.playerColor = color;
        this.countries=new ArrayList<>();
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
     * Player has lost and boolean is set to true
     */
    public void setHasLost(){
     this.hasLost = true;
    }
    public void setCountries(Country[] countries){
        Collections.addAll(this.countries,countries);
    }

    public String getName() {
        return name;
    }
    public int getTroopsToDeploy() {
        return troopsToDeploy;
    }
    public Country[] getCountries() {
        Country[] tmpCountries=new Country[countries.size()];
        tmpCountries=countries.toArray(tmpCountries);

        return tmpCountries;
    }
    public boolean getHasLost() {
        return hasLost;
    }
    public Color getColor() {
        return playerColor;
    }

}
