import junit.framework.TestCase;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
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
    public Country getValidSrcCountryForAttack(Country[] playerOwnedCountries, Player currentUser){
        Country ValidSrcCountry = null;
        boolean thereIsAnEnemyAdjacentCountry;
        for(Country coucountry: playerOwnedCountries){
            thereIsAnEnemyAdjacentCountry = false;
            Country[] adjacentCountries = coucountry.getAdjacentCountries();
            for(int i = 0; i < adjacentCountries.length; i++){
                if (adjacentCountries[i].getOwner() != currentUser){
                    thereIsAnEnemyAdjacentCountry = true;
                }
            }
            if(coucountry.getArmy() >1 && thereIsAnEnemyAdjacentCountry){
                ValidSrcCountry = coucountry;
                break;
            }
        }
        return(ValidSrcCountry);
    }
    public void setCountriesToPlayer(RiskModel model, int user, int[] countryIndexes){
        for(int index : countryIndexes){
            model.players[user].addCountry(model.map.getCountries()[index]);
            model.map.getCountries()[index].setOwner(model.players[user]);

        }
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

        assertEquals(Phase.DEPLOY_DST, test.ac.getPhase());
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
    public void testGetConnectedOwnedCountries(){
        RiskModel test = new RiskModel(twoPlayers);
        for(int i = 0; i < test.map.getCountries().length; i++){
            test.map.getCountries()[i].setOwner(null);
        }
        test.players[0].countryIndexes = new ArrayList<Integer>();
        test.players[1].countryIndexes = new ArrayList<Integer>();
        int[] countries = {0,1,5,6,7,8,3,27};
        setCountriesToPlayer(test, 0,countries);

        ArrayList<Country> toTest = new ArrayList<>();
        Collections.addAll(toTest,test.map.getCountries()[1].getConnectedOwnedCountries(test.players[0]));
        //should not include china
        assertEquals(6, toTest.size());
        assertTrue(toTest.contains(test.map.getCountries()[6]));
        assertTrue(toTest.contains(test.map.getCountries()[7]));
        assertTrue(toTest.contains(test.map.getCountries()[5]));
        assertTrue(toTest.contains(test.map.getCountries()[0]));
        assertTrue(toTest.contains(test.map.getCountries()[8]));
        assertTrue(toTest.contains(test.map.getCountries()[3]));
    }
    public void testGetConnectedCountriesThatReturnsNone(){
        RiskModel test = new RiskModel(twoPlayers);
        for(int i = 0; i < test.map.getCountries().length; i++){
            test.map.getCountries()[i].setOwner(null);
        }
        test.players[0].setCountries(new Country[8]);
        test.players[1].setCountries(new Country[0]);
        int[] countries = {41,1,40,39,39,37,36,35};
        setCountriesToPlayer(test, 0,countries);
        ArrayList<Country> toTest = new ArrayList<>();
        Collections.addAll(toTest,test.map.getCountries()[1].getConnectedOwnedCountries(test.players[0]));
        //should not include china
        assertEquals(0, toTest.size());

    }

    public void testAllocateBonusUnits() {
        RiskModel test = new RiskModel(twoPlayers);
        for(int i = 0; i < test.map.getCountries().length; i++){
            test.map.getCountries()[i].setOwner(null);
        }
        test.players[0].countryIndexes = new ArrayList<Integer>();
        test.players[1].countryIndexes = new ArrayList<Integer>();
        int[] countries = {0,1,2,3,4,5,6,7,8};
        setCountriesToPlayer(test,0,countries);
        test.players[0].removeTroops(test.players[0].troopsToDeploy);
        test.allocateBonusTroops(test.players[0]);
        assertEquals(8,test.players[0].getTroopsToDeploy());
    }
    public void testAttackMethod(){
        RiskModel test = new RiskModel(twoPlayers);
        for(int i = 0; i < test.map.getCountries().length; i++){
            test.map.getCountries()[i].setOwner(null);
        }
        test.players[0].countryIndexes = new ArrayList<Integer>();
        test.players[1].countryIndexes = new ArrayList<Integer>();
        setCountriesToPlayer(test, 0, new int[]{0,7});
        setCountriesToPlayer(test, 1, new int[]{5,3});
        assertEquals(test.players[0],test.map.getCountries()[7].getOwner());
        test.getCountries()[0].setArmy(5);
        test.getCountries()[5].setArmy(5);
    }
    public void testDeployMethod(){
        RiskModel test = new RiskModel(twoPlayers);
        for(int i = 0; i < test.map.getCountries().length; i++){
            test.map.getCountries()[i].setOwner(null);
        }
        test.players[0].countryIndexes = new ArrayList<Integer>();
        test.players[1].countryIndexes = new ArrayList<Integer>();
        setCountriesToPlayer(test, 0, new int[]{0});
        test.players[0].removeTroops(test.players[0].troopsToDeploy);
        test.players[0].troopsToDeploy=5;
        test.getCountries()[0].setArmy(0);
        test.deploy(test.players[0],test.getCountries()[0],test.players[0].troopsToDeploy);
        assertEquals(5, test.getCountries()[0].getArmy());
    }
    public void testFortifyMethod(){
        RiskModel test = new RiskModel(twoPlayers);
        for(int i = 0; i < test.map.getCountries().length; i++){
            test.map.getCountries()[i].setOwner(null);
        }
        test.players[0].countryIndexes = new ArrayList<Integer>();
        test.players[1].countryIndexes = new ArrayList<Integer>();
        setCountriesToPlayer(test, 0, new int[]{0,5,1,6,8,22});
        test.getCountries()[0].setArmy(10);
        test.getCountries()[8].setArmy(0);
        test.fortify(test.getCountries()[0],test.getCountries()[8],9);
        assertEquals(9,test.getCountries()[8].getArmy());
    }
    public void testFortifyWrongMethod(){
        RiskModel test = new RiskModel(twoPlayers);
        for(int i = 0; i < test.map.getCountries().length; i++){
            test.map.getCountries()[i].setOwner(null);
        }
        test.players[0].countryIndexes = new ArrayList<Integer>();
        test.players[1].countryIndexes = new ArrayList<Integer>();
        setCountriesToPlayer(test, 0, new int[]{0,5,1,6,8,22});
        test.getCountries()[0].setArmy(10);
        test.getCountries()[22].setArmy(0);
        assertEquals(0,test.getCountries()[22].getArmy());
    }
    public void testExportImportMethod(){
        RiskModel test = new RiskModel(sixPlayers);
        for(int x = 0; x < test.map.getCountries().length; x ++) {
            test.map.getCountries()[x].setArmy(x + 1);
        }

        test.exportGame("Save");
        RiskModel imported = new RiskModel();
        imported.importMap("maps/demo.zip");
        imported.importGame("saves/Save.RiskGame");
        //checking players
        for(int x = 0; x < test.players.length; x++) {
            assertEquals(test.players[x].name, imported.players[x].name);
            assertEquals(test.players[x].troopsToDeploy, imported.players[x].troopsToDeploy);
            assertEquals(test.players[x].countryIndexes, imported.players[x].countryIndexes);
            assertEquals(test.players[x].isAI, imported.players[x].isAI);
            assertEquals(test.players[x].hasLost, imported.players[x].hasLost);
        }
        for(int x = 0; x < test.getCountries().length; x ++){
            System.out.println(test.getCountries()[x].getOwner().getName() +" : "+imported.getCountries()[x].getOwner().getName());
        }
        for(int x = 0; x < test.map.getCountries().length; x ++ ){
            assertEquals(test.map.getCountries()[x].getArmy(), imported.map.getCountries()[x].getArmy());
            assertEquals(test.getCountries()[x].getOwner().name, imported.getCountries()[x].getOwner().name);
        }
        assertEquals(test.ac.getPlayer().getName(),imported.ac.getPlayer().getName());
        assertEquals(test.ac.getPhase(),imported.ac.getPhase());
    }
}
