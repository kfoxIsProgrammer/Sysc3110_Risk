import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.zip.*;

/**
 * Accepts a RiskMap file and generates the required object from that information
 *
 * @author Omar Hashmi
 * @version 11.01.2020
 */
public class MapImport {
    private String filepath;
    private ZipFile zipFile;
    private BufferedImage mapImage;

    /** List of the countries in the game **/
    private ArrayList<Country> countries;
    /** List of all the continents in the game **/
    private ArrayList<Continent> continents;

    /** Reads the file data into countries and continents **/
    public MapImport(String filepath){
        this.filepath=filepath;
        this.countries=new ArrayList<>();
        this.continents=new ArrayList<>();

        try{
            this.zipFile=new ZipFile(filepath);

            for(Enumeration<? extends ZipEntry> entryReader=this.zipFile.entries(); entryReader.hasMoreElements();){
                ZipEntry entry=entryReader.nextElement();

                if(entry.getName().toLowerCase().equals("map.png")){
                    parseMapImage(entry);
                }
                else if(entry.getName().toLowerCase().equals("map.json")){
                    parseJSONData(entry);
                }
            }
        }
        catch(IOException e){
            System.err.printf("Unable to load %s\n", filepath);
        }
    }

    private void parseMapImage(ZipEntry entry){
        try {
            this.mapImage=ImageIO.read(this.zipFile.getInputStream(entry));
        } catch (IOException e) {
            System.err.printf("Unable to load map image\n");
        }
    }
    private void parseJSONData(ZipEntry entry){
        //TODO Add JSON parser
    }

    /**
     * @return The contents of the countries ArrayList
     */
    public ArrayList<Country> getCountries() {
        return countries;
    }
    /**
     * @return The contents of the continents ArrayList
     */
    public ArrayList<Continent> getContinents() {
        return continents;
    }
    /**
     * @return An image of the map
     */
    public BufferedImage getMapImage(){
        return mapImage;
    }

    /**
     * Displays the country information for debug purposes
     */
    public void printCountries(){
        for(int i=0;i<countries.size();i++){
            System.out.printf("%s\n",countries.get(i).getName());
            for(int j=0;j<countries.get(i).getAdjacentCountries().size();j++){
                System.out.printf("\t%s\n",countries.get(i).getAdjacentCountries().get(j).getName());
            }
        }
    }
    /**
     * Displays the continent information for debug purposes
     */
    public void printContinents(){
        for(int i=0;i<continents.size();i++){
            System.out.printf("%s\n",continents.get(i).getName());
            for(int j=0;j<continents.get(i).getCountryList().size();j++){
                System.out.printf("\t%s\n",continents.get(i).getCountryList().get(j).getName());
            }
        }
    }

    public static void main(String[] args) {
        MapImport parser=new MapImport("D:/Downloads/chao.risk");

        parser.printCountries();
        parser.printContinents();
    }
}