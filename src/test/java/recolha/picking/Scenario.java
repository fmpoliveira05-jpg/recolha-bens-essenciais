package recolha.picking;

import com.estg.core.ContainerType;
import java.time.LocalDateTime;
import recolha.core.AidBoxImp;
import recolha.core.ContainerImp;
import recolha.core.ContainerTypeImp;
import recolha.core.Distance;
import recolha.core.InstitutionImp;
import recolha.core.MeasurementImp;

/**
 * Pequeno cenário usado pelos testes de rotas.
 *
 * <pre>
 *   Base --1000-- A --500-- B
 *     \_____________2000____/
 * </pre>
 */
final class Scenario {

    static final ContainerTypeImp CLOTHING = new ContainerTypeImp("clothing");
    static final ContainerTypeImp PERISHABLE = new ContainerTypeImp(ContainerTypeImp.PERISHABLE_FOOD);
    static final LocalDateTime NOW = LocalDateTime.of(2024, 6, 18, 12, 0);

    final InstitutionImp institution = new InstitutionImp("Teste");
    final AidBoxImp a = new AidBoxImp("A", null);
    final AidBoxImp b = new AidBoxImp("B", null);

    Scenario() throws Exception {
        institution.addAidBox(a);
        institution.addAidBox(b);
        institution.addBaseDistance(new Distance("A", 1000, 10));
        institution.addBaseDistance(new Distance("B", 2000, 20));
        a.addDistance(new Distance("B", 500, 5));
        b.addDistance(new Distance("A", 500, 5));
    }

    ContainerImp install(AidBoxImp box, String code, ContainerTypeImp type, double capacity, double reading) throws Exception {
        ContainerImp container = new ContainerImp(code, type, capacity);
        box.addContainer(container);
        container.addMeasurement(new MeasurementImp(NOW, reading));
        return container;
    }

    void spares(ContainerTypeImp type, int count) throws Exception {
        for (int i = 0; i < count; i++) {
            institution.addSpareContainer(new ContainerImp("S-" + type.getName() + "-" + i, type, 100));
        }
    }

    VehicleImp vehicle(String code, ContainerTypeImp type, int capacity) throws Exception {
        VehicleImp vehicle = new VehicleImp(code, new ContainerType[] {type}, new int[] {capacity});
        institution.addVehicle(vehicle);
        return vehicle;
    }
}
