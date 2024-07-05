package Core;

import com.estg.core.AidBox;
import com.estg.core.Container;
import com.estg.core.ContainerType;
import com.estg.core.exceptions.AidBoxException;
import com.estg.core.exceptions.ContainerException;
import java.util.Arrays;
import java.util.Objects;

/*
* Nome: Francisco Miguel Pereira Oliveira
* Número: 8230148
* Turma: LEIT2
*/

/**
 * AidBoxImp, classe que representa as aid boxes
 * 
 * @author Francisco Miguel Pereira Oliveira
 * @version 1.0
 * @since 2024-06-27
 */

public class AidBoxImp implements AidBox {
    private final int INICIAL_SIZE = 4;
    
    private String code;
    
    private int numContainers;
    
    private Container[] containers;
    
    private String zone;
    
    private int numDistances;
    
    private Distance[] distances; // conjunto das distâncias entre uma aid box e as restantes

    /**
    * Método construtor de AidBoxImp
    * 
    * @param code código da aid box, único para cada uma;
    * @param zone Zona onde está a aid box
    */   
    public AidBoxImp(String code, String zone) {
        this.code = code;
        this.numContainers = 0;
        this.containers = new Container[INICIAL_SIZE];
        this.zone = zone;
        this.numDistances = 0;
        this.distances = new Distance[INICIAL_SIZE];
    }
    
    /**
    * Método construtor de AidBoxImp (para ler do ficheiro JSON)
    * 
    * @param code código da aid box, único para cada uma;
    */   
    public AidBoxImp(String code) {
        this.code = code;
        this.numContainers = 0;
        this.containers = new Container[INICIAL_SIZE];
        this.numDistances = 0;
        this.distances = new Distance[INICIAL_SIZE];
    }

    /**
    * Método construtor de AidBoxImp (método de cópia)
    * 
    * @param other uma nova instância de AidBoxImp
    */
    public AidBoxImp(AidBoxImp other) {
        this.code = other.code;
        this.numContainers = other.numContainers;
        this.containers = other.containers;
        this.zone = other.zone;
        this.numDistances = other.numDistances;
        this.distances = other.distances;
    }
    
    /**
    * Método getter para obter o código da aid box
    * 
    * @return o código da aid box
    */
    @Override
    public String getCode() {
        return this.code;
    }

    /**
    * Método getter para obter a zona da aid box
    * 
    * @return a zona da aid box
    */
    @Override
    public String getZone() {
        return this.zone;
    }
    
    /**
    * Método getter para obter o número de containers que a aid box tem
    * 
    * @return o número de containers da aid box
    */
    private int getNumContainers() {
        return this.numContainers;
    }    
    
    /**
     * Método que verifica se uma aid box existe no array de Distances
     * 
     * @param aidbox, aid box destino
     * @return i, posição da aid box, se esta for encontrada no Array de Distances
     * @return -1, caso não seja encontrada
     */
    private int findAidBox(AidBox aidbox) {
        for (int i = 0; i < this.numDistances; i++) {
            if (aidbox != null && this.distances[i].getAidDestino().equals(aidbox.getCode())) {
                return i;
            }
        }
        
        return -1;
    }

    /**
    * Método que duplica o espaço do array distance sempre que necessário.
    */
    private void raiseDistances() {
        Distance[] tmp = new Distance[this.numDistances * 2];
        
        for (int i = 0; i < this.numDistances; i++) {
            tmp[i] = this.distances[i];
        }
        
        this.distances = tmp;
    }
    
    /**
     * Método que adiciona uma nova distância à aid box
     * 
     * @param distance nova distância a adicionar
     * @return true, se conseguiu adicionar
     */
    public boolean addDistance(Distance distance) {
        if (this.numDistances >= this.distances.length) {
            this.raiseDistances();
        }
        
        this.distances[this.numDistances++] = distance;
        return true;
    }
    
    /**
    * Método getter para a distância entre a aid box atual e a aidbox recebida
    * 
    * @param aidbox aid box alvo
    * @return a distância (em metros) entre duas aid boxes
    * @throws AidBoxException, se a aid box alvo não existir
    */
    @Override
    public double getDistance(AidBox aidbox) throws AidBoxException { 
        int pos = this.findAidBox(aidbox);
        
        if (pos == -1) {            
            throw new AidBoxException("A aid box não existe!");
        }
                
        return this.distances[pos].getDistance();
    }
       
    /**
    * Método getter para a duração entre a aid box atual e a aid box recebida
    * 
    * @param aidbox aid box alvo
    * @return o tempo (em minutos) que demora a chegar de uma aid box à outra
    * @throws AidBoxException, se a aid box alvo não existir
    */
    @Override
    public double getDuration(AidBox aidbox) throws AidBoxException {        
        int pos = this.findAidBox(aidbox); 
        
        if (pos == -1) {
            throw new AidBoxException("A aid box não existe!");
        }
                
        return this.distances[pos].getDuration();
    }
        
    /**
    * Método para comparar se o container atual é igual ao container recebido
    * 
    * @param container container a comparar
    * @return true, se os containers forem iguais
    * false, se os containers forem diferentes
    */
    private boolean findContainer(Container container) {
        for (int i = 0; i < this.numContainers; i++) {
            if (this.containers[i].equals(container)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
    * Método para comparar se o container atual tem o mesmo tipo que o container recebido
    * 
    * @param container container a comparar
    * @return true, se os containers forem do mesmo tipo
    * false, se os containers não forem do mesmo tipo 
    */
    private boolean containerTypeExists(Container container) {
        for (int i = 0; i < this.numContainers; i++) {
            if (this.containers[i].getType() == container.getType()) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
    * Método que duplica o espaço do array containers sempre que necessário.
    */
    private void raiseContainers() {
        Container[] tmp = new ContainerImp[this.numContainers * 2];
        
        for (int i = 0; i < this.numContainers; i++) {
            tmp[i] = this.containers[i];
        }
        
        this.containers = tmp;
    }
    
    /**
    * Método para adicionar um novo container à aid box
    * 
    * @param cntnr container a adicionar
    * @return true, se o container foi inserido na lista de containers
    * @throws ContainerException, se o container é null ou 
    * se a aid box já tiver um container daquele tipo
    */
    @Override
    public boolean addContainer(Container cntnr) throws ContainerException {
        if (cntnr == null) {
            throw new ContainerException("O container está nulo!");
        }
        
        if (cntnr.getType() != null && this.containerTypeExists(cntnr)) {
            throw new ContainerException("Já existe um container do tipo " + cntnr.getType() + " !");
        }
        
        if (this.findContainer(cntnr)) {
            System.out.println("O container que tentou introduzir já existe!");
            return false;
        }

        if (this.numContainers >= this.containers.length) {
            this.raiseContainers();
        }

        this.containers[this.numContainers++] = cntnr;
        return true;
    }
    
    /**
    * Método para retirar um container da aid box
    * 
    * @param cntnr container a remover
    * @throws AidBoxException
    */
    @Override
    public void removeContainer(Container cntnr) throws AidBoxException {
        if (this.findContainer(cntnr) == false) {
            throw new AidBoxException("O container não existe!");
        }
        
        for (int i = 0; i < this.numContainers; i++) {
            if (this.containers[i].equals(cntnr)) {
                for (int j = i; j < this.numContainers - 1; j++) {
                    this.containers[j] = this.containers[j + 1];
                }
                this.containers[this.numContainers - 1] = null;
                this.numContainers--;
            }
        }
        
        System.out.println("O container com código " + cntnr.getCode() + " foi eliminado!");
    }

    /**
    * Método que, considerando o tipo, retorna o container correspondente
    * 
    * @param ct tipo do container
    * @return o container, caso exista um container do tipo recebido numa certa aid box
    * null, se o container daquele tipo não existir
    */
    @Override
    public Container getContainer(ContainerType ct) {
        for (int i = 0; i < this.numContainers; i++) {
            if (this.containers[i].getType().equals(ct)) {
                return this.containers[i];
            }
        }
   
        return null;
    }
        
    /**
    * Método que devolve uma shallow copy dos containers já existentes
    * @return uma shallow copy dos containers já existentes
    */
    @Override
    public Container[] getContainers() { 
        if (this.numContainers == this.containers.length) {
            return this.containers;
        }
                
        Container[] tmp = new Container[this.numContainers];
            
        for (int i = 0; i < this.numContainers; i++) {
            tmp[i] = this.containers[i];
        }
        
        return tmp;
    }
   
    /** Método equals de AidBoxImp
     * 
     * Compara uma aid box com o objeto recebido
     * 
     * @param obj o objeto a ser comparado com uma aid box
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
        final AidBoxImp other = (AidBoxImp) obj;
        if (this.numContainers != other.numContainers) {
            return false;
        }
        if (this.numDistances != other.numDistances) {
            return false;
        }
        if (!Objects.equals(this.code, other.code)) {
            return false;
        }
        if (!Objects.equals(this.zone, other.zone)) {
            return false;
        }
        if (!Arrays.deepEquals(this.containers, other.containers)) {
            return false;
        }
        return Arrays.deepEquals(this.distances, other.distances);
    }

    /**
    * Método toString de AidBoxImp, que retorna a representação em string de uma aid box.
    *
    * @return uma string que representa uma aid box (contém o código da aid box, número total de containers,
    * zona, referência do local e coordenadas geográficas, etc).
    */
    @Override
    public String toString() {
        return "AidBoxImp{" + "code=" + code + ", numContainers=" + numContainers + ", containers=" + Arrays.toString(containers) + ", zone=" + zone + ", numDistances=" + numDistances + ", distance=" + Arrays.toString(distances) + '}';
    }
    
    
}
