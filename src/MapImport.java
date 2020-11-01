import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import javafx.util.Pair;

public class MapImport {
    /** List of the countries in the game **/
    private ArrayList<Country> countries;
    /** List of all the continents in the game **/
    private ArrayList<Continent> continents;

    public MapImport(String filename){
        this.countries=new ArrayList<>();
        this.continents=new ArrayList<>();

        try{
            File riskMap=new File(filename);
            Scanner fileReader=new Scanner(riskMap);
            while(fileReader.hasNextLine()){
                String line=fileReader.nextLine();

                //Line is a comment
                if(line.charAt(0)=='#'){
                    System.out.printf("%s\n",line);
                }
                //Line is a country
                else if(line.charAt(0)=='c'&&line.charAt(1)=='n'){
                    Country country;
                    String countryName="";
                    ArrayList<Pair<Integer,Integer>> countryVertices=new ArrayList<>();

                    int i=4;
                    for(;line.charAt(i)!='\"';i++){
                        countryName+=line.charAt(i);
                    }

                    String[] vertices=line.substring(i+2).split(" ");
                    for(String vertex: vertices){
                        String[] pos=vertex.split(",");
                        int x=Integer.parseInt(pos[0]);
                        int y=Integer.parseInt(pos[1]);
                        countryVertices.add(new Pair<>(x,y));
                    }

                    country=new Country(countryName,countryVertices);
                    this.countries.add(country);
                }
                //Line is a continent
                else if(line.charAt(0)=='c'&&line.charAt(1)=='t'){
                    Continent continent;
                    String continentName="";
                    ArrayList<Country> countries=new ArrayList<>();
                    int continentBonus;

                    int i=4;
                    for(;line.charAt(i)!='\"';i++){
                        continentName+=line.charAt(i);
                    }

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
            System.out.printf("File not found\n");
            e.printStackTrace();
        }
    }

    public ArrayList<Country> getCountries() {
        return countries;
    }
    public ArrayList<Continent> getContinents() {
        return continents;
    }

    public void printCountries(){
        for(int i=0;i<countries.size();i++){
            System.out.printf("%s\n",countries.get(i).getName());
            for(int j=0;j<countries.get(i).getAdjacentCountries().size();j++){
                System.out.printf("\t%s\n",countries.get(i).getAdjacentCountries().get(j).getName());
            }
        }
    }
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
}