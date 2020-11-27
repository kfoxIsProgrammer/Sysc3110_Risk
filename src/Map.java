import java.awt.image.BufferedImage;

/**
 * Used to represent a Risk Map with the list of countries, continents and the image in memory
 * @author Kevin Fox (Documentation)
 * @version 11.07.2020
 */
public class Map {
    /** List of the countries in the game **/
    private Country[] countries;
    /** List of all the continents in the game **/
    private Continent[] continents;
    /** The map image **/
    private transient BufferedImage mapImage;

    public static Map Import(String filename){
        MapImport mapFileParser=new MapImport(filename);
        Map tmpMap=mapFileParser.getMapData();
        tmpMap.mapImage=mapFileParser.getMapImage();

        return tmpMap;
    }

    /**
     * @return The array of countries
     */
    public Country[] getCountries() {
        return countries;
    }
    /**
     * @return The array of continents
     */
    public Continent[] getContinents() {
        return continents;
    }
    /**
     * @return The map image
     */
    public BufferedImage getMapImage() {
        return mapImage;
    }

    /**
     * Displays the country information
     */
    public void printCountries(){
        for (Country country : countries) {
            System.out.printf("%s\n", country.getName());
        }
    }
    /**
     * Displays the continent information
     */
    public void printContinents(){
        for (Continent continent : continents) {
            System.out.printf("%s\n", continent.getName());
            for (Country country: continent.getCountries()) {
                System.out.printf("\t%s\n", country.getName());
            }
        }
    }
}
