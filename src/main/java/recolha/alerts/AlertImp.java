package recolha.alerts;

import java.time.LocalDateTime;

/**
 * Implementação imutável de {@link Alert}.
 *
 * @author Francisco Miguel Pereira Oliveira
 */
public final class AlertImp implements Alert {

    private final String message;
    private final Object subject;
    private final LocalDateTime date;

    /**
     * @param message descrição do problema
     * @param subject objeto que causou o alerta
     */
    public AlertImp(String message, Object subject) {
        this(message, subject, LocalDateTime.now());
    }

    /**
     * @param message descrição do problema
     * @param subject objeto que causou o alerta
     * @param date instante da ocorrência
     */
    public AlertImp(String message, Object subject, LocalDateTime date) {
        this.message = message;
        this.subject = subject;
        this.date = date;
    }

    @Override
    public String getMessage() {
        return this.message;
    }

    @Override
    public Object getSubject() {
        return this.subject;
    }

    @Override
    public LocalDateTime getDate() {
        return this.date;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s -> %s", this.date.withNano(0), this.message, this.subject);
    }
}
