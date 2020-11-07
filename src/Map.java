import java.awt.image.BufferedImage;

public class Map {
    /** List of the countries in the game **/
    public final Country[] countries;
    /** List of all the continents in the game **/
    public final Continent[] continents;
    private transient BufferedImage mapImage;

    Map(Country[] countries, Continent[] continents){
        this.countries=countries;
        this.continents=continents;
    }
    public BufferedImage getMapImage() {
        return mapImage;
    }
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
