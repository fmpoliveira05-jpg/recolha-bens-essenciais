package recolha.alerts;

import java.time.LocalDateTime;

/**
 * Alerta gerado quando chegam dados inválidos (da API ou dos ficheiros).
 *
 * <p>O enunciado pede que o objeto problemático fique guardado, com a data da ocorrência,
 * para poder ser analisado mais tarde.</p>
 *
 * @author Francisco Miguel Pereira Oliveira
 */
public interface Alert {

    /** @return descrição do problema */
    String getMessage();

    /** @return o objeto que causou o alerta (texto JSON, leitura, contentor, ...) */
    Object getSubject();

    /** @return instante em que o problema foi detetado */
    LocalDateTime getDate();
}
