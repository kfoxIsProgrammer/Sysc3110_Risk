public class Map {
    /** List of the countries in the game **/
    public final Country[] countries;
    /** List of all the continents in the game **/
    public final Continent[] continents;

    Map(Country[] countries, Continent[] continents){
        this.countries=countries;
        this.continents=continents;
    }
}
