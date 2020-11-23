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
    /** The map of the game **/
    Map map;
    /** The current action context **/
    ActionContext actionContext;
    /** The current risk view**/
    RiskView riskView;
    /** The current RiskController**/
    RiskController riskController;
    int numPlayers;
    String textBuffer;
    int numBuffer;

    /**
     * 1 param constructor for testing purposes
     * @param names the names of players
     */
    public RiskModel(String[] names){
        this.map=new MapImport("maps/demo.zip").getMap();
        this.actionContext=new ActionContext(Phase.NEW_GAME,null);

        this.riskController=new RiskController(this);
        this.riskView=new RiskView(this.riskController,map);
        newGame(names);
        this.riskView.update(this.actionContext);
    }

    /** Constructor of Risk Model*/
    private RiskModel(){
        this.map=new MapImport("maps/demo.zip").getMap();
        this.actionContext=new ActionContext(Phase.NUM_PLAYERS,null);

        this.riskController=new RiskController(this);
        this.riskView=new RiskView(this.riskController,map);
        this.riskView.update(this.actionContext);
    }
    public void newGameTemp(){

    }
    private boolean newPlayer(String name){
        Color[] playerColors={
                new Color(255, 102, 0),
                new Color(81, 119, 241),
                new Color(255, 0  , 0),
                new Color(0  , 255, 0),
                new Color(255, 0, 255),
                new Color(0, 255, 255)
        };

        int startingArmySize=
                numPlayers==2?
                        50:
                        50-5*numPlayers;

        int i=actionContext.getPlayerId();
        players[i]=new PlayerHuman(name,playerColors[i],startingArmySize,i);

        //All players added
        if(i==numPlayers-1){
            allocateCountries();
            return true;
        }
        //More players need to be added
        else{
            return false;
        }
    }
    private void allocateCountries(){
        Random rand = new Random(System.currentTimeMillis());

        //make randomized list of the countries
        ArrayList<Country> ran = new ArrayList<>();
        Collections.addAll(ran,this.map.getCountries());
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
        this.actionContext=new ActionContext(Phase.DEPLOY_DST, this.players[0]);
    }
    private void allocateArmies(){
        Random rand = new Random(System.currentTimeMillis());

        for(Player player: players){
            while (player.getArmiesToAllocate() > 0){
                ArrayList<Country> temp = player.getCountries();
                Collections.shuffle(temp,rand);
                for(Country count: temp){
                    if (player.getArmiesToAllocate() >0) {
                        count.addArmy(1);
                        player.removeArmy(1);
                    }
                }
            }
        }
    }

    /** Very important method... can't remove **/
    public void newGame(String[] playerNames){
        newGamePlayerCreator(playerNames);
        riskView.update(this.actionContext);
    }
    /**
     * Method to process the names sent from the view
     *
     * @param playerNames The names of the players
     */
    public void newGameNameProcessor(String[] playerNames){
        newGamePlayerCreator(playerNames);
        riskView.update(this.actionContext);
    }

    /**
     * Queries the user for the necessary information from players to start the game. This includes player count and player names. It then proceeds to initialize the player objects
     *
     */
    private void newGamePlayerCreator(String[] playerNames){
        int startingArmySize;
        Random rand = new Random(System.currentTimeMillis());

        Color[] colorsToAllocate={
                new Color(200, 150, 0),
                new Color(125, 125, 125),
                new Color(255, 0  , 0),
                new Color(0  , 255, 0),
                new Color(255, 0  , 255),
                new Color(0  , 255, 255)
        };

        //Determines starting army size which depends on amount of players
        if (numPlayers == 2) {
            startingArmySize = 50;
        } else {
            startingArmySize = (50) - 5 * numPlayers;
        }

        players=new Player[playerNames.length];
        for(int i = 0; i < playerNames.length; i++){
            players[i]=new PlayerHuman(playerNames[i], colorsToAllocate[i], startingArmySize,i);
        }

        //make randomized list of the countries
        ArrayList<Country> ran = new ArrayList<>();
        Collections.addAll(ran,this.map.getCountries());
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
        for(Player player: players){
            while (player.getArmiesToAllocate() > 0){
                ArrayList<Country> temp = player.getCountries();
                Collections.shuffle(temp,rand);
                for(Country count: temp){
                    if (player.getArmiesToAllocate() >0) {
                        count.addArmy(1);
                        player.removeArmy(1);
                    }
                }
            }
        }
        this.actionContext=new ActionContext(Phase.ATTACK_SRC, this.players[0]);
    }

    /**
     * Helper method to determine if the game is over based on 2 win conditions
     * 1. All players have lost because they cannot make an attack
     * 2. 1 player has won because they own the most countries and no one else can move
     * @return Pair of boolean (false = game over), int (what type of win condition)
     */
    private boolean gameIsOver(){
        int numPlayers=this.players.length;
        Player winner=null;

        for(Player player: players) {
            if(player.getHasLost()){
                numPlayers--;
            }
            else {
                winner=player;
            }
        }
        if(numPlayers==1){
            this.actionContext = new ActionContext(Phase.GAME_OVER, winner);
            return true;
        }
        return false;
    }
    /**
     * Checks if anyone has met a lost condition when they attacker
     * @param thatAttacked The attacking player
     * @param thatDefended The defending player
     */
    private boolean hasAnyoneLost(Player thatAttacked, Player thatDefended){
        Player[] playersToCheck = {thatAttacked,thatDefended};

        for(Player player : playersToCheck){
            //If they do not own anymore countries they lose
            if(player.getCountries().isEmpty()){
                player.setHasLost();
                return true;
            }
            //If they have no more available attacking units, they lose as well
            //If sum of total units = sum of all countries, you can't make a turn
            //and you lose.
            int sumOfUnits = 0;
            for(Country country: player.getCountries()){
                sumOfUnits += country.getArmy();
            }
            if(sumOfUnits == player.getCountries().size()){
                player.setHasLost();
                return true;
            }
        }
        return false;
    }
    /**
     * Checks if a point is inside any country
     *
     * @param point The point to be tested
     * @return The country that the points is inside, null if it isn't in any country
     */
    private Country pointToCountry(Point point){
        for(Country country: this.map.getCountries()){
            if(country.containsPoint(point)){
                return country;
            }
        }
        return null;
    }
    /**
     * Method to select the next player that still is in the game
     * @param player the currentPlayer to determine position
     * @return the next Player still in game
     */
    private Player nextPlayer(Player player){
        //TODO handle invalid players

        int indexOfCurrentPlayer = Arrays.asList(this.players).indexOf(player);
        int modValue = this.players.length;

        for(int nextIndex = (indexOfCurrentPlayer+1)%modValue;; nextIndex = (nextIndex+1)%modValue){
            if(!this.players[nextIndex].getHasLost()){
                return this.players[nextIndex];
            }
            //Error no more players call game is over
            if(this.players[nextIndex].equals(player)){
                gameIsOver();
                riskView.update(this.actionContext);
                return null;
            }
        }

/*
        for(int i=0;;i=(i+1)%this.players.length){
            if(this.players[i].equals(player)){
                if(!this.players[(i+1)%this.players.length].getHasLost()) {
                    return this.players[(i + 1) % this.players.length];
                }
            }
        }
        */

    }

    /**
     * Method to handle when the user clicks on the map of the view
     * This will also return the country to the view
     * @param point the x,y position of where the click happened
     */
    public void mapClicked(Point point){
        Country clickedCountry=pointToCountry(point);
        System.out.printf("(%d,%d):\t",point.x,point.y);
        if(clickedCountry==null){
            System.out.printf("No Country\n");
            menuBack();
            return;
        }
        System.out.printf("%s\n",clickedCountry.getName());
        switch(this.actionContext.getPhase()){
            case DEPLOY_DST:
                if(clickedCountry.getOwner().equals(this.actionContext.getPlayer())) {
                    this.actionContext.setPhase(Phase.DEPLOY_ARMY);
                    this.actionContext.setDstCountry(clickedCountry);
                }
                break;
            case ATTACK_SRC:
                if(clickedCountry.getOwner().equals(this.actionContext.getPlayer())) {
                    this.actionContext.setPhase(Phase.ATTACK_DST);
                    this.actionContext.setSrcCountry(clickedCountry);
                    ArrayList<Country> enemyCountries = new ArrayList<>();
                    if (clickedCountry.getArmy() > 1) {

                        for (Country c : clickedCountry.getAdjacentCountries()) {
                            System.out.println(c.getName());
                            if (c.getOwner() != this.actionContext.getPlayer()) {
                                enemyCountries.add(c);
                            }
                        }
                        Country[] temp = new Country[enemyCountries.size()];
                        this.actionContext.setHighlightedCountries(enemyCountries.toArray(temp));
                    }
                }
                break;
            case ATTACK_DST:
                if(Arrays.asList(clickedCountry.getAdjacentCountries())
                        .contains(this.actionContext.getSrcCountry()) &
                        !clickedCountry.getOwner().equals(this.actionContext.getPlayer())) {
                    this.actionContext.setPhase(Phase.ATTACK_ARMY);
                    this.actionContext.setDstCountry(clickedCountry);
                }
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
        riskView.update(this.actionContext);
    }
    public void textEntered(String text){
        textBuffer=text;
        menuConfirm();
    }
    public void sliderMoved(int num){
        numBuffer=num;
    }
    /**
     * Method used to deal with when the user clicks the skip button
     */
    public void menuSkip(){
        switch (this.actionContext.getPhase()){
            case DEPLOY_DST:
            case DEPLOY_ARMY:
            case DEPLOY_CONFIRM:
                this.actionContext=new ActionContext(Phase.ATTACK_SRC,this.actionContext.getPlayer());
                break;
            case ATTACK_SRC:
            case ATTACK_DST:
            case ATTACK_ARMY:
            case ATTACK_CONFIRM:
                this.actionContext=new ActionContext(Phase.FORTIFY_SRC,this.actionContext.getPlayer());
                break;
            case RETREAT_ARMY:
                this.actionContext.setDstArmy(0);
                menuConfirm();
                this.actionContext=new ActionContext(Phase.ATTACK_SRC,this.actionContext.getPlayer());
                break;
            case FORTIFY_SRC:
            case FORTIFY_DST:
            case FORTIFY_ARMY:
            case FORTIFY_CONFIRM:
                this.actionContext=new ActionContext(Phase.DEPLOY_DST,nextPlayer(this.actionContext.getPlayer()));
                break;
        }
        riskView.update(this.actionContext);
    }
    /**
     * Method to deal with when the user clicks a confirm button
     */
    public void menuConfirm(){
        switch (this.actionContext.getPhase()) {
            case NUM_PLAYERS:
                this.numPlayers=numBuffer;
                this.actionContext=new ActionContext(Phase.PLAYER_NAME,null);
                players=new Player[numPlayers];
                System.out.printf("Num Players: %d\n",numBuffer);
                break;
            case PLAYER_NAME:
                if(newPlayer(textBuffer)){
                    this.actionContext=new ActionContext(Phase.DEPLOY_DST,players[0]);
                }else{
                    this.actionContext.setPlayerId(actionContext.getPlayerId()+1);
                }
                break;
            case DEPLOY_CONFIRM:
                if(deploy(this.actionContext.getPlayer(),
                        this.actionContext.getDstCountry(),
                        this.actionContext.getSrcArmy()))
                    this.actionContext=new ActionContext(Phase.DEPLOY_DST,this.actionContext.getPlayer());
                else
                    System.out.println("Deploy Failed");
                break;
            case ATTACK_CONFIRM:
                if(attack(this.actionContext.getPlayer(),
                        this.actionContext.getSrcCountry(),
                        this.actionContext.getDstCountry(),
                        this.actionContext.getSrcArmy()))
                    this.actionContext.setPhase(Phase.RETREAT_ARMY);
                else
                    System.out.println("Attack failed");
                break;
            case RETREAT_CONFIRM:
                if(retreat(this.actionContext.getPlayer(),
                        this.actionContext.getSrcCountry(),
                        this.actionContext.getDstCountry(),
                        this.actionContext.getDstArmy()))
                    this.actionContext=new ActionContext(Phase.ATTACK_SRC,this.actionContext.getPlayer());
                else
                    System.out.println("Retreat failed");
                break;
            case FORTIFY_CONFIRM:
                if(fortify(this.actionContext.getPlayer(),
                        this.actionContext.getSrcCountry(),
                        this.actionContext.getDstCountry(),
                        this.actionContext.getSrcArmy()))
                    this.actionContext=new ActionContext(Phase.ATTACK_SRC,nextPlayer(this.actionContext.getPlayer()));
                else{
                    System.out.println("Fortify failed");
                }
                break;
        }
        riskView.update(this.actionContext);
    }
    /**
     * Methods to deal with when the user clicks a back button
     */
    public void menuBack(){
        switch(this.actionContext.getPhase()){
            case DEPLOY_DST:
            case DEPLOY_ARMY:
            case DEPLOY_CONFIRM:
                this.actionContext=new ActionContext(Phase.DEPLOY_DST,this.actionContext.getPlayer());
                break;
            case ATTACK_SRC:
            case ATTACK_DST:
            case ATTACK_ARMY:
            case ATTACK_CONFIRM:
                this.actionContext=new ActionContext(Phase.ATTACK_SRC,this.actionContext.getPlayer());
                break;
            case RETREAT_ARMY:
                retreat(this.actionContext.getPlayer(),
                        this.actionContext.getSrcCountry(),
                        this.actionContext.getDstCountry(),
                        0);
                this.actionContext=new ActionContext(Phase.ATTACK_SRC,this.actionContext.getPlayer());
                break;
            case FORTIFY_SRC:
            case FORTIFY_DST:
            case FORTIFY_ARMY:
            case FORTIFY_CONFIRM:
                this.actionContext=new ActionContext(Phase.FORTIFY_SRC,this.actionContext.getPlayer());
                break;
        }
        riskView.update(this.actionContext);
    }
    /**
     * Method to deal with numbers sent from the view
     * @param numTroops the number of troops to handle
     */
    public void menuNumTroops(int numTroops){
        switch (this.actionContext.getPhase()) {
            case NEW_GAME:
                this.actionContext.setSrcArmy(numTroops);
                menuConfirm();
                break;
            case DEPLOY_ARMY:
            case RETREAT_ARMY:
                this.actionContext.setDstArmy(numTroops);
                actionContext.setPhase(Phase.RETREAT_CONFIRM);
                menuConfirm();
                break;
            case ATTACK_ARMY:
                this.actionContext.setSrcArmy(numTroops);
                //ADDED FOR TESTING
                actionContext.setPhase(Phase.ATTACK_CONFIRM);
                menuConfirm();
                break;
            case FORTIFY_ARMY:

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


        ArrayList<Integer> rollsAttackerMade = new ArrayList<>();
        ArrayList<Integer> rollsDefenderMade = new ArrayList<>();

        while(attackingArmy > 0 && defendingArmy > 0) {
            Integer[] defenderRolls = new Integer[Math.min(defendingArmy, 2)];
            Integer[] attackRolls = new Integer[Math.min(attackingArmy, 3)];

            Random random = new Random();

            //Min of(attackers left alive, 3)
            //vs
            //Min of(defenders alive, 2)

            //Allocate the dice rolls for attackers, maximum of 3 attacker rolls per 2 defenders
            for (int i = 0; i < attackingArmy && i < 3; i++) {
                attackRolls[i] = (random.nextInt(5) + 1);
            }
            //Allocate the dice rolls of the defenders, maximum of 2 defender rolls per 2 defenders
            for (int i = 0; i < defendingArmy && i < 2; i++) {
                defenderRolls[i] = (random.nextInt(5) + 1);
            }

            //Sort each array in desc order
            Arrays.sort(attackRolls, Collections.reverseOrder());
            Arrays.sort(defenderRolls, Collections.reverseOrder());

            //Create the queue to check dice rolls from
            Queue<Integer> attackersQueue = new LinkedList<>(Arrays.asList(attackRolls));
            Queue<Integer> defenderQueue = new LinkedList<>(Arrays.asList(defenderRolls));

            //The Dice rolls comparisons
            while (!defenderQueue.isEmpty() && !attackersQueue.isEmpty() && attackingArmy> 0 && defendingArmy>0) {
                int attack = attackersQueue.remove();
                int defence = defenderQueue.remove();
                if (attack > defence) {
                    defendingArmy--;
                } else {
                    attackingArmy--;
                }
                //Add the dice rolls to the list to send to the view
                rollsAttackerMade.add(attack);
                rollsDefenderMade.add(defence);
            }
        }

        //Send dice rolls
        actionContext.setDiceRolls(new Integer[][]{rollsAttackerMade.toArray(new Integer[rollsAttackerMade.size()]), rollsDefenderMade.toArray(new Integer[rollsDefenderMade.size()])});

        this.actionContext.setSrcArmyDead(unitsToAttack - attackingArmy);
        this.actionContext.setDstArmy(defendingCountry.getArmy());
        this.actionContext.setDstArmyDead(defendingCountry.getArmy()-defendingArmy);


        //Attacker wins
        if(defendingArmy <= 0){
            defendingCountry.removeArmy(defendingCountry.getArmy());
            attackingCountry.removeArmy(unitsToAttack-attackingArmy);

            defendingCountry.getOwner().removeCountry(defendingCountry);
            attackingCountry.getOwner().addCountry(defendingCountry);
            defendingCountry.setOwner(attackingCountry.getOwner());
            this.actionContext.setAttackerVictory(true);
        }
        //Attacker loses
        if(attackingArmy <= 0){
            attackingCountry.removeArmy(unitsToAttack);
            defendingCountry.removeArmy(defendingCountry.getArmy() - defendingArmy);
            this.actionContext.setAttackerVictory(false);
        }


        if(hasAnyoneLost(attackingCountry.getOwner(), defendingCountry.getOwner())){
            if(gameIsOver()){
                //Force game over here
                riskView.update(this.actionContext);
            }
        }

        return true;
    }
    /**
     * Method to process a successful attack when sending units back to the attacking country
     * @param player the current player
     * @param attackingCountry the Country of the attacking player
     * @param defendingCountry the defending country that has lost
     * @param unitsToRetreat the number of units to send to the attacking country
     * @return boolean True: success, False: fail
     */
    private boolean retreat(Player player, Country attackingCountry, Country defendingCountry, int unitsToRetreat){
        defendingCountry.setArmy((this.actionContext.getSrcArmy()-this.actionContext.getSrcArmyDead())-unitsToRetreat);
        attackingCountry.removeArmy((this.actionContext.getSrcArmy()-this.actionContext.getSrcArmyDead())-unitsToRetreat);
        defendingCountry.getOwner().removeCountry(defendingCountry);
        defendingCountry.setOwner(attackingCountry.getOwner());
        attackingCountry.getOwner().addCountry(defendingCountry);
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
        Stack<Country> countriesWithinBorder = new Stack<>();
        boolean valid = false;
        countriesWithinBorder = getConnectedOwnedCountries(sourceCountry, sourceCountry, player, countriesWithinBorder);
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
     * Return the Array of countries.
     * @return Array containing all the country objects
     */
    public Country[] getCountries(){
        return this.map.getCountries();
    }
    /**
     * Method that returns a stack of all the countries that the source country is connected to via friendly territory.
     * @param sourceCountry the source Country
     * @param  user the Player object that is doing the action
     * @param toTest Stack containing all of the connected owned countries
     * @return Stack that contains all the countries connected to source through friendly territory
     */
    public Stack<Country> getConnectedOwnedCountries(Country sourceCountry,Country root, Player user, Stack<Country> toTest){
        for(Country count: sourceCountry.getAdjacentCountries()){
            if(count.getOwner() == user && !toTest.contains(count) && count != root){
                toTest.add(count);
                return(getConnectedOwnedCountries(count,root, user, toTest));
            }
        }
        return toTest;
    }

    /**
     * Method that checks whether a player owns an entire continent and if they do they get bonus troops to deploy
     * @param user the player that is gaining the countries
     */
    public void allocateBonusTroops(Player user){
        boolean willTroopsBeAssigned;
        for (Continent cont : this.map.getContinents()){
            willTroopsBeAssigned = true;
            for(int count: cont.getCountryList()){
                if (!user.countries.contains(this.map.getCountries()[count])){ willTroopsBeAssigned = false; }

            }
        }
        this.riskView.boardUpdate(this.actionContext);
    }

    public static void main(String[] args) {
        new RiskModel();
    }
}
