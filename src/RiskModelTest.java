import junit.framework.TestCase;


import java.awt.*;
import java.util.ArrayList;

import static org.junit.Assert.*;
//Tests applicable for milestone 2




public class RiskModelTest extends TestCase {

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
        RiskModel test = new RiskModel();
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
        RiskModel test = new RiskModel();
        for(int i = 0; i < test.countries.length; i++){
            assertTrue(test.countries[i].containsPoint(getValidPoint(test.countries[i])));
        }

    }

    /***
     * Tests that clicking on the space designated as alberta will select the correct country and change the phase to
     * waiting for destination country.
     */
    public void testClickingSourceCountry() {
        RiskModel test = new RiskModel();
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
        RiskModel test = new RiskModel();
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
        RiskModel test = new RiskModel();
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
        RiskModel test = new RiskModel();
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

        RiskModel test = new RiskModel();
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
        RiskModel test = new RiskModel();
        assertEquals(test.players[0],test.actionContext.player);
        test.menuSkip();
        assertEquals(test.players[1],test.actionContext.player);
    }

    /**
     * skips through whole list of players.
     */
    public void testSkipButtonCycle(){
        RiskModel test = new RiskModel();
        assertEquals(test.players[0],test.actionContext.player);
        for(int i = 0; i < test.players.length; i++) {
            test.menuSkip();
        }

        assertEquals(test.players[0],test.actionContext.player);
    }








}