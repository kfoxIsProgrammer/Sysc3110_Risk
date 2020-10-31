import java.util.*;
import java.util.Random;

/**
 * Risk Model class used to model the ongoing game
 * @author Dimitry Koutchine, Kevin Fox, Omar Hashmi, Kshitij Sawhney
 * @version 10/25/20/
 */
public class RiskModel {
    /**List of all the players in the game **/
    ArrayList<Player> players;
    /**   list of the countries in the game **/
    ArrayList<Country> countries;
    /**   list of all the continents in the game **/
    ArrayList<Continent> continents;
    /** Command Parser **/
    CommandParser parser;


    public RiskModel(int players, String[] playerNames){
        this.players = new ArrayList<Player>();
        this.countries = new ArrayList<Country>();
        this.continents = new ArrayList<Continent>();
        this.createMap();
        this.parser=new CommandParser(this.countries);
        this.newGame(players, playerNames);

    }

    /** Constructor of Risk Model*/
    private RiskModel(){
        this.players = new ArrayList<>();

        //TODO Allow user to select files
        MapImport map=new MapImport("maps\\demoMap.RiskMap");
        this.countries= map.getCountries();
        this.continents=map.getContinents();
        //TODO Swap CommandParser calls to gui calls
        this.parser=new CommandParser(this.countries);

        this.newGame();
        this.play();
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
        Country westernUnitedStates = new Country("Western United States");
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

        temp = Arrays.asList(alaska, northwestTerritory, ontario, westernUnitedStates);
        adjacent = new ArrayList<Country>();
        adjacent.addAll(temp);
        alberta.addAdjacentCountries(adjacent);

        temp = Arrays.asList(westernUnitedStates, venezuela, easternUnitedStates);
        adjacent = new ArrayList<Country>();
        adjacent.addAll(temp);
        centralAmerica.addAdjacentCountries(adjacent);

        temp = Arrays.asList(westernUnitedStates, ontario,quebec, centralAmerica);
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

        temp = Arrays.asList(northwestTerritory, greenLand,quebec, easternUnitedStates, westernUnitedStates, alberta);
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
        westernUnitedStates.addAdjacentCountries(adjacent);

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
        temp = Arrays.asList(alaska,alberta,centralAmerica,easternUnitedStates,greenLand, northwestTerritory, ontario,quebec , westernUnitedStates);
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
        x = playerNum;

        //Determines starting army size which depends on amount of players
        if (x == 2) {
            startingArmySize = 50;
        } else {
            startingArmySize = (50) - 5 * x;
        }





        for(int i = 0; i < x; i++){
            players.add(new Player(playerNames[i], startingArmySize));
        }

        //make randomized list of the countries
        ArrayList<Country> ran = countries;
        Collections.shuffle(ran, rand);
        Stack<Country> addStack = new Stack<>();
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
                ArrayList<Country> temp = new ArrayList<>(play.getOwnedCountries().values());
                Collections.shuffle(temp,rand);
                for(Country count: temp){
                    if (play.getArmiesToAllocate() >0) {
                        count.addArmy(1);
                        play.removeArmy(1);
                    }
                }


            }
        }

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
        return new ArrayList<>(play.getOwnedCountries().values());
    }

    /**
     * Main control function for the Risk game
     */
    private void play(){
        Command command;
        while(gameIsNotOver()[0])
            for(Player currentPlayer: players){
            if(!currentPlayer.getHasLost()){


                //parser.Deploy(currentPlayer);

                /*
                while(true) {
                    command = parser.Deploy(currentPlayer);
                    if (command.commandCode==CommandCode.SKIP) {
                        break;
                    }
                    else if (command.commandCode==CommandCode.DEPLOY) {
                        //TODO deploy method
                    }
                }
                */
                hasAnyoneLost(currentPlayer,currentPlayer);
                while(gameIsNotOver()[0]) {
                    command = parser.Attack(currentPlayer);
                    if (command.commandCode==CommandCode.SKIP) {
                        break;
                    }
                    else if (command.commandCode==CommandCode.ATTACK) {
                        if(command.countrySrc.getAdjacentCountries().contains(command.countryDst)) {
                            if (!this.attack(command.countrySrc, command.countryDst, command.numTroops)) {
                                System.out.println("Error you sent too many units");
                            }
                        }
                        else{
                            System.out.println(command.countryDst.getName()+" is not attack able from "+ command.countrySrc.getName());
                        }

                    }
                }
                /*
                parser.Fortify(currentPlayer);
                if (command.commandCode==CommandCode.SKIP) {
                    continue;
                }
                else if (command.commandCode==CommandCode.FORTIFY) {
                    //TODO fortify method
                }
                */
            }
            //1 or more players are left and game is over
        }

        //The game is over
        if(gameIsNotOver()[1]){
            for(Player play: players) {
                if (!play.getHasLost())
                    parser.gameIsOver(players, play);
            }
        }
        else{
            Player mostCountry = players.get(0);
            for(Player play: players) {
                if(mostCountry.getOwnedCountries().size() < play.getOwnedCountries().size()){
                    mostCountry = play;
                }
            }
            parser.gameIsOver(players, players.get(players.indexOf(mostCountry)));
        }
    }

    /**
     * Helper method to determine if the game is over based on 2 win conditions
     * 1. All players have lost because they cannot make an attack
     * 2. 1 player has won because they own the most countries and no one else can move
     * @return Pair of boolean (false = game over), int (what type of win condition)
     */
    private boolean[] gameIsNotOver(){
        int count = 0;
        for(Player player: players)
            if(player.getHasLost()){
                count++;
            }

        if(count == players.size() || count == players.size() - 1){
            return new boolean[]{false, true};
        }
        else
            return new boolean[]{true, false};

    }

    /**
     * This method is the attack phase controller for the game of risk
     * @param attackingCountry The attacking country
     * @param defendingCountry The defending country
     * @param unitsToAttack number of attackers from the attacking country
     * @return Boolean true = no error, false = units to attack error
     */
    public boolean attack(Country attackingCountry, Country defendingCountry, int unitsToAttack){

        BattleContext finalBattleOutcome = new BattleContext();

            if(attackingCountry.getArmy() - unitsToAttack <= 0)return false;


            int defendingArmy = defendingCountry.getArmy();
            int attackingArmy = unitsToAttack;


            Integer[] attackRolls = new Integer[Math.max(unitsToAttack, defendingCountry.getArmy())];
            Integer[] defenderRolls = new Integer[Math.max(unitsToAttack, defendingCountry.getArmy())];

            //Get int array of dice rolls
            for(int i=0; i< attackRolls.length; i++){
                attackRolls[i] = (int)(Math.ceil(Math.random()*5));
                defenderRolls[i] = (int)(Math.ceil(Math.random()*5));
            }

            //Sort each array in desc order
            Arrays.sort(attackRolls, Collections.reverseOrder());
            Arrays.sort(defenderRolls, Collections.reverseOrder());


            //Compare rolls until someone loses
                for(int i=0; i< attackRolls.length; i++){
                    if(defendingArmy > 0 && attackingArmy > 0)
                        if(attackRolls[i] > defenderRolls[i]){
                            finalBattleOutcome.addDiceRollBattle(new Integer[]{attackRolls[i], defenderRolls[i]});
                            defendingArmy--;
                        }
                        else{
                            finalBattleOutcome.addDiceRollBattle(new Integer[]{attackRolls[i], defenderRolls[i]});
                            attackingArmy--;
                        }
                }


            //Attacker wins
            if(defendingArmy == 0){

                finalBattleOutcome.setAttackingCountry(attackingCountry);
                finalBattleOutcome.setDefendingCountry(defendingCountry);
                finalBattleOutcome.setInitialAttackingArmy(unitsToAttack);
                finalBattleOutcome.setInitialDefendingArmy(defendingCountry.getArmy());
                finalBattleOutcome.setFinalAttackingArmy(attackingArmy);
                finalBattleOutcome.setFinalDefendingArmy(defendingArmy);
                finalBattleOutcome.setDidAttackerWin(true);


                //Send the battle data to parser and get number of units to send to new country
                int numsToSend = -1;
                while(numsToSend < 0 && numsToSend < attackingArmy){
                    numsToSend = parser.battleOutcome(finalBattleOutcome);
                }

                //Set the new owner and initial value
                defendingCountry.setInitialArmy(attackingArmy-numsToSend);
                attackingCountry.removeArmy(attackingArmy-numsToSend);
                defendingCountry.getOwner().removeCountry(defendingCountry);
                defendingCountry.setOwner(attackingCountry.getOwner());
                attackingCountry.getOwner().addCountry(defendingCountry);
            }
            //Attacker loses
            if(attackingArmy == 0){

                finalBattleOutcome.setAttackingCountry(attackingCountry);
                finalBattleOutcome.setDefendingCountry(defendingCountry);
                finalBattleOutcome.setInitialAttackingArmy(unitsToAttack);
                finalBattleOutcome.setInitialDefendingArmy(defendingCountry.getArmy());
                finalBattleOutcome.setFinalAttackingArmy(attackingArmy);
                finalBattleOutcome.setFinalDefendingArmy(defendingArmy);
                finalBattleOutcome.setDidAttackerWin(false);

                parser.battleOutcome(finalBattleOutcome);

                attackingCountry.removeArmy(unitsToAttack);
                defendingCountry.removeArmy(defendingCountry.getArmy()-defendingArmy);
            }

            hasAnyoneLost(attackingCountry.getOwner(), defendingCountry.getOwner());
            return true;
        }

    /**
     * Checks if anyone has met a lost condition when they attacker
     * @param thatAttacked The attacking player
     * @param thatDefended The defending player
     */
    private void hasAnyoneLost(Player thatAttacked, Player thatDefended){
            Player[] playersToCheck = {thatAttacked,thatDefended};

            for(Player player : playersToCheck){
                //If they do not own anymore countries they lose
                if(player.getOwnedCountries().isEmpty()){
                    player.hasLost();
                    parser.playerHasLost(player,"Has no more owned countries");
                    break;
                }
                //If they have no more available attacking units, they lose as well
                //If sum of total units = sum of all countries, you can't make a turn
                //and you lose.
                int sumOfUnits = 0;
                for(Country country: player.getOwnedCountries().values()){
                    sumOfUnits += country.getArmy();
                }
                if(sumOfUnits == player.getOwnedCountries().size()){
                    player.hasLost();
                    parser.playerHasLost(player, "Has no more available moves");
                }
            }
        }


    public static void main(String[] args) {

       RiskModel main = new RiskModel();
       main.createMap();
       main.parser = new CommandParser(main.countries);
       main.newGame();
       main.play();



    }
}
