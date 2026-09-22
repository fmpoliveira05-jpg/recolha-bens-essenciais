package recolha.alerts;

import recolha.util.DynamicArray;

/**
 * Registo de todos os alertas produzidos durante a utilização da aplicação.
 *
 * @author Francisco Miguel Pereira Oliveira
 */
public class AlertManager {

    private final DynamicArray<Alert> alerts = new DynamicArray<>();

    /**
     * Regista um alerta.
     *
     * @param alert alerta a guardar
     * @throws IllegalArgumentException se o alerta for nulo
     */
    public void add(Alert alert) {
        if (alert == null) {
            throw new IllegalArgumentException("O alerta é nulo.");
        }
        this.alerts.add(alert);
    }

    /**
     * Atalho para criar e registar um alerta.
     *
     * @param message descrição do problema
     * @param subject objeto que causou o alerta
     */
    public void add(String message, Object subject) {
        add(new AlertImp(message, subject));
    }

    /** @return número de alertas registados */
    public int count() {
        return this.alerts.size();
    }

    /** @return cópia dos alertas, pela ordem em que foram registados */
    public Alert[] getAll() {
        return this.alerts.toArray(Alert[]::new);
    }
}
