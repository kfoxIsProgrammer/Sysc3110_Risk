import junit.framework.TestCase;

import java.awt.*;

import static org.junit.Assert.*;
//Tests applicable for milestone 2


public class RiskModelTest extends TestCase {
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
     * Tests that clicking on the space designated as alberta will select the correct country and change the phase to
     * waiting for destination country.
     */
    public void testClickingSourceCountry() {
        RiskModel test = new RiskModel();
        test.mapClicked(new Point(100,75)); //Clicks on Alberta
        assertEquals(Phase.ATTACK_DST, test.actionContext.phase); //confirms phase changes to search for dst Country
        assertEquals("Alberta",test.actionContext.srcCountry.getName()); // confirms country clicked was alberta
    }

    /***
     * Test that after selecting a source country that clicking invalid country will change phase back to looking for
     * source country.
     */
    public void testClickingOutOfDstCountry() {
        RiskModel test = new RiskModel();
        test.mapClicked(new Point(295,25)); //Clicks on Green Land
        assertEquals("Greenland", test.actionContext.srcCountry.getName());
        test.mapClicked(new Point(250,169)); //Clicks out of country onto blank space
        assertEquals(Phase.ATTACK_SRC, test.actionContext.phase);//confirms phase went back to finding src country
    }
    /***
     * Test whether it can correctly proceed to the attack_army phase by clicking on a dst country
     */
    public void testClickingDstCountry() {
        RiskModel test = new RiskModel();
        test.mapClicked(new Point(232,265)); //Clicks on Brazil
        assertEquals("Brazil",test.actionContext.srcCountry.getName());
        test.mapClicked(new Point(360,180)); //Clicks on North Africa
        assertEquals("North Africa", test.actionContext.dstCountry.getName());
        assertEquals(Phase.ATTACK_ARMY, test.actionContext.phase);//confirms phase proceeded to choosing army
    }

    /***
     * Tests clicking blank pace after reaching army phase of attack
     */
    public void testClickingOutOfArmyCountry() {
        RiskModel test = new RiskModel();
        test.mapClicked(new Point(356,79)); //Clicks on Great Britain
        assertEquals("Great Britain",test.actionContext.srcCountry.getName());
        test.mapClicked(new Point(332,49)); //Clicks on North Africa
        assertEquals("Iceland", test.actionContext.dstCountry.getName());
        test.mapClicked(new Point(22,387)); //Clicks blank space
        assertEquals(Phase.ATTACK_SRC, test.actionContext.phase);//confirms phase went back to choosing src country
    }
    public void testSelectingArmySize() {
        RiskModel test = new RiskModel();
        test.mapClicked(new Point(232,265)); //Clicks on Brazil
        assertEquals("Brazil",test.actionContext.srcCountry.getName());
        test.mapClicked(new Point(360,180)); //Clicks on North Africa
        assertEquals("North Africa", test.actionContext.dstCountry.getName());
        assertEquals(Phase.ATTACK_ARMY, test.actionContext.phase);//confirms phase proceeded to choosing army
        test.menuNumTroops(4);
        assertEquals(4, test.actionContext.srcArmy);
        assertEquals(Phase.ATTACK_CONFIRM,test.actionContext.phase);
    }



}