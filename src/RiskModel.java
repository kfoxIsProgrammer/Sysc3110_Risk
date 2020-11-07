import java.awt.*;
import java.util.*;

/**
 * Risk Model class used to model the ongoing game
 *
 * @author Dimitry Koutchine, Kevin Fox, Omar Hashmi, Kshitij Sawhney
 * @version 11.04.2020
 */
public class RiskModel {
    /**List of all the players in the game **/
    Player[] players;
    /**   list of the countries in the game **/
    Country[] countries;
    /** List of all the continents in the game **/
    Continent[] continents;
    Map map;
    /** The current action context **/
    ActionContext actionContext;
    /** The current risk view**/
    RiskView riskView;
    /** The current RiskController**/
    RiskController riskController;

    /** Constructor of Risk Model*/
    private RiskModel(){
        MapImport mapReader=new MapImport("maps/demo.zip");
        this.map=mapReader.getMap();
        map.printCountries();
        map.printContinents();
        this.countries=map.countries;
        this.continents=map.continents;
        newGame(2, new String[]{"jeff", "assman"});

        this.riskController=new RiskController(this);
        this.riskView=new RiskView(this.riskController,map.getMapImage(),this.countries);
        updateView();

        this.play();
    }

    /**
     * Queries the user for the necessary information from players to start the game. This includes player count and player names. It then proceeds to initialize the player objects
     *
     */
    private void newGame(int playerNum, String[] playerNames){
        int startingArmySize;
        Random rand = new Random(System.currentTimeMillis());

        Color[] colorsToAllocate = {new Color(255, 255, 0), new Color(0,0,255),
        new Color(255,0,0), new Color(0,255,0),
        new Color(255,0,255), new Color(0,255,255)};

        //Determines starting army size which depends on amount of players
        if (playerNum == 2) {
            startingArmySize = 50;
        } else {
            startingArmySize = (50) - 5 * playerNum;
        }

        players=new Player[playerNum];
        for(int i = 0; i < playerNum; i++){
            players[i]=new Player(playerNames[i], startingArmySize, colorsToAllocate[i]);
        }

        //make randomized list of the countries
        ArrayList<Country> ran = new ArrayList<>();
        Collections.addAll(ran,this.countries);
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
        Country[] countriesArray=new Country[ran.size()];
        countriesArray = ran.toArray(countriesArray);
        this.countries=countriesArray;
        this.actionContext=new ActionContext(Phase.ATTACK_SRC, this.players[0]);

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

        if(count == players.length || count == players.length - 1){
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
        for(int i=0;;i=(i+1)%this.players.length){
            if(this.players[i].equals(player)){
                return this.players[(i+1)%this.players.length];
            }
        }
    }

    public void mapClicked(Point point){
        Country clickedCountry=pointToCountry(point);
        System.out.printf("(%d,%d):\t",point.x,point.y);
        if(clickedCountry==null){
            System.out.printf("No Country\n");
            menuBack();
            return;
        }
        //TODO error checking
        System.out.printf("%s\n",clickedCountry.getName());
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
        updateView();
    }
    public void menuSkip(){
        switch (this.actionContext.phase){
            case DEPLOY_DST:
            case DEPLOY_ARMY:
            case DEPLOY_CONFIRM:
                this.actionContext=new ActionContext(Phase.ATTACK_SRC,this.actionContext.player);
                break;
            case ATTACK_SRC:
            case ATTACK_DST:
            case ATTACK_ARMY:
            case ATTACK_CONFIRM:
                this.actionContext=new ActionContext(Phase.FORTIFY_SRC,this.actionContext.player);
                break;
            case RETREAT_ARMY:
                retreat(this.actionContext.player,
                        this.actionContext.srcCountry,
                        this.actionContext.dstCountry,
                        0);
                this.actionContext=new ActionContext(Phase.FORTIFY_SRC,this.actionContext.player);
            case FORTIFY_SRC:
            case FORTIFY_DST:
            case FORTIFY_ARMY:
            case FORTIFY_CONFIRM:
                this.actionContext=new ActionContext(Phase.ATTACK_SRC,nextPlayer(this.actionContext.player));
                break;
        }
    }
    public void menuConfirm(){
        //TODO error checking
        switch (this.actionContext.phase) {
            case DEPLOY_CONFIRM:
                deploy(this.actionContext.player,
                       this.actionContext.dstCountry,
                       this.actionContext.srcArmy);
                this.actionContext=new ActionContext(Phase.DEPLOY_DST,this.actionContext.player);
                break;
            case ATTACK_CONFIRM:
                attack(this.actionContext.player,
                       this.actionContext.srcCountry,
                       this.actionContext.dstCountry,
                       this.actionContext.srcArmy);
                this.actionContext.phase=Phase.RETREAT_ARMY;
                this.actionContext.srcArmy=0;
                break;
            case RETREAT_CONFIRM:
                retreat(this.actionContext.player,
                        this.actionContext.srcCountry,
                        this.actionContext.dstCountry,
                        this.actionContext.srcArmy);
                this.actionContext=new ActionContext(Phase.ATTACK_SRC,this.actionContext.player);
                break;
            case FORTIFY_CONFIRM:
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
            case DEPLOY_CONFIRM:
                this.actionContext=new ActionContext(Phase.DEPLOY_DST,this.actionContext.player);
                break;
            case ATTACK_SRC:
            case ATTACK_DST:
            case ATTACK_ARMY:
            case ATTACK_CONFIRM:
                this.actionContext=new ActionContext(Phase.ATTACK_SRC,this.actionContext.player);
                break;
            case RETREAT_ARMY:
                retreat(this.actionContext.player,
                        this.actionContext.srcCountry,
                        this.actionContext.dstCountry,
                        0);
                this.actionContext=new ActionContext(Phase.ATTACK_SRC,this.actionContext.player);
            case FORTIFY_SRC:
            case FORTIFY_DST:
            case FORTIFY_ARMY:
            case FORTIFY_CONFIRM:
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
                    //TODO bro im so tired
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
            this.actionContext.setSrcArmyDead(unitsToAttack-attackingArmy);
            this.actionContext.setDstArmyDead(defendingCountry.getArmy()-defendingArmy);
            this.actionContext.setAttackerVictory(true);

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
            this.actionContext.setSrcArmyDead(unitsToAttack-attackingArmy);
            this.actionContext.setDstArmyDead(defendingCountry.getArmy()-defendingArmy);
            this.actionContext.setAttackerVictory(false);

            attackingCountry.removeArmy(unitsToAttack);
            defendingCountry.removeArmy(defendingCountry.getArmy()-defendingArmy);
        }

        hasAnyoneLost(attackingCountry.getOwner(), defendingCountry.getOwner());
        return true;
    }
    private boolean retreat(Player player, Country attackingCountry, Country defendingCountry, int unitsToRetreat){
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

    private void updateView(){
        this.riskView.boardUpdate(this.actionContext);

    }

    public static void main(String[] args) {
       new RiskModel();
    }

    public void countryHasBeenSelected(int x, int y) {
    }

    public void startNewGame(int players, String[] playerNames) {
    }

    public void sendAction(String actionCommand) {
    }
}
