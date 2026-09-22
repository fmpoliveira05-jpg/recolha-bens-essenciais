package recolha.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.estg.core.exceptions.AidBoxException;
import com.estg.core.exceptions.ContainerException;
import org.junit.jupiter.api.Test;

class AidBoxImpTest {

    private final ContainerTypeImp clothing = new ContainerTypeImp("clothing");
    private final ContainerTypeImp medicine = new ContainerTypeImp("medicine");

    @Test
    void acceptsOneContainerPerType() throws Exception {
        AidBoxImp box = new AidBoxImp("A1", "Norte");
        ContainerImp c1 = new ContainerImp("C1", clothing, 100);
        assertTrue(box.addContainer(c1));
        assertFalse(box.addContainer(c1), "the same container twice is ignored");
        assertThrows(ContainerException.class, () -> box.addContainer(new ContainerImp("C2", new ContainerTypeImp("CLOTHING"), 50)));
        assertTrue(box.addContainer(new ContainerImp("C3", medicine, 50)));

        assertEquals(2, box.getContainers().length);
        assertSame(c1, box.getContainer(clothing));
        assertNull(box.getContainer(new ContainerTypeImp("books")));
    }

    @Test
    void removesContainers() throws Exception {
        AidBoxImp box = new AidBoxImp("A1", null);
        ContainerImp c1 = new ContainerImp("C1", clothing, 100);
        box.addContainer(c1);
        box.removeContainer(c1);
        assertEquals(0, box.getContainers().length);
        assertThrows(AidBoxException.class, () -> box.removeContainer(c1));
    }

    @Test
    void knowsDistancesToOtherBoxes() throws Exception {
        AidBoxImp a = new AidBoxImp("A", null);
        AidBoxImp b = new AidBoxImp("B", null);
        a.addDistance(new Distance("B", 1000, 5));
        a.addDistance(new Distance("B", 1200, 6));

        assertEquals(1200, a.getDistance(b), "a second distance to the same box replaces the first");
        assertEquals(6, a.getDuration(b));
        assertEquals(0, a.getDistance(a));
        assertThrows(AidBoxException.class, () -> b.getDistance(a));
        assertThrows(IllegalArgumentException.class, () -> new Distance("X", -1, 0));
    }
}
