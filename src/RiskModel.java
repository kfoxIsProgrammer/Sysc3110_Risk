import java.util.*;
/**
 * Risk Model class used to model the ongoing game
 * @author Dimitry Koutchine
 * @version 10/23/20
 */
public class RiskModel {
    ArrayList<Player> players;
    ArrayList<Country> countries;
    ArrayList<Continent> continents;

    public RiskModel(){
        players = new ArrayList<Player>();
        countries = new ArrayList<Country>();
        continents = new ArrayList<Continent>();

    }
    /**
     * Initialize the countries and there adjacency's, continents, and setup the game board
     *
     */
    public void createMap(){
        ArrayList<Country> adjacent;
        Continent northAmerica, southAmerica, europe, africa, asia, australia;
        //Constructing countries
        //North America
        Country alaska = new Country("Alaska");
        Country alberta = new Country("Alberta");
        Country centralAmerica = new Country("Central America");
        Country easternUnitedStates = new Country("Eastern United States");
        Country greenLand = new Country("Greenland");
        Country northwestTerritory = new Country("Northwest Territory");
        Country ontario = new Country("Ontario");
        Country quebec = new Country("Quebec");
        Country westernUnitedSates = new Country("Western United States");
        //South America
        Country argentina = new Country("Argentina");
        Country brazil = new Country("Brazil");
        Country peru = new Country("Peru");
        Country venezuela = new Country("Venezuela");
        //Europe
        Country greatBritain = new Country("Great Britain");
        Country iceLand = new Country("Iceland");
        Country northernEurope = new Country("Northern Europe");
        Country scandinavia = new Country("Scandinavia");
        Country southernEurope = new Country("Southern Europe");
        Country ukraine = new Country("Ukraine");
        Country westernEurope = new Country("Western Europe");
        //Africa
        Country congo = new Country("Congo");
        Country eastAfrica = new Country("East Africa");
        Country egypt = new Country("Egypt");
        Country madagascar = new Country("Madagascar");
        Country northAfrica = new Country("North Africa");
        Country southAfrica = new Country("South Africa");
        //Asia
        Country afghanistan = new Country("Afghanistan");
        Country china = new Country("China");
        Country india = new Country("India");
        Country irkutsk = new Country("Irkutsk");
        Country japan = new Country("Japan");
        Country kamchatka = new Country("Kamchatka");
        Country middleEast = new Country("Middle East");
        Country mongolia = new Country("Mongolia");
        Country siam = new Country("Siam");
        Country siberia = new Country("Siberia");
        Country ural = new Country("Ural");
        Country yakutsk = new Country("Yakutsk");
        //Australia
        Country easternAustralia = new Country("Eastern Australia");
        Country indonesia = new Country("Indonesia");
        Country newGuinea = new Country("New Guinea");
        Country westernAustralia = new Country("Western Australia");
        //----------------------------adding adjacent ------------------------------

        //North America-----------------------------------------------------------------------
        List<Country> temp = Arrays.asList(alberta, northwestTerritory, kamchatka);
        adjacent = new ArrayList<Country>();
        adjacent.addAll(temp);
        alaska.addAdjacentCountries(adjacent);

        temp = Arrays.asList(alaska, northwestTerritory, ontario, westernUnitedSates);
        adjacent = new ArrayList<Country>();
        adjacent.addAll(temp);
        alberta.addAdjacentCountries(adjacent);

        temp = Arrays.asList(westernUnitedSates, venezuela, easternUnitedStates);
        adjacent = new ArrayList<Country>();
        adjacent.addAll(temp);
        centralAmerica.addAdjacentCountries(adjacent);

        temp = Arrays.asList(westernUnitedSates, ontario,quebec, centralAmerica);
        adjacent = new ArrayList<Country>();
        adjacent.addAll(temp);
        easternUnitedStates.addAdjacentCountries(adjacent);

        temp = Arrays.asList(northwestTerritory, ontario,quebec, iceLand);
        adjacent = new ArrayList<Country>();
        adjacent.addAll(temp);
        greenLand.addAdjacentCountries(adjacent);

        temp = Arrays.asList(alaska, greenLand, ontario, alberta);
        adjacent = new ArrayList<Country>();
        adjacent.addAll(temp);
        northwestTerritory.addAdjacentCountries(adjacent);

        temp = Arrays.asList(northwestTerritory, greenLand,quebec, easternUnitedStates, westernUnitedSates, alberta);
        adjacent = new ArrayList<Country>();
        adjacent.addAll(temp);
        ontario.addAdjacentCountries(adjacent);

        temp = Arrays.asList(ontario, greenLand, easternUnitedStates);
        adjacent = new ArrayList<Country>();
        adjacent.addAll(temp);
        quebec.addAdjacentCountries(adjacent);

        temp = Arrays.asList(alberta, ontario, easternUnitedStates, centralAmerica);
        adjacent = new ArrayList<Country>();
        adjacent.addAll(temp);
        westernUnitedSates.addAdjacentCountries(adjacent);

        //South America-----------------------------------------------------------------------------

        temp = Arrays.asList(venezuela,brazil, peru);
        adjacent = new ArrayList<Country>();
        adjacent.addAll(temp);
        argentina.addAdjacentCountries(adjacent);

        temp = Arrays.asList(venezuela, northAfrica, argentina, peru);
        adjacent = new ArrayList<Country>();
        adjacent.addAll(temp);
        brazil.addAdjacentCountries(adjacent);

        temp = Arrays.asList(venezuela,argentina, brazil);
        adjacent = new ArrayList<Country>();
        adjacent.addAll(temp);
        peru.addAdjacentCountries(adjacent);

        temp = Arrays.asList(centralAmerica, peru, brazil);
        adjacent = new ArrayList<Country>();
        adjacent.addAll(temp);
        venezuela.addAdjacentCountries(adjacent);

        // Europe----------------------------------------------------------------------
        temp = Arrays.asList(iceLand,scandinavia,northernEurope,westernEurope);
        adjacent = new ArrayList<Country>();
        adjacent.addAll(temp);
        greatBritain.addAdjacentCountries(adjacent);

        temp = Arrays.asList(greenLand,greatBritain,scandinavia);
        adjacent = new ArrayList<Country>();
        adjacent.addAll(temp);
        iceLand.addAdjacentCountries(adjacent);

        temp = Arrays.asList(greatBritain, scandinavia, southernEurope, ukraine, westernEurope);
        adjacent = new ArrayList<Country>();
        adjacent.addAll(temp);
        northernEurope.addAdjacentCountries(adjacent);

        temp = Arrays.asList(greatBritain, iceLand,northernEurope,ukraine);
        adjacent = new ArrayList<Country>();
        adjacent.addAll(temp);
        scandinavia.addAdjacentCountries(adjacent);

        temp = Arrays.asList(northernEurope,ukraine,westernEurope, middleEast, egypt, northAfrica);
        adjacent = new ArrayList<Country>();
        adjacent.addAll(temp);
        southernEurope.addAdjacentCountries(adjacent);

        temp = Arrays.asList(northernEurope, scandinavia, southernEurope, afghanistan, ural,middleEast);
        adjacent = new ArrayList<Country>();
        adjacent.addAll(temp);
        ukraine.addAdjacentCountries(adjacent);

        temp = Arrays.asList(greatBritain, northernEurope,southernEurope,northAfrica);
        adjacent = new ArrayList<Country>();
        adjacent.addAll(temp);
        westernEurope.addAdjacentCountries(adjacent);
        //Africa-------------------------------------------------------------------------
        temp = Arrays.asList(eastAfrica,northAfrica,southAfrica);
        adjacent = new ArrayList<Country>();
        adjacent.addAll(temp);
        congo.addAdjacentCountries(adjacent);

        temp = Arrays.asList(congo,egypt, madagascar,northAfrica, southAfrica, middleEast);
        adjacent = new ArrayList<Country>();
        adjacent.addAll(temp);
        eastAfrica.addAdjacentCountries(adjacent);

        temp = Arrays.asList(eastAfrica,northAfrica,southernEurope,middleEast);
        adjacent = new ArrayList<Country>();
        adjacent.addAll(temp);
        egypt.addAdjacentCountries(adjacent);

        temp = Arrays.asList(eastAfrica, southAfrica);
        adjacent = new ArrayList<Country>();
        adjacent.addAll(temp);
        madagascar.addAdjacentCountries(adjacent);

        temp = Arrays.asList(congo,eastAfrica,egypt,southernEurope, westernEurope, brazil);
        adjacent = new ArrayList<Country>();
        adjacent.addAll(temp);
        northAfrica.addAdjacentCountries(adjacent);

        temp = Arrays.asList(congo, eastAfrica, madagascar);
        adjacent = new ArrayList<Country>();
        adjacent.addAll(temp);
        southAfrica.addAdjacentCountries(adjacent);

        //Asia-------------------------------------------------------------

        temp = Arrays.asList(china, india,middleEast, ural,ukraine);
        adjacent = new ArrayList<Country>();
        adjacent.addAll(temp);
        afghanistan.addAdjacentCountries(adjacent);

        temp = Arrays.asList( afghanistan, india, mongolia, ural, siam, siberia);
        adjacent = new ArrayList<Country>();
        adjacent.addAll(temp);
        china.addAdjacentCountries(adjacent);

        temp = Arrays.asList(afghanistan, china,middleEast, siam);
        adjacent = new ArrayList<Country>();
        adjacent.addAll(temp);
        india.addAdjacentCountries(adjacent);

        temp = Arrays.asList(kamchatka,mongolia,siberia,yakutsk);
        adjacent = new ArrayList<Country>();
        adjacent.addAll(temp);
        irkutsk.addAdjacentCountries(adjacent);

        temp = Arrays.asList(kamchatka, mongolia);
        adjacent = new ArrayList<Country>();
        adjacent.addAll(temp);
        japan.addAdjacentCountries(adjacent);

        temp = Arrays.asList(irkutsk, japan,mongolia, yakutsk, alaska);
        adjacent = new ArrayList<Country>();
        adjacent.addAll(temp);
        kamchatka.addAdjacentCountries(adjacent);

        temp = Arrays.asList(afghanistan, india, southernEurope, siberia, eastAfrica,egypt);
        adjacent = new ArrayList<Country>();
        adjacent.addAll(temp);
        middleEast.addAdjacentCountries(adjacent);

        temp = Arrays.asList(china, irkutsk, japan, kamchatka,siberia);
        adjacent = new ArrayList<Country>();
        adjacent.addAll(temp);
        mongolia.addAdjacentCountries(adjacent);

        temp = Arrays.asList(china, india, indonesia);
        adjacent = new ArrayList<Country>();
        adjacent.addAll(temp);
        siam.addAdjacentCountries(adjacent);

        temp = Arrays.asList(irkutsk, china, mongolia, ural,  yakutsk);
        adjacent = new ArrayList<Country>();
        adjacent.addAll(temp);
        siberia.addAdjacentCountries(adjacent);

        temp = Arrays.asList(afghanistan,china, siberia, ukraine);
        adjacent = new ArrayList<Country>();
        adjacent.addAll(temp);
        ural.addAdjacentCountries(adjacent);

        temp = Arrays.asList(irkutsk, kamchatka, siberia);
        adjacent = new ArrayList<Country>();
        adjacent.addAll(temp);
        yakutsk.addAdjacentCountries(adjacent);
        //Australia-------------------------------------------
        temp = Arrays.asList(indonesia, newGuinea, westernAustralia);
        adjacent = new ArrayList<Country>();
        adjacent.addAll(temp);
        easternAustralia.addAdjacentCountries(adjacent);

        temp = Arrays.asList(easternAustralia, newGuinea, westernAustralia, siam);
        adjacent = new ArrayList<Country>();
        adjacent.addAll(temp);
        indonesia.addAdjacentCountries(adjacent);

        temp = Arrays.asList(indonesia, easternAustralia, westernAustralia);
        adjacent = new ArrayList<Country>();
        adjacent.addAll(temp);
        newGuinea.addAdjacentCountries(adjacent);

        temp = Arrays.asList(easternAustralia, newGuinea, indonesia);
        adjacent = new ArrayList<Country>();
        adjacent.addAll(temp);
        westernAustralia.addAdjacentCountries(adjacent);
        //--------------------------------------------adding to continents
        temp = Arrays.asList(alaska,alberta,centralAmerica,easternUnitedStates,greenLand, northwestTerritory, ontario,quebec , westernUnitedSates);
        adjacent = new ArrayList<Country>();
        countries.addAll(temp);
        adjacent.addAll(temp);
        northAmerica = new Continent("North America", adjacent, 5);

        temp = Arrays.asList(argentina,brazil,peru, venezuela);
        adjacent = new ArrayList<Country>();
        countries.addAll(temp);
        adjacent.addAll(temp);
        southAmerica = new Continent("South America", adjacent, 2);

        temp = Arrays.asList(greatBritain, iceLand, northernEurope, scandinavia, southernEurope, ukraine, westernEurope);
        adjacent = new ArrayList<Country>();
        countries.addAll(temp);
        adjacent.addAll(temp);
        europe = new Continent("Europe", adjacent, 5);

        temp = Arrays.asList(congo, eastAfrica, egypt,madagascar, northAfrica,southAfrica);
        adjacent = new ArrayList<Country>();
        countries.addAll(temp);
        adjacent.addAll(temp);
        africa = new Continent("Africa", adjacent, 3);

        temp = Arrays.asList(afghanistan, china, india, irkutsk, japan, kamchatka, middleEast, mongolia, siam, siberia, ural, yakutsk);
        adjacent = new ArrayList<Country>();
        countries.addAll(temp);
        adjacent.addAll(temp);
        asia = new Continent("Asia", adjacent, 7);

        temp = Arrays.asList(easternAustralia, indonesia, newGuinea, westernAustralia);
        adjacent = new ArrayList<Country>();
        countries.addAll(temp);
        adjacent.addAll(temp);
        australia = new Continent("Australia", adjacent, 2);
        //----------------------------------------------------------------------
        List<Continent> conttemp = Arrays.asList(northAmerica, southAmerica, europe, africa, asia, australia);
        continents.addAll(conttemp);
    }

    /**
     * Queries the user for the necessary information from players to start the game. This includes player count and player names. It then proceeds to initialize the player objects
     *
     */
    public void newGame(){
        int x = 0;
        int startingArmySize;

        Scanner choice = new Scanner(System.in);

        //Queries user for number of players
        while( x < 2 || x > 6) {
            if(x != 0) System.out.println("Invalid selection");
            System.out.println("How many players? (2-6)");
            x = choice.nextInt();
            choice.nextLine();
        }

        //Determines starting army size which depends on amount of players
        switch(x){
            case 2:
                startingArmySize = 50;
                break;
            default:
                startingArmySize = (50) - 5*x;
                break;
        }

        //Queries User for player names and initializes player objects
        System.out.println("Please enter player Names:");
        String name;

        for(int i = 0; i < x; i++){
            System.out.println("Player " +(i+1) +":");
            name = choice.nextLine();
            players.add(new Player(name, startingArmySize));
        }

        //make randomized list of the countries
        ArrayList<Country> ran =new ArrayList<Country>();
        ran = countries;
        Collections.shuffle(ran);
        Stack<Country> addStack = new Stack<Country>();
        addStack.addAll(ran);
        //Splits up the countries amongst players
        while(!addStack.empty()){
            for(Player play: players){
                if(!addStack.empty()){
                    play.addCountry(addStack.pop());
                }
            }

        }
       /* for (Player play: players){
           System.out.println(play.getOwnedCountries());
        }*/


    }

    public static void main(String[] args) {
       /* CommandParser cp = new CommandParser();
        Country country = new Country("Canada");
        ArrayList<Country> countrylist = new ArrayList<>();
        countrylist.add(country);
        Continent con = new Continent("North America", countrylist,69);
        Player pl = new Player("Kevin", 0);
        System.out.println(cp);
        System.out.println(con);
        System.out.println(country);
        System.out.println(pl);\
        */


       RiskModel main = new RiskModel();
       main.createMap();
       main.newGame();



    }
}
