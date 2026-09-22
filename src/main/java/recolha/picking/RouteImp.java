package recolha.picking;

import com.estg.core.AidBox;
import com.estg.core.Container;
import com.estg.core.exceptions.AidBoxException;
import com.estg.pickingManagement.Report;
import com.estg.pickingManagement.Route;
import com.estg.pickingManagement.Vehicle;
import com.estg.pickingManagement.exceptions.RouteException;
import recolha.core.InstitutionImp;
import recolha.util.DynamicArray;

/**
 * Rota de um veículo: sai da base, passa pelas caixas pela ordem indicada e regressa à base.
 *
 * <p>A distância e a duração são calculadas sempre que são pedidas (na versão original eram
 * acumuladas num atributo, e cada chamada somava outra vez os mesmos valores).</p>
 *
 * @author Francisco Miguel Pereira Oliveira
 */
public class RouteImp implements Route {

    private final Vehicle vehicle;
    private final InstitutionImp institution;
    private final DynamicArray<AidBox> stops = new DynamicArray<>();
    private final DynamicArray<Pickup> pickups = new DynamicArray<>();
    private Report report;

    /**
     * @param vehicle veículo que faz a rota
     * @param institution instituição (dá as distâncias a partir da base)
     */
    public RouteImp(Vehicle vehicle, InstitutionImp institution) {
        if (vehicle == null || institution == null) {
            throw new IllegalArgumentException("A rota precisa de um veículo e de uma instituição.");
        }
        this.vehicle = vehicle;
        this.institution = institution;
    }

    @Override
    public Vehicle getVehicle() {
        return this.vehicle;
    }

    @Override
    public Report getReport() {
        return this.report;
    }

    void setReport(Report report) {
        this.report = report;
    }

    // ---------------------------------------------------------------- paragens

    @Override
    public void addAidBox(AidBox aidBox) throws RouteException {
        validateNewStop(aidBox);
        this.stops.add(aidBox);
    }

    @Override
    public AidBox removeAidBox(AidBox aidBox) throws RouteException {
        int index = indexOfExisting(aidBox);
        return this.stops.removeAt(index);
    }

    @Override
    public boolean containsAidBox(AidBox aidBox) {
        return this.stops.contains(aidBox);
    }

    @Override
    public void replaceAidBox(AidBox current, AidBox replacement) throws RouteException {
        int index = indexOfExisting(current);
        validateNewStop(replacement);
        this.stops.set(index, replacement);
    }

    @Override
    public void insertAfter(AidBox existing, AidBox newStop) throws RouteException {
        int index = indexOfExisting(existing);
        validateNewStop(newStop);
        this.stops.insertAt(index + 1, newStop);
    }

    @Override
    public AidBox[] getRoute() {
        return this.stops.toArray(AidBox[]::new);
    }

    /** @return número de paragens */
    public int getNumStops() {
        return this.stops.size();
    }

    /**
     * Reordena as paragens (usado pelo gerador depois de otimizar o percurso).
     *
     * @param newOrder as mesmas caixas, por outra ordem
     * @throws IllegalArgumentException se o conjunto de caixas não for o mesmo
     */
    void reorder(AidBox[] newOrder) {
        if (newOrder.length != this.stops.size()) {
            throw new IllegalArgumentException("A nova ordem tem de ter as mesmas paragens.");
        }
        for (AidBox box : newOrder) {
            if (!this.stops.contains(box)) {
                throw new IllegalArgumentException("A caixa " + box.getCode() + " não pertence à rota.");
            }
        }
        for (int i = 0; i < newOrder.length; i++) {
            this.stops.set(i, newOrder[i]);
        }
    }

    private int indexOfExisting(AidBox aidBox) throws RouteException {
        if (aidBox == null) {
            throw new RouteException("A caixa é nula.");
        }
        int index = this.stops.indexOf(aidBox);
        if (index == -1) {
            throw new RouteException("A caixa " + aidBox.getCode() + " não faz parte da rota.");
        }
        return index;
    }

    private void validateNewStop(AidBox aidBox) throws RouteException {
        if (aidBox == null) {
            throw new RouteException("A caixa é nula.");
        }
        if (this.stops.contains(aidBox)) {
            throw new RouteException("A caixa " + aidBox.getCode() + " já faz parte da rota.");
        }
        if (!isCompatible(aidBox)) {
            throw new RouteException("O veículo " + this.vehicle.getCode() + " não transporta nenhum dos tipos de contentor da caixa " + aidBox.getCode() + ".");
        }
    }

    /**
     * Uma caixa é compatível se tiver pelo menos um contentor de um tipo que o veículo transporte.
     * (A versão original só verificava o primeiro contentor da caixa.)
     */
    private boolean isCompatible(AidBox aidBox) {
        for (Container container : aidBox.getContainers()) {
            if (this.vehicle.getCapacity(container.getType()) > 0) {
                return true;
            }
        }
        return false;
    }

    // ---------------------------------------------------------------- recolhas

    /**
     * Regista uma troca de contentores numa das paragens.
     *
     * @param pickup troca planeada
     * @throws IllegalArgumentException se a caixa não fizer parte da rota
     */
    void addPickup(Pickup pickup) {
        if (!this.stops.contains(pickup.getAidBox())) {
            throw new IllegalArgumentException("A caixa " + pickup.getAidBox().getCode() + " não faz parte da rota.");
        }
        this.pickups.add(pickup);
    }

    /** @return cópia das trocas planeadas nesta rota */
    public Pickup[] getPickups() {
        return this.pickups.toArray(Pickup[]::new);
    }

    // ---------------------------------------------------------------- distâncias

    /**
     * @return metros percorridos: base → 1.ª caixa → ... → última caixa → base
     * @throws IllegalStateException se faltar alguma distância nos dados
     */
    @Override
    public double getTotalDistance() {
        return sumLegs(true);
    }

    /**
     * @return minutos de viagem para o mesmo percurso de {@link #getTotalDistance()}
     * @throws IllegalStateException se faltar alguma distância nos dados
     */
    @Override
    public double getTotalDuration() {
        return sumLegs(false);
    }

    private double sumLegs(boolean meters) {
        if (this.stops.isEmpty()) {
            return 0;
        }
        try {
            AidBox first = this.stops.get(0);
            AidBox last = this.stops.get(this.stops.size() - 1);
            double total = meters ? this.institution.getDistance(first) : this.institution.getDuration(first);
            for (int i = 0; i < this.stops.size() - 1; i++) {
                AidBox from = this.stops.get(i);
                AidBox to = this.stops.get(i + 1);
                total += meters ? from.getDistance(to) : from.getDuration(to);
            }
            total += meters ? this.institution.getDistance(last) : this.institution.getDuration(last);
            return total;
        } catch (AidBoxException ex) {
            throw new IllegalStateException("Não é possível calcular a rota do veículo " + this.vehicle.getCode() + ": " + ex.getMessage(), ex);
        }
    }

    @Override
    public String toString() {
        StringBuilder text = new StringBuilder("Veículo ").append(this.vehicle.getCode()).append(": Base");
        for (int i = 0; i < this.stops.size(); i++) {
            text.append(" → ").append(this.stops.get(i).getCode());
        }
        return text.append(" → Base").toString();
    }
}
