package Core;

import PickingManagement.VehicleImp;
import com.estg.core.AidBox;
import com.estg.core.Container;
import com.estg.core.ContainerType;
import com.estg.core.Measurement;
import com.estg.core.exceptions.AidBoxException;
import com.estg.core.exceptions.ContainerException;
import com.estg.core.exceptions.MeasurementException;
import com.estg.core.exceptions.PickingMapException;
import com.estg.core.exceptions.VehicleException;
import com.estg.pickingManagement.Vehicle;
import com.estg.pickingManagement.exceptions.RouteException;
import java.time.LocalDateTime;
import org.json.simple.parser.ParseException;

/*
* Nome: Francisco Miguel Pereira Oliveira
* Número: 8230148
* Turma: LEIT2
*/

/**
 * Main Class PP_ER_8230148, classe para testes
 * @author Francisco Miguel Pereira Oliveira
 * @version 1.0
 * @since 2024-06-27
*/
public class PP_ER_8230148 {
    
    public static void main(String[] args) throws ContainerException, AidBoxException, PickingMapException, RouteException, ParseException, VehicleException, MeasurementException {
        
        InstitutionImp i1 = new InstitutionImp("Base");
        ContainerType ct1 = new ContainerTypeImp("perishable food");
        ContainerType ct2 = new ContainerTypeImp("non perishable food");
        ContainerType ct3 = new ContainerTypeImp("medicine");
        ContainerType ct4 = new ContainerTypeImp("clothing");
        ContainerType ct5 = new ContainerTypeImp("books");
        ContainerType[] cts1 = {ct1, ct2, ct3, ct4, ct5};
        int[] capacities1 = {2, 3, 3, 4, 1};
        Container c1 = new ContainerImp("c1", ct1, 500);
        Container c2 = new ContainerImp("c2", ct2, 200);
        Container c3 = new ContainerImp("c3", ct3, 300);
        Container c4 = new ContainerImp("c4", ct4, 150);
        Measurement m1 = new MeasurementImp(LocalDateTime.now(), 50);
        Measurement m2 = new MeasurementImp(LocalDateTime.now(), 80);
        Measurement m3 = new MeasurementImp(LocalDateTime.now(), 90);
        Measurement m4 = new MeasurementImp(LocalDateTime.now(), 100);
        AidBox a1 = new AidBoxImp("a1", "z1");
        AidBox a2 = new AidBoxImp("a2", "z2");
        AidBox a3 = new AidBoxImp("a3", "z3");
        AidBox a4 = new AidBoxImp("a4", "z4");
        Vehicle v1 = new VehicleImp("v1", cts1, capacities1);
        
        c1.addMeasurement(m1);
        c1.getMeasurements();
        c2.getCode();
        c2.getCapacity();
        c2.getType();
        a1.addContainer(c2);
        a2.getContainers();
        a1.removeContainer(c2);
    }
    
}
