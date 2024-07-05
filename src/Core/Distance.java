package Core;

import java.util.Objects;

/*
 * Nome: Francisco Miguel Pereira Oliveira
 * Número: 8230148
 * Turma: LEIT2
*/

/**
 * Distance, classe para guardar a distância e a duração entre duas aid boxes
 * 
 * @author Francisco Miguel Pereira Oliveira
 * @version 1.0
 * @since 2024-06-27
 */
public class Distance {
    private String aidboxDestino; 
    
    private double distance;

    private double duration; 

    /**
    * Método construtor de Distance
    * 
    * @param aidboxDestino código da aid box destino
    * @param distance valor da distância de uma aid box para a aid box destino
    * @param duration valor da duração de uma aid box para a aid box destino
    */ 
    public Distance(String aidboxDestino, double distance, double duration) {
        this.aidboxDestino = aidboxDestino;
        if (distance <= 0) {
            this.distance = 0;
        } else {
            this.distance = distance;
        }
        if (duration <= 0) {
            this.duration = 0;
        } else {
            this.duration = duration;
        }
    }

    /**
    * Método getter do código da aid box destino
    * 
    * @return o código da aid box destino
    */
    public String getAidDestino() {
        return this.aidboxDestino;
    }

    /**
    * Método getter da distância entre a aid box origem e a aid box destino
    * 
    * @return a distância entre a aid box origem e a aid box destino
    */
    public double getDistance() {
        return this.distance;
    }

    /**
    * Método getter da duração entre a aid box origem e a aid box destino
    * 
    * @return a duração entre a aid box origem e a aid box destino
    */
    public double getDuration() {
        return this.duration;
    }

    /** Método equals de Distance
     * 
     * Compara uma distance com o objeto recebido
     * 
     * @param obj o objeto a ser comparado com uma distance
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
        final Distance other = (Distance) obj;
        if (Double.doubleToLongBits(this.distance) != Double.doubleToLongBits(other.distance)) {
            return false;
        }
        if (Double.doubleToLongBits(this.duration) != Double.doubleToLongBits(other.duration)) {
            return false;
        }
        return Objects.equals(this.aidboxDestino, other.aidboxDestino);
    }

    /**
     * Método toString de Distance
     *
     * @return o código da aid box destino, a distância e a duração
     */
    @Override
    public String toString() {
        return "Distance{" + "aidboxDestino=" + aidboxDestino + ", distance=" + distance + ", duration=" + duration + '}';
    }

    
}
