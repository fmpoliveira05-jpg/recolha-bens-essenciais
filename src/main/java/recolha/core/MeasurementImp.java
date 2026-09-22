package recolha.core;

import com.estg.core.Measurement;
import com.estg.core.exceptions.MeasurementException;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Leitura enviada pelo sensor de um contentor: quantos quilos tinha num dado instante.
 *
 * <p>Os objetos são imutáveis, por isso podem ser partilhados sem cópias defensivas.</p>
 *
 * @author Francisco Miguel Pereira Oliveira
 */
public final class MeasurementImp implements Measurement {

    private final LocalDateTime date;
    private final double value;

    /**
     * @param date instante da leitura
     * @param value peso lido, em kg (0 significa contentor vazio)
     * @throws MeasurementException se a data for nula ou o valor for negativo
     */
    public MeasurementImp(LocalDateTime date, double value) throws MeasurementException {
        if (date == null) {
            throw new MeasurementException("A medição tem de ter data.");
        }
        if (value < 0 || Double.isNaN(value)) {
            throw new MeasurementException("O valor da medição não pode ser negativo.");
        }
        this.date = date;
        this.value = value;
    }

    @Override
    public LocalDateTime getDate() {
        return this.date;
    }

    @Override
    public double getValue() {
        return this.value;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof MeasurementImp)) {
            return false;
        }
        MeasurementImp other = (MeasurementImp) obj;
        return Double.compare(this.value, other.value) == 0 && this.date.equals(other.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.date, this.value);
    }

    @Override
    public String toString() {
        return String.format("%.1f kg em %s", this.value, this.date);
    }
}
