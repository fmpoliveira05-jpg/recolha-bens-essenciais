package recolha.picking;

import com.estg.core.AidBox;
import com.estg.core.Container;
import com.estg.core.ContainerType;
import com.estg.core.Institution;
import com.estg.core.exceptions.AidBoxException;
import com.estg.core.exceptions.PickingMapException;
import com.estg.pickingManagement.Route;
import com.estg.pickingManagement.RouteGenerator;
import com.estg.pickingManagement.Vehicle;
import com.estg.pickingManagement.exceptions.RouteException;
import java.time.Clock;
import java.time.LocalDateTime;
import recolha.core.ContainerImp;
import recolha.core.ContainerTypeImp;
import recolha.core.InstitutionImp;
import recolha.util.DynamicArray;

/**
 * Gera o plano de recolha do dia.
 *
 * <p>O planeamento é feito em duas fases: construção gulosa das rotas e melhoria local.</p>
 * <ol>
 *   <li>Junta todos os contentores que precisam de ser recolhidos: alimentos perecíveis com
 *       conteúdo e restantes tipos acima de 80% da capacidade.</li>
 *   <li>Ordena-os por prioridade: primeiro os perecíveis, depois os mais cheios.</li>
 *   <li>Atribui cada contentor a um veículo ativo que suporte o tipo, ainda tenha lugar e
 *       tenha levado da base um contentor vazio do mesmo tipo para a troca. Dá preferência a
 *       um veículo que já passe nessa caixa e, a seguir, à rota onde a caixa acrescenta menos
 *       metros (inserção mais barata).</li>
 *   <li>Se nenhum veículo já em rota tiver lugar, um veículo compatível regressa à base e
 *       faz uma nova viagem (o enunciado prevê que possa ser preciso mais do que um caminho),
 *       até {@link #MAX_TRIPS_PER_VEHICLE} viagens por dia.</li>
 *   <li>Por fim, ordena as paragens de cada rota pelo vizinho mais próximo, a partir da base,
 *       e melhora essa ordem com 2-opt: inverte troços da rota enquanto isso encurtar o
 *       percurso total.</li>
 * </ol>
 * <p>Contentores que precisavam de recolha mas não couberam contam como "não recolhidos" no
 * relatório. Não é uma solução ótima (o problema é uma variante do VRP, que é NP-difícil),
 * mas é determinística, respeita todas as restrições do enunciado e a fase de 2-opt garante que
 * nenhuma rota pode ser encurtada invertendo um troço do percurso.</p>
 *
 * @author Francisco Miguel Pereira Oliveira
 */
public class RouteGeneratorImp implements RouteGenerator {

    /** Número máximo de viagens que cada veículo faz num dia. */
    public static final int MAX_TRIPS_PER_VEHICLE = 3;

    private final Clock clock;

    /** Cria um gerador que usa o relógio do sistema. */
    public RouteGeneratorImp() {
        this(Clock.systemDefaultZone());
    }

    /**
     * @param clock relógio usado para datar o mapa (permite testes determinísticos)
     */
    public RouteGeneratorImp(Clock clock) {
        this.clock = clock;
    }

    /**
     * Gera as rotas e guarda o resultado como novo mapa de recolha da instituição.
     *
     * @param institution instituição (tem de ser uma {@link InstitutionImp})
     * @return as rotas com pelo menos uma paragem
     * @throws IllegalArgumentException se a instituição não for suportada
     */
    @Override
    public Route[] generateRoutes(Institution institution) {
        if (!(institution instanceof InstitutionImp)) {
            throw new IllegalArgumentException("O gerador precisa de uma InstitutionImp (distâncias à base e contentores de reserva).");
        }
        InstitutionImp inst = (InstitutionImp) institution;

        Vehicle[] vehicles = inst.getVehicles();
        DynamicArray<VehiclePlan> plans = new DynamicArray<>();
        for (Vehicle vehicle : vehicles) {
            plans.add(new VehiclePlan(new RouteImp(vehicle, inst, 1)));
        }
        DynamicArray<Container> sparePool = new DynamicArray<>();
        for (Container spare : inst.getSpareContainers()) {
            sparePool.add(spare);
        }

        PendingPickup[] pending = collectPending(inst);
        int picked = 0;
        int nonPicked = 0;
        for (PendingPickup item : pending) {
            if (assign(item, plans, sparePool, inst)) {
                picked++;
            } else {
                nonPicked++;
            }
        }

        DynamicArray<Route> usedRoutes = new DynamicArray<>();
        DynamicArray<Vehicle> usedVehicles = new DynamicArray<>();
        double totalDistance = 0;
        double totalDuration = 0;
        for (int p = 0; p < plans.size(); p++) {
            VehiclePlan plan = plans.get(p);
            if (plan.route.getNumStops() > 0) {
                plan.route.reorder(twoOpt(nearestNeighbourOrder(plan.route.getRoute(), inst), inst));
                totalDistance += plan.route.getTotalDistance();
                totalDuration += plan.route.getTotalDuration();
                usedRoutes.add(plan.route);
                if (!usedVehicles.contains(plan.route.getVehicle())) {
                    usedVehicles.add(plan.route.getVehicle());
                }
            }
        }

        LocalDateTime now = LocalDateTime.now(this.clock);
        ReportImp report = new ReportImp(now, usedVehicles.size(), vehicles.length - usedVehicles.size(),
                picked, nonPicked, totalDistance, totalDuration);
        Route[] routes = usedRoutes.toArray(Route[]::new);
        for (Route route : routes) {
            ((RouteImp) route).setReport(report);
        }

        try {
            inst.addPickingMap(new PickingMapImp(now, routes));
        } catch (PickingMapException ex) {
            // Só acontece se o mapa for nulo, o que aqui é impossível.
            throw new IllegalStateException(ex);
        }
        return routes;
    }

    /**
     * Junta os contentores a recolher, já ordenados por prioridade. Caixas sem distância
     * conhecida à base ficam de fora e geram um alerta.
     */
    private PendingPickup[] collectPending(InstitutionImp inst) {
        DynamicArray<PendingPickup> pending = new DynamicArray<>();
        for (AidBox box : inst.getAidBoxes()) {
            if (!hasBaseDistance(inst, box)) {
                inst.getAlerts().add("Caixa ignorada no planeamento: distância à base desconhecida", box);
                continue;
            }
            for (Container container : box.getContainers()) {
                if (container instanceof ContainerImp && ((ContainerImp) container).needsPickup()) {
                    pending.add(new PendingPickup(box, (ContainerImp) container));
                }
            }
        }
        PendingPickup[] sorted = pending.toArray(PendingPickup[]::new);
        // Ordenação por inserção: o número de contentores é pequeno e evita usar a JCF.
        for (int i = 1; i < sorted.length; i++) {
            PendingPickup current = sorted[i];
            int j = i - 1;
            while (j >= 0 && current.hasPriorityOver(sorted[j])) {
                sorted[j + 1] = sorted[j];
                j--;
            }
            sorted[j + 1] = current;
        }
        return sorted;
    }

    private boolean assign(PendingPickup item, DynamicArray<VehiclePlan> plans, DynamicArray<Container> sparePool, InstitutionImp inst) {
        ContainerType type = item.container.getType();
        int spareIndex = findSpare(sparePool, type);
        if (spareIndex == -1) {
            return false;
        }

        VehiclePlan best = null;
        double bestScore = Double.MAX_VALUE;
        for (int p = 0; p < plans.size(); p++) {
            VehiclePlan plan = plans.get(p);
            if (!plan.hasRoomFor(type)) {
                continue;
            }
            if (!plan.route.containsAidBox(item.aidBox) && !plan.canVisit(item.aidBox)) {
                continue;
            }
            double score = plan.route.containsAidBox(item.aidBox) ? -1 : insertionCost(plan.route.getRoute(), item.aidBox, inst);
            if (score < bestScore) {
                bestScore = score;
                best = plan;
            }
        }
        if (best == null) {
            best = startNewTrip(item, plans, inst);
        }
        if (best == null) {
            return false;
        }

        try {
            if (!best.route.containsAidBox(item.aidBox)) {
                best.route.addAidBox(item.aidBox);
            }
        } catch (RouteException ex) {
            return false;
        }
        best.route.addPickup(new Pickup(item.aidBox, item.container, sparePool.removeAt(spareIndex)));
        best.use(type);
        return true;
    }

    /**
     * Abre uma nova viagem para o veículo compatível que tiver feito menos viagens.
     *
     * @return o plano da nova viagem, ou {@code null} se nenhum veículo puder sair outra vez
     */
    private static VehiclePlan startNewTrip(PendingPickup item, DynamicArray<VehiclePlan> plans, InstitutionImp inst) {
        ContainerType type = item.container.getType();
        VehiclePlan chosen = null;
        int chosenTrips = Integer.MAX_VALUE;
        for (int p = 0; p < plans.size(); p++) {
            Vehicle vehicle = plans.get(p).route.getVehicle();
            if (vehicle.getCapacity(type) <= 0) {
                continue;
            }
            int trips = 0;
            for (int q = 0; q < plans.size(); q++) {
                if (plans.get(q).route.getVehicle().equals(vehicle)) {
                    trips++;
                }
            }
            if (trips < MAX_TRIPS_PER_VEHICLE && trips < chosenTrips) {
                chosenTrips = trips;
                chosen = plans.get(p);
            }
        }
        if (chosen == null) {
            return null;
        }
        VehiclePlan trip = new VehiclePlan(new RouteImp(chosen.route.getVehicle(), inst, chosenTrips + 1));
        plans.add(trip);
        return trip;
    }

    private static int findSpare(DynamicArray<Container> pool, ContainerType type) {
        for (int i = 0; i < pool.size(); i++) {
            if (pool.get(i).getType().equals(type)) {
                return i;
            }
        }
        return -1;
    }

    private static boolean hasBaseDistance(InstitutionImp inst, AidBox box) {
        try {
            inst.getDistance(box);
            return true;
        } catch (AidBoxException ex) {
            return false;
        }
    }

    /**
     * Distância de um troço; {@code null} representa a base.
     */
    private static double leg(AidBox from, AidBox to, InstitutionImp inst) {
        try {
            if (from == null && to == null) {
                return 0;
            }
            if (from == null) {
                return inst.getDistance(to);
            }
            if (to == null) {
                return inst.getDistance(from);
            }
            return from.getDistance(to);
        } catch (AidBoxException ex) {
            // Troço desconhecido: fica tão caro que nunca é escolhido se houver alternativa.
            return Double.MAX_VALUE / 1e6;
        }
    }

    /**
     * Comprimento de um percurso base → paragens → base.
     *
     * @param order paragens pela ordem de visita
     * @param inst instituição (distâncias à base)
     * @return metros
     */
    static double routeLength(AidBox[] order, InstitutionImp inst) {
        double total = 0;
        AidBox previous = null;
        for (AidBox box : order) {
            total += leg(previous, box, inst);
            previous = box;
        }
        return total + leg(previous, null, inst);
    }

    /**
     * Metros que uma caixa acrescenta a uma rota se for inserida na melhor posição possível.
     */
    private static double insertionCost(AidBox[] stops, AidBox target, InstitutionImp inst) {
        double best = Double.MAX_VALUE;
        for (int position = 0; position <= stops.length; position++) {
            AidBox before = position == 0 ? null : stops[position - 1];
            AidBox after = position == stops.length ? null : stops[position];
            double extra = leg(before, target, inst) + leg(target, after, inst) - leg(before, after, inst);
            if (extra < best) {
                best = extra;
            }
        }
        return best;
    }

    /**
     * Melhoria 2-opt: enquanto houver um troço cuja inversão encurte o percurso, inverte-o.
     * Como as distâncias entre caixas podem não ser simétricas, cada candidato é avaliado pelo
     * comprimento total da rota.
     *
     * @param order ordem inicial (por exemplo, a do vizinho mais próximo)
     * @param inst instituição (distâncias à base)
     * @return uma ordem igual ou mais curta
     */
    static AidBox[] twoOpt(AidBox[] order, InstitutionImp inst) {
        AidBox[] best = order.clone();
        double bestLength = routeLength(best, inst);
        boolean improved = true;
        while (improved) {
            improved = false;
            for (int i = 0; i < best.length - 1; i++) {
                for (int j = i + 1; j < best.length; j++) {
                    AidBox[] candidate = best.clone();
                    for (int left = i, right = j; left < right; left++, right--) {
                        AidBox tmp = candidate[left];
                        candidate[left] = candidate[right];
                        candidate[right] = tmp;
                    }
                    double length = routeLength(candidate, inst);
                    if (length + 1e-9 < bestLength) {
                        best = candidate;
                        bestLength = length;
                        improved = true;
                    }
                }
            }
        }
        return best;
    }

    /**
     * Ordena as paragens escolhendo sempre a caixa mais próxima da posição atual,
     * começando na base.
     */
    static AidBox[] nearestNeighbourOrder(AidBox[] stops, InstitutionImp inst) {
        AidBox[] ordered = new AidBox[stops.length];
        boolean[] visited = new boolean[stops.length];
        AidBox current = null;
        for (int position = 0; position < stops.length; position++) {
            int nearest = -1;
            double nearestDistance = Double.MAX_VALUE;
            for (int i = 0; i < stops.length; i++) {
                if (visited[i]) {
                    continue;
                }
                double distance;
                try {
                    distance = current == null ? inst.getDistance(stops[i]) : current.getDistance(stops[i]);
                } catch (AidBoxException ex) {
                    distance = Double.MAX_VALUE / 2;
                }
                if (nearest == -1 || distance < nearestDistance) {
                    nearest = i;
                    nearestDistance = distance;
                }
            }
            visited[nearest] = true;
            ordered[position] = stops[nearest];
            current = stops[nearest];
        }
        return ordered;
    }

    /** Contentor que precisa de ser recolhido, com a caixa onde está. */
    private static final class PendingPickup {
        private final AidBox aidBox;
        private final ContainerImp container;

        PendingPickup(AidBox aidBox, ContainerImp container) {
            this.aidBox = aidBox;
            this.container = container;
        }

        boolean hasPriorityOver(PendingPickup other) {
            boolean perishable = ContainerTypeImp.isPerishable(this.container.getType());
            boolean otherPerishable = ContainerTypeImp.isPerishable(other.container.getType());
            if (perishable != otherPerishable) {
                return perishable;
            }
            return this.container.getFillRatio() > other.container.getFillRatio();
        }
    }

    /** Estado de um veículo durante o planeamento: a rota e os lugares já ocupados. */
    private static final class VehiclePlan {
        private final RouteImp route;
        private final DynamicArray<ContainerType> usedTypes = new DynamicArray<>();
        private final DynamicArray<Integer> usedCounts = new DynamicArray<>();

        VehiclePlan(RouteImp route) {
            this.route = route;
        }

        boolean hasRoomFor(ContainerType type) {
            int index = this.usedTypes.indexOf(type);
            int used = index == -1 ? 0 : this.usedCounts.get(index);
            return used < this.route.getVehicle().getCapacity(type);
        }

        boolean canVisit(AidBox box) {
            for (Container container : box.getContainers()) {
                if (this.route.getVehicle().getCapacity(container.getType()) > 0) {
                    return true;
                }
            }
            return false;
        }

        void use(ContainerType type) {
            int index = this.usedTypes.indexOf(type);
            if (index == -1) {
                this.usedTypes.add(type);
                this.usedCounts.add(1);
            } else {
                this.usedCounts.set(index, this.usedCounts.get(index) + 1);
            }
        }
    }
}
