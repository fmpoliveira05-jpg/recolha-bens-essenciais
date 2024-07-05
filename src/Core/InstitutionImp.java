package Core;

import PickingManagement.PickingMapImp;
import PickingManagement.VehicleImp;
import com.estg.core.AidBox;
import com.estg.core.Container;
import com.estg.core.ContainerType;
import com.estg.core.exceptions.AidBoxException;
import com.estg.core.exceptions.ContainerException;
import com.estg.core.exceptions.MeasurementException;
import com.estg.core.exceptions.PickingMapException;
import com.estg.core.exceptions.VehicleException;
import com.estg.pickingManagement.PickingMap;
import com.estg.pickingManagement.Vehicle;
import com.estg.core.Institution;
import com.estg.core.Measurement;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

/*
* Nome: Francisco Miguel Pereira Oliveira
* Número: 8230148
* Turma: LEIT2
*/

/**
 * InstitutionImp, classe que representa uma instituição
 * 
 * @author Francisco Miguel Pereira Oliveira
 * @version 1.0
 * @since 2024-06-27
 */
public class InstitutionImp implements Institution {
    private final int INICIAL_SIZE = 5; 
    
    private String name; 

    private int numPickingMaps;
    
    private PickingMap[] pickingMaps; 
    
    private int numAidBoxes; 
    
    private AidBox[] aidBoxes; 
    
    private int numVehicles; 
    
    private Vehicle[] vehicles; // conjunto dos veículos disponíveis da instituição
    
    private int numDisabledVehicles;
    
    private Vehicle[] disabledVehicles; // conjunto dos veículos indisponíveis da instituição
    
    private int numDistances; 
    
    private Distance[] distances;  // conjunto das distâncias entre a instituição e cada uma das suas aid boxes
    
    private int numTypes;
    
    private ContainerType[] types; // conjunto dos tipos de containers que fazem parte da instituição
    
    /**
    * Método construtor de InstitutionImp
    * 
    * @param name nome da instituição
    */
    public InstitutionImp(String name) {
        this.name = name;
        this.vehicles = new Vehicle[INICIAL_SIZE];
        this.disabledVehicles = new Vehicle[INICIAL_SIZE];
        this.aidBoxes = new AidBoxImp[INICIAL_SIZE];
        this.pickingMaps = new PickingMap[INICIAL_SIZE];
        this.numVehicles = 0;
        this.numPickingMaps = 0;
        this.numAidBoxes = 0;
        this.numDistances = 0;
        this.numTypes = 0;
        this.distances = new Distance[INICIAL_SIZE];
        this.types = new ContainerType[INICIAL_SIZE];
    }
    
    /**
    * Método getter para o nome da instituição
    * 
    * @return o nome da instituição
    */
    @Override
    public String getName() {
        return this.name;
    }
    
    /**
    * Método getter para o número de aid boxes da instituição
    * 
    * @return o número de aid boxes da instituição
    */
    private int getNumAidBoxes() {
        return this.numAidBoxes;
    }
    
    /**
    * Método getter para o número de veículos (ativos) da instituição
    * 
    * @return o número de veículos (ativos) da instituição
    */
    private int getNumVehicles() {
        return this.numVehicles;
    }    
    
    /**
    * Método getter para o número de veículos inativos da instituição
    * 
    * @return o número de veículos inativos da instituição
    */
    private int getNumDisabledVehicles() {
        return this.numDisabledVehicles;
    }
    
    /**
    * Método getter para o número de distâncias que existem da instituição a cada aid box
    * 
    * @return o número de distâncias que existem da instituição a cada aid box
    */
    private int getNumDistances() {
        return this.numDistances;
    }
    
    /**
    * Método getter para o número de tipos de containers que existem na instituição
    * 
    * @return o número de tipos de containers que existem na instituição
    */
    private int getNumTypes() {
        return this.numTypes;
    }
    
    /**
    * Método getter para o conjunto das distâncias da instituição
    * 
    * @return o conjunto das distâncias da instituição
    */
    public Distance[] getDistances() {
        return this.distances;
    }
    
    /**
    * Método getter para o conjunto dos conjuntos de rotas da instituição (array de PickingMap)
    * 
    * @return o conjunto dos conjuntos de rotas da instituição (array de PickingMap)
    */
    @Override
    public PickingMap[] getPickingMaps() {
        return this.pickingMaps;
    }
    
    /**
    * Método getter do conjunto de veículos indisponíveis (inativos) da instituição
    * 
    * @return o conjunto de veículos indisponíveis (inativos) da instituição
    */
    public Vehicle[] getDisabledVehicles() {
        return this.disabledVehicles;
    }
    
    /**
    * Método que duplica o espaço do array dos tipos de containers sempre que necessário.
    */
    private void raiseTypes() {
        ContainerType[] tmp = new ContainerType[this.types.length * 2];
        
        for (int i = 0; i < this.numTypes; i++) {
            tmp[i] = this.types[i];
        }
        
        this.types = tmp;
    }
    
    /**
    * Método para adicionar um novo tipo ao conjunto de tipos de containers da instituição.
    * 
    * @param ct tipo de containers
    * @return true, se o novo tipo for adicionado com sucesso
    * false, caso contrário
    */
    public boolean addType(ContainerType ct) {
        if (this.numTypes == this.types.length) {
            this.raiseTypes();
        }
        
        for (int i = 0; i < this.numAidBoxes; i++) {
            for (Container container : this.aidBoxes[i].getContainers()) {
                if (container.getType().equals(ct)) {
                    this.types[numTypes++] = ct;
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Metodo para retornar o número total de containers que a instituição possui
     * 
     * @return o número total de containers que a instituição possui
     */
    public int getNumContainers() {
        int numContainers = 0;

        for (int i = 0; i < this.numAidBoxes; i++) {
           numContainers += this.aidBoxes[i].getContainers().length;
        }

        return numContainers;
    }
     
    /**
    * Método que pesquisa por uma aid box
    * 
    * @param code código da aid box
    * @return i, que corresponde à posição da aid box, se a aidbox for encontrada
    * -1, se a aid box não for encontrada
    */
    public int findAidBox(String code) {
        for (int i = 0; i < this.numAidBoxes; i++) {
            if (this.aidBoxes[i].getCode().equals(code)) {
                return i;
            }
        }
        
        return -1;
    }
    
    /**
    * Método que duplica o espaço do array das aid boxes sempre que necessário.
    */
    private void raiseAidboxes() {
        AidBox[] tmp = new AidBoxImp[this.aidBoxes.length * 2];
        
        for (int i = 0; i < this.numAidBoxes; i++) {
            tmp[i] = this.aidBoxes[i];
        }
        
        this.aidBoxes = tmp;
    }
    
    /**
    * Método que duplica o espaço do array das distâncias sempre que necessário.
    */
    private void raiseDistances() {
        Distance[] tmp = new Distance[this.numDistances * 2]; 
        
        for (int i = 0; i < this.numDistances; i++) {
            tmp[i] = this.distances[i];
        }
        
        this.distances = tmp;
    }
    
    /**
     * Método que adiciona uma distância da instituição a uma aid box
     * 
     * @param distance, distância a ser inserida no array de distances
     * @return true, caso a distância possa ser adicionada
     */
    public boolean addDistance(Distance distance) {
        if (this.numDistances >= this.distances.length) {
            this.raiseDistances();
        }
        
        this.distances[this.numDistances++] = distance;
        return true;
    }
    
    /**
     * Método que adiciona uma distância entre duas aid boxes da instituição
     * 
     * @param code, código da aid box que receberá a nova distance
     * @param distance, distância entre as duas aid boxes
     * @return true, se conseguir adicionar a distância,
     * false se a aid box que recebe a distância não pertencer à instituição
     */
    public boolean addDistanceAid(String code, Distance distance) {
        int pos = this.findAidBox(code);
        
        if (pos == -1) {
            System.out.println("A aid box inserida não existe na instituição!");
            return false;
        }
        
        ((AidBoxImp) this.aidBoxes[pos]).addDistance(distance);
        
        return true;
    }
    
    /**
    * Método que adiciona uma aid box à coleção de aid boxes da instituição
    * 
    * @param aidbox aid box a adicionar
    * @return true, se a aid box foi adicionada com sucesso
    * false, se não foi possível adicionar a aid box
    * @throws AidBoxException, se a aid box for nula,
    * ou se tiver pelo menos dois containers duplicados do mesmo tipo
    */
    @Override
    public boolean addAidBox(AidBox aidbox) throws AidBoxException {
        if (aidbox == null) {
            throw new AidBoxException("A caixa de suprimentos é nula!");
        }
        
        Container[] containers = aidbox.getContainers();
        
        for (int i = 0; i < containers.length; i++) {
            if (containers[i].getType() != null && containers[i].getType().equals(containers[i + 1].getType())) {
                throw new AidBoxException("A caixa de suprimentos é inválida,"
                        + "isto é, apresenta containers duplicados de um certo tipo!");
            }
        }
        
        if (this.numAidBoxes >= this.aidBoxes.length) {
            this.raiseAidboxes();
        }
        
        if (this.findAidBox(aidbox.getCode()) != -1) {
            return false;
        }
        
        this.aidBoxes[this.numAidBoxes++] = aidbox;
        return true;     
    }
    
    /**
    * Método que verifica se o container recebido já faz parte de alguma aid box da instituição
    * 
    * @param cntnr container a verificar
    * @return i, a posição da aid box em que se encontra o container, caso o container seja encontrado
    * @return -1, caso o container não faça parte de nenhuma aid box da instituição
    */
    private int findAidBoxContainer(Container cntnr) {
        for (int i = 0; i < this.numAidBoxes; i++) {
            for (Container container: this.aidBoxes[i].getContainers()) {
                if (container.equals(cntnr)) {
                    return i;
                }
            }
        }
        
        return -1;
    }
    
    /**
    * Método que adiciona uma medição a um container da instituição
    * 
    * @param msrmnt medição de um container
    * @param cntnr container para o qual a medição poderá ser adicionada
    * @return true, se a medição foi adicionada com sucesso ao container
    * false, se o container tiver uma medição com uma data igual à data do container recebido
    * @throws ContainerException se o container não existir
    * @throws MeasurementException se o valor da medição for negativo ou ultrapassar o valor da capacidade máxima do container
    */
    @Override
    public boolean addMeasurement(Measurement msrmnt, Container cntnr) throws ContainerException, MeasurementException {
        int pos_aid = this.findAidBoxContainer(cntnr);
        
        if (pos_aid == -1) {
            throw new ContainerException("O contentor não existe!");
        }
        
        if (msrmnt.getValue() < 0) {
            throw new MeasurementException("O valor da medição é negativo!");
        }

        if (msrmnt.getValue() > cntnr.getCapacity()) {
            throw new MeasurementException("O valor da medição ultrapassa a capacidade máxima do contentor!");
        }

        for (Measurement measurement : cntnr.getMeasurements()) {
            if (measurement != null && measurement.getDate().equals(msrmnt.getDate())) {
                return false;
            }
        }
        
        for (Container container : this.aidBoxes[pos_aid].getContainers()) {
            if (container.equals(cntnr)) {
                container.addMeasurement(msrmnt);
                return true;
            }
        }
        
        return false;
    }
    
    /**
    * Método getter que retorna uma shallow copy do conjunto de aid boxes da instituição
    * 
    * @return uma shallow copy do conjunto de aid boxes da instituição
    */
    @Override
    public AidBox[] getAidBoxes() {
        if (this.numAidBoxes == this.aidBoxes.length) {
            return this.aidBoxes;
        }

        AidBox[] temp = new AidBox[this.numAidBoxes];

        for (int i = 0; i < this.numAidBoxes; i++) {
            temp[i] = this.aidBoxes[i];
        }

        return temp;
    }
    
    /**
    * Método que retorna uma shallow copy de um container através da aid box correspondente, e ainda através do tipo de item que armazena
    * 
    * @param aidbox aid box a verificar
    * @param ct tipo de item armazenado pelo container
    * @return uma shallow copy do container correspondente
    * @throws ContainerException se a aid box não existir,
    * ou se não existir um container que armazene o item prescrito
    */
    @Override
    public Container getContainer(AidBox aidbox, ContainerType ct) throws ContainerException {
        boolean aidboxIsFound = false;
        boolean ctIsFound = false;
        Container container = null;
        
        for (int i = 0; i < this.numAidBoxes; i++) {
            if (this.aidBoxes[i].equals(aidbox)) {
                aidboxIsFound = true;
                Container[] tmp = aidbox.getContainers();
                for (Container tmp1 : tmp) {
                    if (tmp1.getType().equals(ct)) {
                        ctIsFound = true;
                        container = tmp1;
                        break;
                    }
                }
                break;
            }
        }
        
        if (aidboxIsFound == false) {
            throw new ContainerException("A caixa de suprimentos não existe!");
        }
        
        if (ctIsFound == false) {
            throw new ContainerException("Não existe um container que armazene o tipo de item prescrito!");
        }
        
        return container;
    }
    
    /**
    * Método que verifica se um veículo que transporta um determinado tipo de container pertence à instituição.
    * 
    * @param ct tipo de container
    * @return o veículo, se for encontrado
    * null, caso contrário
    */
    public Vehicle getVehicle(ContainerType ct) {
        for (int i = 0; i < this.numVehicles; i++) {
            if (this.vehicles[i] instanceof VehicleImp) {
                if (((VehicleImp) this.vehicles[i]).getType(ct)) {
                    return this.vehicles[i];
                }
            }
        }
        
        return null;
    }
    
    /**
    * Método getter que retorna uma deep copy do conjunto de veículos disponíveis (ativos) da instituição
    * 
    * @return uma deep copy do conjunto de veículos disponíveis (ativos) da instituição
    */
    @Override
    public Vehicle[] getVehicles() {
        if (this.numVehicles == this.vehicles.length) {
            return this.vehicles;
        }

        Vehicle[] tmp = new Vehicle[this.vehicles.length];

        for (int i = 0; i < this.numVehicles; i++) {
            tmp[i] = new VehicleImp((VehicleImp) this.vehicles[i]);
        }

        return tmp;
    }
 
    /**
    * Método que verifica se um veículo já foi adicionado à instituição
    * 
    * @param vehicle veículo
    * @return true, se a instituição tiver um veículo igual ao que foi passado por parâmetro
    * false, se o veículo não foi encontrado
    */
    private boolean findVehicle(Vehicle vehicle) {
        for (int i = 0; i < this.numVehicles; i++) {
            if (this.vehicles[i].equals(vehicle)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
    * Método que duplica o espaço do array de veículos habilitados sempre que necessário.
    */
    private void raiseVehicles() {
        Vehicle[] tmp = new VehicleImp[this.vehicles.length * 2];
        
        for (int i = 0; i < this.numVehicles; i++) {
            tmp[i] = this.vehicles[i];
        }
        
        this.vehicles = tmp;
    }
    
    /**
    * Método que adiciona um veículo à coleção de veículos da instituição
    * 
    * @param vhcl veículo
    * @return true, se o veículo foi adicionado com sucesso
    * false, se não foi possível adicionar o veículo
    * @throws VehicleException se o veículo for nulo
    */
    @Override
    public boolean addVehicle(Vehicle vhcl) throws VehicleException {
        if (vhcl == null) {
            throw new VehicleException("O veículo é nulo!");
        }
        
        if (findVehicle(vhcl)) {
            return false;
        }
        
        if (this.numVehicles >= this.vehicles.length) {
            this.raiseVehicles();
        }
        
        this.vehicles[this.numVehicles++] = vhcl;
        return true;          
    }
    
    /**
    * Método que duplica o espaço do array de veículos desabilitados sempre que necessário.
    */
    private void raiseDisabledVehicles() {
        Vehicle[] tmp = new VehicleImp[this.disabledVehicles.length * 2];
        
        for (int i = 0; i < this.numDisabledVehicles; i++) {
            tmp[i] = this.disabledVehicles[i];
        }
        
        this.disabledVehicles = tmp;
    }
    
    /**
    * Método que desativa um veículo da coleção de veículos da instituição
    * 
    * @param vhcl veículo
    * @throws VehicleException se o veículo não existir, ou se o veículo já se encontrar desativado
    */
    @Override
     public void disableVehicle(Vehicle vhcl) throws VehicleException {
        boolean vhclExists = false;
        
        for (int i = 0; i < this.numDisabledVehicles; i++) {
            if (this.disabledVehicles[i].equals(vhcl)) {
                throw new VehicleException("O veículo com código " + vhcl.getCode() + " já se encontra inativo!");
            }
        }
        
        for (int i = 0; i < this.numVehicles; i++) {
            if (this.vehicles[i].equals(vhcl)) {
                vhclExists = true;
                for (int j = i; j < this.numVehicles - 1; j++) {
                    this.vehicles[j] = this.vehicles [j + 1];
                }
                this.vehicles[this.numVehicles - 1] = null;
                this.numVehicles--;
            }
        }
        
        if (vhclExists == false) {
            throw new VehicleException("O veículo não existe!");
        }
        
        if (this.numDisabledVehicles >= this.disabledVehicles.length) {
            this.raiseDisabledVehicles();
        }
        
        this.disabledVehicles[this.numDisabledVehicles++] = vhcl;
        
        System.out.println("O veículo com código " + vhcl.getCode() + " foi desabilitado com sucesso!");
    }
    
    /**
    * Método que ativa um veículo da coleção de veículos da instituição
    * 
    * @param vhcl veículo
    * @throws VehicleException, se o veículo não existir,
    * ou se o veículo já se encontrar ativo
    */
    @Override
    public void enableVehicle(Vehicle vhcl) throws VehicleException {
        boolean vhclExists = false;
        
        for (int i = 0; i < this.numVehicles; i++) {
            if (this.vehicles[i].equals(vhcl)) {
                throw new VehicleException("O veículo com código " + vhcl.getCode() + " já se encontra ativo!");
            }
        }
        
        for (int i = 0; i < this.numDisabledVehicles; i++) {
            if (this.disabledVehicles[i].equals(vhcl)) {
                vhclExists = true;
                for (int j = i; j < this.numDisabledVehicles - 1; j++) {
                    this.disabledVehicles[j] = this.disabledVehicles [j + 1];
                }
                this.disabledVehicles[this.numDisabledVehicles - 1] = null;
                this.numDisabledVehicles--;
            }
        }
        
        if (vhclExists == false) {
            throw new VehicleException("O veículo não existe!");
        }
        
        if (this.numVehicles >= this.vehicles.length) {
            this.raiseVehicles();
        }
        
        this.vehicles[this.numVehicles++] = vhcl;
        
        System.out.println("O veículo com código " + vhcl.getCode() + " foi habilitado com sucesso!"); 
    }
    
    /**
    * Método que retorna o conjunto total de mapas que a instituição tem
    * 
    * @param ldt data mínima do intervalo
    * @param ldt1 data máxima do intervalo
    * @return o conjunto total de mapas que a instituição tem, dentro do intervalo de datas definido
    */
    @Override
    public PickingMap[] getPickingMaps(LocalDateTime ldt, LocalDateTime ldt1) {
        PickingMap[] tmp = new PickingMap[this.numPickingMaps];
        int count = 0;
        
        for (PickingMap pickingMap : this.pickingMaps) {
            if (pickingMap.getDate().isAfter(ldt) & pickingMap.getDate().isBefore(ldt1)) {
                tmp[count++] = pickingMap;
            }
        }
        
        return tmp;       
    }
    
    /**
    * Método que retorna o conjunto atual de rotas da instituição
    * 
    * @return o conjunto atual de rotas da instituição
    * @throws PickingMapException se não existirem mapas na instituição
    */
    @Override
    public PickingMap getCurrentPickingMap() throws PickingMapException {
        if (this.numPickingMaps == 0) {
            throw new PickingMapException("Não existem mapas na instituição!");
        }
        
        return this.pickingMaps[this.numPickingMaps - 1];
    }
    
    /**
    * Método que verifica se um mapa já existe na instituição
    * 
    * @param pickingMap mapa (conjunto de rotas)
    * @return true, se a instituição contém o conjunto de rotas recebido
    * false, se o conjunto de rotas não foi encontrado
    */
    private boolean findPickingMap(PickingMap pickingMap) {
        for (int i = 0; i < this.numPickingMaps; i++) {
            if (this.pickingMaps[i].equals(pickingMap)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
    * Método que duplica o espaço do array dos mapas da instituição sempre que necessário.
    */
    private void raisePickingMaps() {
        PickingMap[] tmp = new PickingMapImp[this.pickingMaps.length * 2];
        
         for (int i = 0; i < this.numPickingMaps; i++) {
            tmp[i] = this.pickingMaps[i];
        }
        
        this.pickingMaps = tmp;
    }
    
    /**
    * Método que adiciona um conjunto de rotas à instituição
    * 
    * @param pm conjunto de rotas
    * @return true, se o conjunto de rotas foi adicionado com sucesso
    * false, se não foi possível adicionar o conjunto de rotas
    * @throws PickingMapException se o mapa (conjunto de rotas) for nulo
    */
    @Override
    public boolean addPickingMap(PickingMap pm) throws PickingMapException {
        if (pm == null) {
            throw new PickingMapException("O mapa é nulo!");
        }
        
        if (findPickingMap(pm)) {
            return false;
        }
        
        if (this.numPickingMaps >= this.pickingMaps.length) {
            this.raisePickingMaps();
        }
        
        this.pickingMaps[this.numPickingMaps++] = pm;
        return true; 
    }
    
    /**
     * Método para pesquisar se a aid box destino existe na instituição.
     * 
     * @param aidbox aid box recebida
     * @return i, a posição da aid box, caso seja encontrada
     * -1, caso a aid box não seja encontrada
     */
    private int posAidDistance(AidBox aidbox) {
        for (int i = 0; i < this.numDistances; i++) {
            if (aidbox.getCode().equals(this.distances[i].getAidDestino())) {
                return i;
            }
        }
        
        return -1;
    }

    /**
    * Método que retorna a distância entre a instituição e uma caixa de suprimentos
    * 
    * @param aidbox caixa de suprimentos
    * @return o valor da distância entre a instituição e a caixa de suprimentos
    * @throws AidBoxException, se a caixa de suprimentos não existir
    */
    @Override
    public double getDistance(AidBox aidbox) throws AidBoxException {       
        int pos = this.posAidDistance(aidbox); 
        
        if (pos == -1) {
            throw new AidBoxException("A aid box não existe!");
        }
        
        return this.distances[pos].getDistance();
    }
    
    /**
    * Método que retorna o tempo gasto para ir da instituição até a uma aid box
    * 
    * @param aidbox aid box recebida
    * @return o valor do tempo gasto para ir da instituição até à aid box
    * @throws AidBoxException, se a aid box não existir
    */
    public double getDuration(AidBox aidbox) throws AidBoxException {       
        int pos = this.posAidDistance(aidbox); 
        
        if (pos == -1) {
            throw new AidBoxException("A aid box não existe!");
        }
        
        return this.distances[pos].getDuration();
    }

    /** Método equals de InstitutionImp
     * 
     * Compara a instituição com o objeto recebido
     * 
     * @param obj o objeto a ser comparado com a instituição
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
        final InstitutionImp other = (InstitutionImp) obj;
        if (this.numPickingMaps != other.numPickingMaps) {
            return false;
        }
        if (this.numAidBoxes != other.numAidBoxes) {
            return false;
        }
        if (this.numVehicles != other.numVehicles) {
            return false;
        }
        if (this.numDisabledVehicles != other.numDisabledVehicles) {
            return false;
        }
        if (this.numDistances != other.numDistances) {
            return false;
        }
        if (this.numTypes != other.numTypes) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Arrays.deepEquals(this.pickingMaps, other.pickingMaps)) {
            return false;
        }
        if (!Arrays.deepEquals(this.aidBoxes, other.aidBoxes)) {
            return false;
        }
        if (!Arrays.deepEquals(this.vehicles, other.vehicles)) {
            return false;
        }
        if (!Arrays.deepEquals(this.disabledVehicles, other.disabledVehicles)) {
            return false;
        }
        if (!Arrays.deepEquals(this.distances, other.distances)) {
            return false;
        }
        return Arrays.deepEquals(this.types, other.types);
    }

    /**
     * Método toString de InstitutionImp
     *
     * @return os valores de name, vehicles, currentPickingMap, pickingMaps e aidBoxes
     */
    @Override
    public String toString() {
        return "InstitutionImp{" + "name=" + name + ", numPickingMaps=" + numPickingMaps + ", pickingMaps=" + Arrays.toString(pickingMaps) + ", numAidBoxes=" + numAidBoxes + ", aidBoxes=" + Arrays.toString(aidBoxes) + ", numVehicles=" + numVehicles + ", vehicles=" + Arrays.toString(vehicles) + ", numDisabledVehicles=" + numDisabledVehicles + ", disabledVehicles=" + Arrays.toString(disabledVehicles) + ", numDistances=" + numDistances + ", distances=" + Arrays.toString(distances) + ", numTypes=" + numTypes + ", types=" + Arrays.toString(types) + '}';
    }
    
    
}
