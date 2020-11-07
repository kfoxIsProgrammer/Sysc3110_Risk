import com.google.gson.Gson;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Enumeration;
import java.util.Scanner;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Accepts a RiskMap file and generates the required object from that information
 *
 * @author Omar Hashmi
 * @version 11.01.2020
 */
public class MapImport {
    private Map map;
    private BufferedImage mapImage;
    /** List of the countries in the game **/
    private Country[] countries;
    /** List of all the continents in the game **/
    private Continent[] continents;

    /** Reads the file data into countries and continents **/
    public MapImport(String filepath){
        try{
            ZipFile zipFile=new ZipFile(filepath);

            boolean dataLoaded=false;
            boolean imageLoaded=false;

            for(Enumeration<? extends ZipEntry> entryReader = zipFile.entries(); entryReader.hasMoreElements();){
                ZipEntry entry=entryReader.nextElement();

                if(entry.getName().toLowerCase().split("\\.(?=[^\\.]+$)")[1].equals("png")){
                    imageLoaded=parseMapImage(zipFile, entry);
                }
                else if(entry.getName().toLowerCase().split("\\.(?=[^\\.]+$)")[1].equals("json")){
                    InputStream inputStream=zipFile.getInputStream(entry);

                    Scanner s = new Scanner(inputStream).useDelimiter("\\A");
                    String result = s.hasNext() ? s.next() : "";

                    dataLoaded=parseJSONData(result);
                }
            }
            if(dataLoaded && imageLoaded){
                this.map.setMapImage(this.mapImage);
                System.out.printf("Map set\n");
            }
        }
        catch(IOException e){
            System.err.printf("Unable to load %s\n", filepath);
        }
    }

    private boolean parseMapImage(ZipFile zipFile, ZipEntry entry){
        try {
            this.mapImage=ImageIO.read(zipFile.getInputStream(entry));
            System.out.printf("Map image loaded\n");
            return true;
        } catch (IOException e) {
            System.out.printf("Unable to load map image\n");
            return false;
        }
    }

    /**
     * Helper function to parseJSON into objects
     * @param jsonData the JSON string
     * @return boolean if successful
     */
    private boolean parseJSONData(String jsonData){
        Gson gson=new Gson();
        map=gson.fromJson(jsonData,Map.class);

        for(Country country: map.countries){
            int numAdjacentCountries=country.getAdjacentCountryIDs().length;
            Country[] adjacentCountries=new Country[numAdjacentCountries];
            for(int i=0;i<numAdjacentCountries;i++){
                adjacentCountries[i]=map.countries[country.getAdjacentCountryIDs()[i]];
            }
            country.setAdjacentCountries(adjacentCountries);

            int minX=Integer.MAX_VALUE;
            int minY=Integer.MAX_VALUE;
            int maxX=0;
            int maxY=0;
            int avgX=0;
            int avgY=0;
            for(Point vertex: country.getVertices()){
                if(vertex.x<minX){
                    minX=vertex.x;
                }
                if(vertex.y<minY){
                    minY=vertex.y;
                }
                if(vertex.x>maxX){
                    maxX=vertex.x;
                }
                if(vertex.y>maxY){
                    maxY=vertex.y;
                }
                avgX+=vertex.x;
                avgY+=vertex.y;
            }
            avgX/=country.getVertices().length;
            avgY/=country.getVertices().length;
            country.setMinX(minX);
            country.setMinY(minY);
            country.setMaxX(maxX);
            country.setMaxY(maxY);
            country.setCenterCoordinates(new Point(avgX,avgY));
        }

        System.out.printf("Map data loaded\n");
        return true;
    }

    /**
     * Helper function to read JSON from a file
     * @param filepath the JSON file to read
     */
    private void readJSONFile(String filepath){
        Gson gson = new Gson();

        try (Reader reader = new FileReader(filepath)) {

            // Convert JSON File to Java Object
            Map map = gson.fromJson(reader, Map.class);

//            readOldData("oldmaps/test.txt");
//
//            Country[] countries3=new Country[this.countries.size()];
//            countries3=this.countries.toArray(countries3);
//
//            Continent[] continents3=new Continent[this.continents.size()];
//            continents3=this.continents.toArray(continents3);
//
//            Map tess=new Map(countries3,continents3);

            System.out.printf("%s\n",gson.toJson(map));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Helper function to read data from a map image
     * @param filepath the file path to the map
     */
    private void readMapImage(String filepath){
        try {
            this.mapImage=ImageIO.read(new File(filepath));
        }catch(IOException e){
            System.out.printf("Unable to load %s, file not found\n",filepath);
            e.printStackTrace();
        }
    }

    /**
     * @return The contents of the countries ArrayList
     */
    public Country[] getCountries() {
        return countries;
    }
    /**
     * @return The contents of the continents ArrayList
     */
    public Continent[] getContinents() {
        return continents;
    }
    public Map getMap(){
        return map;
    }

    /**
     * Displays the country information for debug purposes
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
     * Displays the continent information for debug purposes
     */
    public void printContinents(){
        for(int i=0;i<continents.length;i++){
            System.out.printf("%s\n", continents[i].getName());
            for(int j = 0; j< continents[i].getCountryList().length; j++){
                System.out.printf("\t%s\n", continents[i].getCountryList()[j].getName());
            }
        }
    }

    public static void main(String[] args) {
        MapImport parser=new MapImport("maps/test.zip");

    }
}