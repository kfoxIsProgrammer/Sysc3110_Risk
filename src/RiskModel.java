/**
 * Risk Model class used to model the ongoing game
 *
 * @author Dimitry Koutchine, Kevin Fox, Omar Hashmi, Kshitij Sawhney
 * @version 11.04.2020
 */

import java.awt.*;
import java.util.*;

public class RiskModel {
    /**List of all the players in the game **/
    ArrayList<Player> players;
    /**   list of the countries in the game **/
    Country[] countries;
    /** List of all the continents in the game **/
    Continent[] continents;
    /** File path for the map being used**/
    String mapImagePath;
    /** The current action context **/
    ActionContext actionContext;

    /** Constructor of Risk Model*/
    private RiskModel(){
        this.players = new ArrayList<>();

        MapImport map=new MapImport("maps/demo.zip","oldmaps/demo.txt");
        this.mapImagePath = "maps/demo.png";

        this.countries= map.getCountries();
        this.continents=map.getContinents();

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
        ArrayList<Country> ran = (ArrayList<Country>) Arrays.asList(countries);
        Collections.shuffle(ran, rand);
        Stack<Country> addStack = new Stack<>();
        addStack.addAll(ran);
        //Splits up the countries amongst players
        while(!addStack.empty()){
            for(Player play: players){
                if(!addStack.empty()){
                    play.addCountry(addStack.peek());
                    addStack.peek().setArmy(1);
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
            }
        }
    }
    /**
     * Checks if a point is inside any country
     *
     * @param point The point to be tested
     * @return The country that the points is inside, null if it isn't in any country
     */
    private Country pointToCountry(Point point){
        for(Country country: this.countries){
            if(country.containsPoint(point)){
                return country;
            }
        }
        return null;
    }
    private Player nextPlayer(Player player){
        //TODO handle invalid players
        for(int i=0;;i=(i+1)%this.players.size()){
            if(this.players.get(i).equals(player)){
                return this.players.get((i+1)%this.players.size());
            }
        }
    }

    public void mapClicked(Point point){
        Country clickedCountry=pointToCountry(point);

        if(clickedCountry==null){
            menuBack();
        }
        //TODO error checking
        switch(this.actionContext.phase){
            case DEPLOY_DST:
                this.actionContext.setPhase(Phase.DEPLOY_ARMY);
                this.actionContext.setDstCountry(clickedCountry);
                break;
            case ATTACK_SRC:
                this.actionContext.setPhase(Phase.ATTACK_DST);
                this.actionContext.setSrcCountry(clickedCountry);
                break;
            case ATTACK_DST:
                this.actionContext.setPhase(Phase.ATTACK_ARMY);
                this.actionContext.setDstCountry(clickedCountry);
                break;
            case FORTIFY_SRC:
                this.actionContext.setPhase(Phase.FORTIFY_DST);
                this.actionContext.setSrcCountry(clickedCountry);
                break;
            case FORTIFY_DST:
                this.actionContext.setPhase(Phase.FORTIFY_ARMY);
                this.actionContext.setDstCountry(clickedCountry);
                break;
        }
    }
    public void menuSkip(){
        switch (this.actionContext.phase){
            case DEPLOY_DST:
            case DEPLOY_ARMY:
                this.actionContext.setPhase(Phase.ATTACK_SRC);
                break;
            case ATTACK_SRC:
            case ATTACK_DST:
            case ATTACK_ARMY:
            case ATTACK_DICE:
                this.actionContext.setPhase(Phase.FORTIFY_SRC);
                break;
            case FORTIFY_SRC:
            case FORTIFY_DST:
            case FORTIFY_ARMY:
                this.actionContext=new ActionContext(Phase.ATTACK_SRC,nextPlayer(this.actionContext.player));
                break;
        }
    }
    public void menuConfirm(){
        //TODO error checking
        switch (this.actionContext.phase) {
            case DEPLOY_ARMY:
                deploy(this.actionContext.player,
                       this.actionContext.dstCountry,
                       this.actionContext.srcArmy);
                this.actionContext=new ActionContext(Phase.DEPLOY_DST,this.actionContext.player);
                break;
            case ATTACK_ARMY:
                attack(this.actionContext.player,
                       this.actionContext.srcCountry,
                       this.actionContext.dstCountry,
                       this.actionContext.srcArmy);
                this.actionContext=new ActionContext(Phase.ATTACK_SRC,this.actionContext.player);
                break;
            case FORTIFY_ARMY:
                fortify(this.actionContext.player,
                        this.actionContext.srcCountry,
                        this.actionContext.dstCountry,
                        this.actionContext.srcArmy);
                this.actionContext=new ActionContext(Phase.ATTACK_SRC,nextPlayer(this.actionContext.player));
                break;
        }
    }
    public void menuBack(){
        switch(this.actionContext.phase){
            case DEPLOY_DST:
            case DEPLOY_ARMY:
                this.actionContext=new ActionContext(Phase.DEPLOY_DST,this.actionContext.player);
                break;
            case ATTACK_SRC:
            case ATTACK_DST:
            case ATTACK_ARMY:
            case ATTACK_DICE:
                this.actionContext=new ActionContext(Phase.ATTACK_SRC,this.actionContext.player);
                break;
            case FORTIFY_SRC:
            case FORTIFY_DST:
            case FORTIFY_ARMY:
                this.actionContext=new ActionContext(Phase.FORTIFY_SRC,this.actionContext.player);
                break;
        }
    }
    public void menuNumTroops(int numTroops){
        switch (this.actionContext.phase) {
            case DEPLOY_ARMY:
            case ATTACK_ARMY:
            case FORTIFY_ARMY:
                this.actionContext.setSrcArmy(numTroops);
                break;
        }
    }

    /**
     * Method that performs the Deploy action. The player is able to deploy troops they have to any owned country.
     * @param  player the Player object that is doing the action
     * @param destinationCountry the country the troops are being sent.
     * @param troopsToDeploy int that represent the amount of army units to move.
     * @return boolean fortify that returns a true if the function was successful.
     */
    private boolean deploy(Player player, Country destinationCountry, int troopsToDeploy){
        if(troopsToDeploy>player.getArmiesToAllocate() || destinationCountry.getOwner()!=player){
            return false;
        }
        else{
            player.removeArmy(troopsToDeploy);
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
    private boolean attack(Player player, Country attackingCountry, Country defendingCountry, int unitsToAttack){
        //TODO action context
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
                    //finalBattleOutcome.addDiceRollBattle(new Integer[]{attackRolls[i], defenderRolls[i]});
                    defendingArmy--;
                }
                else{
                    //finalBattleOutcome.addDiceRollBattle(new Integer[]{attackRolls[i], defenderRolls[i]});
                    attackingArmy--;
                }


                //Set the new owner and initial value
                defendingCountry.setArmy(attackingArmy);
                attackingCountry.removeArmy(attackingArmy);
                defendingCountry.getOwner().removeCountry(defendingCountry);
                defendingCountry.setOwner(attackingCountry.getOwner());
                attackingCountry.getOwner().addCountry(defendingCountry);

            }
        }

        //Attacker wins
        if(defendingArmy == 0){
//            finalBattleOutcome.setAttackingCountry(attackingCountry);
//            finalBattleOutcome.setDefendingCountry(defendingCountry);
//            finalBattleOutcome.setInitialAttackingArmy(unitsToAttack);
//            finalBattleOutcome.setInitialDefendingArmy(defendingCountry.getArmy());
//            finalBattleOutcome.setFinalAttackingArmy(attackingArmy);
//            finalBattleOutcome.setFinalDefendingArmy(defendingArmy);
//            finalBattleOutcome.setDidAttackerWin(true);

            //Send the battle data to parser and get number of units to send to new country
            int numToSend = -1;
            while(numToSend < 0 && numToSend < attackingArmy){
                //numToSend = parser.battleOutcome(finalBattleOutcome);
            }

            //Set the new owner and initial value
            defendingCountry.setArmy(attackingArmy-numToSend);
            attackingCountry.removeArmy(attackingArmy-numToSend);
            defendingCountry.getOwner().removeCountry(defendingCountry);
            defendingCountry.setOwner(attackingCountry.getOwner());
            attackingCountry.getOwner().addCountry(defendingCountry);
        }
        //Attacker loses
        if(attackingArmy == 0){
//            finalBattleOutcome.setAttackingCountry(attackingCountry);
//            finalBattleOutcome.setDefendingCountry(defendingCountry);
//            finalBattleOutcome.setInitialAttackingArmy(unitsToAttack);
//            finalBattleOutcome.setInitialDefendingArmy(defendingCountry.getArmy());
//            finalBattleOutcome.setFinalAttackingArmy(attackingArmy);
//            finalBattleOutcome.setFinalDefendingArmy(defendingArmy);
//            finalBattleOutcome.setDidAttackerWin(false);

            attackingCountry.removeArmy(unitsToAttack);
            defendingCountry.removeArmy(defendingCountry.getArmy()-defendingArmy);
        }

        hasAnyoneLost(attackingCountry.getOwner(), defendingCountry.getOwner());
        return true;
    }
    /**
     * Method that performs the fortification action. The army of one country is moved to another country owned by the player and that is also connected through owned territory.
     * @param sourceCountry the source Country
     * @param  player the Player object that is doing the action
     * @param destinationCountry the country the troops are being sent.
     * @param unitsToSend int that represent the amount of army units to move.
     * @return boolean  that returns a true if the function was successful.
     */
    private boolean fortify(Player player, Country sourceCountry, Country destinationCountry, int unitsToSend){
        Stack countriesWithinBorder = new Stack();
        boolean valid = false;
        countriesWithinBorder = getConnectedOwnedCountries(sourceCountry, player, countriesWithinBorder);
        while (!countriesWithinBorder.isEmpty()){
            if(destinationCountry == countriesWithinBorder.pop()){
                valid = true;
            }
        }
        if(sourceCountry.getOwner()!=player ||
            destinationCountry.getOwner()!=player ||
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

    /**
     * Return the ArrayList of countries.
     * @return ArrayList containing all the country objects
     */
    public Country[] getCountries(){
        return this.countries;
    }
    /**
     * Getter for player countries
     * @param player player object
     * @return ArrayList of the countries the player owns
     */
    public ArrayList<Country> getPlayerCountries(Player player){
        return new ArrayList<>(player.getOwnedCountries().values());
    }
    /**
     * Return the path for the image being used as the map
     * @return String path of the map image
     */
    public String getMapImagePath() {
        return mapImagePath;
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

    public static void main(String[] args) {
       RiskModel rm=new RiskModel();
    }
}
