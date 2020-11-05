import java.awt.*;
import java.util.ArrayList;

/**Describing a country on the map in a game of RISK
 *
 * @author  Kshitij Sawhney, Omar Hashmi
 * @version  11.04.2020
 */
public class Country {
    /** Name of the country **/
    private final String name;
    /** List of the county's vertices **/
    private final ArrayList<Point> vertices;
    /** Bounding coordinates for the country **/
    private final int minX,minY,maxX,maxY;
    /** The coordinates of the country's center **/
    private final Point centerCoordinates;
    /** ArrayList of countries that lie adjacent to the current country **/
    private ArrayList<Country> adjacentCountries;
    /** Name of the owner **/
    private Player owner;
    /** Army currently occupying this country **/
    private int army;

    /**
     * Constructor for Country
     *
     * @param name Name of the country
     * @param vertices List of the vertices of the country on the map
     * @param minX  Minimum X coordinate from the vertices
     * @param minY  Minimum Y coordinate from the vertices
     * @param maxX  Maximum X coordinate from the vertices
     * @param maxY  Maximum Y coordinate from the vertices
     */
    public Country(String name, ArrayList<Point> vertices, int minX, int minY, int maxX, int maxY, Point centerCoordinates) {
        this.name = name;
        this.vertices = vertices;
        this.minX = minX;
        this.minY = minY;
        this.maxX = maxX;
        this.maxY = maxY;
        this.centerCoordinates = centerCoordinates;
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
            Point ray1=new Point(this.minX-25,this.minY-25);
            Point ray2=point;

            //Get every connected pair of vertices
            ArrayList<Point> vertices=this.getVertices();
            int numSides=vertices.size();
            for(int v1=0,v2=1;v1<numSides;v1++,v2=(v2+1)%numSides){
                Point vertex1=vertices.get(v1);
                Point vertex2=vertices.get(v2);

                //Get equation of the polygon's line in the form Ax+By+C=0
                int eqnA,eqnB,eqnC;

                eqnA=vertex2.y-vertex1.y;
                eqnB=vertex1.x-vertex2.x;
                eqnC=(vertex2.x*vertex1.y)-(vertex1.x*vertex2.y);

                //Plug the endpoints of the ray into the line equation
                float solution1 = (eqnA * ray1.x) + (eqnB * ray1.y) + eqnC;
                float solution2 = (eqnA * ray2.x) + (eqnB * ray2.y) + eqnC;

                //If the sign of both the solutions is the same, the are on the same side of the line therefore no intersection
                if ((solution1>0 && solution2>0)||
                        (solution1<0 && solution2<0)){
                    continue;
                }

                //Get the equation of the ray in the form Ax+By+C=0
                eqnA=ray2.y-ray1.y;
                eqnB=ray1.x-ray2.x;
                eqnC=(ray2.x*ray1.y)-(ray1.x*ray2.y);

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
     * Adds an adjacent country
     *
     * @param adjacentCountries The adjacent country
     */
    public void addAdjacentCountries(ArrayList<Country> adjacentCountries){
        this.adjacentCountries =adjacentCountries;
    }

    /**
     * Gets the country name
     *
     * @return The Name of the country
     */
    public String getName() {
        return name;
    }
    /**
     * Gets a list of the country's defining vertices
     *
     * @return The list of vertices
     */
    public ArrayList<Point> getVertices() {
        return vertices;
    }
    /**
     * Gets the min X value of the country's vertices
     *
     * @return The min X value
     */
    public int getMinX() {
        return minX;
    }
    /**
     * Gets the min Y value of the country's vertices
     *
     * @return The min Y value
     */
    public int getMinY() {
        return minY;
    }
    /**
     * Gets the max X value of the country's vertices
     *
     * @return The max X value
     */
    public int getMaxX() {
        return maxX;
    }
    /**
     * Gets the max Y value of the country's vertices
     *
     * @return The max Y value
     */
    public int getMaxY() {
        return maxY;
    }
    /**
     * Gets the coordinates of the center of the country
     *
     * @return The center coordinates
     */
    public Point getCenterCoordinates() {
        return centerCoordinates;
    }
    /**
     * Gets a list of adjacent countries
     *
     * @return The adjacent countries
     */
    public ArrayList<Country> getAdjacentCountries() {
        return adjacentCountries;
    }
    /**
     * Gets the country's owner
     *
     * @return The owner
     */
    public Player getOwner() {
        return owner;
    }
    /**
     * Gets the number of troops in the country
     *
     * @return The army
     */
    public int getArmy() {
        return army;
    }
}

