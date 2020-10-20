import java.util.ArrayList;

/**Describing a country on the map in a game of RISK
 * @author  Kshitij Sawhney
 * @version  10 / 20 / 2020
 */
public class Country {
    /** army currently occupying this country*/
    private int army;

    /**ArrayList of countries that lie adjacent to the current country*/
    private ArrayList<Country> adjancentCountries;

    /**Name of the country*/
    private String name;

    /**Name of the owner*/
    private Player owner;

    //private isOwned;                  //(left out as all countries are owned at all times)

    /**
     *1 param Constructor for Country
     * @param name name of the country
     */
    public Country(String name){
        this.name=name;
    }

    /**Add countries adjacent to this country
     * @param adjancentCountries list of countries adjacent to this one*/
    public void addAdjacentCountries(ArrayList<Country> adjancentCountries){
        this.adjancentCountries=adjancentCountries;
    }

    /**Getter for name
     * @return name of the country*/
    public String getName(){
        return this.name;
    }

    /**Setter for the owner
     * @param owner owner of the given country*/
    public void setOwner(Player owner) {
        this.owner = owner;
    }

    /**Getter for the owner
     * @return the owner of the given country*/
    public Player getOwner() {
        return owner;
    }

    /**Setter of the initial army
     * @param army the initial number of soldiers occupying the country*/
    public void setInitialArmy(int army){
        this.army=army;
    }

    /**Adds more troops to this country
     * @param army  the number of troops being added*/
    public void addArmy(int army){
        this.army+=army;
    }

    /**Removes the number of fallen soldiers
     * @param army number of soldiers lost*/
    public void removeArmy(int army){
        if(army >= this.army){
            this.army=0;
        }else{
            this.army-=army;
        }
    }

    /**Getter for army
     * @return number of soldiers currently in the country*/
    public int getArmy(){
        return army;
    }

    /**Getter for adjacentCountries
     * @return list of countries adjacent to this one*/
    public ArrayList<Country> getAdjancentCountries() {
        return adjancentCountries;
    }
}
