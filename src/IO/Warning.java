package IO;

import com.estg.core.Measurement;
import java.time.LocalDateTime;

/*
* Nome: Francisco Miguel Pereira Oliveira
* Número: 8230148
* Turma: LEIT2
*/


public interface Warning {
    
    public String getMessage();
    
    public Measurement getMeasurement();
    
    public LocalDateTime getDate();
}
