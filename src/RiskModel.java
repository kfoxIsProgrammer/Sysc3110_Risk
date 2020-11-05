/**
 * Risk Model class used to model the ongoing game
 *
 * @author Dimitry Koutchine, Kevin Fox, Omar Hashmi, Kshitij Sawhney
 * @version 11.04.2020
 */

import java.util.*;
import java.util.Random;

public class RiskModel {
    /**List of all the players in the game **/
    ArrayList<Player> players;
    /**   list of the countries in the game **/
    ArrayList<Country> countries;
    /**   list of all the continents in the game **/
    ArrayList<Continent> continents;
    /** Command Parser **/
    CommandParser parser;

    //Test constructor
    public RiskModel(int players, String[] playerNames){
        this.players = new ArrayList<>();
        this.countries = new ArrayList<>();
        this.continents = new ArrayList<>();
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
     * Method that returns a stack of all the countries that the source country is connected to via friendly territory.
     * @param sourceCountry the source Country
     * @param  user the Player object that is doing the action
     * @param toTest Stack containing all of the connected owned countries
     * @return Stack that contains all the countries connected to source through friendly territory
     */
    public Stack getConnectedOwnedCountries(Country sourceCountry, Player user, Stack toTest){


        for(Country count: sourceCountry.getAdjacentCountries()){
            if(count.getOwner() == user && !toTest.contains(count)){
                //  System.out.println(count.getName());
                toTest.add(count);
                return(getConnectedOwnedCountries(count, user, toTest));

            }

        }
        return toTest;

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

    /**
     * Method that performs the Deploy action. The user is able to deploy troops they have to any owned country.
     * @param  user the Player object that is doing the action
     * @param destinationCountry the country the troops are being sent.
     * @param troopsToDeploy int that represent the amount of army units to move.
     * @return boolean fortify that returns a true if the function was successful.
     */
    public boolean deploy(int troopsToDeploy, Player user, Country destinationCountry){
        if(troopsToDeploy>user.getArmiesToAllocate() || destinationCountry.getOwner()!=user){
            return false;
        }
        else{
            user.removeArmy(troopsToDeploy);
            destinationCountry.addArmy(troopsToDeploy);
            return true;
        }
    }
    /**
     * This method is the attack phase controller for the game of risk
     * @param attackingCountry The attacking country
     * @param defendingCountry The defending country
     * @param unitsToAttack number of attackers from the attacking country
     * @return Boolean true = no error, false = units to attack error
     */
    public boolean attack(Country attackingCountry, Country defendingCountry, int unitsToAttack){
        BattleContext finalBattleOutcome=new BattleContext();
        if(attackingCountry.getArmy()-unitsToAttack<=0) {
            return false;
        }

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
            if(defendingArmy>0 && attackingArmy>0) {
                if(attackRolls[i] > defenderRolls[i]){
                    finalBattleOutcome.addDiceRollBattle(new Integer[]{attackRolls[i], defenderRolls[i]});
                    defendingArmy--;
                }
                else{
                    finalBattleOutcome.addDiceRollBattle(new Integer[]{attackRolls[i], defenderRolls[i]});
                    attackingArmy--;
                }
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
     * Method that performs the fortification action. The army of one country is moved to another country owned by the player and that is also connected through owned territory.
     * @param sourceCountry the source Country
     * @param  user the Player object that is doing the action
     * @param destinationCountry the country the troops are being sent.
     * @param unitsToSend int that represent the amount of army units to move.
     * @return boolean  that returns a true if the function was successful.
     */
    public boolean fortify(Country sourceCountry, Country destinationCountry, int unitsToSend, Player user){
        Stack countriesWithinBorder = new Stack();
        boolean valid = false;
        countriesWithinBorder = getConnectedOwnedCountries(sourceCountry, user, countriesWithinBorder);
        while (!countriesWithinBorder.isEmpty()){
            if(destinationCountry == countriesWithinBorder.pop()){
                valid = true;
            }
        }
        if(sourceCountry.getOwner()!=user ||
            destinationCountry.getOwner()!=user ||
            !valid ||
            (sourceCountry.getArmy()-1)<unitsToSend ){
            return false;
        }
        else{
            sourceCountry.removeArmy(unitsToSend);
            destinationCountry.addArmy(unitsToSend);
        }
        return true;
    }

    public static void main(String[] args) {
       new RiskModel();
    }
}
