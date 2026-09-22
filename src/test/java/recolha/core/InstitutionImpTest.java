package recolha.core;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.estg.core.ContainerType;
import com.estg.core.exceptions.AidBoxException;
import com.estg.core.exceptions.ContainerException;
import com.estg.core.exceptions.PickingMapException;
import com.estg.core.exceptions.VehicleException;
import com.estg.pickingManagement.PickingMap;
import com.estg.pickingManagement.Route;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import recolha.picking.PickingMapImp;
import recolha.picking.VehicleImp;

class InstitutionImpTest {

    private final ContainerTypeImp clothing = new ContainerTypeImp("clothing");
    private InstitutionImp institution;
    private AidBoxImp box;
    private ContainerImp container;

    @BeforeEach
    void setUp() throws Exception {
        this.institution = new InstitutionImp("Teste");
        this.box = new AidBoxImp("A1", "Centro");
        this.container = new ContainerImp("C1", this.clothing, 100);
        this.box.addContainer(this.container);
        this.institution.addAidBox(this.box);
    }

    @Test
    void addsAidBoxesOnlyOnce() throws Exception {
        assertFalse(this.institution.addAidBox(new AidBoxImp("A1", "Outra")));
        assertEquals(1, this.institution.getAidBoxes().length);
        assertThrows(AidBoxException.class, () -> this.institution.addAidBox(null));
        assertSame(this.box, this.institution.findAidBox("A1"));
        assertSame(this.container, this.institution.findContainer("C1"));
        assertSame(this.box, this.institution.findAidBoxOf(this.container));
    }

    @Test
    void getContainerValidatesBoxAndType() throws Exception {
        assertSame(this.container, this.institution.getContainer(this.box, this.clothing));
        assertThrows(ContainerException.class, () -> this.institution.getContainer(new AidBoxImp("X", null), this.clothing));
        assertThrows(ContainerException.class, () -> this.institution.getContainer(this.box, new ContainerTypeImp("books")));
    }

    @Test
    void measurementsOnlyForInstalledContainers() throws Exception {
        assertTrue(this.institution.addMeasurement(new MeasurementImp(LocalDateTime.now(), 10), this.container));
        ContainerImp stranger = new ContainerImp("ZZ", this.clothing, 10);
        assertThrows(ContainerException.class, () -> this.institution.addMeasurement(new MeasurementImp(LocalDateTime.now(), 1), stranger));
    }

    @Test
    void vehiclesCanBeDisabledAndEnabled() throws Exception {
        VehicleImp vehicle = new VehicleImp("V1", new ContainerType[] {this.clothing}, new int[] {2});
        assertTrue(this.institution.addVehicle(vehicle));
        assertFalse(this.institution.addVehicle(new VehicleImp("V1", new ContainerType[0], new int[0])));

        this.institution.disableVehicle(vehicle);
        assertEquals(0, this.institution.getVehicles().length);
        assertEquals(1, this.institution.getDisabledVehicles().length);
        assertThrows(VehicleException.class, () -> this.institution.disableVehicle(vehicle));
        assertFalse(this.institution.addVehicle(vehicle), "a disabled vehicle is still part of the fleet");

        this.institution.enableVehicle(vehicle);
        assertEquals(1, this.institution.getVehicles().length);
        assertThrows(VehicleException.class, () -> this.institution.enableVehicle(vehicle));
        assertThrows(VehicleException.class, () -> this.institution.enableVehicle(new VehicleImp("V9", new ContainerType[0], new int[0])));
    }

    @Test
    void pickingMapsByDateRange() throws Exception {
        assertThrows(PickingMapException.class, () -> this.institution.getCurrentPickingMap());
        PickingMap june = new PickingMapImp(LocalDateTime.of(2024, 6, 1, 8, 0), new Route[0]);
        PickingMap july = new PickingMapImp(LocalDateTime.of(2024, 7, 1, 8, 0), new Route[0]);
        assertTrue(this.institution.addPickingMap(june));
        assertTrue(this.institution.addPickingMap(july));
        assertFalse(this.institution.addPickingMap(july));

        assertSame(july, this.institution.getCurrentPickingMap());
        assertArrayEquals(new PickingMap[] {june},
                this.institution.getPickingMaps(LocalDateTime.of(2024, 6, 1, 8, 0), LocalDateTime.of(2024, 6, 30, 0, 0)));
        assertEquals(2, this.institution.getPickingMaps().length);
    }

    @Test
    void baseDistances() throws Exception {
        assertThrows(AidBoxException.class, () -> this.institution.getDistance(this.box));
        this.institution.addBaseDistance(new Distance("A1", 500, 3));
        assertEquals(500, this.institution.getDistance(this.box));
        assertEquals(3, this.institution.getDuration(this.box));
    }

    @Test
    void spareContainersCannotBeInstalledOnes() throws Exception {
        assertFalse(this.institution.addSpareContainer(this.container));
        assertTrue(this.institution.addSpareContainer(new ContainerImp("S1", this.clothing, 100)));
        assertEquals(1, this.institution.getSpareContainers().length);
    }
}
