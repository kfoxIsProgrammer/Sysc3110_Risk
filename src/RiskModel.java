import java.lang.reflect.Array;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Random;

/**
 * Risk Model class used to model the ongoing game
 * @author Dimitry Koutchine
 * @version 10/23/20
 */
public class RiskModel {
    /**List of all the players in the game **/
    ArrayList<Player> players;
    /**   list of the countries in the game **/
    ArrayList<Country> countries;
    /**   list of all the continents in the game **/
    ArrayList<Continent> continents;
/** Contructor of Risk Model*/
    private RiskModel(){
        players = new ArrayList<Player>();
        countries = new ArrayList<Country>();
        continents = new ArrayList<Continent>();

    }
    /**
     * Initialize the countries and there adjacency's, continents, and setup the game board
     *
     */
    private void createMap(){
        //test
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
    private void newGame(){
        int x = 0;
        int startingArmySize;
        Random rand = new Random(System.currentTimeMillis());

        Scanner choice = new Scanner(System.in);

        //Queries user for number of players
        while( x < 2 || x > 6) {
            if(x != 0) System.out.println("Invalid selection");
            System.out.println("How many players? (2-6)");

            while(!choice.hasNextInt()) {

                System.out.println("Invalid selection");

                choice.next();


            }
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
        String name = "";


        for(int i = 0; i < x; i++){
            while(name.isEmpty()) {
                System.out.println("Player " + (i+1) + ":");
                name = choice.nextLine();
            }
            players.add(new Player(name, startingArmySize));
            name = "";
        }

        //make randomized list of the countries
        ArrayList<Country> ran =new ArrayList<Country>();
        ran = countries;
        Collections.shuffle(ran, rand);
        Stack<Country> addStack = new Stack<Country>();
        addStack.addAll(ran);
        //Splits up the countries amongst players
        while(!addStack.empty()){
            for(Player play: players){
                if(!addStack.empty()){
                    play.addCountry(addStack.peek());
                    addStack.peek().setInitialArmy(1);
                    play.removeArmy(1);
                    addStack.pop().setOwner(play);
                }
            }

        }
        for(Player play: players){
            while (play.getArmiesToAllocate() > 0){
                ArrayList<Country> temp = new ArrayList<Country>(play.getOwnedCountries().values());
                Collections.shuffle(temp,rand);
                for(Country count: temp){
                    if (play.getArmiesToAllocate() >0) {
                        count.addArmy(1);
                        play.removeArmy(1);
                    }
                }


            }
        }
        //Testing if armies allocated correctly
        /*for (Player play: players){
            Iterator it = play.getOwnedCountries().entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                Country count =((Country)pair.getValue());
                System.out.println(play.getName()+" "+count.getName());


            }
            System.out.println(play.getName()+" =" + play.getArmiesToAllocate());

            }*/


    }
    private void deploy(Player currentPlayer){
    }

    /**
     * Return the ArrayList of countries.
     * @return ArrayList containing all the country objects
     */
    public ArrayList<Country> getCountries(){
        return this.countries;
    }

    /**
     * Getter for player countries
     * @param play player object
     * @return ArrayList of the countries the player owns
     */
    public ArrayList<Country> getPlayerCountries(Player play){
        ArrayList<Country> temp = new ArrayList<Country>(play.getOwnedCountries().values());
        return temp;
    }

    private void play(){

        for(Player currentPlayer: players){

            System.out.println(currentPlayer.getName()+"'s turn, deploy phase, please enter command:");

            /*for(Country country: currentPlayer.getOwnedCountries().values()){
                System.out.println("Country: "+country.getName()+"  Army: "+country.getArmy()+" Can attack");
                for(Country attackable: country.getAdjancentCountries()){
                    if(attackable.getOwner() != currentPlayer){
                        System.out.println("Country: "+attackable.getName()+"Army: "+attackable.getArmy());
                    }
                }
                System.out.println();
            }

            Scanner scan = new Scanner(System.in);
            System.out.println("Pick your country to attack from");
            String attackCountry = scan.nextLine();
            System.out.println("Pick a country to attack");
            String defendCountry = scan.nextLine();
            System.out.println("How many units to attack");
            int num = scan.nextInt();



            Attack(currentPlayer.getOwnedCountries().get(attackCountry),
                    searchForCountryByString(defendCountry,currentPlayer.getOwnedCountries().get(attackCountry)),
                    num);

            System.out.println("Finished");
            break;*/

        }
    }

    private Country searchForCountryByString(String findThis, Country original){
        for(Country c: original.getAdjancentCountries()){
            if(c.getName().equals(findThis)){
                return c;
            }
        }
        return null;
    }

    /**
     * This method is the attack phase controller for the game of risk
     * @param attacker The attacking country
     * @param defender The defending country
     * @param unitsToAttack number of attackers from the attacking country
     */
    public void Attack(Country attacker, Country defender, int unitsToAttack){


            int defenders = defender.getArmy();
            int attackers = unitsToAttack;


            Integer[] attackRolls = new Integer[Math.max(unitsToAttack, defender.getArmy())];
            Integer[] defenderRolls = new Integer[Math.max(unitsToAttack, defender.getArmy())];

            //Get int array of dice rolls
            for(int i=0; i< attackRolls.length; i++){
                attackRolls[i] = ThreadLocalRandom.current().nextInt(1, 6 + 1);
                defenderRolls[i] = ThreadLocalRandom.current().nextInt(1, 6 + 1);
            }

            //Sort each array in desc order
            Arrays.sort(attackRolls, Collections.reverseOrder());
            Arrays.sort(defenderRolls, Collections.reverseOrder());


            //Compare rolls until someone loses
            while(defenders > 0 && attackers > 0){
                for(int i=0; i< attackRolls.length; i++){
                    if(attackRolls[i] > defenderRolls[i]){
                        defenders--;
                    }
                    else{
                        attackers--;
                    }
                }
            }

            //Add the description of the battle
            BattleObject finalBattleOutcome;

            //Attacker wins
            if(defenders == 0){


                finalBattleOutcome = new BattleObject(attacker,
                        defender,
                        attacker.getArmy(),
                        defender.getArmy(),
                        attackers,
                        defenders,
                        true);

                //Send the battle data to parser
                parser.sendBattleOutcome(finalBattleOutcome);

                //Get number of units to send to new country
                int numsToSend = -1;
                while(numsToSend <= 0 && numsToSend > attackers){
                    numsToSend = getNumberOfUnitsToSendAfterAttackerWin();
                }

                //Set the new owner and intial value
                defender.setInitialArmy(attackers - numsToSend);
                attacker.removeArmy(unitsToAttack-attackers+numsToSend);
                defender.setOwner(attacker.getOwner());
                defender.getOwner().removeCountry(defender);
                attacker.getOwner().addCountry(defender);
            }
            //Attacker loses
            if(attackers == 0){

                finalBattleOutcome = new BattleObject(attacker,
                        defender,
                        attacker.getArmy(),
                        defender.getArmy(),
                        attackers,
                        defenders,
                        false);

                parser.sendBattleObject(finalBattleOutcome);

                attacker.removeArmy(unitsToAttack);
                defender.removeArmy(defender.getArmy()-defenders);
            }


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
       main.play();



    }
}
