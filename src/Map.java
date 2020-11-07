import java.awt.image.BufferedImage;

/**
 * Used to represent a Risk Map with the list of countries, continents and the image in memory
 * @author Kevin Fox (Documentation)
 * @version 11.07.2020
 */
public class Map {
    /** List of the countries in the game **/
    public final Country[] countries;
    /** List of all the continents in the game **/
    public final Continent[] continents;
    /** Image path **/
    private transient BufferedImage mapImage;

    /**
     * 2 param contstructor of a Map
     * @param countries array of countries on the map
     * @param continents array of continents on the map
     */
    public Map(Country[] countries, Continent[] continents){
        this.countries=countries;
        this.continents=continents;
    }

    /**
     * getter for MapImage
     * @return the mapImage in memory
     */
    public BufferedImage getMapImage() {
        return mapImage;
    }

    /**
     * Setter for mapImage
     * @param mapImage the image in memory
     */
    public void setMapImage(BufferedImage mapImage) {
        this.mapImage = mapImage;
    }

    /**
     * Displays the country information
     */
    public void printCountries(){
        for(int i=0;i<countries.length;i++){
            System.out.printf("%s\n", countries[i].getName());
            for(int j = 0; j< countries[i].getAdjacentCountries().length; j++){
                System.out.printf("\t%s\n", countries[i].getAdjacentCountries()[j].getName());
            }
        }
    }
    /**
     * Displays the continent information
     */
    public void printContinents(){
        for(int i=0;i<continents.length;i++){
            System.out.printf("%s\n", continents[i].getName());
            for(int j = 0; j< continents[i].getCountryList().length; j++){
                System.out.printf("\t%s\n", continents[i].getCountryList()[j].getName());
            }
        }
    }
}
