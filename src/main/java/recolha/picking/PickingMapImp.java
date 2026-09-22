package recolha.picking;

import com.estg.pickingManagement.PickingMap;
import com.estg.pickingManagement.Route;
import java.time.LocalDateTime;

/**
 * Conjunto das rotas geradas num determinado momento (o "plano do dia").
 *
 * @author Francisco Miguel Pereira Oliveira
 */
public final class PickingMapImp implements PickingMap {

    private final LocalDateTime date;
    private final Route[] routes;

    /**
     * @param date instante em que o mapa foi gerado
     * @param routes rotas do mapa
     */
    public PickingMapImp(LocalDateTime date, Route[] routes) {
        this.date = date;
        this.routes = routes.clone();
    }

    @Override
    public LocalDateTime getDate() {
        return this.date;
    }

    @Override
    public Route[] getRoutes() {
        return this.routes.clone();
    }

    @Override
    public String toString() {
        return "Mapa de " + this.date.withNano(0) + " com " + this.routes.length + " rota(s)";
    }
}
