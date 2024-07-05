package Core;

import com.estg.core.Measurement;
import com.estg.core.exceptions.MeasurementException;
import java.time.LocalDateTime;
import java.util.Objects;

/*
* Nome: Francisco Miguel Pereira Oliveira
* Número: 8230148
* Turma: LEIT2
*/


/**
 * MeasurementImp, classe que regista a data de uma medição e o valor (em kg) de um contentor
 * 
 * @author Francisco Miguel Pereira Oliveira
 * @version 1.0
 * @since 2024-06-27
 */
public class MeasurementImp implements Measurement {
    
    private LocalDateTime date;
    
    private double value;
    
    /**
    * Método construtor de MeasurementImp
    * 
    * @param date data da medição (LocalDateTime)
    * @param value valor da medição (em Kg)
    * @throws MeasurementException se o valor da medição for negativo ou nulo
    */
    public MeasurementImp(LocalDateTime date, double value) throws MeasurementException {
        this.date = date;
        if (value <= 0) {
            throw new MeasurementException("O valor da medição é inferior ou igual a 0!");
        } else {
            this.value = value;
        }
    }
    
    /**
    * Método construtor de MeasurementImp (método de cópia)
    * 
    * @param other uma nova instância de MeasurementImp
    */
    public MeasurementImp(MeasurementImp other) {
        this.date = other.date;
        this.value = other.value;
    }
    
    /**
    * Método getter da data da medição (LocalDateTime)
    * 
    * @return o valor da data da medição (LocalDateTime)
    */
    @Override
    public LocalDateTime getDate() {
        return this.date;
    }
    
    /**
    * Método getter do valor da medição (em Kg)
    * 
    * @return o valor da medição (em Kg)
    */
    @Override
    public double getValue() { 
        return this.value;
    }

    /** Método equals de MeasurementImp
     * 
     * Compara uma medição com o objeto recebido
     * 
     * @param obj o objeto a ser comparado com uma medição
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
        final MeasurementImp other = (MeasurementImp) obj;
        if (Double.doubleToLongBits(this.value) != Double.doubleToLongBits(other.value)) {
            return false;
        }
        return Objects.equals(this.date, other.date);
    }

    /**
     * Método toString de MeasurementImp
     *
     * @return os valores de date e value
     */
    @Override
    public String toString() {
        return "MeasurementImp{" + "date=" + date + ", value=" + value + '}';
    }
    
    
}
