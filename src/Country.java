import java.awt.*;
import java.util.ArrayList;
import java.util.Stack;

/**Describing a country on the map in a game of RISK
 *
 * @author  Kshitij Sawhney, Omar Hashmi, Kevin Fox
 * @version  11.07.2020
 */
public class Country {
    /** Name of the country **/
    private  final String name;
    /** List of the county's vertices **/
    private final Point[] vertices;
    /** Bounding coordinates for the country **/
    private transient int minX,minY,maxX,maxY;
    /** The coordinates of the country's center **/
    private transient Point centerCoordinates;
    /** Array of countries that lie adjacent to the current country **/
    private transient Country[] adjacentCountries;
    /** Array of country Ids that lie adjacent to the current country **/
    private int[] adjacentCountriesIDs;
    /** Name of the owner **/
    private Player owner;
    /** Army currently occupying this country **/
    private int army;
    private int continentId;


    /**
     * Constructor for Country
     *
     * @param name Name of the country
     * @param vertices List of the vertices of the country on the map
     */
    public Country(String name, Point[] vertices) {
        this.name = name;
        this.vertices = vertices;
    }

    /**
     * Checks if a given point is contained in the country
     *
     * @param point The point to check
     * @return True if the point is contained in the country, false if not
     */
    public boolean containsPoint(Point point){
        //Outside bounding box
        if(point.x<this.getMinX() || point.x>this.getMaxX() || point.y<this.getMinY() || point.y>this.getMaxY()){
            return false;
        }
        //Inside bounding box
        else{
            int numIntersections=0;

            //Create a ray that passes through the bounding box
            Point ray=new Point(this.minX-25,this.minY-25);

            //Get every connected pair of vertices
            Point[] vertices=this.getVertices();
            int numSides=vertices.length;
            for(int v1=0,v2=1;v1<numSides;v1++,v2=(v2+1)%numSides){
                Point vertex1=vertices[v1];
                Point vertex2=vertices[v2];

                //Get equation of the polygon's line in the form Ax+By+C=0
                int eqnA,eqnB,eqnC;

                eqnA=vertex2.y-vertex1.y;
                eqnB=vertex1.x-vertex2.x;
                eqnC=(vertex2.x*vertex1.y)-(vertex1.x*vertex2.y);

                //Plug the endpoints of the ray into the line equation
                float solution1 = (eqnA * ray.x) + (eqnB * ray.y) + eqnC;
                float solution2 = (eqnA * point.x) + (eqnB * point.y) + eqnC;

                //If the sign of both the solutions is the same, the are on the same side of the line therefore no intersection
                if ((solution1>0 && solution2>0)||
                        (solution1<0 && solution2<0)){
                    continue;
                }

                //Get the equation of the ray in the form Ax+By+C=0
                eqnA= point.y-ray.y;
                eqnB=ray.x- point.x;
                eqnC=(point.x*ray.y)-(ray.x* point.y);

                //Plug the endpoints of the line into the ray equation
                solution1 = (eqnA * vertex1.x) + (eqnB * vertex1.y) + eqnC;
                solution2 = (eqnA * vertex2.x) + (eqnB * vertex2.y) + eqnC;

                //If the sign of both the solutions is the same, the are on the same side of the ray therefore no intersection
                if ((solution1>0 && solution2>0)||
                        (solution1<0 && solution2<0)){
                    continue;
                }

                //If here the lines are either intersecting or collinear. The guy on stack overflow ignored collinear so I will as well
                numIntersections++;
            }

            //Point is in the polygon
            return (numIntersections%2==1);
        }
    }

    /**
     * Sets the country's owner
     *
     * @param owner The player who owns the country
     */
    public void setOwner(Player owner) {
        this.owner = owner;
    }
    /**
     * Sets the country's army size
     *
     * @param army Number of troops in the army
     */
    public void setArmy(int army){
        this.army=army;
    }
    /**
     * Adds troops to the army
     *
     * @param army Number of troops to add to the army
     */
    public void addArmy(int army){
        this.army+=army;
    }
    /**
     * Removes troops from the army
     *
     * @param army Number of troops to remove from the army
     */
    public void removeArmy(int army){
        if(army >= this.army){
            this.army=0;
        }else{
            this.army-=army;
        }
    }



    /**
     * Setter for adjacent countries to this country
     * @param adjacentCountries the adjacent countries in an array
     */
    public void setAdjacentCountries(Country[] adjacentCountries){
        this.adjacentCountries=adjacentCountries;
    }
    /**
     * Minimum x coordinate of the country's viewable box
     * @param minX minimum x coordinate
     */
    public void setMinX(int minX) {
        this.minX = minX;
    }
    /**
     * Minimum y coordinate of the country's viewable box
     * @param minY minimum y coordinate
     */
    public void setMinY(int minY) {
        this.minY = minY;
    }
    /**
     * Maximum x coordinate of the country's viewable box
     * @param maxX maximum x coordinate
     */
    public void setMaxX(int maxX) {
        this.maxX = maxX;
    }
    /**
     * Maximum y coordinate of the country's viewable box
     * @param maxY minimum y coordinate
     */
    public void setMaxY(int maxY) {
        this.maxY = maxY;
    }
    /**
     * Setter of the center coordinate for displaying the number of units
     * @param centerCoordinates the center x,y coordinate of the country
     */
    public void setCenterCoordinates(Point centerCoordinates) {
        this.centerCoordinates = centerCoordinates;
    }

    /**
     * @return Continent ID
     */
    public int getContinentId(){return continentId;}
    /**
     * @return The Name of the country
     */
    public int[] getMinMaxValues(){
        return new int[]{minX,maxX,minY,maxY};
    }
    /**
     * @return The country's name
     */
    public String getName() {
        return name;
    }
    /**
     * @return The list of the country's defining vertices
     */
    public Point[] getVertices() {
        return vertices;
    }
    /**
     * @return The min X value of the country's vertices
     */
    public int getMinX() {
        return minX;
    }
    /**
     * @return The min Y value of the country's vertices
     */
    public int getMinY() {
        return minY;
    }
    /**
     * @return The max X value of the country's vertices
     */
    public int getMaxX() {
        return maxX;
    }
    /**
     * @return The max Y value of the country's vertices
     */
    public int getMaxY() {
        return maxY;
    }
    /**
     * @return The coordinates of the center of the country
     */
    public Point getCenterCoordinates() {
        return centerCoordinates;
    }
    /**
     * @return A list of adjacent countries
     */
    public Country[] getAdjacentCountries() {
        return adjacentCountries;
    }
    /**
     * @return A list of adjacent countries owned by player
     */
    public Country[] getAdjacentOwnedCountries(Player player) {
        ArrayList<Country> adjacentOwnedCountriesList=new ArrayList<>();
        for(Country country: adjacentCountries){
            if(country.getOwner()==player){
                adjacentOwnedCountriesList.add(country);
            }
        }
        Country[] adjacentOwnedCountries=new Country[adjacentOwnedCountriesList.size()];
        adjacentOwnedCountries=adjacentOwnedCountriesList.toArray(adjacentOwnedCountries);
        return adjacentOwnedCountries;
    }
    /**
     * @return A list of adjacent countries not owned by player
     */
    public Country[] getAdjacentUnownedCountries(Player player) {
        ArrayList<Country> adjacentUnownedCountriesList=new ArrayList<>();
        for(Country country: adjacentCountries){
            if(country.getOwner()!=player){
                adjacentUnownedCountriesList.add(country);
            }
        }
        Country[] adjacentUnownedCountries=new Country[adjacentUnownedCountriesList.size()];
        adjacentUnownedCountries=adjacentUnownedCountriesList.toArray(adjacentUnownedCountries);
        return adjacentUnownedCountries;
    }
    public Country[] getConnectedOwnedCountries(Player player){
        return getConnectedOwnedCountries(this,this,player,new Stack<Country>());
    }
    private Country[] getConnectedOwnedCountries(Country sourceCountry, Country root, Player player, Stack<Country> toTest){
        for(Country count: sourceCountry.getAdjacentCountries()){
            if(count.getOwner() == player && !toTest.contains(count) && count != root){
                toTest.add(count);
                return(getConnectedOwnedCountries(count,root, player, toTest));
            }
        }
        Country[] tmp=new Country[toTest.size()];
        tmp=toTest.toArray(tmp);
        return tmp;
    }
    /**
     * @return A list of adjacent country IDs
     */
    public int[] getAdjacentCountryIDs() {
        return adjacentCountriesIDs;
    }
    /**
     * @return The country's owner
     */
    public Player getOwner() {
        return owner;
    }
    /**
     * @return The number of troops in the country
     */
    public int getArmy() {
        return army;
    }

    public boolean isConnected(Country country) {
        return getConnectedCountries(this,this,new Stack<Country>()).contains(country);
    }
    public Stack<Country> getConnectedCountries(Country sourceCountry,Country root, Stack<Country> toTest){
        for(Country count: sourceCountry.getAdjacentCountries()){
            if(toTest.contains(count) && count != root){
                toTest.add(count);
                return(getConnectedCountries(count,root,toTest));
            }
        }
        return toTest;
    }
}