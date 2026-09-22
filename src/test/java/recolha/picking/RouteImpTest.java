package recolha.picking;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.estg.core.AidBox;
import com.estg.pickingManagement.exceptions.RouteException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import recolha.core.AidBoxImp;
import recolha.core.Distance;

class RouteImpTest {

    private Scenario scenario;
    private RouteImp route;

    @BeforeEach
    void setUp() throws Exception {
        scenario = new Scenario();
        scenario.install(scenario.a, "CA", Scenario.CLOTHING, 100, 90);
        scenario.install(scenario.b, "CB", Scenario.CLOTHING, 100, 90);
        route = new RouteImp(scenario.vehicle("V1", Scenario.CLOTHING, 5), scenario.institution);
    }

    @Test
    void emptyRouteHasNoDistance() {
        assertEquals(0, route.getTotalDistance());
        assertEquals(0, route.getTotalDuration());
    }

    @Test
    void distanceIncludesLeavingAndReturningToBase() throws Exception {
        route.addAidBox(scenario.a);
        route.addAidBox(scenario.b);
        // Base→A (1000) + A→B (500) + B→Base (2000)
        assertEquals(3500, route.getTotalDistance());
        assertEquals(35, route.getTotalDuration());
        assertEquals(3500, route.getTotalDistance(), "repeated calls must not accumulate");
    }

    @Test
    void editingOperations() throws Exception {
        AidBoxImp c = new AidBoxImp("C", null);
        scenario.install(c, "CC", Scenario.CLOTHING, 100, 10);

        route.addAidBox(scenario.a);
        route.insertAfter(scenario.a, scenario.b);
        route.insertAfter(scenario.a, c);
        assertArrayEquals(new AidBox[] {scenario.a, c, scenario.b}, route.getRoute());

        AidBoxImp d = new AidBoxImp("D", null);
        scenario.install(d, "CD", Scenario.CLOTHING, 100, 10);
        route.replaceAidBox(c, d);
        assertEquals("D", route.getRoute()[1].getCode());

        assertEquals(scenario.a, route.removeAidBox(scenario.a));
        assertFalse(route.containsAidBox(scenario.a));
        assertTrue(route.containsAidBox(scenario.b));
    }

    @Test
    void rejectsInvalidStops() throws Exception {
        route.addAidBox(scenario.a);
        assertThrows(RouteException.class, () -> route.addAidBox(null));
        assertThrows(RouteException.class, () -> route.addAidBox(scenario.a));
        assertThrows(RouteException.class, () -> route.removeAidBox(scenario.b));
        assertThrows(RouteException.class, () -> route.insertAfter(scenario.b, scenario.a));

        AidBoxImp onlyMedicine = new AidBoxImp("M", null);
        scenario.install(onlyMedicine, "CM", new recolha.core.ContainerTypeImp("medicine"), 10, 9);
        assertThrows(RouteException.class, () -> route.addAidBox(onlyMedicine), "the vehicle cannot carry medicine");
    }

    @Test
    void missingDistanceIsReportedClearly() throws Exception {
        AidBoxImp far = new AidBoxImp("FAR", null);
        scenario.install(far, "CF", Scenario.CLOTHING, 10, 9);
        route.addAidBox(far);
        assertThrows(IllegalStateException.class, () -> route.getTotalDistance());
        scenario.institution.addBaseDistance(new Distance("FAR", 10, 1));
        assertEquals(20, route.getTotalDistance());
    }
}
