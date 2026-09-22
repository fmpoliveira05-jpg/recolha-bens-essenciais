package recolha.picking;

import com.estg.pickingManagement.Report;
import java.time.LocalDateTime;

/**
 * Resumo de uma geração de rotas. É imutável: os valores são calculados pelo
 * {@link RouteGeneratorImp} e ficam fixos a partir daí.
 *
 * @author Francisco Miguel Pereira Oliveira
 */
public final class ReportImp implements Report {

    private final LocalDateTime date;
    private final int usedVehicles;
    private final int notUsedVehicles;
    private final int pickedContainers;
    private final int nonPickedContainers;
    private final double totalDistance;
    private final double totalDuration;

    /**
     * @param date instante em que as rotas foram geradas
     * @param usedVehicles veículos com pelo menos uma paragem
     * @param notUsedVehicles veículos ativos que ficaram na base
     * @param pickedContainers contentores que serão recolhidos
     * @param nonPickedContainers contentores que precisavam de recolha mas não couberam em nenhum veículo
     * @param totalDistance soma das distâncias de todas as rotas, em metros
     * @param totalDuration soma das durações de todas as rotas, em minutos
     */
    public ReportImp(LocalDateTime date, int usedVehicles, int notUsedVehicles, int pickedContainers,
            int nonPickedContainers, double totalDistance, double totalDuration) {
        this.date = date;
        this.usedVehicles = usedVehicles;
        this.notUsedVehicles = notUsedVehicles;
        this.pickedContainers = pickedContainers;
        this.nonPickedContainers = nonPickedContainers;
        this.totalDistance = totalDistance;
        this.totalDuration = totalDuration;
    }

    @Override
    public LocalDateTime getDate() {
        return this.date;
    }

    @Override
    public int getUsedVehicles() {
        return this.usedVehicles;
    }

    @Override
    public int getNotUsedVehicles() {
        return this.notUsedVehicles;
    }

    @Override
    public int getPickedContainers() {
        return this.pickedContainers;
    }

    @Override
    public int getNonPickedContainers() {
        return this.nonPickedContainers;
    }

    @Override
    public double getTotalDistance() {
        return this.totalDistance;
    }

    @Override
    public double getTotalDuration() {
        return this.totalDuration;
    }

    @Override
    public String toString() {
        return String.format("Veículos usados: %d | parados: %d | contentores recolhidos: %d | por recolher: %d | %.1f km | %.0f min",
                this.usedVehicles, this.notUsedVehicles, this.pickedContainers, this.nonPickedContainers,
                this.totalDistance / 1000, this.totalDuration);
    }
}
