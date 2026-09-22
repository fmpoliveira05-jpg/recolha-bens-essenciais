package recolha.picking;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.estg.core.AidBox;
import com.estg.pickingManagement.Report;
import com.estg.pickingManagement.Route;
import java.time.Clock;
import java.time.ZoneOffset;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RouteGeneratorImpTest {

    private Scenario scenario;
    private RouteGeneratorImp generator;

    @BeforeEach
    void setUp() throws Exception {
        scenario = new Scenario();
        generator = new RouteGeneratorImp(Clock.fixed(Scenario.NOW.toInstant(ZoneOffset.UTC), ZoneOffset.UTC));
    }

    @Test
    void collectsOnlyContainersThatNeedIt() throws Exception {
        scenario.install(scenario.a, "FULL", Scenario.CLOTHING, 100, 85);
        scenario.install(scenario.b, "HALF", Scenario.CLOTHING, 100, 50);
        scenario.spares(Scenario.CLOTHING, 5);
        scenario.vehicle("V1", Scenario.CLOTHING, 5);

        Route[] routes = generator.generateRoutes(scenario.institution);

        assertEquals(1, routes.length);
        assertArrayEquals(new AidBox[] {scenario.a}, routes[0].getRoute());
        Pickup[] pickups = ((RouteImp) routes[0]).getPickups();
        assertEquals(1, pickups.length);
        assertEquals("FULL", pickups[0].getCollected().getCode());
        assertEquals(Scenario.CLOTHING, pickups[0].getReplacement().getType());
    }

    @Test
    void perishableFoodIsCollectedAtAnyLevelAndFirst() throws Exception {
        scenario.install(scenario.a, "CLOTH", Scenario.CLOTHING, 100, 95);
        scenario.install(scenario.b, "FOOD", Scenario.PERISHABLE, 100, 10);
        scenario.spares(Scenario.CLOTHING, 1);
        scenario.spares(Scenario.PERISHABLE, 1);
        VehicleImp vehicle = new VehicleImp("V1", new com.estg.core.ContainerType[] {Scenario.CLOTHING, Scenario.PERISHABLE}, new int[] {1, 1});
        scenario.institution.addVehicle(vehicle);

        Route[] routes = generator.generateRoutes(scenario.institution);
        Pickup[] pickups = ((RouteImp) routes[0]).getPickups();

        assertEquals(2, pickups.length);
        assertEquals("FOOD", pickups[0].getCollected().getCode(), "perishable food has priority");
    }

    @Test
    void respectsVehicleCapacityAndSpareContainers() throws Exception {
        scenario.install(scenario.a, "A1", Scenario.CLOTHING, 100, 90);
        scenario.install(scenario.b, "B1", Scenario.CLOTHING, 100, 99);
        scenario.spares(Scenario.CLOTHING, 5);
        scenario.vehicle("V1", Scenario.CLOTHING, 1);

        Route[] routes = generator.generateRoutes(scenario.institution);
        Report report = routes[0].getReport();

        assertEquals(1, report.getPickedContainers());
        assertEquals(1, report.getNonPickedContainers());
        assertEquals("B1", ((RouteImp) routes[0]).getPickups()[0].getCollected().getCode(), "the fullest one goes first");
    }

    @Test
    void withoutSpareContainersNothingCanBeSwapped() throws Exception {
        scenario.install(scenario.a, "A1", Scenario.CLOTHING, 100, 90);
        scenario.vehicle("V1", Scenario.CLOTHING, 3);

        Route[] routes = generator.generateRoutes(scenario.institution);

        assertEquals(0, routes.length);
        assertEquals(0, scenario.institution.getCurrentPickingMap().getRoutes().length);
    }

    @Test
    void reportAndPickingMapAreConsistent() throws Exception {
        scenario.install(scenario.a, "A1", Scenario.CLOTHING, 100, 90);
        scenario.install(scenario.b, "B1", Scenario.CLOTHING, 100, 90);
        scenario.spares(Scenario.CLOTHING, 2);
        scenario.vehicle("V1", Scenario.CLOTHING, 2);
        scenario.vehicle("V2", Scenario.CLOTHING, 2);

        Route[] routes = generator.generateRoutes(scenario.institution);
        Report report = routes[0].getReport();

        assertEquals(1, report.getUsedVehicles(), "one vehicle is enough and B is close to A");
        assertEquals(1, report.getNotUsedVehicles());
        assertEquals(2, report.getPickedContainers());
        assertEquals(3500, report.getTotalDistance());
        assertEquals(Scenario.NOW, report.getDate());
        assertArrayEquals(new AidBox[] {scenario.a, scenario.b}, routes[0].getRoute(), "nearest neighbour from the base");
        assertSame(routes[0], scenario.institution.getCurrentPickingMap().getRoutes()[0]);
    }

    @Test
    void boxesWithoutKnownDistanceAreSkippedWithAlert() throws Exception {
        recolha.core.AidBoxImp unknown = new recolha.core.AidBoxImp("U", null);
        scenario.install(unknown, "U1", Scenario.CLOTHING, 100, 99);
        scenario.institution.addAidBox(unknown);
        scenario.spares(Scenario.CLOTHING, 1);
        scenario.vehicle("V1", Scenario.CLOTHING, 1);

        assertEquals(0, generator.generateRoutes(scenario.institution).length);
        assertEquals(1, scenario.institution.getAlerts().count());
    }

    @Test
    void rejectsMissingInstitution() {
        assertThrows(IllegalArgumentException.class, () -> generator.generateRoutes(null));
    }
}
