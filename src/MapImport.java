import java.awt.*;
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
    /** List of the countries in the game **/
    private ArrayList<Country> countries;
    /** List of all the continents in the game **/
    private ArrayList<Continent> continents;

    /** Reads the file data into countries and continents **/
    public MapImport(String filename){
        this.countries=new ArrayList<>();
        this.continents=new ArrayList<>();

        try{
            File riskMap=new File(filename);
            Scanner fileReader=new Scanner(riskMap);
            while(fileReader.hasNextLine()){
                String line=fileReader.nextLine();

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
                    try {
                        Country country;
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

                        country = new Country(countryName, countryVertices,minX,minY,maxX,maxY,new Point(avgX,avgY));
                        this.countries.add(country);
                    }
                    catch (Exception e){
                        System.out.printf("Unable to load %s, error in file\n",filename);
                    }
                }
                //Line is a continent
                else if(line.charAt(0)=='c'&&line.charAt(1)=='t'){
                    Continent continent;
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
                    continent=new Continent(continentName,countries,continentBonus);
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

                    this.countries.get(Integer.parseInt(countryIds[0])).addAdjacentCountries(adjacentCountries);
                }
            }
        }
        catch(FileNotFoundException e){
            System.out.printf("Unable to load %s, file not found\n",filename);
            e.printStackTrace();
        }
    }

    /**
     * Returns the contents of the countries ArrayList
     *
     * @return The country data from the file
     */
    public ArrayList<Country> getCountries() {
        return countries;
    }
    /**
     * Returns the contents of the continents ArrayList
     *
     * @return The continent data from the file
     */
    public ArrayList<Continent> getContinents() {
        return continents;
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
        MapImport parser=new MapImport("maps\\demoMap.RiskMap");

        parser.printCountries();
        parser.printContinents();
    }
//    public static void main(String[] args) throws IOException {
//        ZipFile zipFile = new ZipFile("D:/Downloads/chao.risk");
//
//        Enumeration<? extends ZipEntry> entries = zipFile.entries();
//
//        while(entries.hasMoreElements()){
//            ZipEntry entry = entries.nextElement();
//            System.out.printf("%s\n",entry.getName());
//
//            //InputStream stream = zipFile.getInputStream(entry);
//        }
//    }
}