import java.util.HashMap;

/**
 * Player class used to represent a player or ai, playing the game of risk
 * @author Kevin
 * @version 10-25-2020
 */
public class Player {

    /**
     * Boolean describing if this player is an ai
     */
    private Boolean isAi;

    /**
     * Name of this player
     */
    private String name;


    /**
     * Number of armies that can be allocated to a country
     */
    private int ArmiesToAllocate;

    /**
     * Hashmap of owned Countries
     */
    private HashMap<String,Country> ownedCountries;

    /**
     * Hashmap of Owned Continents
     */
    private HashMap<String, Continent> ownedContinents;

    /**
     * This is used for lose condition
     */
    private boolean hasLost = false;

    /**
     * 2 param Constructor for new human players
     * @param name name of the player
     * @param armyValue initial army to allocate
     */
    public Player(String name, int armyValue){
        this.isAi = false;
        this.name = name;
        this.ArmiesToAllocate = armyValue;
        ownedCountries = new HashMap<>();
        ownedContinents = new HashMap<>();
    }

    /**
     * 3 param Constructor for new ai players
     * @param name name of the ai
     * @param armyValue initial army to allocate
     * @param isAi boolean representing that this player is an ai
     */
    public Player(String name, int armyValue, Boolean isAi){
        this.isAi = true;
        this.name = name;
        this.ArmiesToAllocate = armyValue;
        ownedCountries = new HashMap<>();
        ownedContinents = new HashMap<>();
    }

    /**
     * Getter for name
     * @return name:String
     */
    public String getName(){
        return this.name;
    }

    /**
     * Getter for ArmiesToAllocate
     * @return the number of army that this player can allocate
     */
    public int getArmiesToAllocate(){
        return this.ArmiesToAllocate;
    }

    /**
     * Add more army to allocate for army allocation phase
     * @param newArmy the new army to add
     */
    public void addArmyToAllocate(int newArmy){
        this.ArmiesToAllocate += newArmy;
    }

    /**
     * Used to decrement armiesToAllocate
     * @param removeArmy the number of army to remove
     */
    public void removeArmy(int removeArmy){
        if(this.ArmiesToAllocate - removeArmy < 0)
            this.ArmiesToAllocate = 0;
        else
            this.ArmiesToAllocate -= removeArmy;
    }

    /**
     * Getter for the Hashmap of countries owned
     * @return the owned Country Hashmap
     */
    public HashMap<String,Country> getOwnedCountries(){
        return this.ownedCountries;
    }

    /**
     * Getter for the Hashmap of Continents owned
     * @return the owned continents Hashmap
     */
    public HashMap<String, Continent> getOwnedContinents(){
        return this.ownedContinents;
    }

    /**
     * Add an owned country to this player
     * @param countryToAdd the owned country to add
     */
    public void addCountry(Country countryToAdd){
        getOwnedCountries().put(countryToAdd.getName(), countryToAdd);
    }

    /**
     * Remove an owned country to this player
     * @param countryToRemove the owned country to remove
     */
    public void removeCountry(Country countryToRemove){
        getOwnedCountries().remove(countryToRemove.getName());
    }

    /**
     * Add an owned continent to this player
     * @param continentToAdd the owned continent to add
     */
    public void addContinent(Continent continentToAdd){
        getOwnedContinents().put(continentToAdd.getName(), continentToAdd);
    }

    /**
     * Remove an owned continent to this player
     * @param continentToRemove the owned continent to remove
     */
    public void removeContinent(Continent continentToRemove){
        getOwnedContinents().remove(continentToRemove.getName());
    }

    /**
     * Player has lost and boolean is set to true
     */
    public void hasLost(){
     this.hasLost = true;
    }

    /**
     * getter for hasLost boolean
     * @return the boolean if the player has lost
     */
    public boolean getHasLost(){return this.hasLost;}
}
