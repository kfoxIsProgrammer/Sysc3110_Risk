import junit.framework.TestCase;


import java.awt.*;
import java.util.ArrayList;
import java.util.Stack;

import static org.junit.Assert.*;
//Tests applicable for milestone 2




public class RiskModelTest extends TestCase {

    String[] twoPlayers = {"Jon", "joey"};
    String[] sixPlayers = {"Jon", "joey","Ross","Chandler","Rogers","test"};

    /***
     * Tests every point in between the x and y min max values to find a valid point to send back
     * @param country Country object that is tested
     * @return Point that is contained in the param country
     */
    public Point getValidPoint(Country country){
       Boolean isValidPoint = false;
       Point toTest;
       //[0] = minX, [1] = maxX, [2] = min Y, [4] = max Y
       int[] arrayofMinMaxValues = country.getMinMaxValues();
       for(int x = arrayofMinMaxValues[0]; x < arrayofMinMaxValues[1]; x++){
           for(int y = arrayofMinMaxValues[2]; y < arrayofMinMaxValues[3]; y++){
               toTest = new Point(x,y);
               if(country.containsPoint(toTest)){
                   return toTest;
               }


           }
       }
       return null;
   }

    /***
     *  Checks all the countries owned by the passed in user, and chooses any country with an army over 1 and with a
     *  adjacent country that is owned by an enemy so that it can attack.
     * @param playerOwnedCountries
     * @param currentUser
     * @return A valid Country object
     */
    public Country getValidSrcCountryforAttack(ArrayList<Country> playerOwnedCountries, Player currentUser){
        Country ValidSrcCountry = null;
        boolean thereIsAnEnemyAdjacentCountry;
        for(Country count: playerOwnedCountries){
            thereIsAnEnemyAdjacentCountry = false;
            Country[] arrayofadjacentCountries = count.getAdjacentCountries();
            for(int i = 0; i < arrayofadjacentCountries.length; i++){
                if (arrayofadjacentCountries[i].getOwner() != currentUser){
                    thereIsAnEnemyAdjacentCountry = true;
                }
            }
            if(count.getArmy() >1 && thereIsAnEnemyAdjacentCountry){
                ValidSrcCountry = count;
                break;
            }
        }
        return(ValidSrcCountry);
    }

    /***
     * Checks all the adjacent countries of the passed in Country count and finds one that is not owned by the user.
     * @param count Passed in Country
     * @param currentUser user that the passed in country belongs too.
     * @return
     */
    public Country getValidDstCountryForAttack(Country count, Player currentUser){
        Country validDstCountry = null;
        for(int i = 0; i < count.getAdjacentCountries().length; i++){
            if (count.getAdjacentCountries()[i].getOwner() != currentUser){
                validDstCountry= count.getAdjacentCountries()[i];
                break;
            }
        }
        return validDstCountry;
    }


    @org.junit.Test


/***
 * Tests that the correct map is being loaded in on startup by measuring country and continent array size.
 */
    public void testInitialDefaultMap() {
        RiskModel test = new RiskModel(twoPlayers);
        assertEquals(42, test.countries.length);
        assertEquals(6,test.continents.length);
        assertNotEquals(null, test.riskController);

        assertEquals(Phase.ATTACK_SRC,test.actionContext.phase);
    }

    /***
     * Confirms that for every country in the game there is at least one valid point that can be clicked in order to
     * access  it.
     */
    public void testEveryCountryIsClickable(){
        RiskModel test = new RiskModel(twoPlayers);
        for(int i = 0; i < test.countries.length; i++){
            assertTrue(test.countries[i].containsPoint(getValidPoint(test.countries[i])));
        }

    }

    /***
     * Tests that clicking on the space designated as alberta will select the correct country and change the phase to
     * waiting for destination country.
     */
    public void testClickingSourceCountry() {
        RiskModel test = new RiskModel(twoPlayers);
        ArrayList<Country> playerOwnedCountries = new ArrayList<Country>(test.players[0].getOwnedCountries().values());
        Country sourceCountryToTest = null;
        //find a country the player owns that has enough soldiers to attack
        sourceCountryToTest= getValidSrcCountryforAttack(playerOwnedCountries, test.players[0]);
        test.mapClicked(getValidPoint(sourceCountryToTest)); //Clicks on Alberta
        assertEquals(sourceCountryToTest.getName(),test.actionContext.srcCountry.getName()); // confirms country clicked was alberta
        assertEquals(Phase.ATTACK_DST, test.actionContext.phase); //confirms phase changes to search for dst Country

    }

    /***
     * Test that after selecting a source country that clicking invalid country will change phase back to looking for
     * source country.
     */
    public void testClickingBackOutOfDstCountry() {
        RiskModel test = new RiskModel(twoPlayers);
        Country sourceCountryToTest = null;
        ArrayList<Country> playerOwnedCountries = new ArrayList<Country>(test.players[0].getOwnedCountries().values());
        sourceCountryToTest = getValidSrcCountryforAttack(playerOwnedCountries, test.players[0]);
        test.mapClicked(getValidPoint(sourceCountryToTest)); //Clicks on source Country
        assertEquals(sourceCountryToTest.getName(), test.actionContext.srcCountry.getName());
        test.menuBack(); //Clicks Back
        assertEquals(Phase.ATTACK_SRC, test.actionContext.phase);//confirms phase went back to finding src country
    }
    /***
     * Test whether it can correctly proceed to the attack_army phase by clicking on a dst country
     */
    public void testClickingDstCountry() {
        RiskModel test = new RiskModel(twoPlayers);
        Country sourceCountryToTest;


        ArrayList<Country> playerOwnedCountries = new ArrayList<Country>(test.players[0].getOwnedCountries().values());

        sourceCountryToTest = getValidSrcCountryforAttack(playerOwnedCountries, test.players[0]);
        test.mapClicked(getValidPoint(sourceCountryToTest)); //Clicks on source Country
        assertEquals(sourceCountryToTest.getName(),test.actionContext.srcCountry.getName());

        Country destinationCountryToTest = getValidDstCountryForAttack(sourceCountryToTest, test.players[0]);
        test.mapClicked(getValidPoint(destinationCountryToTest)); //Clicks on dstCountry
        assertEquals(Phase.ATTACK_ARMY, test.actionContext.phase);//confirms phase proceeded to choosing army
    }

    /***
     * Tests clicking blank pace after reaching army phase of attack
     */
    public void testClickingBackOutOfArmyCountry() {
        RiskModel test = new RiskModel(twoPlayers);
        Country sourceCountryToTest;


        ArrayList<Country> playerOwnedCountries = new ArrayList<Country>(test.players[0].getOwnedCountries().values());

        sourceCountryToTest = getValidSrcCountryforAttack(playerOwnedCountries, test.players[0]);
        test.mapClicked(getValidPoint(sourceCountryToTest)); //Clicks on source Country
        assertEquals(sourceCountryToTest.getName(),test.actionContext.srcCountry.getName());

        Country destinationCountryToTest = getValidDstCountryForAttack(sourceCountryToTest, test.players[0]);
        test.mapClicked(getValidPoint(destinationCountryToTest)); //Clicks on dstCountry


        test.menuBack(); //Clicks Back
        assertEquals(Phase.ATTACK_SRC, test.actionContext.phase);//confirms phase went back to choosing src country
    }

    /***
     * Test that the correct phase is reached after selecting units.
     */
    public void testSelectingArmySize() {

        RiskModel test = new RiskModel(twoPlayers);
        Country sourceCountryToTest;


        ArrayList<Country> playerOwnedCountries = new ArrayList<Country>(test.players[0].getOwnedCountries().values());

        sourceCountryToTest = getValidSrcCountryforAttack(playerOwnedCountries, test.players[0]);
        test.mapClicked(getValidPoint(sourceCountryToTest)); //Clicks on source Country
        assertEquals(sourceCountryToTest.getName(),test.actionContext.srcCountry.getName());

        Country destinationCountryToTest = getValidDstCountryForAttack(sourceCountryToTest, test.players[0]);
        test.mapClicked(getValidPoint(destinationCountryToTest)); //Clicks on dstCountry

        test.menuNumTroops(1); //Clicks number of troops}
        assertEquals(Phase.ATTACK_CONFIRM, test.actionContext.phase);//confirms phase went back to choosing src country
    }

    /***
     * Test the functionality of the skip button
     */
    public void testSkipButton(){
        RiskModel test = new RiskModel(twoPlayers);
        assertEquals(test.players[0],test.actionContext.player);
        test.menuSkip();
        assertEquals(test.players[1],test.actionContext.player);
    }

    /**
     * skips through whole list of players.
     */
    public void testSkipButtonCycle(){
        RiskModel test = new RiskModel(twoPlayers);
        assertEquals(test.players[0],test.actionContext.player);
        for(int i = 0; i < test.players.length; i++) {
            test.menuSkip();
        }

        assertEquals(test.players[0],test.actionContext.player);
    }

    /***
     * Skips through an entire cycle of turns of 6 players
     */
    public void testSkipSixButtonCycle(){
        RiskModel test = new RiskModel(sixPlayers);
        assertEquals(test.players[0],test.actionContext.player);
        for(int i = 0; i < test.players.length; i++) {
            test.menuSkip();
        }

        assertEquals(test.players[0],test.actionContext.player);
    }

    /***
     * makes sure skipping works from attack_dst phase
     */
    public void testSkipInAttackDstPhase(){
        RiskModel test = new RiskModel(twoPlayers);
        ArrayList<Country> playerOwnedCountries = new ArrayList<Country>(test.players[0].getOwnedCountries().values());
        Country sourceCountryToTest = null;
        //find a country the player owns that has enough soldiers to attack
        sourceCountryToTest= getValidSrcCountryforAttack(playerOwnedCountries, test.players[0]);
        test.mapClicked(getValidPoint(sourceCountryToTest)); //Clicks on src Country
        assertEquals(sourceCountryToTest.getName(),test.actionContext.srcCountry.getName()); // confirms country
        assertEquals(Phase.ATTACK_DST, test.actionContext.phase); //confirms phase changes to search for dst Country
        test.menuSkip();
        assertEquals(test.players[1],test.actionContext.player);
        assertEquals(Phase.ATTACK_SRC, test.actionContext.phase);
    }

    /***
     * Tests that the array of country objects in model is in the right order for the default map
     */
    public void testCountriesArrayOrder(){
        RiskModel test = new RiskModel(twoPlayers);

        String[] toTest = {"Alaska","Alberta","Central America","Eastern United States","Greenland","Northwest Territory",
                "Ontario","Quebec","Western United States","Argentina","Brazil","Peru","Venezuela","Great Britain","Iceland",
                "Northern Europe","Scandinavia","Southern Europe","Ukraine","Western Europe","Congo","East Africa","Egypt",
                "Madagascar","North Africa","South Africa","Afghanistan","China","India","Irkutsk","Japan","Kamchatka","Middle East","Mongolia","Siam","Siberia","Ural",
                "Yakutsk","Eastern Australia","Indonesia","New Guinea","Western Australia"};
        for(int i = 0; i < toTest.length; i++){
            assertEquals(toTest[i],test.countries[i].getName());
        }
        // Not a big deal if fails, just should be fixed in order to help with testing
    }

    /***
     * Test that forfeit with only 2 players will lead to a gameover
     */
    public void testTwoPlayerForfeit(){
        RiskModel test = new RiskModel(twoPlayers);
        test.playerForfeit();
        assertEquals(Phase.FORFEIT_CLICKED,test.actionContext.phase);
        test.playerForfeit();
        assertEquals(Phase.GAME_OVER,test.actionContext.phase);
    }

    /***
     * Tests that a forfeit with 6 players will cause the game to continue without the forfeited player
     */
    public void testSixPlayerOneForfeit(){
        RiskModel test = new RiskModel(sixPlayers);
        test.playerForfeit();
        assertEquals(Phase.FORFEIT_CLICKED,test.actionContext.phase);
        test.playerForfeit();
        assertEquals(Phase.ATTACK_SRC,test.actionContext.phase);
        assertEquals(test.players[1], test.actionContext.player);
        for(int i = 0; i<5; i++){test.menuSkip();}
        assertEquals(test.players[1], test.actionContext.player);

    }

    /***
     * Tests the getConnected method returns the correct countries
     */
    public void testGetConnectedCountries(){
        RiskModel test = new RiskModel(twoPlayers);
        Stack <Country> toTest = new Stack();
        test.players[0].getOwnedCountries().clear();
        test.players[1].getOwnedCountries().clear();
        for(int i = 0; i < test.countries.length; i++){
            test.countries[i].setOwner(null);
        }
        test.players[0].addCountry(test.countries[0]);//add Alaska
        test.countries[0].setOwner(test.players[0]);

        test.players[0].addCountry(test.countries[1]);//add alberta
        test.countries[1].setOwner(test.players[0]);

        test.players[0].addCountry(test.countries[5]);//add NorthWest Territory
        test.countries[5].setOwner(test.players[0]);

        test.players[0].addCountry(test.countries[6]);//add Ontario
        test.countries[6].setOwner(test.players[0]);

        test.players[0].addCountry(test.countries[7]);//add Quebec
        test.countries[7].setOwner(test.players[0]);

        test.players[0].addCountry(test.countries[8]); // add western United states
        test.countries[8].setOwner(test.players[0]);

        test.players[0].addCountry(test.countries[3]); // add Eastern united states
        test.countries[3].setOwner(test.players[0]);

        test.players[0].addCountry(test.countries[27]);//add china
        test.countries[27].setOwner(test.players[0]);
        toTest = test.getConnectedOwnedCountries(test.countries[1], test.countries[1],test.players[0],toTest);
        //should not include china
        assertEquals(6, toTest.size());
        assertTrue(toTest.contains(test.countries[6]));
        assertTrue(toTest.contains(test.countries[7]));
        assertTrue(toTest.contains(test.countries[5]));
        assertTrue(toTest.contains(test.countries[0]));
        assertTrue(toTest.contains(test.countries[8]));
        assertTrue(toTest.contains(test.countries[3]));












    }











}