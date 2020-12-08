import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Used to represent a Risk Map with the list of countries, continents and the image in memory
 * @author Kevin Fox (Documentation)
 * @version 11.07.2020
 */
public class Map {
    /** List of the countries in the game **/
    private  Country[] countries;
    /** List of all the continents in the game **/
    private  Continent[] continents;
    /** The map image **/
    private transient BufferedImage mapImage;
    private String filename;
    private ArrayList<Country> validCountries;

    public static Map Import(String filename){
        MapImport mapFileParser=new MapImport(filename);
        Map tmpMap=mapFileParser.getMapData();
        tmpMap.mapImage=mapFileParser.getMapImage();
        tmpMap.filename = filename;
        if(tmpMap.checkValidMap()){
            System.out.println("This is a valid map");
        }
        else{
            System.out.println("This is an invalid map");
        }
            return tmpMap;

    }

    public String getFilename(){return filename;}

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
     * Checks if a point is inside any country
     *
     * @param point The point to be tested
     * @return The country that the points is inside, null if it isn't in any country
     */
    public Country pointToCountry(Point point){
        for(Country country: countries){
            if(country.containsPoint(point)){
                return country;
            }
        }
        return null;
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
     * Used to check if data supplied will be able to be a valid map
     * @return true : valid, False : invalid
     */
    public boolean checkValidMap(){
        validCountries = new ArrayList<>();

        Country startCountry = this.getCountries()[0];

        if(startCountry!= null){
            checkAdjacentCountryHelper(startCountry);
        }
        if(countries.length == validCountries.size() && countries.length > 6) {
            return true;
        }
        else {
            return false;
        }

    }

    /**
     * This is a helper recursive function of DFS to check all countries
     * @param country the country to check
     */
    private void checkAdjacentCountryHelper(Country country){
        if (validCountries.size() <= countries.length  ) {
            for (Country nextCountry : country.getAdjacentCountries()) {
                if(!validCountries.contains(nextCountry)){
                    validCountries.add(nextCountry);
                    checkAdjacentCountryHelper(nextCountry);
                }
            }
        }
        return;


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
    public int getIndexOfCountry(Country country) {
        for (int x = 0; x < this.getCountries().length; x++) {
            if (country == this.getCountries()[x]) {
                return x;
            }
        }
        return -1;
    }
}
