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
    public Player[] players;
    /** The map of the game **/
    public Map map;
    /** The current action context **/
    public ActionContext ac;
    /** The current risk view**/
    public RiskView view;
    /** The current RiskController**/
    public RiskController controller;
    public int numPlayers;
    public int numHumans;
    public int numAI;

    /**
     * Constructor for testing purposes
     *
     * @param names the names of players
     */
    public RiskModel(String[] names){
        this.map=Map.Import("maps/demo.zip");
        this.ac =new ActionContext(Phase.NUM_HUMANS,null);

        this.controller =new RiskController(this,map);
        this.view =new RiskView(controller,map);
        setNumHumans(names.length);
        setNumAI(0);
        for(int i=0;i<numPlayers;i++){
            ac.setPlayerIndex(i);
            setPlayerName(names[i]);
        }
        allocateArmies();
        this.view.update(ac);
    }

    /** Constructor of Risk Model*/
    public RiskModel(){
        this.map=Map.Import("maps/test.zip");
        this.ac =new ActionContext(Phase.NUM_HUMANS,null);

        this.controller =new RiskController(this,map);
        this.view =new RiskView(this.controller,map);
        this.view.update(this.ac);
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
        this.ac =new ActionContext(Phase.DEPLOY_DST, this.players[0]);
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
            this.ac = new ActionContext(Phase.GAME_OVER, winner);
            return true;
        }
        return false;
    }
    /**
     * Checks if anyone has met a lost condition when they attacker
     * @param attacker The attacking player
     * @param defender The defending player
     */
    private boolean hasAnyoneLost(Player attacker, Player defender){
        Player[] playersToCheck = {attacker,defender};

        for(Player player : playersToCheck){
            //If they do not own anymore countries they lose
            if(player.getCountries().length==0){
                player.setHasLost(true);
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
                player.setHasLost(true);
                return true;
            }
        }
        return false;
    }

    /**
     * Method to select the next player that still is in the game
     * @param player the currentPlayer to determine position
     * @return the next Player still in game
     */
    private Player nextPlayer(Player player){
        int playerId = Arrays.asList(this.players).indexOf(player);

        for(int nextIndex = (playerId+1)%numPlayers;; nextIndex = (nextIndex+1)%numPlayers){
            if(!this.players[nextIndex].getHasLost()){
                return this.players[nextIndex];
            }
            //Error no more players call game is over
            if(this.players[nextIndex].equals(player)){
                gameIsOver();
                view.update(this.ac);
                return null;
            }
        }
    }

    private void AIMoves(){
        PlayerAI AI=(PlayerAI) ac.getPlayer();
        ActionContext AIAction;
    }

    /********************  USER INPUT METHODS  ********************/
    public void countrySelected(Country country){
        switch (ac.getPhase()){
            case CLAIM_COUNTRY:
                if(country.getOwner()==null){
                    claim(ac.getPlayer(),country);
                }
                break;
            case INITIAL_DEPLOY_DST:
                if(ac.getPlayer()==country.getOwner()){
                    ac.setPhase(Phase.INITIAL_DEPLOY_NUM_TROOPS);
                    ac.setDstCountry(country);
                }
                break;
            case DEPLOY_DST:
                if(ac.getPlayer()==country.getOwner()){
                    ac.setPhase(Phase.DEPLOY_NUM_TROOPS);
                    ac.setDstCountry(country);
                }
                break;
            case ATTACK_SRC:
                if(ac.getPlayer()==country.getOwner() && country.getArmy()>1) {
                    ac.setPhase(Phase.ATTACK_DST);
                    ac.setSrcCountry(country);
                    ac.setHighlightedCountries(country.getAdjacentUnownedCountries(ac.getPlayer()));
                }
                break;
            case ATTACK_DST:
                if(Arrays.asList(ac.getSrcCountry().getAdjacentUnownedCountries(ac.getPlayer())).contains(country)) {
                    ac.setPhase(Phase.ATTACK_NUM_TROOPS);
                    ac.setDstCountry(country);
                }
                break;
            case FORTIFY_SRC:
                if(ac.getPlayer()==country.getOwner() && country.getArmy()>1) {
                    ac.setPhase(Phase.FORTIFY_DST);
                    ac.setSrcCountry(country);
                    ac.setHighlightedCountries(country.getConnectedOwnedCountries(ac.getPlayer()));
                }
                break;
            case FORTIFY_DST:
                if(Arrays.asList(ac.getSrcCountry().getConnectedOwnedCountries(ac.getPlayer())).contains(country)){
                    ac.setPhase(Phase.FORTIFY_NUM_TROOPS);
                    ac.setDstCountry(country);
                }
                break;
        }
        view.update(ac);
    }
    public void numTroops(int numTroops){
        switch (this.ac.getPhase()) {
            case INITIAL_DEPLOY_NUM_TROOPS:
                ac.setDstArmy(numTroops);
                ac.setPhase(Phase.INITIAL_DEPLOY_CONFIRM);
                break;
            case DEPLOY_NUM_TROOPS:
                ac.setDstArmy(numTroops);
                ac.setPhase(Phase.DEPLOY_CONFIRM);
                break;
            case ATTACK_NUM_TROOPS:
                ac.setSrcArmy(numTroops);
                ac.setPhase(Phase.ATTACK_CONFIRM);
                break;
            case DEFEND_NUM_TROOPS:
                ac.setDstArmy(numTroops);
                ac.setPhase(Phase.DEFEND_CONFIRM);
                break;
            case RETREAT_NUM_TROOPS:
                ac.setDstArmy(numTroops);
                ac.setPhase(Phase.RETREAT_CONFIRM);
                break;
            case FORTIFY_NUM_TROOPS:
                ac.setSrcArmy(numTroops);
                ac.setPhase(Phase.FORTIFY_CONFIRM);
                break;
        }
        view.update(this.ac);
    }
    /**
     * Method used to deal with when the user clicks the skip button
     */
    public void menuSkip(){
        switch (this.ac.getPhase()){
            case DEPLOY_DST:
            case DEPLOY_NUM_TROOPS:
                break;
            case DEPLOY_CONFIRM:
                this.ac =new ActionContext(Phase.ATTACK_SRC,this.ac.getPlayer());
                break;
            case ATTACK_SRC:
            case ATTACK_DST:
            case ATTACK_NUM_TROOPS:
            case ATTACK_CONFIRM:
                this.ac =new ActionContext(Phase.FORTIFY_SRC,this.ac.getPlayer());
                break;
            case RETREAT_NUM_TROOPS:
                this.ac.setDstArmy(0);
                this.ac =new ActionContext(Phase.ATTACK_SRC,this.ac.getPlayer());
                break;
            case FORTIFY_SRC:
            case FORTIFY_CONFIRM:
            case FORTIFY_NUM_TROOPS:
            case FORTIFY_DST:
                ac.setPlayer(nextPlayer(this.ac.getPlayer()));
                allocateBonusTroops(ac.getPlayer());
                if(ac.getPlayer().getTroopsToDeploy()>0){
                    this.ac =new ActionContext(Phase.DEPLOY_DST,this.ac.getPlayer());
                }else{
                    this.ac =new ActionContext(Phase.ATTACK_SRC,this.ac.getPlayer());
                }
                break;
        }

        view.update(this.ac);
    }
    /**
     * Method to deal with when the user clicks a confirm button
     */
    public void menuConfirm(){
        switch (this.ac.getPhase()) {
//            case DEPLOY_DST:
//                if(this.ac.getPlayer().isAI) {
//                    this.ac.setPhase(Phase.DEPLOY_NUM_TROOPS);
//                }
//                break;
//            case DEPLOY_NUM_TROOPS:
//                if(!ac.getPlayer().isAI) {
//                    ac.setDstArmy(numBuffer);
//                    ac.setPhase(Phase.DEPLOY_CONFIRM);
//                }else{
//                    this.ac = (((PlayerAI)this.ac.getPlayer()).getMove(this.ac));
//
//                }
//                break;
            case INITIAL_DEPLOY_CONFIRM:
                deploy(this.ac.getPlayer(),
                        this.ac.getDstCountry(),
                        this.ac.getDstArmy());
                if (ac.getPlayer().getTroopsToDeploy() == 0) {
                    this.ac = new ActionContext(Phase.INITIAL_DEPLOY_DST, nextPlayer(ac.getPlayer()));
                    if(ac.getPlayer().getTroopsToDeploy()==0){
                        this.ac = new ActionContext(Phase.ATTACK_SRC, players[0]);
                    }
                }else{
                    this.ac = new ActionContext(Phase.INITIAL_DEPLOY_DST, ac.getPlayer());
                }
                break;
            case DEPLOY_CONFIRM:
                deploy(this.ac.getPlayer(),
                        this.ac.getDstCountry(),
                        this.ac.getDstArmy());
                if (ac.getPlayer().getTroopsToDeploy() == 0) {
                    this.ac = new ActionContext(Phase.ATTACK_SRC, this.ac.getPlayer());
                }else{
                    this.ac = new ActionContext(Phase.DEPLOY_DST, this.ac.getPlayer());
                }
                break;
//            case ATTACK_SRC:
//                this.ac = (((PlayerAI)this.ac.getPlayer()).getMove(this.ac));
//                break;
            case ATTACK_CONFIRM:
                ac.setPhase(Phase.DEFEND_NUM_TROOPS);
                this.ac.setPlayer(ac.getDstCountry().getOwner());
                break;
            case DEFEND_CONFIRM:
                attack(ac.getPlayer(),
                        ac.getSrcCountry(),
                        ac.getDstCountry(),
                        ac.getSrcArmy(),
                        ac.getDstArmy());
                this.ac.setPhase(Phase.RETREAT_NUM_TROOPS);
                this.ac.setPlayer(ac.getSrcCountry().getOwner());
                break;
//            case ATTACK_NUM_TROOPS:
//                ac.setSrcArmy(numBuffer);
//                ac.setPhase(Phase.ATTACK_CONFIRM);
//                break;
//            case DEFEND_NUM_TROOPS:
//                ac.setDstArmy(numBuffer);
//                ac.setPhase(Phase.DEFEND_CONFIRM);
//                break;
//            case RETREAT_NUM_TROOPS:
//                ac.setDstArmy(numBuffer);
//                ac.setPhase(Phase.RETREAT_CONFIRM);
//                break;
            case RETREAT_CONFIRM:
                if(this.ac.getPlayer().isAI){
                    retreat(ac.getPlayer(),
                            ac.getSrcCountry(),
                            ac.getDstCountry(),
                            0);

                }else {
                    retreat(ac.getPlayer(),
                            ac.getSrcCountry(),
                            ac.getDstCountry(),
                            ac.getDstArmy());
                }
                this.ac =new ActionContext(Phase.ATTACK_SRC,this.ac.getPlayer());
                break;
//            case FORTIFY_SRC:
//            case FORTIFY_NUM_TROOPS:
//                if(this.ac.getPlayer().isAI){
//                    this.ac = (((PlayerAI)this.ac.getPlayer()).getMove(this.ac));
//                }else {
//                    ac.setSrcArmy(numBuffer);
//                }
//                if(!this.ac.getPhase().equals(Phase.DEPLOY_DST)) {
//                    fortify(this.ac.getPlayer(),
//                            this.ac.getSrcCountry(),
//                            this.ac.getDstCountry(),
//                            this.ac.getSrcArmy());
//                }
//                ac.setPlayer(nextPlayer(this.ac.getPlayer()));
//                allocateBonusTroops(ac.getPlayer());
//                if (ac.getPlayer().getTroopsToDeploy() > 0) {
//                    this.ac = new ActionContext(Phase.DEPLOY_DST, this.ac.getPlayer());
//                } else {
//                    this.ac = new ActionContext(Phase.ATTACK_SRC, this.ac.getPlayer());
//                }
//                break;
        }
        view.update(this.ac);
    }
    /**
     * Methods to deal with when the user clicks a back button
     */
    public void menuBack(){
        switch(ac.getPhase()){
            case DEPLOY_DST:
            case DEPLOY_NUM_TROOPS:
            case DEPLOY_CONFIRM:
                ac =new ActionContext(Phase.DEPLOY_DST, ac.getPlayer());
                break;
            case ATTACK_DST:
            case ATTACK_NUM_TROOPS:
            case ATTACK_CONFIRM:
                ac =new ActionContext(Phase.ATTACK_SRC, ac.getPlayer());
                break;
            case DEFEND_CONFIRM:
                ac.setPhase(Phase.DEFEND_NUM_TROOPS);
            case FORTIFY_DST:
            case FORTIFY_NUM_TROOPS:
            case FORTIFY_CONFIRM:
                ac =new ActionContext(Phase.FORTIFY_SRC, ac.getPlayer());
                break;
        }
        view.update(this.ac);
    }

    /**********************  PHASE METHODS  **********************/
    public void setNumHumans(int n){
        System.out.printf("again\n");
        numHumans=n;
        numPlayers=numHumans;

        if(numHumans==6){
            numAI=0;
            players=new Player[6];

            ac =new ActionContext(Phase.PLAYER_NAME,null);
            ac.setPlayerIndex(0);
        }else{
            ac =new ActionContext(Phase.NUM_AI,null);
            ac.setPlayerIndex(numHumans);
        }

        view.update(ac);
    }
    public void setNumAI(int n){
        numAI=n;
        numPlayers+=numAI;
        players=new Player[numPlayers];

        ac =new ActionContext(Phase.PLAYER_NAME,null);
        ac.setPlayerIndex(0);
        view.update(ac);
    }
    public void setPlayerName(String name){
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

        int i=ac.getPlayerIndex();
        System.out.printf("%d\n", ac.getPlayerIndex());

        if(i<numHumans){
            this.players[i]=new PlayerHuman(name,playerColors[i],startingArmySize,i);
        }else{
            this.players[i]=new PlayerAI(name, playerColors[i],startingArmySize,i,map.getContinents());
        }
        ac.setPlayerIndex(i+1);

        //All players added
        if(i==numPlayers-1){
            /*
            allocateCountries();
            allocateArmies();
            this.ac=new ActionContext(Phase.ATTACK_SRC,players[0]);
             */
            ac=new ActionContext(Phase.CLAIM_COUNTRY,players[0]);
        }
        view.update(ac);
    }
    private void claim(Player player, Country country){
        player.addCountry(country);
        player.removeTroops(1);
        country.setOwner(ac.getPlayer());
        country.setArmy(1);
        ac.addHighlightedCountry(country);

        int allocatedCountries=0;
        for(Player player1: players){
            allocatedCountries+=player1.getCountries().length;
        }
        if(allocatedCountries<map.getCountries().length){
            ac.setPlayer(nextPlayer(player));
        }else{
            ac=new ActionContext(Phase.INITIAL_DEPLOY_DST,players[0]);
        }
    }
    /**
     * Method that performs the Deploy action. The player is able to deploy troops they have to any owned country.
     * @param  player the Player object that is doing the action
     * @param destinationCountry the country the troops are being sent.
     * @param troopsToDeploy int that represent the amount of army units to move.
     * @return boolean fortify that returns a true if the function was successful.
     */
    private void deploy(Player player, Country destinationCountry, int troopsToDeploy){
        player.removeTroops(troopsToDeploy);
        destinationCountry.addArmy(troopsToDeploy);
    }
    /**
     * This method is the attack phase controller for the game of risk
     * @param attackingCountry The attacking country
     * @param defendingCountry The defending country
     * @param unitsToAttack number of attackers from the attacking country
     * @return Boolean true = no error, false = units to attack error
     */
    private void attack(Player player, Country attackingCountry, Country defendingCountry, int unitsToAttack, int unitsToDefend){
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
        ac.setDiceRolls(new Integer[][]{rollsAttackerMade.toArray(new Integer[rollsAttackerMade.size()]), rollsDefenderMade.toArray(new Integer[rollsDefenderMade.size()])});

        this.ac.setSrcArmyDead(unitsToAttack - attackingArmy);
        this.ac.setDstArmy(unitsToDefend);
        this.ac.setDstArmyDead(unitsToDefend - defendingArmy);


        //Attacker wins
        if(defendingArmy <= 0){
            defendingCountry.removeArmy(unitsToDefend);
            attackingCountry.removeArmy(ac.getSrcArmyDead());
            if(defendingCountry.getArmy()==0) {
                defendingCountry.getOwner().removeCountry(defendingCountry);
                attackingCountry.getOwner().addCountry(defendingCountry);
                defendingCountry.setOwner(attackingCountry.getOwner());
                attackingCountry.removeArmy(attackingArmy);
                defendingCountry.setArmy(attackingArmy);

            }
            this.ac.setAttackerVictory(true);
        }
        //Attacker lost
        else{
            attackingCountry.removeArmy(ac.getSrcArmyDead());
            defendingCountry.removeArmy(ac.getDstArmyDead());
            this.ac.setAttackerVictory(false);
        }


        if(hasAnyoneLost(attackingCountry.getOwner(), defendingCountry.getOwner())){
            if(gameIsOver()){
                //Force game over here
                this.view.update(this.ac);
            }
        }
    }
    /**
     * Method to process a successful attack when sending units back to the attacking country
     * @param player the current player
     * @param attackingCountry the Country of the attacking player
     * @param defendingCountry the defending country that has lost
     * @param unitsToRetreat the number of units to send to the attacking country
     * @return boolean True: success, False: fail
     */
    private void retreat(Player player, Country attackingCountry, Country defendingCountry, int unitsToRetreat){
        defendingCountry.removeArmy(unitsToRetreat);
        attackingCountry.addArmy(unitsToRetreat);
    }
    /**
     * Method that performs the fortification action. The army of one country is moved to another country owned by the player and that is also connected through owned territory.
     * @param sourceCountry the source Country
     * @param  player the Player object that is doing the action
     * @param destinationCountry the country the troops are being sent.
     * @param unitsToSend int that represent the amount of army units to move.
     * @return boolean  that returns a true if the function was successful.
     */
    private void fortify(Player player, Country sourceCountry, Country destinationCountry, int unitsToSend){
        sourceCountry.removeArmy(unitsToSend);
        destinationCountry.addArmy(unitsToSend);
    }

    /**
     * Return the Array of countries.
     * @return Array containing all the country objects
     */
    public Country[] getCountries(){
        return this.map.getCountries();
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
    public void importFromJson(){
        ModelSaveLoad saveLoad = new ModelSaveLoad();
        saveLoad.modelLoad(this);
        view.update(this.ac);

    }

    public static void main(String[] args) {
      RiskModel test = new RiskModel();
      test.importFromJson();

    }
}
