package Core;

import com.estg.core.ContainerType;
import java.util.Objects;

/*
* Nome: Francisco Miguel Pereira Oliveira
* Número: 8230148
* Turma: LEIT2
*/


/**
 * ContainerTypeImp, classe que regista tipos de containers e permite modificá-los ao longo do tempo
 * 
 * @author Francisco Miguel Pereira Oliveira
 * @version 1.0
 * @since 2024-06-27
 */
public class ContainerTypeImp implements ContainerType {
    private String type;
    
    /**
    * Método construtor de ContainerTypeImp
    * 
    * @param type nome que representa o tipo de container a instanciar
    */ 
    public ContainerTypeImp(String type) {
        this.type = type;
    }
    
    /**
    * Método getter do nome que representa o tipo de container
    * 
    * @return o nome que representa o tipo de container
    */
    public String getType() {
        return this.type;
    }
    
   /**
    * Método setter do nome que representa o tipo de container
    * 
    * @param type nome que representa o tipo de container
    */
    public void setType(String type) {
        this.type = type;
    }

    /** Método equals de ContainerTypeImp
     * 
     * Compara um tipo de container com o objeto recebido
     * 
     * @param obj o objeto a ser comparado com um tipo de container
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
        final ContainerTypeImp other = (ContainerTypeImp) obj;
        return Objects.equals(this.type, other.type);
    }

    @Override
    public String toString() {
        return "ContainerTypeImp{" + "type=" + type + '}';
    }

    
}
