package PickingManagement;

import com.estg.pickingManagement.Report;
import java.time.LocalDateTime;

/*
* Nome: Francisco Miguel Pereira Oliveira
* Número: 8230148
* Turma: LEIT2
*/


/**
 * ReportImp, classe que geras as estatísticas das rotas geradas
 * 
 * @author Francisco Miguel Pereira Oliveira
 * @version 1.0
 * @since 2024-06-27
 */
public class ReportImp implements Report {
    private int numUsedVehicles;
    
    private int numPickedContainers;
    
    private double totalDistance;
    
    private double totalDuration;
    
    private int numNonPickedContainers;
    
    private int numNotUsedVehicles;
    
    /**
    * Método construtor de ReportImp.
    * 
    */ 
    public ReportImp() {
        this.numUsedVehicles = 0;
        this.numPickedContainers = 0;
        this.totalDistance = 0;
        this.totalDuration = 0;
        this.numNonPickedContainers = 0;
        this.numNotUsedVehicles = 0;
    }
    
    /**
    * Método getter para o número de veículos usados na recolha de containers.
    * 
    * @return o número de veículos usados
    */
    @Override
    public int getUsedVehicles() {
        return this.numUsedVehicles;
    }
    
    /**
    * Método setter para o número de veículos usados na recolha de containers.
    * 
    */
    public void setUsedVehicles() {
        this.numUsedVehicles++;
    }

    /**
    * Método getter para o número de containers recolhidos.
    * 
    * @return o número de containers recolhidos
    */
    @Override
    public int getPickedContainers() {
        return numPickedContainers;
    }
    
    /**
    * Método getter para o número de containers recolhidos.
    * 
    */
    public void setPickedContainers() {
        this.numPickedContainers++;
    }

    /**
    * Método getter para a distância total abrangida pelos veículos.
    * 
    * @return a distância total abrangida pelos veículos
    */
    @Override
    public double getTotalDistance() {
        return totalDistance;
    }
    
    /**
    * Método setter para a distância total abrangida pelos veículos.
    * 
    * @param distance valor da distância
    * 
    */
    public void setTotalDistance(double distance) {
        this.totalDistance += distance;
    }

    /**
    * Método getter para o tempo total gasto pelos veículos na recolha de containers.
    * 
    * @return o tempo total gasto pelos veículos
    */
    @Override
    public double getTotalDuration() {
        return totalDuration;
    }
    
    /**
    * Método setter para o tempo total gasto pelos veículos na recolha de containers.
    * 
    * @param duration tempo total gasto pelos veículos
    */
    public void setTotalDuration(double duration) {
        this.totalDuration += duration;
    }

    /**
    * Método getter para o número de containers não recolhidos.
    * 
    * @return o número de containers não recolhidos
    */
    @Override
    public int getNonPickedContainers() {
        return numNonPickedContainers;
    }
    
    /**
    * Método setter para o número de containers não recolhidos.
    * 
    */
    public void setNonPickedContainers() {
        this.numNonPickedContainers++;
    }

    /**
    * Método getter para o número de veículos não usados.
    * 
    * @return o número de veículos não usados
    */
    @Override
    public int getNotUsedVehicles() {
        return numNotUsedVehicles;
    }
    
    /**
    * Método setter para o número de veículos não usados.
    * 
    */
    public void setNotUsedVehicles() {
        this.numNotUsedVehicles++;
    }
    
    /**
    * Método getter para a data do report
    * 
    * @return a data do report
    */
    @Override
    public LocalDateTime getDate() {
        return LocalDateTime.now();
    }

    /** Método equals de ReportImp
     * 
     * Compara um report com o objeto recebido
     * 
     * @param obj o objeto a ser comparado com um report
     * @return true, se os objetos forem iguais,
     * false, caso contrário
     * 
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ReportImp other = (ReportImp) obj;
        if (this.numUsedVehicles != other.numUsedVehicles) {
            return false;
        }
        if (this.numPickedContainers != other.numPickedContainers) {
            return false;
        }
        if (Double.doubleToLongBits(this.totalDistance) != Double.doubleToLongBits(other.totalDistance)) {
            return false;
        }
        if (Double.doubleToLongBits(this.totalDuration) != Double.doubleToLongBits(other.totalDuration)) {
            return false;
        }
        if (this.numNonPickedContainers != other.numNonPickedContainers) {
            return false;
        }
        return this.numNotUsedVehicles == other.numNotUsedVehicles;
    }

    @Override
    public String toString() {
        return "ReportImp{" + "numUsedVehicles=" + numUsedVehicles + ", numPickedContainers=" + numPickedContainers + ", totalDistance=" + totalDistance + ", totalDuration=" + totalDuration + ", numNonPickedContainers=" + numNonPickedContainers + ", numNotUsedVehicles=" + numNotUsedVehicles + '}';
    }
    
    
}
