package PickingManagement;

import Core.AidBoxImp;
import com.estg.core.Container;
import com.estg.core.AidBox;
import com.estg.core.ContainerType;
import com.estg.core.exceptions.AidBoxException;
import com.estg.pickingManagement.Report;
import com.estg.pickingManagement.Route;
import com.estg.pickingManagement.Vehicle;
import com.estg.pickingManagement.exceptions.RouteException;
import java.util.Arrays;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
* Nome: Francisco Miguel Pereira Oliveira
* Número: 8230148
* Turma: LEIT2
*/


/**
 * RouteImp, classe que representa as rotas
 * 
 * @author Francisco Miguel Pereira Oliveira
 * @version 1.0
 * @since 2024-06-27
 */
public class RouteImp implements Route {
    private final int MAX_AID_BOXES = 1;

    private int numAidBoxes;
    
    private AidBox[] aidboxes;
    
    private Vehicle vehicle;
    
    private Report report;
    
    private double totalDistance;
   
    private double totalDuration;
   
    /**
    * Método construtor de RouteImp
    * 
    * @param vehicle veículo utilizado na rota
    * @param report report da rota gerada
    */      
    public RouteImp(Vehicle vehicle, Report report) {
        this.numAidBoxes = 0;
        this.aidboxes = new AidBox[MAX_AID_BOXES];
        this.vehicle = vehicle;
        this.report = report;
        this.totalDistance = 0;
        this.totalDuration = 0;
    }
    
    /**
    * Método getter para o número de aid boxes que constituem a rota
    * 
    * @return o número de aid boxes que constituem a rota
    */
    public int getNumAidBoxes() {
        return this.numAidBoxes;
    }
    
    /**
    * Método getter para o veículo da rota
    * 
    * @return o veículo da rota
    */
    @Override
    public Vehicle getVehicle() {
        return this.vehicle;
    }
    
    /**
    * Método getter para o report da rota gerada
    * 
    * @return o report da rota gerada
    */
    @Override
    public Report getReport() {
        return this.report;
    }
   
    /**
    * Método para comparar se as aid boxes são iguais
    * 
    * @param aidbox aid box a comparar
    * @return i, se as aid boxes forem iguais
    * @return -1, se as aid boxes forem diferentes
    */
    private int findAidBox(AidBox aidbox) {
        for (int i = 0; i < this.numAidBoxes; i++) {
            if (this.aidboxes[i].equals(aidbox)) {
                return i;
            }
        }
        
        return -1;
    }
    
    /**
    * Método para adicionar uma nova aid box à rota
    * 
    * @param aidbox aid box a adicionar
    * @throws RouteException se a aid box não puder ser adicionada à rota:
    * se a aid box for nula
    * se a aid box já existir na rota
    * se a aid box não for compatível (se não tiver um container que possa ser escolhido pelo veículo) com o veículo da rota
    * 
    */
    @Override
    public void addAidBox(AidBox aidbox) throws RouteException {
        boolean containerExists = false;
        
        if (aidbox == null) {
            throw new RouteException("A aid box que pretende introduzir está nula!");
        }
        
        if (findAidBox(aidbox) != -1) {
            throw new RouteException("A aid box que pretende introduzir já existe na rota!");
        }
        
        for (Container container : aidbox.getContainers()) {
            if (this.vehicle instanceof VehicleImp) {
                ContainerType[] cts = ((VehicleImp) this.vehicle).getTypes();
                for (ContainerType containerType : cts) {
                    if (containerType.equals(container.getType())) {
                        containerExists = true;
                        break;
                    }
                }
                break;
            }  
        }
        
        if (containerExists == false) {
            throw new RouteException("A aid box não é compatível com o veículo da rota, "
                    + "ou seja, não tem nenhum container que o veículo possa transportar!");
        }
        
        if (numAidBoxes >= aidboxes.length) {
            throw new RouteException("A rota está cheia!");
        }
        
        this.aidboxes[this.numAidBoxes++] = aidbox;
    }

    /**
    * Método para remover uma aid box da rota
    * 
    * @param aidbox aid box a remover
    * @return temp, a aid box eliminada
    * @throws RouteException se a aid box não puder ser removida da rota:
    * se a aid box for nula
    * se a aid box não existir na rota
    */
    @Override
    public AidBox removeAidBox(AidBox aidbox) throws RouteException {
        if (aidbox == null) {
            throw new RouteException("A aid box que pretende remover está nula!");
        }
        
        int pos = findAidBox(aidbox); 
        
        if (pos == -1) {
            throw new RouteException("A aid box que pretende remover não existe na rota!");
        }
        
        AidBox tmp = aidbox;
        
        for (int i = pos; i < this.numAidBoxes - 1; i++) {
            this.aidboxes[i] = this.aidboxes[i + 1];
        }
        
        this.aidboxes[this.numAidBoxes - 1] = null;
        this.numAidBoxes--;
        return tmp;        
    }

    /**
    * Método para verificar se uma determinada aid box já se encontra inserida na rota
    * 
    * @param aidbox aid box para verificar se existe na rota
    * @return true, se a aid box existir na rota
    * false, caso contrário
    */
    @Override
    public boolean containsAidBox(AidBox aidbox) {
        for (int i = 0; i < this.numAidBoxes; i++) {
            if (this.aidboxes[i].equals(aidbox)) {
                return true;
            }
        }
        
        return false;
    }

    /**
    * Método para substituir uma aid box da rota
    * 
    * @param aidbox a aid box da rota a ser subtituida
    * @param aidbox1 aid box a ser reposta no lugar da aidbox a ser substituida
    * @throws RouteException se a aid box não puder ser substituida na rota:
    * se alguma das aid boxes for nula
    * (seja a aid box a substituir ou a aid box a ser reposta no lugar da aid box a ser substituida)
    * se a aid box origem não existir na rota
    * se a aid box destino já existir na rota
    * se a aid box a inserir não for compatível
    * (não possui um container que possa ser escolhido pelo veículo)
    * com o veículo da rota.
    */
    @Override
    public void replaceAidBox(AidBox aidbox, AidBox aidbox1) throws RouteException {
        boolean containerExists = false;
        
        if (aidbox == null || aidbox1 == null) {
            throw new RouteException("Pelo menos uma das aid boxes é nula!");
        }
        
        if (this.findAidBox(aidbox) == -1) {
            throw new RouteException("A aid box origem não existe na rota!");
        }
        
        if (this.findAidBox(aidbox1) != -1) {
            throw new RouteException("A aid box destino já existe na rota!");
        }      
        
        for (Container container : aidbox.getContainers()) {
            if (this.vehicle instanceof VehicleImp) {
                for (ContainerType ct : ((VehicleImp) this.vehicle).getTypes()) {
                    if (ct.equals(container.getType())) {
                        containerExists = true;
                        break;
                    }
                }
                break;
            }  
        }
        
        if (containerExists == false) {
            throw new RouteException("A aid box não é compatível com o veículo da rota,"
                                + "ou seja, não tem nenhum container que o veículo possa transportar!");
        }
        
        this.aidboxes[this.findAidBox(aidbox)] = aidbox1;
    }

    /**
    * Método para substituir uma aid box da rota
    * 
    * @param aidbox aid box da rota que passará a ter a aid box toInsert a lhe suceder
    * @param aidbox1 aid box a ser inserida depois da aid box local
    * @throws RouteException se a aid box não puder ser adicionada à rota:
    * se alguma das aid boxes for nula (seja a aid box local ou a aid box a inserir após a anterior)
    * se a aid box local não existir na rota
    * se a aid box a substituir já existir na rota
    * se a aid box a inserir não for compatível (não possui um container que possa ser escolhido pelo veículo) com o veículo da rota.
    */
    @Override
    public void insertAfter(AidBox aidbox, AidBox aidbox1) throws RouteException {
        boolean containerExists = false;
        
        if (aidbox == null || aidbox1 == null) {
            throw new RouteException("Pelo menos uma das aid boxes é nula!");
        }
        
        if (this.findAidBox(aidbox) == -1) {
            throw new RouteException("A aid box origem não existe na rota!");
        }
        
        if (this.findAidBox(aidbox1) != -1) {
            throw new RouteException("A aid box destino já existe na rota!");
        }
        
        for (Container container : aidbox.getContainers()) {
            if (this.vehicle instanceof VehicleImp) {
                ContainerType[] cts = ((VehicleImp) this.vehicle).getTypes();
                for (ContainerType containerType : cts) {
                    if (containerType.equals(container.getType())) {
                        containerExists = true;
                        break;
                    }
                }
                break;
            }  
        }
        
        if (containerExists == false) {
            throw new RouteException("A aid box não é compatível com o veículo da rota,"
                                + "ou seja, não tem nenhum container que o veículo possa transportar!");
        }
        
        for (int i = this.findAidBox(aidbox); i < this.numAidBoxes - 1; i++) {
            this.aidboxes[i] = this.aidboxes[i + 1];
        }
                
        if (this.numAidBoxes < this.aidboxes.length) {
            this.aidboxes[this.findAidBox(aidbox) - 1] = aidbox1;
            this.numAidBoxes++;
        }
    } 

    /**
    * Método que retorna uma deep copy da rota
    * 
    * @return uma deep copy da rota (conjunto de aid boxes)
    */
    @Override
    public AidBox[] getRoute() {
        if (this.numAidBoxes == this.aidboxes.length) {
            return this.aidboxes;
        }
        
        AidBox[] tmp = new AidBoxImp[this.aidboxes.length];
        
        for (int i = 0; i < this.numAidBoxes; i++) {
            tmp[i] = new AidBoxImp((AidBoxImp) this.aidboxes[i]);
        }
        
        return tmp;
    }

    /**
    * Método que retorna a distância total (em metros) da rota
    * (somatório das distâncias entre todas as aid boxes da rota)
    * 
    * @return a distância total (em metros) da rota
    */
    @Override
    public double getTotalDistance() {
        for (int i = 0; i < this.numAidBoxes - 1; i++) {
            try {
                this.totalDistance += this.aidboxes[i].getDistance(this.aidboxes[(i + 1)]);
            } catch (AidBoxException ex) {               
                Logger.getLogger(RouteImp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
         
        return this.totalDistance;
    }

    /**
    * Método que retorna o tempo (em minutos) que demora a percorrer a rota
    * (somatório dos tempos entre todas as aid boxes da rota)
    * 
    * @return a duração total (em minutos) da rota
    */
    @Override
    public double getTotalDuration() {
        for (int i = 0; i < this.numAidBoxes - 1; i++) {
            try {
                this.totalDuration += this.aidboxes[i].getDuration(this.aidboxes[(i + 1)]);
            } catch (AidBoxException ex) {               
                Logger.getLogger(RouteImp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
         
        return this.totalDuration;
    }

    /** Método equals de RouteImp
     * 
     * Compara uma rota com o objeto recebido
     * 
     * @param obj o objeto a ser comparado com uma rota
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
        final RouteImp other = (RouteImp) obj;
        if (this.numAidBoxes != other.numAidBoxes) {
            return false;
        }
        if (Double.doubleToLongBits(this.totalDistance) != Double.doubleToLongBits(other.totalDistance)) {
            return false;
        }
        if (Double.doubleToLongBits(this.totalDuration) != Double.doubleToLongBits(other.totalDuration)) {
            return false;
        }
        if (!Arrays.deepEquals(this.aidboxes, other.aidboxes)) {
            return false;
        }
        if (!Objects.equals(this.vehicle, other.vehicle)) {
            return false;
        }
        return Objects.equals(this.report, other.report);
    }

    /**
     * Método toString, que retorna a representação em string de uma rota.
     *
     * @return uma string que representa uma rota.
     */
    @Override
    public String toString() {
        return "RouteImp{" + "numAidBoxes=" + numAidBoxes + ", aidboxes=" + Arrays.toString(aidboxes) + ", vehicle=" + vehicle + ", report=" + report + ", totalDistance=" + totalDistance + ", totalDuration=" + totalDuration + '}';
    }
    
    
}
