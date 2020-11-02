
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

        this.play();
    }

    /**
     * Queries the user for the necessary information from players to start the game. This includes player count and player names. It then proceeds to initialize the player objects
     *
     */
    private void newGame(int playerNum, String[] playerNames){
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
                int numToSend = -1;
                while(numToSend < 0 && numToSend < attackingArmy){
                    numToSend = parser.battleOutcome(finalBattleOutcome);
                }

                //Set the new owner and initial value
                defendingCountry.setInitialArmy(attackingArmy-numToSend);
                attackingCountry.removeArmy(attackingArmy-numToSend);
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
    }
}

