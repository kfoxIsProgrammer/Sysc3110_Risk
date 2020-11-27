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
    /** A destination object for the imported data **/
    private Map map;
    /** The loaded image **/
    private BufferedImage mapImage;

    /** Reads the file data into countries and continents **/
    public MapImport(String filepath){
        try{
            ZipFile zipFile=new ZipFile(filepath);

            boolean dataLoaded=false;
            boolean imageLoaded=false;

            for(Enumeration<? extends ZipEntry> entryReader = zipFile.entries(); entryReader.hasMoreElements();){
                ZipEntry entry=entryReader.nextElement();

                if(entry.getName().toLowerCase().split("\\.")[1].equals("png")){
                    imageLoaded=parseMapImage(zipFile, entry);
                    if(imageLoaded){
                        System.out.printf("Map set: %s\n",entry.getName());
                    }
                }
                else if(entry.getName().toLowerCase().split("\\.")[1].equals("json")){
                    InputStream inputStream=zipFile.getInputStream(entry);

                    Scanner s = new Scanner(inputStream).useDelimiter("\\A");
                    String result = s.hasNext() ? s.next() : "";

                    dataLoaded=parseJSONData(result);

                    if(dataLoaded){
                        System.out.printf("Data set: %s\n",entry.getName());
                    }
                }
            }
            if (!dataLoaded || !imageLoaded) {
                System.out.printf("Unable to load %s\n",filepath);
            }
        }
        catch(IOException e){
            System.err.printf("Unable to load %s\n", filepath);
        }
    }

    /** Loads the map image from file into a buffer **/
    private boolean parseMapImage(ZipFile zipFile, ZipEntry entry){
        try {
            this.mapImage=ImageIO.read(zipFile.getInputStream(entry));
            return true;
        } catch (IOException e) {
            System.out.printf("Unable to load map image: %s\n",entry.getName());
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

        for(Country country: map.getCountries()){
            int numAdjacentCountries=country.getAdjacentCountryIDs().length;
            Country[] adjacentCountries=new Country[numAdjacentCountries];
            for(int i=0;i<numAdjacentCountries;i++){
                adjacentCountries[i]= map.getCountries()[country.getAdjacentCountryIDs()[i]];
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

        for(Continent continent:map.getContinents()){
            continent.IDsToCountries(map.getCountries());
        }

        return true;
    }

    public Map getMap(){
        return map;
    }
    public Map getMapData(){
        return map;
    }
    public BufferedImage getMapImage(){
        return mapImage;
    }
}