package recolha.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.estg.core.exceptions.InstitutionException;
import java.io.IOException;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import recolha.core.ContainerImp;
import recolha.core.InstitutionImp;

class ImporterImpTest {

    /** Origem em memória: cada teste define exatamente os documentos que quer. */
    private static DataSource inMemory(String types, String containers, String aidBoxes, String distances, String vehicles, String readings) {
        return new DataSource() {
            @Override
            public String load(Dataset dataset) {
                switch (dataset) {
                    case TYPES: return types;
                    case CONTAINERS: return containers;
                    case AID_BOXES: return aidBoxes;
                    case DISTANCES: return distances;
                    case VEHICLES: return vehicles;
                    default: return readings;
                }
            }

            @Override
            public String describe() {
                return "memória";
            }
        };
    }

    @Test
    void importsTheSampleFilesShippedWithTheProject() throws Exception {
        InstitutionImp institution = new InstitutionImp("Teste");
        ImporterImp importer = new ImporterImp(new FileDataSource(Path.of("data")));

        importer.importData(institution);

        assertEquals(20, institution.getAidBoxes().length);
        assertEquals(70, institution.getNumContainers());
        assertEquals(48, institution.getSpareContainers().length);
        assertEquals(15, institution.getVehicles().length);
        assertEquals(68, importer.getLastSummary().getReadings());
        // As duas leituras acima da capacidade ficam registadas como alertas.
        assertEquals(2, institution.getAlerts().count());
        assertEquals(2971, institution.getDistance(institution.findAidBox("CAIXF32")), 0.001);
    }

    @Test
    void invalidRecordsBecomeAlertsInsteadOfStoppingTheImport() throws Exception {
        InstitutionImp institution = new InstitutionImp("Teste");
        DataSource source = inMemory(
                "[{\"types\": [\"clothing\"]}]",
                "[{\"code\": \"C1\", \"capacity\": 100, \"type\": \"clothing\"}, {\"code\": \"BAD\"}]",
                "[{\"code\": \"A1\", \"Zona\": \"Norte\", \"containers\": [\"C1\", \"GHOST\"]}]",
                "[{\"from\": \"A1\", \"to\": [{\"name\": \"Base\", \"distance\": 100, \"duration\": 2}]}]",
                "[{\"code\": \"V1\", \"capacity\": {\"clothing\": 2, \"books\": 0}}]",
                "[{\"contentor\": \"C1\", \"data\": \"2024-06-18T12:00:00Z\", \"valor\": 90},"
                        + " {\"contentor\": \"NOPE\", \"data\": \"2024-06-18T12:00:00Z\", \"valor\": 1},"
                        + " {\"contentor\": \"C1\", \"data\": \"not a date\", \"valor\": 1}]");

        new ImporterImp(source).importData(institution);

        assertEquals("Norte", institution.findAidBox("A1").getZone());
        ContainerImp c1 = (ContainerImp) institution.findContainer("C1");
        assertNotNull(c1);
        assertTrue(c1.needsPickup());
        assertEquals(100, institution.getDistance(institution.findAidBox("A1")));
        assertEquals(1, institution.getVehicles().length);
        // BAD (campos em falta), GHOST (contentor inexistente), NOPE (leitura órfã) e a data inválida.
        assertEquals(4, institution.getAlerts().count());
    }

    @Test
    void malformedJsonIsAnIoError() {
        DataSource source = inMemory("{", "[]", "[]", "[]", "[]", "[]");
        assertThrows(IOException.class, () -> new ImporterImp(source).importData(new InstitutionImp("x")));
    }

    @Test
    void missingFilesAreReported() {
        ImporterImp importer = new ImporterImp(new FileDataSource(Path.of("does-not-exist")));
        assertThrows(IOException.class, () -> importer.importData(new InstitutionImp("x")));
        assertThrows(InstitutionException.class, () -> importer.importData(null));
    }
}
