import com.google.gson.Gson;

import javax.imageio.ImageIO;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
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

        try{
            this.zipFile=new ZipFile(filepath);

            for(Enumeration<? extends ZipEntry> entryReader = this.zipFile.entries(); entryReader.hasMoreElements();){
                ZipEntry entry=entryReader.nextElement();

                if(entry.getName().toLowerCase().equals("test.png")){
                    parseMapImage(entry);
                }
                else if(entry.getName().toLowerCase().equals("test.json")){
                    InputStream inputStream=zipFile.getInputStream(entry);
                    //parseJSONData();
                }
            }
        }
        catch(IOException e){
            System.err.printf("Unable to load %s\n", filepath);
        }
    }
    public MapImport(String zipPath,String dataPath){
        this.mapImage=new MapImport(zipPath).getMapImage();
        readOldFile(dataPath);
    }
    private void parseMapImage(ZipEntry entry){
        try {
            this.mapImage=ImageIO.read(this.zipFile.getInputStream(entry));
            System.out.printf("Map image loaded\n");
        } catch (IOException e) {
            System.err.printf("Unable to load map image\n");
        }
    }
    private void readJSONFile(String filepath){
        Gson gson = new Gson();

        try (Reader reader = new FileReader("maps/test.json")) {

            // Convert JSON File to Java Object
            Map map = gson.fromJson(reader, Map.class);

            // print staff
            System.out.println(map);

        } catch (IOException e) {
            e.printStackTrace();
        }


    }
    private void parseJSONData(String jsonData){
        Gson gson=new Gson();
        gson.fromJson(jsonData,Map.class);

        System.out.printf("Map data loaded\n");
    }
    private void readOldFile(String filename){
        this.countries=new ArrayList<>();
        this.continents=new ArrayList<>();

        try{
            File riskMap=new File(filename);
            Scanner fileReader=new Scanner(riskMap);
            while(fileReader.hasNextLine()){
                String line=fileReader.nextLine();
                System.out.printf("%s\n",line);
                //Line is empty
                if(line.length()==0){
                    continue;
                }
                //Line is a comment
                else if(line.charAt(0)=='#'){
                    System.out.printf("%s\n",line);
                }
                //Line is a country
                else if(line.charAt(0)=='c'&&line.charAt(1)=='n'){
                    String countryName = "";
                    ArrayList<Point> countryVertices = new ArrayList<>();

                    //Read the country name including spaces
                    int i = 4;
                    for (; line.charAt(i) != '\"'; i++) {
                        countryName += line.charAt(i);
                    }

                    //Everything following the country name must be coordinates
                    int minX=Integer.MAX_VALUE;
                    int minY=Integer.MAX_VALUE;
                    int maxX=0;
                    int maxY=0;
                    int avgX=0;
                    int avgY=0;

                    String[] vertices = line.substring(i + 2).split(" ");
                    for (int j=0;j<vertices.length;j++) {
                        String[] pos = vertices[j].split(",");
                        int x = Integer.parseInt(pos[0]);
                        int y = Integer.parseInt(pos[1]);

                        if(x<minX){minX=x;}
                        if(y<minY){minY=y;}
                        if(x>maxX){maxX=x;}
                        if(y>maxY){maxY=y;}

                        avgX+=x;
                        avgY+=y;

                        countryVertices.add(new Point(x, y));
                    }
                    avgX/=vertices.length;
                    avgY/=vertices.length;

                    Point[] countryVertices1=new Point[countryVertices.size()];
                    countryVertices1 = countryVertices.toArray(countryVertices1);
                    Country country = new Country(countryName, countryVertices1,minX,minY,maxX,maxY,new Point(avgX,avgY));
                    this.countries.add(country);
                }
                //Line is a continent
                else if(line.charAt(0)=='c'&&line.charAt(1)=='t'){
                    String continentName="";
                    ArrayList<Country> countries=new ArrayList<>();
                    int continentBonus;

                    //Read the continent name including spaces
                    int i=4;
                    for(;line.charAt(i)!='\"';i++){
                        continentName+=line.charAt(i);
                    }

                    //Everything following the continent name must be countries
                    String[] countryIds=line.substring(i+2).split(" ");
                    continentBonus=Integer.parseInt(countryIds[0]);
                    for(int j=1;j<countryIds.length;j++){
                        int countryIndex=Integer.parseInt(countryIds[j]);
                        countries.add(this.countries.get(countryIndex));
                    }
                    Country[] countries1=new Country[countries.size()];
                    countries1 = countries.toArray(countries1);
                    Continent continent=new Continent(continentName, countries1,continentBonus);
                    this.continents.add(continent);
                }
                //Line is an adjacency
                else if(line.charAt(0)=='a'&&line.charAt(1)=='d'){
                    String[] countryIds=line.substring(3).split(" ");
                    ArrayList<Country> adjacentCountries=new ArrayList<>();

                    for(int i=1;i<countryIds.length;i++){
                        int countryIndex=Integer.parseInt(countryIds[i]);
                        adjacentCountries.add(this.countries.get(countryIndex));
                    }

                    Country[] adjacentCountries1=new Country[adjacentCountries.size()];
                    adjacentCountries1 = adjacentCountries.toArray(adjacentCountries1);
                    this.countries.get(Integer.parseInt(countryIds[0])).setAdjacentCountries(adjacentCountries1);
                }
            }
        }
        catch(FileNotFoundException e){
            System.out.printf("Unable to load %s, file not found\n",filename);
            e.printStackTrace();
        }
    }

    /**
     * @return The contents of the countries ArrayList
     */
    public Country[] getCountries() {
        return (Country[]) countries.toArray();
    }
    /**
     * @return The contents of the continents ArrayList
     */
    public Continent[] getContinents() {
        return (Continent[]) continents.toArray();
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
            System.out.printf("%s\n", countries.get(i).getName());
            for(int j = 0; j< countries.get(i).getAdjacentCountries().length; j++){
                System.out.printf("\t%s\n", countries.get(i).getAdjacentCountries()[j].getName());
            }
        }
    }
    /**
     * Displays the continent information for debug purposes
     */
    public void printContinents(){
        for(int i=0;i<continents.size();i++){
            System.out.printf("%s\n", continents.get(i).getName());
            for(int j = 0; j< continents.get(i).getCountryList().length; j++){
                System.out.printf("\t%s\n", continents.get(i).getCountryList()[j].getName());
            }
        }
    }

    public static void main(String[] args) {
        MapImport parser=new MapImport("maps/test.zip","oldmaps/test.txt");
        //
//        Gson gson=new Gson();
//
//        Map map=new Map(parser.countries,parser.continents);
//
//        try {
//            FileWriter fw=new FileWriter("D:\\Downloads\\helpme.json");
//            gson.toJson(map, fw);
//            fw.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        parser.printCountries();
        parser.printContinents();
    }
}