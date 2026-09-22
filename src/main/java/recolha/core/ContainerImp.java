package recolha.core;

import com.estg.core.Container;
import com.estg.core.ContainerType;
import com.estg.core.Measurement;
import com.estg.core.exceptions.ContainerException;
import com.estg.core.exceptions.MeasurementException;
import java.time.LocalDate;
import recolha.util.DynamicArray;

/**
 * Contentor instalado numa caixa de suprimentos (ou vazio, à espera de ser trocado).
 *
 * <p>Cada contentor guarda um único tipo de bens e o histórico de leituras do seu sensor.
 * A identidade de um contentor é o seu código: dois objetos com o mesmo código representam
 * o mesmo contentor físico, mesmo que tenham leituras diferentes.</p>
 *
 * @author Francisco Miguel Pereira Oliveira
 */
public class ContainerImp implements Container {

    /** Lotação (0 a 1) a partir da qual um contentor tem de ser recolhido. */
    public static final double PICKUP_THRESHOLD = 0.8;

    private final String code;
    private final ContainerType type;
    private final double capacity;
    private final DynamicArray<Measurement> measurements;

    /**
     * @param code código único do contentor
     * @param type tipo de bens que guarda
     * @param capacity capacidade máxima, em kg
     * @throws ContainerException se algum dos dados for inválido
     */
    public ContainerImp(String code, ContainerType type, double capacity) throws ContainerException {
        if (code == null || code.isBlank()) {
            throw new ContainerException("O contentor tem de ter código.");
        }
        if (type == null) {
            throw new ContainerException("O contentor " + code + " tem de ter tipo.");
        }
        if (capacity <= 0 || Double.isNaN(capacity)) {
            throw new ContainerException("A capacidade do contentor " + code + " tem de ser superior a 0.");
        }
        this.code = code.trim();
        this.type = type;
        this.capacity = capacity;
        this.measurements = new DynamicArray<>();
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public double getCapacity() {
        return this.capacity;
    }

    @Override
    public ContainerType getType() {
        return this.type;
    }

    /**
     * @return cópia de todas as leituras, da mais antiga para a mais recente
     */
    @Override
    public Measurement[] getMeasurements() {
        return this.measurements.toArray(Measurement[]::new);
    }

    /**
     * @param date dia pretendido
     * @return cópia das leituras feitas nesse dia (vetor vazio se não houver)
     */
    @Override
    public Measurement[] getMeasurements(LocalDate date) {
        DynamicArray<Measurement> sameDay = new DynamicArray<>();
        for (int i = 0; i < this.measurements.size(); i++) {
            Measurement measurement = this.measurements.get(i);
            if (measurement.getDate().toLocalDate().equals(date)) {
                sameDay.add(measurement);
            }
        }
        return sameDay.toArray(Measurement[]::new);
    }

    /**
     * Regista uma nova leitura.
     *
     * <p>Regras: o valor não pode ultrapassar a capacidade, as leituras chegam por ordem
     * cronológica e não pode haver duas leituras diferentes no mesmo instante. Repetir
     * exatamente a mesma leitura não é erro (acontece quando se importa duas vezes), apenas
     * é ignorado.</p>
     *
     * @param measurement leitura a registar
     * @return {@code true} se foi registada, {@code false} se já existia
     * @throws MeasurementException se a leitura violar alguma das regras
     */
    @Override
    public boolean addMeasurement(Measurement measurement) throws MeasurementException {
        if (measurement == null) {
            throw new MeasurementException("A medição é nula.");
        }
        if (measurement.getValue() < 0) {
            throw new MeasurementException("O valor da medição não pode ser negativo.");
        }
        if (measurement.getValue() > this.capacity) {
            throw new MeasurementException(String.format(
                    "A medição de %.1f kg ultrapassa a capacidade do contentor %s (%.1f kg).",
                    measurement.getValue(), this.code, this.capacity));
        }
        for (int i = 0; i < this.measurements.size(); i++) {
            Measurement existing = this.measurements.get(i);
            if (existing.getDate().equals(measurement.getDate())) {
                if (existing.getValue() == measurement.getValue()) {
                    return false;
                }
                throw new MeasurementException("Já existe uma medição diferente no mesmo instante para o contentor " + this.code + ".");
            }
        }
        Measurement last = getLastMeasurement();
        if (last != null && measurement.getDate().isBefore(last.getDate())) {
            throw new MeasurementException("A medição é anterior à última leitura do contentor " + this.code + ".");
        }
        this.measurements.add(measurement);
        return true;
    }

    /**
     * @return a leitura mais recente ou {@code null} se ainda não houver leituras
     */
    public Measurement getLastMeasurement() {
        return this.measurements.isEmpty() ? null : this.measurements.get(this.measurements.size() - 1);
    }

    /**
     * @return lotação atual entre 0 e 1 (0 se ainda não houver leituras)
     */
    public double getFillRatio() {
        Measurement last = getLastMeasurement();
        return last == null ? 0 : last.getValue() / this.capacity;
    }

    /**
     * Aplica as regras de recolha do enunciado.
     *
     * @return {@code true} se o contentor tiver alimentos perecíveis (com algum conteúdo) ou se
     *         estiver acima de 80% da capacidade
     */
    public boolean needsPickup() {
        Measurement last = getLastMeasurement();
        if (last == null) {
            return false;
        }
        if (ContainerTypeImp.isPerishable(this.type)) {
            return last.getValue() > 0;
        }
        return getFillRatio() > PICKUP_THRESHOLD;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Container)) {
            return false;
        }
        return this.code.equals(((Container) obj).getCode());
    }

    @Override
    public int hashCode() {
        return this.code.hashCode();
    }

    @Override
    public String toString() {
        return String.format("%s (%s, %.0f kg, %.0f%% cheio)", this.code, this.type, this.capacity, getFillRatio() * 100);
    }
}
