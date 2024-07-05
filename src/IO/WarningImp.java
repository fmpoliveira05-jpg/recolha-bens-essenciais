package IO;

import com.estg.core.Container;
import com.estg.core.Measurement;
import java.time.LocalDateTime;
import java.util.Objects;

/*
* Nome: Francisco Miguel Pereira Oliveira
* Número: 8230148
* Turma: LEIT2
*/


/**
 * Classe WarningImp, classe que representa um alerta que guardará um objeto inválido
 * @author Francisco Miguel Pereira Oliveira
 * @version 1.0
 * @since 2024-06-27
*/
public class WarningImp implements Warning {
    String message;
    
    Measurement measurement;
    
    LocalDateTime date;
    
    /**
    * Método construtor de WarningImp.
    * 
    * @param message texto do alerta
    * @param measurement medição a guardar
    */
    public WarningImp(String message, Measurement measurement) {
        this.message = message;
        this.measurement = measurement;
        this.date = LocalDateTime.now();
    }
    
    /**
    * Método getter para o texto do alerta
    * 
    * @return o texto do alerta
    */
    @Override
    public String getMessage() {
        return this.message;
    }
    
    /**
    * Método getter para a medição guardada
    * 
    * @return a medição guardada
    */
    @Override
    public Measurement getMeasurement() {
        return this.measurement;
    }
    
    /**
    * Método getter para a data da ocorrência
    * 
    * @return a data da ocorrência
    */
    @Override
    public LocalDateTime getDate() {
        return this.date;
    }

    /** Método equals de WarningImp
     * 
     * Compara um alerta com o objeto recebido
     * 
     * @param obj o objeto a ser comparado com um alerta
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
        final WarningImp other = (WarningImp) obj;
        if (!Objects.equals(this.message, other.message)) {
            return false;
        }
        if (!Objects.equals(this.measurement, other.measurement)) {
            return false;
        }
        return Objects.equals(this.date, other.date);
    }

    /** Método toString de WarningImp
     * 
     * @return os valores de message, container e date
     */
    @Override
    public String toString() {
        return "WarningImp{" + "message=" + message + ", measurement=" + measurement + ", date=" + date + '}';
    }
    
    
    
    
}
