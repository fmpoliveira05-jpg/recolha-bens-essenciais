package recolha.core;

import com.estg.core.AidBox;
import com.estg.core.Container;
import com.estg.core.ContainerType;
import com.estg.core.exceptions.AidBoxException;
import com.estg.core.exceptions.ContainerException;
import recolha.util.DynamicArray;

/**
 * Caixa de suprimentos: um ponto de entrega onde estão instalados vários contentores,
 * no máximo um por tipo de bens.
 *
 * <p>Cada caixa conhece a distância a que está das outras caixas. A identidade de uma caixa
 * é o seu código.</p>
 *
 * @author Francisco Miguel Pereira Oliveira
 */
public class AidBoxImp implements AidBox {

    private final String code;
    private final String zone;
    private final DynamicArray<Container> containers;
    private final DynamicArray<Distance> distances;

    /**
     * @param code código único da caixa
     * @param zone zona onde está instalada (pode ser desconhecida, isto é, {@code null})
     * @throws IllegalArgumentException se o código for vazio
     */
    public AidBoxImp(String code, String zone) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("A caixa de suprimentos tem de ter código.");
        }
        this.code = code.trim();
        this.zone = zone;
        this.containers = new DynamicArray<>();
        this.distances = new DynamicArray<>();
    }

    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getZone() {
        return this.zone;
    }

    /**
     * Regista (ou atualiza) a distância desta caixa até outra.
     *
     * @param distance distância até ao destino
     */
    public void addDistance(Distance distance) {
        for (int i = 0; i < this.distances.size(); i++) {
            if (this.distances.get(i).getDestinationCode().equals(distance.getDestinationCode())) {
                this.distances.set(i, distance);
                return;
            }
        }
        this.distances.add(distance);
    }

    @Override
    public double getDistance(AidBox destination) throws AidBoxException {
        return findDistance(destination).getMeters();
    }

    @Override
    public double getDuration(AidBox destination) throws AidBoxException {
        return findDistance(destination).getMinutes();
    }

    private Distance findDistance(AidBox destination) throws AidBoxException {
        if (destination == null) {
            throw new AidBoxException("A caixa de destino é nula.");
        }
        if (this.code.equals(destination.getCode())) {
            return new Distance(this.code, 0, 0);
        }
        Distance distance = this.distances.find(d -> d.getDestinationCode().equals(destination.getCode()));
        if (distance == null) {
            throw new AidBoxException("Não se conhece a distância entre " + this.code + " e " + destination.getCode() + ".");
        }
        return distance;
    }

    /**
     * Instala um contentor na caixa.
     *
     * @param container contentor a instalar
     * @return {@code true} se foi instalado, {@code false} se já lá estava
     * @throws ContainerException se o contentor for nulo, não tiver tipo ou já existir outro do mesmo tipo
     */
    @Override
    public boolean addContainer(Container container) throws ContainerException {
        if (container == null) {
            throw new ContainerException("O contentor é nulo.");
        }
        if (container.getType() == null) {
            throw new ContainerException("O contentor " + container.getCode() + " não tem tipo definido.");
        }
        if (this.containers.contains(container)) {
            return false;
        }
        if (getContainer(container.getType()) != null) {
            throw new ContainerException("A caixa " + this.code + " já tem um contentor do tipo " + container.getType() + ".");
        }
        this.containers.add(container);
        return true;
    }

    @Override
    public Container getContainer(ContainerType type) {
        return this.containers.find(c -> c.getType().equals(type));
    }

    @Override
    public Container[] getContainers() {
        return this.containers.toArray(Container[]::new);
    }

    @Override
    public void removeContainer(Container container) throws AidBoxException {
        int index = this.containers.indexOf(container);
        if (index == -1) {
            throw new AidBoxException("O contentor não pertence à caixa " + this.code + ".");
        }
        this.containers.removeAt(index);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof AidBox)) {
            return false;
        }
        return this.code.equals(((AidBox) obj).getCode());
    }

    @Override
    public int hashCode() {
        return this.code.hashCode();
    }

    @Override
    public String toString() {
        return this.code + (this.zone != null ? " (" + this.zone + ")" : "");
    }
}
