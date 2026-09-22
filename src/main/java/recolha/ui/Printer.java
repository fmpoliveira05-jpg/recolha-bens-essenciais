package recolha.ui;

import com.estg.core.AidBox;
import com.estg.core.Container;
import com.estg.pickingManagement.PickingMap;
import com.estg.pickingManagement.Route;
import com.estg.pickingManagement.Vehicle;
import java.io.PrintStream;
import recolha.alerts.Alert;
import recolha.core.InstitutionImp;
import recolha.picking.Pickup;
import recolha.picking.RouteImp;

/**
 * Formatação da informação mostrada ao utilizador. Separar a apresentação da lógica permite
 * testar a lógica sem depender do texto que aparece no ecrã.
 *
 * @author Francisco Miguel Pereira Oliveira
 */
final class Printer {

    private Printer() {
    }

    static void institution(PrintStream out, InstitutionImp inst) {
        out.println();
        out.println(inst);
        for (AidBox box : inst.getAidBoxes()) {
            out.println("  Caixa " + box);
            for (Container container : box.getContainers()) {
                out.println("    - " + container);
            }
        }
    }

    static void vehicles(PrintStream out, String title, Vehicle[] vehicles) {
        out.println();
        out.println(title + " (" + vehicles.length + ")");
        for (Vehicle vehicle : vehicles) {
            out.println("  " + vehicle);
        }
    }

    static void pickingMap(PrintStream out, PickingMap map) {
        Route[] routes = map.getRoutes();
        out.println();
        out.println(map);
        if (routes.length == 0) {
            out.println("  Nenhum contentor precisa de ser recolhido.");
            return;
        }
        for (Route route : routes) {
            out.printf("%n  %s%n  %.1f km, %.0f min%n", route, route.getTotalDistance() / 1000, route.getTotalDuration());
            if (route instanceof RouteImp) {
                for (Pickup pickup : ((RouteImp) route).getPickups()) {
                    out.println("    • " + pickup);
                }
            }
        }
        out.println();
        out.println("  " + routes[0].getReport());
    }

    static void alerts(PrintStream out, Alert[] alerts) {
        out.println();
        if (alerts.length == 0) {
            out.println("Não há alertas.");
            return;
        }
        out.println("Alertas (" + alerts.length + "):");
        for (Alert alert : alerts) {
            out.println("  " + alert);
        }
    }
}
