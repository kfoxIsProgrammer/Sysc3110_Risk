import javax.sound.sampled.*;
import java.awt.*;
import java.io.File;
import java.util.*;

/**
 * Risk Model class used to model the ongoing game
 *
 * @author Dimitry Koutchine, Kevin Fox, Omar Hashmi, Kshitij Sawhney
 * @version 11.04.2020
 */
public class RiskModel {
    /**List of all the players in the game **/
    protected Player[] players;
    /** The map of the game **/
    protected Map map;
    /** The current action context **/
    protected ActionContext ac;
    protected ArrayList<RiskView> views;
    protected int numPlayers;
    protected int numHumans;
    protected int numAI;

    /**
     * Constructor for testing purposes
     *
     * @param names the names of players
     */
    public RiskModel(String[] names){
        map=Map.Import("maps/demo.zip");
        ac=new ActionContext(Phase.NUM_HUMANS,null);
        views=new ArrayList<>();
        views.add(new RiskGUI(this,map));
        this.playSound("resources/risk.wav");

        numHumans(names.length);
        numAI(0);

        for(int i=0;i<numPlayers;i++){
            ac.setPlayerIndex(i);
            playerName(names[i]);
        }

        allocateCountries();
        allocateArmies();
        updateViews(ac);
    }
    /** Constructor of Risk Model*/
    public RiskModel(){
        ac=new ActionContext(Phase.NUM_HUMANS,null);
        views=new ArrayList<>();
        playSound("resources/risk.wav");
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

    /********************************************  UTILITY METHODS  ********************************************/
    private void updateViews(ActionContext actionContext){
        for(RiskView view: this.views){
            view.update(actionContext);
        }
    }
    private void updateViewLogs(String message){
        for(RiskView view: this.views){
            view.log(message);
        }
    }
    public void updateSaveFiles(){
        for(RiskView view: this.views){
            view.updateSaveFileList(ModelSaveLoad.getSaves());
        }
    }
    public void updateMapFiles(){
        for(RiskView view: this.views){
            view.updateMapFileList(MapImport.getMaps());
        }
    }
    /**
     * Helper method to determine if the game is over based on 2 win conditions
     * 1. All players have lost because they cannot make an attack
     * 2. 1 player has won because they own the most countries and no one else can move
     * @return Pair of boolean (false = game over), int (what type of win condition)
     */
    private boolean gameIsOver(){
        playSound("resources/train hits guy.wav");

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
            int ownedCountries =0;

            //If they do not own anymore countries they lose
            for(Country country: map.getCountries()){
                if(country.getOwner().equals(player)){

                    ownedCountries++;
                }
            }
            if(ownedCountries==0){
                player.setHasLost(true);
                return true;
            }
        }
        return false;
    }
    /**
     * Method to select the next player that still is in the game
     * @param currentPlayer the currentPlayer to determine position
     */
    private Player nextPlayer(Player currentPlayer){
        int i=(Arrays.asList(this.players).indexOf(currentPlayer)+1)%numPlayers;

        while(players[i]!=currentPlayer){
            if(!players[i].getHasLost()){
                return players[i];
            }
            i=(i+1)%numPlayers;
        }
        gameIsOver();
        updateViews(ac);
        return null;
    }
    /**
     * Allocates troops for a player to deploy
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

    /****************************************  USER INPUT METHODS  ****************************************/
    public void countrySelected(Country country){
        playSound("resources/bite.wav");
        switch (ac.getPhase()){
            case CLAIM_COUNTRY:
                if(country.getOwner()==null){
                    claim(ac.getPlayer(),country);

                    int allocatedCountries=0;
                    for(Player player1: players) {
                        allocatedCountries += player1.getCountries().length;
                    }
                    if(allocatedCountries<map.getCountries().length){
                        ac=new ActionContext(Phase.CLAIM_COUNTRY,nextPlayer(ac.getPlayer()));

                        for(Country country1: map.getCountries()){
                            if(country1.getOwner()!=null){
                                ac.addHighlightedCountry(country1);
                            }
                        }
                    }else{
                        ac=new ActionContext(Phase.INITIAL_DEPLOY_DST,players[0]);
                    }
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
        updateViews(ac);
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
        updateViews(ac);
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
                this.ac=new ActionContext(Phase.ATTACK_SRC,this.ac.getPlayer());
                break;
            case ATTACK_SRC:
            case ATTACK_DST:
            case ATTACK_NUM_TROOPS:
            case ATTACK_CONFIRM:
                this.ac=new ActionContext(Phase.FORTIFY_SRC,this.ac.getPlayer());
                break;
            case RETREAT_NUM_TROOPS:
                this.ac.setDstArmy(0);
                this.ac=new ActionContext(Phase.ATTACK_SRC,this.ac.getPlayer());
                break;
            case FORTIFY_SRC:
            case FORTIFY_CONFIRM:
            case FORTIFY_NUM_TROOPS:
            case FORTIFY_DST:
                ac.setPlayer(nextPlayer(ac.getPlayer()));
                allocateBonusTroops(ac.getPlayer());
                ac=new ActionContext(Phase.DEPLOY_DST,ac.getPlayer());

                break;
        }
        updateViews(ac);
    }
    /**
     * Method to deal with when the user clicks a confirm button
     */
    public void menuConfirm(){
        switch (this.ac.getPhase()) {
            case INITIAL_DEPLOY_CONFIRM:
                deploy(this.ac.getPlayer(),
                        this.ac.getDstCountry(),
                        this.ac.getDstArmy());
                for(Player player: players){
                    if(player.troopsToDeploy>0){
                        ac=new ActionContext(Phase.INITIAL_DEPLOY_DST,player);
                        updateViews(ac);
                        return;
                    }
                }
                ac = new ActionContext(Phase.ATTACK_SRC, ac.getPlayer());
                break;
            case DEPLOY_CONFIRM:
                deploy(ac.getPlayer(),
                        ac.getDstCountry(),
                        ac.getDstArmy());
                if (ac.getPlayer().getTroopsToDeploy() == 0) {
                    ac=new ActionContext(Phase.ATTACK_SRC, ac.getPlayer());
                }else{
                    ac=new ActionContext(Phase.DEPLOY_DST, ac.getPlayer());
                }
                break;
            case ATTACK_CONFIRM:
                ac.setPhase(Phase.DEFEND_NUM_TROOPS);
                ac.setPlayer(ac.getDstCountry().getOwner());
                Player defender = ac.getDstCountry().getOwner();
                if(ac.getPlayer().isAI){
                    ac.setPlayer(ac.getSrcCountry().getOwner());
                    ac.setDstArmy(Math.min(2,ac.getDstCountry().getArmy()));
                    attack(
                            ac.getSrcCountry(),
                            ac.getDstCountry(),
                            ac.getSrcArmy(),
                            ac.getDstArmy());
                    if(ac.attackerVictory()){
                            ac.setPhase(Phase.RETREAT_NUM_TROOPS);
                        if(hasAnyoneLost(ac.getPlayer(), defender)){
                            if(gameIsOver()){

                            }
                        }
                    }else{
                        //retreat(ac.getSrcCountry(),ac.getDstCountry(),ac.getSrcArmy()-ac.getSrcArmyDead());
                        this.ac=new ActionContext(Phase.ATTACK_SRC,this.ac.getPlayer());
                    }
                }
                break;
            case DEFEND_CONFIRM:
                ac.setPlayer(ac.getSrcCountry().getOwner());
                 defender = ac.getDstCountry().getOwner();
                attack(
                        ac.getSrcCountry(),
                        ac.getDstCountry(),
                        ac.getSrcArmy(),
                        ac.getDstArmy());
                if(ac.attackerVictory()){
                        ac.setPhase(Phase.RETREAT_NUM_TROOPS);
                    if(hasAnyoneLost(ac.getPlayer(), defender)){
                        if(gameIsOver()){

                        }
                    }
                }else{
                   //retreat(ac.getSrcCountry(),ac.getDstCountry(),ac.getSrcArmy()-ac.getSrcArmyDead());
                   this.ac=new ActionContext(Phase.ATTACK_SRC,this.ac.getPlayer());
                }

                break;
            case RETREAT_CONFIRM:
                if(this.ac.getPlayer().isAI){
                    retreat(
                            ac.getSrcCountry(),
                            ac.getDstCountry(),
                            0);

                }else {
                    retreat(
                            ac.getSrcCountry(),
                            ac.getDstCountry(),
                            ac.getDstArmy());
                }
                this.ac=new ActionContext(Phase.ATTACK_SRC,this.ac.getPlayer());
                break;
            case FORTIFY_CONFIRM:
                fortify(
                    ac.getSrcCountry(),
                    ac.getDstCountry(),
                    ac.getSrcArmy());
                updateViews(ac);
                allocateBonusTroops(ac.getPlayer());
                ac=new ActionContext(Phase.DEPLOY_DST, nextPlayer(ac.getPlayer()));
                break;
        }
        updateViews(ac);
    }
    /**
     * Methods to deal with when the user clicks a back button
     */
    public void menuBack(){
        switch(ac.getPhase()){
            case INITIAL_DEPLOY_DST:
            case INITIAL_DEPLOY_NUM_TROOPS:
            case INITIAL_DEPLOY_CONFIRM:
                ac=new ActionContext(Phase.INITIAL_DEPLOY_DST, ac.getPlayer());
                break;
            case DEPLOY_DST:
            case DEPLOY_NUM_TROOPS:
            case DEPLOY_CONFIRM:
                ac=new ActionContext(Phase.DEPLOY_DST, ac.getPlayer());
                break;
            case ATTACK_DST:
            case ATTACK_NUM_TROOPS:
            case ATTACK_CONFIRM:
            case RETREAT_NUM_TROOPS:
            case RETREAT_CONFIRM:
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
        updateViews(ac);
    }
    public void menuOk(){
        Phase tmpPhase=ac.getPhase();
        Player tmpPlayer=ac.getPlayer();

        ac=((PlayerAI) ac.getPlayer()).getMove(ac);
        if(ac==null){
            ac=new ActionContext(tmpPhase,tmpPlayer);
            menuSkip();
        }
        switch(this.ac.getPhase()){
            case CLAIM_COUNTRY:
                countrySelected(ac.getDstCountry());
                break;
            case INITIAL_DEPLOY_DST:
                countrySelected(ac.getDstCountry());
                ac.setPhase(Phase.INITIAL_DEPLOY_NUM_TROOPS);
                numTroops(ac.getDstArmy());
                ac.setPhase(Phase.INITIAL_DEPLOY_CONFIRM);
                menuConfirm();
                break;
            case DEPLOY_DST:
                countrySelected(ac.getDstCountry());
                ac.setPhase(Phase.DEPLOY_NUM_TROOPS);
                numTroops(ac.getDstArmy());
                ac.setPhase(Phase.DEPLOY_CONFIRM);
                menuConfirm();
                break;
            case ATTACK_SRC:
                if(ac.getSrcArmy()<ac.getDstArmy()){
                    ac.setPhase(Phase.ATTACK_SRC);
                    menuSkip();
                    break;
                }
                countrySelected(ac.getSrcCountry());
                ac.setPhase(Phase.ATTACK_DST);
                countrySelected(ac.getDstCountry());
                ac.setPhase(Phase.ATTACK_NUM_TROOPS);
                numTroops(ac.getSrcArmy());
                ac.setPhase(Phase.ATTACK_CONFIRM);
                menuConfirm();
                break;
            case RETREAT_NUM_TROOPS:
                numTroops(0);
                ac.setPhase(Phase.RETREAT_CONFIRM);
                menuConfirm();
                break;
            case FORTIFY_SRC:
                if(ac.getSrcCountry()==null){
                    ac.setPhase(Phase.FORTIFY_SRC);
                    menuSkip();
                    break;
                }
                countrySelected(ac.getSrcCountry());
                ac.setPhase(Phase.FORTIFY_DST);
                countrySelected(ac.getDstCountry());
                ac.setPhase(Phase.FORTIFY_NUM_TROOPS);
                numTroops(ac.getSrcArmy());
                ac.setPhase(Phase.FORTIFY_CONFIRM);
                menuConfirm();
                break;
        }
    }

    /********************************************  PHASE METHODS  ********************************************/
    public void numHumans(int n){
        numHumans=n;
        numPlayers=numHumans;

        if(numHumans==6){
            numAI=0;
            players=new Player[6];

            ac=new ActionContext(Phase.PLAYER_NAME,null);
            ac.setPlayerIndex(0);
        }else{
            ac=new ActionContext(Phase.NUM_AI,null);
            ac.setPlayerIndex(numHumans);
        }

        updateViews(ac);
    }
    public void numAI(int n){
        numAI=n;
        numPlayers+=numAI;
        players=new Player[numPlayers];

        ac=new ActionContext(Phase.PLAYER_NAME,null);
        ac.setPlayerIndex(0);
        if(numHumans==0){
            playerName("H.O.N.K.");
        }
        updateViews(ac);
    }
    public void playerName(String name){
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

        if(i<numHumans){
            this.players[i]=new PlayerHuman(name,playerColors[i],startingArmySize,i,map);
        }else{
            this.players[i]=new PlayerAI(name, playerColors[i],startingArmySize,i,map);
        }
        updateViewLogs(name+" joined the game\n");
        ac.setPlayerIndex(i+1);
        if(ac.getPlayerIndex()>=numHumans && ac.getPlayerIndex()<numPlayers){
            String AIName=new String[]{"H.O.N.K.","Ripley", "Clarke", "Odysseus", "Gygax", "Durand"}[ac.getPlayerIndex()];
            playerName(AIName);
            ac.setPlayerIndex(ac.getPlayerIndex()+1);
        }

        //All players added
        if(i==numPlayers-1){
            /*
            allocateCountries();
            allocateArmies();
            this.ac=new ActionContext(Phase.ATTACK_SRC,players[0]);
             */
            ac=new ActionContext(Phase.CLAIM_COUNTRY,players[0]);
        }
        updateViews(ac);
    }
    private void claim(Player player, Country country){
        player.addCountry(country);
        player.removeTroops(1);
        country.setOwner(ac.getPlayer());
        country.setArmy(1);
        ac.addHighlightedCountry(country);

        updateViewLogs(player+" claimed "+country+"\n");
    }
    /**
     * Method that performs the Deploy action
     *
     * @param  player the Player object that is doing the action
     * @param destinationCountry the country the troops are being sent.
     * @param troopsToDeploy int that represent the amount of army units to move.
     */
    public void deploy(Player player, Country destinationCountry, int troopsToDeploy){
        playSound("resources/spurs.wav",troopsToDeploy,Math.max(20,-2*troopsToDeploy+100));

        player.removeTroops(troopsToDeploy);
        destinationCountry.addArmy(troopsToDeploy);

        updateViewLogs(player+" deployed "+troopsToDeploy+" troops to "+destinationCountry+"\n");
    }
    /**
     * This method is the attack phase controller for the game of risk
     * @param attackingCountry The attacking country
     * @param defendingCountry The defending country
     * @param unitsToAttack number of attackers from the attacking country
     */
    public void attack(Country attackingCountry, Country defendingCountry, int unitsToAttack, int unitsToDefend){
        playSound("resources/gun.wav");
        playSound("resources/fire.wav");
        playSound("resources/grenade.wav");
        playSound("resources/punch.wav");

        Player attacker=attackingCountry.getOwner();
        Player defender=defendingCountry.getOwner();

        int defendingArmy = unitsToDefend;
        int attackingArmy = unitsToAttack;


        ArrayList<Integer> rollsAttackerMade = new ArrayList<>();
        ArrayList<Integer> rollsDefenderMade = new ArrayList<>();


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
        while(!attackersQueue.isEmpty()){
                rollsAttackerMade.add(attackersQueue.remove());
            }
        while(!defenderQueue.isEmpty()){
            rollsDefenderMade.add(defenderQueue.remove());
        }

        //Send dice rolls
        ac.setDiceRolls(new Integer[][]{rollsAttackerMade.toArray(new Integer[0]), rollsDefenderMade.toArray(new Integer[0])});

        ac.setSrcArmyDead(unitsToAttack - attackingArmy);
        ac.setDstArmy(unitsToDefend);
        ac.setDstArmyDead(unitsToDefend - defendingArmy);

        //Attacker wins
        if(defendingCountry.getArmy()-ac.getDstArmyDead()==0) {
            defendingCountry.removeArmy(unitsToDefend);
            attackingCountry.removeArmy(ac.getSrcArmyDead());
            defendingCountry.getOwner().removeCountry(defendingCountry);
            attackingCountry.getOwner().addCountry(defendingCountry);
            defendingCountry.setOwner(attackingCountry.getOwner());
            attackingCountry.removeArmy(attackingArmy);
            defendingCountry.setArmy(attackingArmy);
            ac.setAttackerVictory(true);
        }
        //Attacker lost
        else{
            attackingCountry.removeArmy(ac.getSrcArmyDead());
            defendingCountry.removeArmy(ac.getDstArmyDead());
            ac.setAttackerVictory(false);
        }



        updateViewLogs(attacker+" lost "+ac.getSrcArmyDead()+" troops\n"+defender+" lost "+ac.getDstArmyDead()+" troops\n");
    }
    /**
     * Method to process a successful attack when sending units back to the attacking country
     *  @param attackingCountry the Country of the attacking player
     * @param defendingCountry the defending country that has lost
     * @param unitsToRetreat the number of units to send to the attacking country
     */
    public void retreat(Country attackingCountry, Country defendingCountry, int unitsToRetreat){
        playSound("resources/spurs.wav");

        defendingCountry.removeArmy(unitsToRetreat);
        attackingCountry.addArmy(unitsToRetreat);
    }
    /**
     * Method that performs the fortification action
     *  @param sourceCountry the source Country
     * @param destinationCountry the country the troops are being sent.
     * @param unitsToSend int that represent the amount of army units to move.
     */
    public void fortify(Country sourceCountry, Country destinationCountry, int unitsToSend){
        playSound("resources/cough.wav");

        sourceCountry.removeArmy(unitsToSend);
        destinationCountry.addArmy(unitsToSend);

        updateViewLogs(sourceCountry+" sent "+unitsToSend+" troops to "+destinationCountry);
    }

    public void importMap(String filename){
        map=Map.Import(filename);

        for(RiskView view: views){
            view.updateMap(map);
        }

        ac=new ActionContext(Phase.NUM_HUMANS,null);
        updateViews(ac);
    }
    public void exportGame(String filename){
        boolean succeeded;
        if(
            ac.getPhase()==Phase.NUM_HUMANS||
            ac.getPhase()==Phase.NUM_AI||
            ac.getPhase()==Phase.CLAIM_COUNTRY||
            ac.getPhase()==Phase.INITIAL_DEPLOY_DST||
            ac.getPhase()==Phase.INITIAL_DEPLOY_NUM_TROOPS||
            ac.getPhase()==Phase.INITIAL_DEPLOY_CONFIRM
        ){
            updateViewLogs("Saving to \"" + filename + "\" Failed\n");
            return;
        }

        succeeded = ModelSaveLoad.Save(this,filename);
        updateMapFiles();
        updateSaveFiles();
        if (succeeded){
            updateViewLogs("Game saved to \""+filename+"\"successfully\n");
        }
        else {
            updateViewLogs("Saving to \"" + filename + "\" Failed\n");
        }
    }
    public void importGame(String filename){
        ModelSaveLoad.Load(this,filename);
        updateViews(ac);
    }
    public void addView(RiskView view){
        views.add(view);
        updateViews(ac);
        updateMapFiles();
        updateSaveFiles();
    }
    public void playSound(String filename){
        try{
            AudioInputStream audioStream=AudioSystem.getAudioInputStream(new File(filename));

            Clip clip=AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void playSound(String filename, int loops, int delay){
        Runnable runnable=()->{
            for(int i=0;i<loops;i++){
                playSound(filename);
                try{
                    Thread.sleep(delay);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        };
        new Thread(runnable).start();
    }

    /**************************************************  GETTERS  **************************************************/
    public Map getMap() {
        return map;
    }
    public Country[] getCountries(){
        return map.getCountries();
    }

    public static void main(String[] args){
        RiskModel game=new RiskModel();
        game.importMap("maps/demo.zip");
        game.addView(new RiskGUI(game,game.map));

    }
}