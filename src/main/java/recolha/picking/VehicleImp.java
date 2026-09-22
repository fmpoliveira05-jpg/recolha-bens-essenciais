package recolha.picking;

import com.estg.core.ContainerType;
import com.estg.pickingManagement.Vehicle;

/**
 * Veículo de recolha. Para cada tipo de contentor tem um número máximo de lugares
 * (por exemplo, 3 contentores de roupa e 2 de medicamentos).
 *
 * <p>A identidade de um veículo é a sua matrícula/código.</p>
 *
 * @author Francisco Miguel Pereira Oliveira
 */
public class VehicleImp implements Vehicle {

    private final String code;
    private final ContainerType[] types;
    private final int[] capacities;

    /**
     * @param code código do veículo
     * @param types tipos de contentor que consegue transportar
     * @param capacities número de contentores de cada tipo (mesma ordem que {@code types})
     * @throws IllegalArgumentException se os dados forem inconsistentes
     */
    public VehicleImp(String code, ContainerType[] types, int[] capacities) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("O veículo tem de ter código.");
        }
        if (types == null || capacities == null || types.length != capacities.length) {
            throw new IllegalArgumentException("Cada tipo de contentor tem de ter uma capacidade.");
        }
        for (int i = 0; i < types.length; i++) {
            if (types[i] == null || capacities[i] < 0) {
                throw new IllegalArgumentException("Tipo nulo ou capacidade negativa no veículo " + code + ".");
            }
            for (int j = i + 1; j < types.length; j++) {
                if (types[i].equals(types[j])) {
                    throw new IllegalArgumentException("O tipo " + types[i] + " aparece repetido no veículo " + code + ".");
                }
            }
        }
        this.code = code.trim();
        this.types = types.clone();
        this.capacities = capacities.clone();
    }

    @Override
    public String getCode() {
        return this.code;
    }

    /**
     * @param type tipo de contentor
     * @return quantos contentores desse tipo o veículo consegue levar (0 se não suportar o tipo)
     */
    @Override
    public double getCapacity(ContainerType type) {
        for (int i = 0; i < this.types.length; i++) {
            if (this.types[i].equals(type)) {
                return this.capacities[i];
            }
        }
        return 0;
    }

    /**
     * @param type tipo de contentor
     * @return {@code true} se o veículo tiver pelo menos um lugar para esse tipo
     */
    public boolean supports(ContainerType type) {
        return getCapacity(type) > 0;
    }

    /** @return cópia dos tipos suportados */
    public ContainerType[] getTypes() {
        return this.types.clone();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Vehicle)) {
            return false;
        }
        return this.code.equals(((Vehicle) obj).getCode());
    }

    @Override
    public int hashCode() {
        return this.code.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder text = new StringBuilder(this.code).append(" [");
        for (int i = 0; i < this.types.length; i++) {
            text.append(i > 0 ? ", " : "").append(this.types[i]).append(": ").append(this.capacities[i]);
        }
        return text.append(']').toString();
    }
}
