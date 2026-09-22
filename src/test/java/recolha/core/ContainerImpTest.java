package recolha.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.estg.core.exceptions.ContainerException;
import com.estg.core.exceptions.MeasurementException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class ContainerImpTest {

    private static final LocalDateTime MONDAY = LocalDateTime.of(2024, 6, 17, 12, 0);

    private static ContainerImp container(String type, double capacity) throws ContainerException {
        return new ContainerImp("C1", new ContainerTypeImp(type), capacity);
    }

    @Test
    void rejectsInvalidConstruction() {
        ContainerTypeImp type = new ContainerTypeImp("clothing");
        assertThrows(ContainerException.class, () -> new ContainerImp(" ", type, 10));
        assertThrows(ContainerException.class, () -> new ContainerImp("C1", null, 10));
        assertThrows(ContainerException.class, () -> new ContainerImp("C1", type, 0));
    }

    @Test
    void storesMeasurementsInOrderAndReturnsCopies() throws Exception {
        ContainerImp container = container("clothing", 100);
        assertTrue(container.addMeasurement(new MeasurementImp(MONDAY, 10)));
        assertTrue(container.addMeasurement(new MeasurementImp(MONDAY.plusDays(1), 20)));

        assertEquals(2, container.getMeasurements().length);
        container.getMeasurements()[0] = null;
        assertEquals(10, container.getMeasurements()[0].getValue());
        assertEquals(20, container.getLastMeasurement().getValue());
    }

    @Test
    void filtersMeasurementsByDay() throws Exception {
        ContainerImp container = container("clothing", 100);
        container.addMeasurement(new MeasurementImp(MONDAY, 10));
        container.addMeasurement(new MeasurementImp(MONDAY.plusHours(3), 15));
        container.addMeasurement(new MeasurementImp(MONDAY.plusDays(1), 20));

        assertEquals(2, container.getMeasurements(LocalDate.of(2024, 6, 17)).length);
        assertEquals(0, container.getMeasurements(LocalDate.of(2024, 1, 1)).length);
    }

    @Test
    void enforcesMeasurementRules() throws Exception {
        ContainerImp container = container("clothing", 100);
        container.addMeasurement(new MeasurementImp(MONDAY, 50));

        assertThrows(MeasurementException.class, () -> container.addMeasurement(null));
        assertThrows(MeasurementException.class, () -> container.addMeasurement(new MeasurementImp(MONDAY.plusDays(1), 101)));
        assertThrows(MeasurementException.class, () -> container.addMeasurement(new MeasurementImp(MONDAY.minusDays(1), 10)));
        assertThrows(MeasurementException.class, () -> container.addMeasurement(new MeasurementImp(MONDAY, 60)));
        assertFalse(container.addMeasurement(new MeasurementImp(MONDAY, 50)), "a repeated reading is ignored");
        assertThrows(MeasurementException.class, () -> new MeasurementImp(MONDAY, -1));
    }

    @Test
    void emptyReadingIsValid() throws Exception {
        ContainerImp container = container("clothing", 100);
        assertTrue(container.addMeasurement(new MeasurementImp(MONDAY, 0)));
        assertEquals(0, container.getFillRatio());
    }

    @Test
    void pickupRulesFollowTheBrief() throws Exception {
        ContainerImp clothes = container("clothing", 100);
        assertFalse(clothes.needsPickup(), "without readings nothing is known");
        clothes.addMeasurement(new MeasurementImp(MONDAY, 80));
        assertFalse(clothes.needsPickup(), "exactly 80% is not above the threshold");
        clothes.addMeasurement(new MeasurementImp(MONDAY.plusHours(1), 81));
        assertTrue(clothes.needsPickup());

        ContainerImp perishable = container(" Perishable Food ", 100);
        perishable.addMeasurement(new MeasurementImp(MONDAY, 5));
        assertTrue(perishable.needsPickup(), "perishable food is collected regardless of the level");
    }

    @Test
    void identityIsTheCode() throws Exception {
        ContainerImp a = container("clothing", 100);
        ContainerImp b = new ContainerImp("C1", new ContainerTypeImp("medicine"), 5);
        b.addMeasurement(new MeasurementImp(MONDAY, 1));
        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNull(a.getLastMeasurement());
    }

    @Test
    void typeNamesAreNormalised() {
        assertEquals(new ContainerTypeImp("Clothing "), new ContainerTypeImp("clothing"));
        assertThrows(IllegalArgumentException.class, () -> new ContainerTypeImp(""));
    }
}
