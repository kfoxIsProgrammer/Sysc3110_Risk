import junit.framework.TestCase;

import static org.junit.Assert.*;

public class RiskModelTest extends TestCase {
    @org.junit.Test
    public void testInitialDefaultMap() {
        RiskModel test = new RiskModel();
        assertEquals(42, test.countries.length);
        assertEquals(6,test.continents.length);
        assertNotEquals(null, test.riskController);
        assertEquals(test.actionContext.phase,Phase.ATTACK_SRC);
    }

    @org.junit.Test
    public void mapClicked() {
        RiskModel test = new RiskModel();
    }

    @org.junit.Test
    public void menuSkip() {
    }

    @org.junit.Test
    public void menuConfirm() {
    }

    @org.junit.Test
    public void menuBack() {
    }

    @org.junit.Test
    public void menuNumTroops() {
    }
}