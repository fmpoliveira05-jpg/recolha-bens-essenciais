package Core;

import com.estg.core.Container;
import com.estg.core.ContainerType;
import com.estg.core.Measurement;
import com.estg.core.exceptions.ContainerException;
import com.estg.core.exceptions.MeasurementException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Objects;

/*
* Nome: Francisco Miguel Pereira Oliveira
* Número: 8230148
* Turma: LEIT2
*/


/**
 * ContainerImp, classe que regista um contentor
 * 
 * @author Francisco Miguel Pereira Oliveira
 * @version 1.0
 * @since 2024-06-27
 */
public class ContainerImp implements Container {
    
    private final int INICIAL_MEASUREMENTS = 5;
    
    private String code; 
    
    private ContainerType type; 
    
    private int numMeasurements;
    
    private Measurement[] measurements;
    
    private double capacity; // capacidade máxima do contentor

    
    /**
    * Método construtor de ContainerImp
    * 
    * @param code código do contentor
    * @param type tipo de suplemento que o contentor carrega
    * @param capacity valor da capacidade máxima do contentor
    * @throws ContainerException se o valor da capacidade for negativo ou nulo
    */ 
    public ContainerImp(String code, ContainerType type, double capacity) throws ContainerException {
        this.code = code;
        this.type = type;
        this.measurements = new Measurement[INICIAL_MEASUREMENTS];
        if (capacity <= 0) {
            throw new ContainerException("O valor da  é inferior ou igual a 0!");
        } else {
            this.capacity = capacity;
        }
        this.numMeasurements = 0;
    }
    
    /**
    * Método construtor de ContainerImp (para ler ficheiro JSON)
    * 
    * @param code código do contentor
    */ 
    public ContainerImp(String code) {
        this.code = code;
        this.measurements = new Measurement[INICIAL_MEASUREMENTS];
        this.numMeasurements = 0;
    }
    
    /**
    * Método getter do código identificador do contentor
    * 
    * @return o código identificador do contentor
    */
    @Override
    public String getCode() {
        return this.code;
    }
    
    /**
    * Método getter da capacidade máxima do contentor
    * 
    * @return o valor da capacidade máxima do contentor
    */
    @Override
    public double getCapacity() {
        return this.capacity;
    }
    
    /**
    * Método setter da capacidade máxima do contentor
    * 
    * @param capacity valor da capacidade máxima do contentor
    */
    public void setCapacity(double capacity) {
        if (capacity > 0) {
            this.capacity = capacity;
        }
    }
    
    /**
    * Método getter do tipo de suprimentos que o contentor carrega
    * 
    * @return o tipo de suprimentos que o contentor carrega
    */
    @Override
    public ContainerType getType() {
        return this.type;
    }
    
    /**
    * Método setter do tiplo de suprimentos que o contentor carrega
    * 
    * @param type tipo de suprimentos que o contentor carrega
    */
    public void setType(ContainerType type) {
        this.type = type;
    }
    
    /**
    * Método getter do número de medições do contentor
    * 
    * @return o valor do número de medições do contentor
    */
    public int getNumMeasurements() {
        return this.numMeasurements;
    }
    
    /**
    * Método getter que retorna uma deep copy das medições já existentes
    * 
    * @return uma deep copy das medições do contentor
    */
    @Override
    public Measurement[] getMeasurements() {
        if (this.numMeasurements == this.measurements.length) {
            return this.measurements;
        }

        Measurement[] tmp = new Measurement[this.measurements.length];

        for (int i = 0; i < this.numMeasurements; i++) {
            tmp[i] = new MeasurementImp((MeasurementImp) this.measurements[i]);
        }

        return tmp;
    }
    
    /**
    * Método que retorna uma deep copy do conjunto de medições já existentes com a data recebida
    * 
    * @param ld data da medição do contentor
    * @return uma deep copy do conjunto de medições do contentor com a data recebida
    */
    @Override
    public Measurement[] getMeasurements(LocalDate ld) {
        int count_tmp = 0;
        
        if (this.measurements.length == this.numMeasurements) {
            return this.measurements;
        }
        
        Measurement[] tmp = new Measurement[this.numMeasurements];
        
        for (int i = 0; i < this.numMeasurements; i++) {
            if (this.measurements[i].getDate().toLocalDate().equals(ld)) {
                tmp[count_tmp++] = new MeasurementImp((MeasurementImp) this.measurements[i]);
            }
        }
        
        return tmp;
    }
    
    /**
    * Método que verifica se a medição já foi adicionada ao contentor
    * 
    * @param measurement medição do contentor
    * @return true, se o contentor tiver uma medição igual à que foi passada por parâmetro
    * false, se a medição não foi encontrada
    */
    private boolean findMeasurement(Measurement measurement) {
        for (int i = 0; i < this.numMeasurements; i++) {
            if (this.measurements[i].equals(measurement)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
    * Método que duplica o espaço do array measurements sempre que necessário.
    */
    private void raiseMeasurements() {
        Measurement[] tmp = new MeasurementImp[this.numMeasurements * 2];
        
        for (int i = 0; i < this.numMeasurements; i++) {
            tmp[i] = this.measurements[i];
        }
        
        this.measurements = tmp;
    }
    
    /**
    * Método que adiciona uma medição ao contentor
    * 
    * @param msrmnt medição do contentor
    * @return true, se a medição foi adicionada com sucesso
    * false, se não foi possível adicionar a medição
    * @throws MeasurementException, se a medição for nula, se o valor da medição for negativo,
    * se a data da medição for mais antiga do que a data da última medição introduzida,
    * ou se a medição já existe para uma dada data, mas tem um valor diferente da medição já existente no contentor
    */
    @Override
    public boolean addMeasurement(Measurement msrmnt) throws MeasurementException {
        if (msrmnt == null) {
            throw new MeasurementException("A medição é nula!");
        }
        
        if (msrmnt.getValue() < 0) {
            throw new MeasurementException("O valor da medição é negativo!");
        }
        
        if (this.capacity < msrmnt.getValue()) {
            throw new MeasurementException("O valor da medição é maior que a capacidade!");
        }

        if (this.numMeasurements > 0 && msrmnt.getDate().isBefore(this.measurements[this.numMeasurements - 1].getDate())) {
            throw new MeasurementException("A medição tem uma data anterior à data da última medição introduzida no contentor!");
        }
        
        for (Measurement measurement : this.getMeasurements()) {
            if (measurement != null && measurement.getDate().equals(msrmnt.getDate()) & measurement.getValue() != msrmnt.getValue()) {
                throw new MeasurementException("Já existe uma medição com a mesma data, mas tem um valor diferente da medição recebida!");
            }
        }
        
        if (this.numMeasurements >= this.measurements.length) {
            this.raiseMeasurements();
        }
        
        if (this.findMeasurement(msrmnt)) {
            return false;
        }
        
        this.measurements[this.numMeasurements++] = msrmnt;
        return true;
    }

    /** Método equals de ContainerImp
     * 
     * Compara um contentor com o objeto recebido
     * 
     * @param obj o objeto a ser comparado com um contentor
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
        final ContainerImp other = (ContainerImp) obj;
        if (this.numMeasurements != other.numMeasurements) {
            return false;
        }
        if (Double.doubleToLongBits(this.capacity) != Double.doubleToLongBits(other.capacity)) {
            return false;
        }
        if (!Objects.equals(this.code, other.code)) {
            return false;
        }
        if (!Objects.equals(this.type, other.type)) {
            return false;
        }
        return Arrays.deepEquals(this.measurements, other.measurements);
    }

    /**
    * Método toString de ContainerImp
    * 
    * @return os valores de type, measurements, code, capacity e numMeasurements
    */
    @Override
    public String toString() {
        return "ContainerImp{" + "code=" + code + ", type=" + type + ", numMeasurements=" + numMeasurements + ", measurements=" + Arrays.toString(measurements) + ", capacity=" + capacity + '}';
    }
    
    
}


