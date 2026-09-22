package recolha.core;

import com.estg.core.AidBox;
import com.estg.core.Container;
import com.estg.core.ContainerType;
import com.estg.core.Institution;
import com.estg.core.Measurement;
import com.estg.core.exceptions.AidBoxException;
import com.estg.core.exceptions.ContainerException;
import com.estg.core.exceptions.MeasurementException;
import com.estg.core.exceptions.PickingMapException;
import com.estg.core.exceptions.VehicleException;
import com.estg.pickingManagement.PickingMap;
import com.estg.pickingManagement.Vehicle;
import java.time.LocalDateTime;
import recolha.alerts.AlertManager;
import recolha.util.DynamicArray;

/**
 * Instituição de ajuda humanitária: reúne as caixas de suprimentos, a frota de veículos,
 * os contentores vazios de reserva e o histórico de mapas de recolha.
 *
 * <p>A instituição tem uma base, de onde os veículos partem e para onde regressam. As
 * distâncias da base a cada caixa são guardadas aqui; as distâncias entre caixas ficam em
 * cada {@link AidBoxImp}.</p>
 *
 * @author Francisco Miguel Pereira Oliveira
 */
public class InstitutionImp implements Institution {

    private final String name;
    private final DynamicArray<AidBox> aidBoxes = new DynamicArray<>();
    private final DynamicArray<Vehicle> vehicles = new DynamicArray<>();
    private final DynamicArray<Vehicle> disabledVehicles = new DynamicArray<>();
    private final DynamicArray<PickingMap> pickingMaps = new DynamicArray<>();
    private final DynamicArray<Distance> baseDistances = new DynamicArray<>();
    private final DynamicArray<ContainerType> types = new DynamicArray<>();
    private final DynamicArray<Container> spareContainers = new DynamicArray<>();
    private final AlertManager alerts = new AlertManager();

    /**
     * @param name nome da instituição
     */
    public InstitutionImp(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    /** @return registo de alertas da instituição */
    public AlertManager getAlerts() {
        return this.alerts;
    }

    // ---------------------------------------------------------------- caixas e contentores

    /**
     * Adiciona uma caixa de suprimentos.
     *
     * @param aidBox caixa a adicionar
     * @return {@code true} se foi adicionada, {@code false} se já existia uma caixa com o mesmo código
     * @throws AidBoxException se a caixa for nula ou tiver dois contentores do mesmo tipo
     */
    @Override
    public boolean addAidBox(AidBox aidBox) throws AidBoxException {
        if (aidBox == null) {
            throw new AidBoxException("A caixa de suprimentos é nula.");
        }
        Container[] containers = aidBox.getContainers();
        for (int i = 0; i < containers.length; i++) {
            for (int j = i + 1; j < containers.length; j++) {
                if (containers[i].getType().equals(containers[j].getType())) {
                    throw new AidBoxException("A caixa " + aidBox.getCode() + " tem dois contentores do tipo " + containers[i].getType() + ".");
                }
            }
        }
        if (this.aidBoxes.contains(aidBox)) {
            return false;
        }
        this.aidBoxes.add(aidBox);
        return true;
    }

    @Override
    public AidBox[] getAidBoxes() {
        return this.aidBoxes.toArray(AidBox[]::new);
    }

    /**
     * @param code código da caixa
     * @return a caixa com esse código ou {@code null}
     */
    public AidBox findAidBox(String code) {
        return this.aidBoxes.find(box -> box.getCode().equals(code));
    }

    /**
     * Procura um contentor instalado em qualquer caixa.
     *
     * @param code código do contentor
     * @return o contentor ou {@code null}
     */
    public Container findContainer(String code) {
        for (int i = 0; i < this.aidBoxes.size(); i++) {
            for (Container container : this.aidBoxes.get(i).getContainers()) {
                if (container.getCode().equals(code)) {
                    return container;
                }
            }
        }
        return null;
    }

    /**
     * @param container contentor
     * @return a caixa onde o contentor está instalado ou {@code null}
     */
    public AidBox findAidBoxOf(Container container) {
        for (int i = 0; i < this.aidBoxes.size(); i++) {
            AidBox box = this.aidBoxes.get(i);
            for (Container installed : box.getContainers()) {
                if (installed.equals(container)) {
                    return box;
                }
            }
        }
        return null;
    }

    /** @return número total de contentores instalados em caixas */
    public int getNumContainers() {
        int total = 0;
        for (int i = 0; i < this.aidBoxes.size(); i++) {
            total += this.aidBoxes.get(i).getContainers().length;
        }
        return total;
    }

    @Override
    public Container getContainer(AidBox aidBox, ContainerType type) throws ContainerException {
        int index = this.aidBoxes.indexOf(aidBox);
        if (index == -1) {
            throw new ContainerException("A caixa de suprimentos não pertence à instituição.");
        }
        Container container = this.aidBoxes.get(index).getContainer(type);
        if (container == null) {
            throw new ContainerException("A caixa " + aidBox.getCode() + " não tem contentor do tipo " + type + ".");
        }
        return container;
    }

    /**
     * Regista uma leitura num contentor instalado numa das caixas da instituição.
     *
     * @return {@code true} se a leitura foi registada, {@code false} se era repetida
     * @throws ContainerException se o contentor não pertencer à instituição
     * @throws MeasurementException se a leitura for inválida
     */
    @Override
    public boolean addMeasurement(Measurement measurement, Container container) throws ContainerException, MeasurementException {
        if (container == null) {
            throw new ContainerException("O contentor é nulo.");
        }
        Container installed = findContainer(container.getCode());
        if (installed == null) {
            throw new ContainerException("O contentor " + container.getCode() + " não está instalado em nenhuma caixa.");
        }
        return installed.addMeasurement(measurement);
    }

    // ---------------------------------------------------------------- tipos e reservas

    /**
     * Regista um tipo de contentor conhecido pela instituição.
     *
     * @param type tipo a registar
     * @return {@code true} se era um tipo novo
     */
    public boolean addType(ContainerType type) {
        if (type == null || this.types.contains(type)) {
            return false;
        }
        this.types.add(type);
        return true;
    }

    /** @return cópia dos tipos conhecidos */
    public ContainerType[] getTypes() {
        return this.types.toArray(ContainerType[]::new);
    }

    /**
     * Regista um contentor vazio que está na base e pode ser levado pelos veículos para
     * trocar por um contentor cheio.
     *
     * @param container contentor de reserva
     * @return {@code true} se foi registado
     */
    public boolean addSpareContainer(Container container) {
        if (container == null || this.spareContainers.contains(container) || findContainer(container.getCode()) != null) {
            return false;
        }
        this.spareContainers.add(container);
        return true;
    }

    /** @return cópia dos contentores de reserva */
    public Container[] getSpareContainers() {
        return this.spareContainers.toArray(Container[]::new);
    }

    // ---------------------------------------------------------------- veículos

    /**
     * @return cópia dos veículos <strong>ativos</strong> (os únicos que podem fazer recolhas)
     */
    @Override
    public Vehicle[] getVehicles() {
        return this.vehicles.toArray(Vehicle[]::new);
    }

    /** @return cópia dos veículos desativados */
    public Vehicle[] getDisabledVehicles() {
        return this.disabledVehicles.toArray(Vehicle[]::new);
    }

    /**
     * @param code código do veículo
     * @return o veículo (ativo ou não) ou {@code null}
     */
    public Vehicle findVehicle(String code) {
        Vehicle vehicle = this.vehicles.find(v -> v.getCode().equals(code));
        return vehicle != null ? vehicle : this.disabledVehicles.find(v -> v.getCode().equals(code));
    }

    @Override
    public boolean addVehicle(Vehicle vehicle) throws VehicleException {
        if (vehicle == null) {
            throw new VehicleException("O veículo é nulo.");
        }
        if (this.vehicles.contains(vehicle) || this.disabledVehicles.contains(vehicle)) {
            return false;
        }
        this.vehicles.add(vehicle);
        return true;
    }

    @Override
    public void disableVehicle(Vehicle vehicle) throws VehicleException {
        moveVehicle(vehicle, this.vehicles, this.disabledVehicles, "já está desativado");
    }

    @Override
    public void enableVehicle(Vehicle vehicle) throws VehicleException {
        moveVehicle(vehicle, this.disabledVehicles, this.vehicles, "já está ativo");
    }

    private static void moveVehicle(Vehicle vehicle, DynamicArray<Vehicle> from, DynamicArray<Vehicle> to, String alreadyThereMessage) throws VehicleException {
        if (vehicle == null) {
            throw new VehicleException("O veículo é nulo.");
        }
        if (to.contains(vehicle)) {
            throw new VehicleException("O veículo " + vehicle.getCode() + " " + alreadyThereMessage + ".");
        }
        int index = from.indexOf(vehicle);
        if (index == -1) {
            throw new VehicleException("O veículo " + vehicle.getCode() + " não pertence à instituição.");
        }
        to.add(from.removeAt(index));
    }

    // ---------------------------------------------------------------- mapas de recolha

    @Override
    public PickingMap[] getPickingMaps() {
        return this.pickingMaps.toArray(PickingMap[]::new);
    }

    /**
     * @param from início do intervalo (inclusive)
     * @param to fim do intervalo (inclusive)
     * @return mapas cuja data está no intervalo
     */
    @Override
    public PickingMap[] getPickingMaps(LocalDateTime from, LocalDateTime to) {
        DynamicArray<PickingMap> result = new DynamicArray<>();
        for (int i = 0; i < this.pickingMaps.size(); i++) {
            LocalDateTime date = this.pickingMaps.get(i).getDate();
            if (!date.isBefore(from) && !date.isAfter(to)) {
                result.add(this.pickingMaps.get(i));
            }
        }
        return result.toArray(PickingMap[]::new);
    }

    @Override
    public PickingMap getCurrentPickingMap() throws PickingMapException {
        if (this.pickingMaps.isEmpty()) {
            throw new PickingMapException("Ainda não foi gerado nenhum mapa de recolha.");
        }
        return this.pickingMaps.get(this.pickingMaps.size() - 1);
    }

    @Override
    public boolean addPickingMap(PickingMap pickingMap) throws PickingMapException {
        if (pickingMap == null) {
            throw new PickingMapException("O mapa de recolha é nulo.");
        }
        if (this.pickingMaps.contains(pickingMap)) {
            return false;
        }
        this.pickingMaps.add(pickingMap);
        return true;
    }

    // ---------------------------------------------------------------- distâncias à base

    /**
     * Regista (ou atualiza) a distância entre a base e uma caixa.
     *
     * @param distance distância, cujo destino é o código da caixa
     */
    public void addBaseDistance(Distance distance) {
        for (int i = 0; i < this.baseDistances.size(); i++) {
            if (this.baseDistances.get(i).getDestinationCode().equals(distance.getDestinationCode())) {
                this.baseDistances.set(i, distance);
                return;
            }
        }
        this.baseDistances.add(distance);
    }

    /**
     * @param aidBox caixa
     * @return distância da base até à caixa, em metros
     * @throws AidBoxException se a distância não for conhecida
     */
    @Override
    public double getDistance(AidBox aidBox) throws AidBoxException {
        return findBaseDistance(aidBox).getMeters();
    }

    /**
     * @param aidBox caixa
     * @return duração da viagem da base até à caixa, em minutos
     * @throws AidBoxException se a distância não for conhecida
     */
    public double getDuration(AidBox aidBox) throws AidBoxException {
        return findBaseDistance(aidBox).getMinutes();
    }

    private Distance findBaseDistance(AidBox aidBox) throws AidBoxException {
        if (aidBox == null) {
            throw new AidBoxException("A caixa é nula.");
        }
        Distance distance = this.baseDistances.find(d -> d.getDestinationCode().equals(aidBox.getCode()));
        if (distance == null) {
            throw new AidBoxException("Não se conhece a distância da base até " + aidBox.getCode() + ".");
        }
        return distance;
    }

    @Override
    public String toString() {
        return String.format("%s: %d caixas, %d contentores instalados, %d de reserva, %d veículos ativos, %d desativados, %d mapas de recolha, %d alertas",
                this.name, this.aidBoxes.size(), getNumContainers(), this.spareContainers.size(),
                this.vehicles.size(), this.disabledVehicles.size(), this.pickingMaps.size(), this.alerts.count());
    }
}
