import junit.framework.TestCase;

import java.awt.*;
import java.util.ArrayList;
import java.util.Stack;

import static org.junit.Assert.*;
//Tests applicable for milestone 2


/**
 * Tests the risk model
 *
 * PRESS ENTER WHEN ANY POPUP POPS UP
 *
 * @author Dimitry Kouthine
 * @version 11.09.2020
 */
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
        assertEquals(42, test.map.getCountries().length);
        assertEquals(6,test.map.getContinents().length);
        assertNotEquals(null, test.riskController);

        assertEquals(Phase.ATTACK_SRC, test.actionContext.getPhase());
    }

    /**
     * Confirms that for every country in the game there is at least one valid point that can be clicked in order to
     * access  it.
     */
    public void testEveryCountryIsClickable(){
        RiskModel test = new RiskModel(twoPlayers);
        for(int i = 0; i < test.map.getCountries().length; i++){
            assertTrue(test.map.getCountries()[i].containsPoint(getValidPoint(test.map.getCountries()[i])));
        }

    }

    /***
     * Tests that clicking on the space designated as alberta will select the correct country and change the phase to
     * waiting for destination country.
     */
    public void testClickingSourceCountry() {
        RiskModel test = new RiskModel(twoPlayers);
        ArrayList<Country> playerOwnedCountries = test.players[0].getCountries();
        Country sourceCountryToTest = null;
        //find a country the player owns that has enough soldiers to attack
        sourceCountryToTest= getValidSrcCountryforAttack(playerOwnedCountries, test.players[0]);
        test.mapClicked(getValidPoint(sourceCountryToTest)); //Clicks on Alberta
        assertEquals(sourceCountryToTest.getName(), test.actionContext.getSrcCountry().getName()); // confirms country clicked was alberta
        assertEquals(Phase.ATTACK_DST, test.actionContext.getPhase()); //confirms phase changes to search for dst Country

    }

    /***
     * Test that after selecting a source country that clicking invalid country will change phase back to looking for
     * source country.
     */
    public void
    testClickingBackOutOfDstCountry() {
        RiskModel test = new RiskModel(twoPlayers);
        Country sourceCountryToTest = null;
        ArrayList<Country> playerOwnedCountries = test.players[0].getCountries();
        sourceCountryToTest = getValidSrcCountryforAttack(playerOwnedCountries, test.players[0]);
        test.mapClicked(getValidPoint(sourceCountryToTest)); //Clicks on source Country
        assertEquals(sourceCountryToTest.getName(), test.actionContext.getSrcCountry().getName());
        test.menuBack(); //Clicks Back
        assertEquals(Phase.ATTACK_SRC, test.actionContext.getPhase());//confirms phase went back to finding src country
    }
    /***
     * Test whether it can correctly proceed to the attack_army phase by clicking on a dst country
     */
    public void testClickingDstCountry() {
        RiskModel test = new RiskModel(twoPlayers);
        Country sourceCountryToTest;


        ArrayList<Country> playerOwnedCountries = test.players[0].getCountries();

        sourceCountryToTest = getValidSrcCountryforAttack(playerOwnedCountries, test.players[0]);
        test.mapClicked(getValidPoint(sourceCountryToTest)); //Clicks on source Country
        assertEquals(sourceCountryToTest.getName(), test.actionContext.getSrcCountry().getName());

        Country destinationCountryToTest = getValidDstCountryForAttack(sourceCountryToTest, test.players[0]);
        test.mapClicked(getValidPoint(destinationCountryToTest)); //Clicks on dstCountry
        assertEquals(Phase.ATTACK_ARMY, test.actionContext.getPhase());//confirms phase proceeded to choosing army
    }

    /***
     * Tests clicking blank pace after reaching army phase of attack
     */
    public void testClickingBackOutOfArmyCountry() {
        RiskModel test = new RiskModel(twoPlayers);
        Country sourceCountryToTest;


        ArrayList<Country> playerOwnedCountries = test.players[0].getCountries();

        sourceCountryToTest = getValidSrcCountryforAttack(playerOwnedCountries, test.players[0]);
        test.mapClicked(getValidPoint(sourceCountryToTest)); //Clicks on source Country
        assertEquals(sourceCountryToTest.getName(), test.actionContext.getSrcCountry().getName());

        Country destinationCountryToTest = getValidDstCountryForAttack(sourceCountryToTest, test.players[0]);
        test.mapClicked(getValidPoint(destinationCountryToTest)); //Clicks on dstCountry


        test.menuBack(); //Clicks Back
        assertEquals(Phase.ATTACK_SRC, test.actionContext.getPhase());//confirms phase went back to choosing src country
    }

    /***
     * Test that the correct phase is reached after selecting units.
     */
    public void testSelectingArmySize() {

        RiskModel test = new RiskModel(twoPlayers);
        Country sourceCountryToTest;


        ArrayList<Country> playerOwnedCountries = test.players[0].getCountries();

        sourceCountryToTest = getValidSrcCountryforAttack(playerOwnedCountries, test.players[0]);
        test.mapClicked(getValidPoint(sourceCountryToTest)); //Clicks on source Country
        assertEquals(sourceCountryToTest.getName(), test.actionContext.getSrcCountry().getName());

        Country destinationCountryToTest = getValidDstCountryForAttack(sourceCountryToTest, test.players[0]);
        test.mapClicked(getValidPoint(destinationCountryToTest)); //Clicks on dstCountry

        test.menuNumTroops(1); //Clicks number of troops}
        assertEquals(Phase.RETREAT_ARMY, test.actionContext.getPhase());//confirms phase went back to choosing src country
    }

    /***
     * Test the functionality of the skip button
     */
    public void testSkipButton(){
        RiskModel test = new RiskModel(twoPlayers);
        assertEquals(test.players[0], test.actionContext.getPlayer());
        test.menuSkip();
        assertEquals(test.players[1], test.actionContext.getPlayer());
    }

    /**
     * skips through whole list of players.
     */
    public void testSkipButtonCycle(){
        RiskModel test = new RiskModel(twoPlayers);
        assertEquals(test.players[0], test.actionContext.getPlayer());
        for(int i = 0; i < test.players.length; i++) {
            test.menuSkip();
        }

        assertEquals(test.players[0], test.actionContext.getPlayer());
    }

    /***
     * Skips through an entire cycle of turns of 6 players
     */
    public void testSkipSixButtonCycle(){
        RiskModel test = new RiskModel(sixPlayers);
        assertEquals(test.players[0], test.actionContext.getPlayer());
        for(int i = 0; i < test.players.length; i++) {
            test.menuSkip();
        }

        assertEquals(test.players[0], test.actionContext.getPlayer());
    }

    /***
     * makes sure skipping works from attack_dst phase
     */
    public void testSkipInAttackDstPhase(){
        RiskModel test = new RiskModel(twoPlayers);
        ArrayList<Country> playerOwnedCountries = test.players[0].getCountries();
        Country sourceCountryToTest = null;
        //find a country the player owns that has enough soldiers to attack
        sourceCountryToTest= getValidSrcCountryforAttack(playerOwnedCountries, test.players[0]);
        test.mapClicked(getValidPoint(sourceCountryToTest)); //Clicks on src Country
        assertEquals(sourceCountryToTest.getName(), test.actionContext.getSrcCountry().getName()); // confirms country
        assertEquals(Phase.ATTACK_DST, test.actionContext.getPhase()); //confirms phase changes to search for dst Country
        test.menuSkip();
        assertEquals(test.players[1], test.actionContext.getPlayer());
        assertEquals(Phase.ATTACK_SRC, test.actionContext.getPhase());
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
            assertEquals(toTest[i],test.map.getCountries()[i].getName());
        }
        // Not a big deal if fails, just should be fixed in order to help with testing
    }

    /***
     * Tests the getConnected method returns the correct countries
     */
    public void testGetConnectedCountries(){
        RiskModel test = new RiskModel(twoPlayers);
        Stack<Country> toTest = new Stack<>();
        test.players[0].getCountries().clear();
        test.players[1].getCountries().clear();
        for(int i = 0; i < test.map.getCountries().length; i++){
            test.map.getCountries()[i].setOwner(null);
        }
        test.players[0].addCountry(test.map.getCountries()[0]);//add Alaska
        test.map.getCountries()[0].setOwner(test.players[0]);

        test.players[0].addCountry(test.map.getCountries()[1]);//add alberta
        test.map.getCountries()[1].setOwner(test.players[0]);

        test.players[0].addCountry(test.map.getCountries()[5]);//add NorthWest Territory
        test.map.getCountries()[5].setOwner(test.players[0]);

        test.players[0].addCountry(test.map.getCountries()[6]);//add Ontario
        test.map.getCountries()[6].setOwner(test.players[0]);

        test.players[0].addCountry(test.map.getCountries()[7]);//add Quebec
        test.map.getCountries()[7].setOwner(test.players[0]);

        test.players[0].addCountry(test.map.getCountries()[8]); // add western United states
        test.map.getCountries()[8].setOwner(test.players[0]);

        test.players[0].addCountry(test.map.getCountries()[3]); // add Eastern united states
        test.map.getCountries()[3].setOwner(test.players[0]);

        test.players[0].addCountry(test.map.getCountries()[27]);//add china
        test.map.getCountries()[27].setOwner(test.players[0]);
        toTest = test.getConnectedOwnedCountries(test.map.getCountries()[1], test.map.getCountries()[1],test.players[0],toTest);
        //should not include china
        assertEquals(6, toTest.size());
        assertTrue(toTest.contains(test.map.getCountries()[6]));
        assertTrue(toTest.contains(test.map.getCountries()[7]));
        assertTrue(toTest.contains(test.map.getCountries()[5]));
        assertTrue(toTest.contains(test.map.getCountries()[0]));
        assertTrue(toTest.contains(test.map.getCountries()[8]));
        assertTrue(toTest.contains(test.map.getCountries()[3]));
    }
    /***
     * Should not have any country connected to Alberta
     */
    public void testGetConnectedCountriesThatReturnsnone(){
        RiskModel test = new RiskModel(twoPlayers);
        Stack <Country> toTest = new Stack();
        test.players[0].getCountries().clear();
        test.players[1].getCountries().clear();
        for(int i = 0; i < test.map.getCountries().length; i++){
            test.map.getCountries()[i].setOwner(null);
        }
        test.players[0].addCountry(test.map.getCountries()[41]);//add Western australia
        test.map.getCountries()[41].setOwner(test.players[0]);

        test.players[0].addCountry(test.map.getCountries()[1]);//add alberta
        test.map.getCountries()[1].setOwner(test.players[0]);

        test.players[0].addCountry(test.map.getCountries()[40]);//add New Guinea
        test.map.getCountries()[40].setOwner(test.players[0]);

        test.players[0].addCountry(test.map.getCountries()[39]);//add Indonesia
        test.map.getCountries()[39].setOwner(test.players[0]);

        test.players[0].addCountry(test.map.getCountries()[38]);//add Eastern Australia
        test.map.getCountries()[38].setOwner(test.players[0]);

        test.players[0].addCountry(test.map.getCountries()[37]); // add western Yakutsk
        test.map.getCountries()[37].setOwner(test.players[0]);

        test.players[0].addCountry(test.map.getCountries()[36]); // add Ural
        test.map.getCountries()[36].setOwner(test.players[0]);

        test.players[0].addCountry(test.map.getCountries()[35]);//add siberia
        test.map.getCountries()[35].setOwner(test.players[0]);
        toTest = test.getConnectedOwnedCountries(test.map.getCountries()[1], test.map.getCountries()[1],test.players[0],toTest);
        //should not include china
        assertEquals(0, toTest.size());

    }
    /*public void testAllocateBonusUnits() {
        RiskModel test = new RiskModel(twoPlayers);
        test.players[0].getCountries().clear();
        test.players[1].getCountries().clear();
        assertEquals(0, test.players[0].getArmiesToAllocate());
        for(int i = 0; i < test.map.getCountries().length; i++){
            test.map.getCountries()[i].setOwner(null);
        }
        for( int i  = 0; i < 9; i++){
            test.players[0].addCountry(test.map.getCountries()[i]);//add Western australia
            test.map.getCountries()[i].setOwner(test.players[0]);

        }
        assertEquals(9, test.players[0].getCountries().size());
        assertEquals(9, test.map.getContinents()[0].getCountryList().length);
        assertEquals(test.players[0].getCountries().get(0).getName(), test.map.getContinents()[0].getCountryList()[0].getName());
        System.out.println(test.players[0].countries.contains(test.map.getContinents()[0].getCountryList()[0]));

        test.allocateBonusTroops(test.players[0]);
        assertEquals(10,test.map.getContinents()[0].getBonusArmyValue());
        assertEquals(10,test.players[0].armiesToAllocate);




    }*/


    }