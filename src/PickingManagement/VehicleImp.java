package PickingManagement;

import com.estg.core.Container;
import com.estg.core.ContainerType;
import com.estg.core.exceptions.VehicleException;
import com.estg.pickingManagement.Vehicle;
import java.util.Arrays;
import java.util.Objects;

/*
 * Nome: Francisco Miguel Pereira Oliveira
 * Número: 8230148
 * Turma: LEIT2
 */


/**
 * VehicleImp, classe que representa os veículos para transporte dos contentores
 * 
 * @author Francisco Miguel Pereira Oliveira
 * @version 1.0
 * @since 2024-06-27
 */
public class VehicleImp implements Vehicle {
    private String code;
    
    private Container[] containers; // conjunto de containers que o veículo transporta (inicialmente vazios)
   
    private ContainerType[] types; // conjunto dos tipos de containers que o veículo poderá transportar
    
    public int[] capacities; // conjunto das capacidades máximas (número de containers) do veículo para cada tipo de container
    
    private int[] currentCapacities; // conjunto das capacidades atuais do veículo, uma para cada tipo de container que carrega
    
    /**
    * Método construtor de VechicleImp
    * 
    * @param code código do veículo
    * @param types conjunto dos tipos de containers que o veículo poderá transportar
    * @param capacities número de containers que o veículo vai transportar para cada tipo
    */
    public VehicleImp(String code, ContainerType[] types, int[] capacities) {
        this.code = code;
        this.types = types;
        this.capacities = capacities;
        this.currentCapacities = new int[types.length];
        this.containers = new Container[getNumContainers()];
    }

    /**
    * Método construtor de VehicleImp (método de cópia)
    * 
    * @param other uma nova instância de VehicleImp
    */
    public VehicleImp(VehicleImp other) {
        this.code = other.code;
        this.containers = other.containers;
        this.types = other.types;
        this.capacities = other.capacities;
        this.currentCapacities = other.currentCapacities;
    }
    
    /**
    * Método que calcula o número de containers necessários a transportar pelo veículo
    * 
    * @return o número de containers que o veículo vai transportar
    */
    private int getNumContainers() {
        int numContainers = 0;
        
        for (int i = 0; i < this.capacities.length; i++) {
            numContainers += this.capacities[i];
        }
        
        return numContainers;
    }

    /**
    * Método getter para o código do veículo
    * 
    * @return o código do veículo
    */
    @Override
    public String getCode() {
        return this.code;
    }
    
    /**
    * Método getter para o conjunto de containers que o veículo transporta
    * 
    * @return o conjunto de containers que o veículo transporta
    */
    public Container[] getContainers() {
        return this.containers;
    }
    
    /**
    * Método getter para o conjunto de tipos de containers que o veículo transporta
    * 
    * @return o conjunto de tipos de containers que o veículo transporta
    */
    public ContainerType[] getTypes() {
        return this.types;
    }
    
    /**
    * Método que verifica se um container vazio de um certo tipo é transportado pelo veículo
    * 
    * @param type tipo de container
    * @return j, a posição do container, caso seja encontrado
    * -1, caso não seja encontrado
    */
    public int findContainer(ContainerType type) {
        for (int i = 0; i < types.length; i++) {
            if (this.types[i].equals(type)) {
                for (int j = 0; j < containers.length; j++) {
                    if (this.containers[j] == null) {
                        return j;
                    }
                }
            }
        }
        
        return -1;
    }
    
    /**
    * Método que verifica se um determinado container é transportado pelo veículo
    * 
    * @param container container a verificar
    * @return i, a posição do container, caso seja encontrado
    * -1, caso não seja encontrado
    */
    public int findContainer(Container container) {
        for (int i = 0; i < containers.length; i++) {
            if (this.containers[i] != null && this.containers[i].equals(container)) {
                return i;
            }
        }
        
        return -1;
    }
    
    /**
    * Método para substituir um container vazio por outro container na recolha de containers
    * 
    * @param pos_container posição do container origem
    * @param container1 container destino
    * @throws VehicleException se o container destino for nulo,
    * se o container origem não pertencer ao veículo,
    * se o container destino já pertencer ao veículo
    */
    public void replaceContainer(int pos_container, Container container1) throws VehicleException {
        if (container1 == null) {
            throw new VehicleException("O container destino é nulo!");
        }
        
        if (pos_container == -1) {
            throw new VehicleException("O container origem não pertence ao veículo!");
        }
        
        if (this.findContainer(container1) != -1) {
            throw new VehicleException("O container destino já pertence ao veículo!");
        }     
        
        this.containers[pos_container] = container1;
    }
    
    /**
    * Método que retorna a capacidade atual do veículo para um determinado tipo de contentores
    * 
    * @param type tipo de container
    * @return a capacidade atual do veículo para um determinado tipo de contentores
    */
    public double getCurrentCapacity(ContainerType type) {
        for (int i = 0; i < this.types.length; i++) {
            if (this.types[i].equals(type)) {
                return (double) this.currentCapacities[i];
            }
        }
        return 0.0;
    }
    
    /**
    * Método para atualizar a capacidade atual do veículo para um determinado tipo de contentores.
    * @param type tipo de container
    */
    public void setCurrentCapacity(ContainerType type) { 
        for (int i = 0; i < this.types.length; i++) {
            if (this.types[i].equals(type)) {
                this.currentCapacities[i]++;
            }
        }
    }
    
    /**
    * Método para reiniciar a capacidade atual do veículo para 0 contentores (para um determinado tipo de containers)
    * Usado quando o veículo precisar de descarregar a carga na base.
    *
    */
    public void resetCurrentCapacity() {
        for (int i = 0; i < this.currentCapacities.length; i++) {
            this.currentCapacities[i] = 0;
        }
    }

    /**
    * Método que retorna a capacidade máxima (número de containers) do veículo para um determinado tipo
    * 
    * @param ct tipo de container
    * @return o número de containers que o veículo poderá transportar para um determinado tipo
    */
    @Override
    public double getCapacity(ContainerType ct) {
        for (int i = 0; i < this.types.length; i++) {
            if (this.types[i].equals(ct)) {
                return (double) this.capacities[i];
            }
        }
        return 0.0;
    }
    
    /**
    * Método que verifica se um determinado tipo de container é transportado pelo veículo
    * 
    * @param type tipo de container
    * @return true, se o tipo de container for encontrado
    * false, caso contrário
    */
    public boolean getType(ContainerType type) {
        for (int i = 0; i < this.types.length; i++) {
            if (this.types[i].equals(type)) {
                return true;
            }
        }
        return false;
    }

    /** Método equals de VehicleImp
     * 
     * Compara um veículo com o objeto recebido
     * 
     * @param obj o objeto a ser comparado com um veículo
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
        final VehicleImp other = (VehicleImp) obj;
        if (!Objects.equals(this.code, other.code)) {
            return false;
        }
        if (!Arrays.deepEquals(this.containers, other.containers)) {
            return false;
        }
        if (!Arrays.deepEquals(this.types, other.types)) {
            return false;
        }
        if (!Arrays.equals(this.capacities, other.capacities)) {
            return false;
        }
        return Arrays.equals(this.currentCapacities, other.currentCapacities);
    }

    /**
     * Método toString de VehicleImp, que retorna a representação em string de um veículo
     *
     * @return uma string que representa um veículo (contém a matrícula, a capacidade máxima e o tipo de suprimento que carrega).
     */
    @Override
    public String toString() {
        return "VehicleImp{" + "code=" + code + ", containers=" + Arrays.toString(containers) + ", types=" + Arrays.toString(types) + ", capacities=" + Arrays.toString(capacities) + ", currentCapacities=" + Arrays.toString(currentCapacities) + '}';
    }
    
    
}
