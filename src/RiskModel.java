import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.awt.*;
import java.io.*;
import java.util.*;

/**
 * Risk Model class used to model the ongoing game
 *
 * @author Dimitry Koutchine, Kevin Fox, Omar Hashmi, Kshitij Sawhney
 * @version 11.04.2020
 */
public class RiskModel {
    /**List of all the players in the game **/
    public Player[] players;
    /** The map of the game **/
    public transient Map map;
    /** The current action context **/
    public  ActionContext actionContext;
    /** The current risk view**/
    public transient RiskView view;
    /** The current RiskController**/
    public transient RiskController controller;
    public int numPlayers;
    public int numHumans;
    public int numAI;
    public transient String textBuffer;
    public transient int numBuffer;
    public int [] troopSave;

    /**
     * Constructor for testing purposes
     *
     * @param names the names of players
     */
    public RiskModel(String[] names){
        this.map=Map.Import("maps/demo.zip");
        this.actionContext=new ActionContext(Phase.NEW_GAME,null);

        this.controller =new RiskController(this);
        this.view =new RiskView(controller,map);
        setNumPlayers(names.length);
        for(int i=0;i<numPlayers;i++){
            actionContext.setPlayerId(i);
            newPlayer(names[i]);
        }
        allocateCountries();
        allocateArmies();
        this.view.update(actionContext);
    }

    /** Constructor of Risk Model*/
    public RiskModel(){
        this.map=Map.Import("maps/demo.zip");
        this.actionContext=new ActionContext(Phase.NUM_PLAYERS,null);

        this.controller =new RiskController(this);
        this.view =new RiskView(this.controller,map);
        this.view.update(this.actionContext);
    }
    public void setNumPlayers(int numPlayers){
        this.numPlayers=numPlayers;
        players=new Player[numPlayers];
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

        if(i<numHumans){
            players[i]=new PlayerHuman(name,playerColors[i],startingArmySize,i,map);
        }else{
            players[i]=new PlayerAI(name, playerColors[i],startingArmySize,i,map);
        }

        //All players added
        if(i==numPlayers-1){
            allocateCountries();
            allocateArmies();
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
            for(Player player: players){
                if(!addStack.empty()){
                    player.addCountry(addStack.peek());
                    addStack.peek().setArmy(1);
                    player.removeTroops(1);
                    addStack.pop().setOwner(player);
                }
            }
        }

        this.actionContext=new ActionContext(Phase.DEPLOY_DST, this.players[0]);
    }
    private void allocateArmies(){
        Random rand = new Random(System.currentTimeMillis());

        for(Player player: players){
            while (player.getTroopsToDeploy() > 0){
                ArrayList<Country> temp = new ArrayList<>();
                Collections.addAll(temp,player.getCountries());
                Collections.shuffle(temp,rand);
                for(Country count: temp){
                    if (player.getTroopsToDeploy() >0) {
                        count.addArmy(1);
                        player.removeTroops(1);
                    }
                }
            }
        }
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
            if(player.getCountries().length==0){
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
            if(sumOfUnits == player.getCountries().length){
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
                if(players[nextIndex].isAI){
                    actionContext=new ActionContext(Phase.DEPLOY_CONFIRM,players[nextIndex]);
                    AIMoves();
                    return nextPlayer(players[nextIndex]);
                }
                else{
                    return this.players[nextIndex];
                }
            }
            //Error no more players call game is over
            if(this.players[nextIndex].equals(player)){
                gameIsOver();
                view.update(this.actionContext);
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

    private void AIMoves(){
        PlayerAI AI=(PlayerAI) actionContext.getPlayer();
        ActionContext AIAction;

        actionContext.setPhase(Phase.DEPLOY_CONFIRM);
        for(int i=0;i<5 && AI.canDeploy();i++){
            AIAction=AI.getMove(actionContext);
            deploy(AIAction.getPlayer(),
                    AIAction.getDstCountry(),
                    AIAction.getDstArmy());
            System.out.printf("AI Deploy: Dst: %s, Army: %d\n",AIAction.getDstCountry().getName(),AIAction.getDstArmy());
        }

        actionContext.setPhase(Phase.ATTACK_SRC_CONFIRM);
        for(int i=0;i<5 && AI.canAttack();i++){
            AIAction=AI.getMove(actionContext);
            attack(AIAction.getPlayer(),
                    AIAction.getSrcCountry(),
                    AIAction.getDstCountry(),
                    AIAction.getSrcArmy(),
                    AIAction.getDstArmy());
            System.out.printf("AI Attack: Src: %s, Dst: %s Src Army: %d, Dst Army: %d\n",AIAction.getSrcCountry().getName(),AIAction.getDstCountry().getName(),AIAction.getSrcArmy(),Math.min(AIAction.getDstCountry().getArmy(),2));
        }

        actionContext.setPhase(Phase.FORTIFY_CONFIRM);
        if(AI.canFortify()){
            AIAction=AI.getMove(actionContext);
            fortify(AIAction.getPlayer(),
                    AIAction.getSrcCountry(),
                    AIAction.getDstCountry(),
                    AIAction.getSrcArmy());
        }
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
                if(clickedCountry.getOwner().equals(this.actionContext.getPlayer())&&
                    clickedCountry.getArmy()>1) {
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
                    this.actionContext.setPhase(Phase.ATTACK_SRC_ARMY);
                    this.actionContext.setDstCountry(clickedCountry);
                    actionContext.setSrcArmy(actionContext.getSrcCountry().getArmy());
                }
                break;
            case FORTIFY_SRC:

                if(clickedCountry.getArmy()<2 || !clickedCountry.getOwner().equals(this.actionContext.getPlayer())){
                    break;
                }
                this.actionContext.setPhase(Phase.FORTIFY_DST);
                this.actionContext.setSrcCountry(clickedCountry);
                break;
            case FORTIFY_DST:
                this.actionContext.setPhase(Phase.FORTIFY_ARMY);
                this.actionContext.setDstCountry(clickedCountry);
                break;
        }
        view.update(this.actionContext);
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
            case ATTACK_SRC_ARMY:
            case ATTACK_SRC_CONFIRM:
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
                actionContext.setPlayer(nextPlayer(this.actionContext.getPlayer()));
                allocateBonusTroops(actionContext.getPlayer());
                if(actionContext.getPlayer().getTroopsToDeploy()>0){
                    this.actionContext=new ActionContext(Phase.DEPLOY_DST,this.actionContext.getPlayer());
                }else{
                    this.actionContext=new ActionContext(Phase.ATTACK_SRC,this.actionContext.getPlayer());
                }
                break;
        }
        view.update(this.actionContext);
    }
    /**
     * Method to deal with when the user clicks a confirm button
     */
    public void menuConfirm(){
        switch (this.actionContext.getPhase()) {
            case NUM_PLAYERS:
                this.actionContext=new ActionContext(Phase.NUM_AI,null);
                numHumans=numBuffer;
                actionContext.setPlayerId(numHumans);
                break;
            case NUM_AI:
                this.actionContext=new ActionContext(Phase.PLAYER_NAME,null);
                numAI=numBuffer;
                setNumPlayers(numHumans+numAI);
                break;
            case PLAYER_NAME:
                if(newPlayer(textBuffer)){
                    this.actionContext=new ActionContext(Phase.ATTACK_SRC,players[0]);
                }else{
                    this.actionContext.setPlayerId(actionContext.getPlayerId()+1);
                }
                break;
            case DEPLOY_ARMY:
                actionContext.setDstArmy(numBuffer);
                actionContext.setPhase(Phase.DEPLOY_CONFIRM);
                break;
            case DEPLOY_CONFIRM:
                if(deploy(this.actionContext.getPlayer(),
                        this.actionContext.getDstCountry(),
                        this.actionContext.getDstArmy())) {
                    if(actionContext.getPlayer().getTroopsToDeploy()==0){
                        this.actionContext=new ActionContext(Phase.ATTACK_SRC,this.actionContext.getPlayer());
                    }else{
                        this.actionContext=new ActionContext(Phase.DEPLOY_DST,this.actionContext.getPlayer());
                    }
                } else
                    System.out.println("Deploy Failed");
                break;
            case ATTACK_SRC_CONFIRM:
                actionContext.setPhase(Phase.ATTACK_DST_ARMY);
                this.actionContext.setPlayer(actionContext.getDstCountry().getOwner());
                break;
            case ATTACK_DST_CONFIRM:
                if(attack(this.actionContext.getPlayer(),
                        this.actionContext.getSrcCountry(),
                        this.actionContext.getDstCountry(),
                        this.actionContext.getSrcArmy(),
                        this.actionContext.getDstArmy())){
                    this.actionContext.setPhase(Phase.RETREAT_ARMY);
                    this.actionContext.setPlayer(actionContext.getSrcCountry().getOwner());
                }
                else
                    System.out.println("Attack failed");
                break;
            case ATTACK_SRC_ARMY:
                actionContext.setSrcArmy(numBuffer);
                actionContext.setPhase(Phase.ATTACK_SRC_CONFIRM);
                break;
            case ATTACK_DST_ARMY:
                actionContext.setDstArmy(numBuffer);
                actionContext.setPhase(Phase.ATTACK_DST_CONFIRM);
                break;
            case RETREAT_ARMY:
                actionContext.setDstArmy(numBuffer);
                actionContext.setPhase(Phase.RETREAT_CONFIRM);
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
            case FORTIFY_ARMY:
                actionContext.setSrcArmy(numBuffer);
                if(fortify(this.actionContext.getPlayer(),
                        this.actionContext.getSrcCountry(),
                        this.actionContext.getDstCountry(),
                        this.actionContext.getSrcArmy())) {
                    actionContext.setPlayer(nextPlayer(this.actionContext.getPlayer()));
                    allocateBonusTroops(actionContext.getPlayer());
                    if(actionContext.getPlayer().getTroopsToDeploy()>0){
                        this.actionContext=new ActionContext(Phase.DEPLOY_DST,this.actionContext.getPlayer());
                    }else{
                        this.actionContext=new ActionContext(Phase.ATTACK_SRC,this.actionContext.getPlayer());
                    }
                }
                else{
                    System.out.println("Fortify failed");
                }
                break;
        }
        view.update(this.actionContext);
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
            case RETREAT_ARMY:
                this.actionContext=new ActionContext(Phase.ATTACK_SRC,this.actionContext.getPlayer());
                break;
            case ATTACK_DST:
            case ATTACK_SRC_ARMY:
            case ATTACK_SRC_CONFIRM:
                this.actionContext=new ActionContext(Phase.ATTACK_SRC,this.actionContext.getPlayer());
                break;
            case FORTIFY_SRC:
            case FORTIFY_DST:
            case FORTIFY_ARMY:
            case FORTIFY_CONFIRM:
                this.actionContext=new ActionContext(Phase.FORTIFY_SRC,this.actionContext.getPlayer());
                break;
        }
        view.update(this.actionContext);
    }

    /**
     * Method that performs the Deploy action. The player is able to deploy troops they have to any owned country.
     * @param  player the Player object that is doing the action
     * @param destinationCountry the country the troops are being sent.
     * @param troopsToDeploy int that represent the amount of army units to move.
     * @return boolean fortify that returns a true if the function was successful.
     */
    public boolean deploy(Player player, Country destinationCountry, int troopsToDeploy){
        if(troopsToDeploy>player.getTroopsToDeploy() || destinationCountry.getOwner()!=player){
            return false;
        }
        else{
            player.removeTroops(troopsToDeploy);
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
    public boolean attack(Player player, Country attackingCountry, Country defendingCountry, int unitsToAttack, int unitsToDefend){
        if(attackingCountry.getArmy()-unitsToAttack<=0) {
            return false;
        }

        int defendingArmy = unitsToDefend;
        int attackingArmy = unitsToAttack;


        ArrayList<Integer> rollsAttackerMade = new ArrayList<>();
        ArrayList<Integer> rollsDefenderMade = new ArrayList<>();


            Integer[] defenderRolls = new Integer[defendingArmy < 2? defendingArmy: 2];
            Integer[] attackRolls = new Integer[attackingArmy < 3? attackingArmy: 3];

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
            while(!attackersQueue.isEmpty()){
                rollsAttackerMade.add(attackersQueue.remove());
            }
        while(!defenderQueue.isEmpty()){
            rollsDefenderMade.add(defenderQueue.remove());
        }

        //Send dice rolls
        actionContext.setDiceRolls(new Integer[][]{rollsAttackerMade.toArray(new Integer[rollsAttackerMade.size()]), rollsDefenderMade.toArray(new Integer[rollsDefenderMade.size()])});

        this.actionContext.setSrcArmyDead(unitsToAttack - attackingArmy);
        this.actionContext.setDstArmy(unitsToDefend);
        this.actionContext.setDstArmyDead(unitsToDefend - defendingArmy);


        //Attacker wins
        if(defendingArmy <= 0){
            defendingCountry.removeArmy(unitsToDefend);
            attackingCountry.removeArmy(actionContext.getSrcArmyDead());
            if(defendingCountry.getArmy()==0) {
                defendingCountry.getOwner().removeCountry(defendingCountry);
                attackingCountry.getOwner().addCountry(defendingCountry);
                defendingCountry.setOwner(attackingCountry.getOwner());
                attackingCountry.removeArmy(attackingArmy);
                defendingCountry.setArmy(attackingArmy);

            }
            this.actionContext.setAttackerVictory(true);
        }
        //Attacker lost
        else{
            attackingCountry.removeArmy(actionContext.getSrcArmyDead());
            defendingCountry.removeArmy(actionContext.getDstArmyDead());
            this.actionContext.setAttackerVictory(false);
        }


        if(hasAnyoneLost(attackingCountry.getOwner(), defendingCountry.getOwner())){
            if(gameIsOver()){
                //Force game over here
                this.view.update(this.actionContext);
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
        defendingCountry.setArmy((this.actionContext.getSrcArmy())-unitsToRetreat);
        attackingCountry.removeArmy(-unitsToRetreat);
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
    public boolean fortify(Player player, Country sourceCountry, Country destinationCountry, int unitsToSend){
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
     * @param player the player that is gaining the countries
     */
    public void allocateBonusTroops(Player player){
        player.troopsToDeploy=0;
        for(Continent continent: map.getContinents()){
            if(continent.isOwnedBy(player)){
                player.troopsToDeploy+=continent.getBonusTroops();
            }
        }

        player.troopsToDeploy+=Math.max(3,player.countryIndexes.size()/3);
    }
    public void exportToJson(){
       ModelSaveLoad saveLoad = new ModelSaveLoad();
       saveLoad.modelSave(this);
    }
    public RiskModel importFromJson(){
        ModelSaveLoad saveLoad = new ModelSaveLoad();
        RiskModel temp = saveLoad.modelLoad();
        return  temp;

    }

    public static void main(String[] args) {
        new RiskModel().importFromJson();
        System.out.println("test");

    }
}
