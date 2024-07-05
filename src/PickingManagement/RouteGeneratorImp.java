package PickingManagement;

import Core.ContainerImp;
import Core.ContainerTypeImp;
import Core.InstitutionImp;
import com.estg.core.ContainerType;
import com.estg.core.Institution;
import com.estg.core.exceptions.AidBoxException;
import com.estg.core.exceptions.PickingMapException;
import com.estg.core.exceptions.VehicleException;
import com.estg.pickingManagement.Route;
import com.estg.pickingManagement.RouteGenerator;
import com.estg.pickingManagement.exceptions.RouteException;
import java.time.LocalDateTime;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
* Nome: Francisco Miguel Pereira Oliveira
* Número: 8230148
* Turma: LEIT2
*/


/**
 * RouteGeneratorImp, classe que gera as rotas para uma dada instituição
 * 
 * @author Francisco Miguel Pereira Oliveira
 * @version 1.0
 * @since 2024-06-27
 */
public class RouteGeneratorImp implements RouteGenerator {
    
    /**
    * Método construtor de RouteGeneratorImp.
    * 
    */
    public RouteGeneratorImp() {}
    
    /** Método para criar novas rotas, associando a cada uma delas um veículo da instituição
     * 
     * @param tmpRoutes conjunto de rotas (vazio)
     * @param instn instituição recebida
     */
    public void instantiateRoutes(Route[] tmpRoutes, Institution instn) {
        for (int i = 0; i < instn.getVehicles().length; i++) {
            tmpRoutes[i] = new RouteImp(instn.getVehicles()[i], new ReportImp());
        }
    }
    
    /** Método para preencher as novas rotas, adicionado as aid boxes da instituição
     * 
     * @param tmpRoutes conjunto de rotas (com as rotas já instanciadas)
     * @param instn instituição recebida
     */
    public void fillRoutes(Route[] tmpRoutes, Institution instn) {
        int numTmpRoutes = 0;
        
        for (int i = 0; i < instn.getAidBoxes().length; i++) {
            try {
                tmpRoutes[numTmpRoutes].addAidBox(instn.getAidBoxes()[i]);
            } catch (RouteException ex) {
                if (ex.getMessage().equals("A rota está cheia!")) {
                    numTmpRoutes++;
                } else {
                    Logger.getLogger(RouteGeneratorImp.class.getName()).log(Level.SEVERE, null, ex);
                }
            }   
        }
    }
    
    /** Método para calcular os números de veículos usados e não usados para cada rota
     * 
     * @param tmpRoutes conjunto de rotas (com as rotas já preenchidas)
     */
    public void createVehicleReport(Route[] tmpRoutes) {
        for (int i = 0; i < tmpRoutes.length; i++) {
            if (((RouteImp) tmpRoutes[i]).getNumAidBoxes() != 0) {
                ((ReportImp) tmpRoutes[i].getReport()).setUsedVehicles();
            } else {
                ((ReportImp) tmpRoutes[i].getReport()).setNotUsedVehicles();
            }
        }
    }
    
    /** Método para recolher os containers do tipo perishable food em primeiro lugar
     * 
     * @param tmpRoutes conjunto de rotas (com as rotas já preenchidas)
     * @param ct tipo de container (inicializado como perishable food)
     * @param instn instituição recebida
     */
    public void pickPerishableFoodContainers(Route[] tmpRoutes, ContainerType ct, Institution instn) {      
        for (int i = 0; i < tmpRoutes.length; i++) {
            for (int j = 0; j < tmpRoutes[i].getRoute().length; j++) {
                if (tmpRoutes[i].getRoute()[j] != null) {
                    for (int k = 0; k < tmpRoutes[i].getRoute()[j].getContainers().length; k++) {
                        if (tmpRoutes[i].getRoute()[j].getContainers()[k].getType().equals(ct)) {
                            if (((VehicleImp) tmpRoutes[i].getVehicle()).getCurrentCapacity(tmpRoutes[i].getRoute()[j].getContainers()[k].getType()) <
                                    tmpRoutes[i].getVehicle().getCapacity(tmpRoutes[i].getRoute()[j].getContainers()[k].getType())) {
                                try {
                                    ((ReportImp) tmpRoutes[i].getReport()).setTotalDistance(instn.getDistance(tmpRoutes[i].getRoute()[j]) * 2);
                                    ((ReportImp) tmpRoutes[i].getReport()).setTotalDuration(((InstitutionImp) instn).getDuration(tmpRoutes[i].getRoute()[j]) * 2);
                                } catch (AidBoxException ex) {
                                    Logger.getLogger(RouteGeneratorImp.class.getName()).log(Level.SEVERE, null, ex);
                                }

                                try {
                                    ((VehicleImp) tmpRoutes[i].getVehicle()).replaceContainer(((VehicleImp) tmpRoutes[i].getVehicle()).findContainer(tmpRoutes[i].getRoute()[j].getContainers()[k].getType()), tmpRoutes[i].getRoute()[j].getContainers()[k]);
                                } catch (VehicleException ex) {
                                    Logger.getLogger(RouteGeneratorImp.class.getName()).log(Level.SEVERE, null, ex);
                                }

                                ((ReportImp) tmpRoutes[i].getReport()).setPickedContainers();

                            } else {
                                try {
                                    instn.getDistance(tmpRoutes[i].getRoute()[j]);
                                    ((InstitutionImp) instn).getDuration(tmpRoutes[i].getRoute()[j]);
                                } catch (AidBoxException ex) {
                                    Logger.getLogger(RouteGeneratorImp.class.getName()).log(Level.SEVERE, null, ex);
                                }

                                ((VehicleImp) tmpRoutes[i].getVehicle()).resetCurrentCapacity();

                                try {
                                    instn.getDistance(tmpRoutes[i].getRoute()[j]);
                                    ((InstitutionImp) instn).getDuration(tmpRoutes[i].getRoute()[j]);
                                } catch (AidBoxException ex) {
                                    Logger.getLogger(RouteGeneratorImp.class.getName()).log(Level.SEVERE, null, ex);
                                }

                                try {
                                    ((VehicleImp) tmpRoutes[i].getVehicle()).replaceContainer(((VehicleImp) tmpRoutes[i].getVehicle()).findContainer(tmpRoutes[i].getRoute()[j].getContainers()[k].getType()), tmpRoutes[i].getRoute()[j].getContainers()[k]);
                                } catch (VehicleException ex) {
                                    Logger.getLogger(RouteGeneratorImp.class.getName()).log(Level.SEVERE, null, ex);
                                }

                                ((ReportImp) tmpRoutes[i].getReport()).setPickedContainers();
                            }
                        }
                    }
                }
            }
        }
    }
    
    /** Método para recolher os restantes containers, com lotação superior a 80% preenchida
     * 
     * @param tmpRoutes conjunto de rotas (com as rotas já preenchidas)
     * @param ct tipo de container (inicializado como perishable food)
     * @param instn instituição recebida
     */
    public void pickRemainingContainers(Route[] tmpRoutes, ContainerType ct, Institution instn) {
        for (int i = 0; i < tmpRoutes.length; i++) {
            for (int j = 0; j < tmpRoutes[i].getRoute().length; j++) {
                if (tmpRoutes[i].getRoute()[j] != null) {
                    for (int k = 0; k < tmpRoutes[i].getRoute()[j].getContainers().length; k++) {
                        if (!tmpRoutes[i].getRoute()[j].getContainers()[k].getType().equals(ct)) {
                            if (((ContainerImp) tmpRoutes[i].getRoute()[j].getContainers()[k]).getMeasurements()[((ContainerImp) tmpRoutes[i].getRoute()[j].getContainers()[k]).getNumMeasurements() - 1].getValue()
                                    > (tmpRoutes[i].getRoute()[j].getContainers()[k].getCapacity() - tmpRoutes[i].getRoute()[j].getContainers()[k].getCapacity() * 0.20)) {
                                if (((VehicleImp) tmpRoutes[i].getVehicle()).getCurrentCapacity(tmpRoutes[i].getRoute()[j].getContainers()[k].getType()) <
                                        tmpRoutes[i].getVehicle().getCapacity(tmpRoutes[i].getRoute()[j].getContainers()[k].getType())) {
                                    try {
                                        ((ReportImp) tmpRoutes[i].getReport()).setTotalDistance(instn.getDistance(tmpRoutes[i].getRoute()[j]) * 2);
                                        ((ReportImp) tmpRoutes[i].getReport()).setTotalDuration(((InstitutionImp) instn).getDuration(tmpRoutes[i].getRoute()[j]) * 2);
                                    } catch (AidBoxException ex) {
                                        Logger.getLogger(RouteGeneratorImp.class.getName()).log(Level.SEVERE, null, ex);
                                    }

                                    try {
                                        ((VehicleImp) tmpRoutes[i].getVehicle()).replaceContainer(((VehicleImp) tmpRoutes[i].getVehicle()).findContainer(tmpRoutes[i].getRoute()[j].getContainers()[k].getType()), tmpRoutes[i].getRoute()[j].getContainers()[k]);
                                    } catch (VehicleException ex) {
                                        Logger.getLogger(RouteGeneratorImp.class.getName()).log(Level.SEVERE, null, ex);
                                    }

                                    ((ReportImp) tmpRoutes[i].getReport()).setPickedContainers();

                                } else {
                                    try {
                                        instn.getDistance(tmpRoutes[i].getRoute()[j]);
                                        ((InstitutionImp) instn).getDuration(tmpRoutes[i].getRoute()[j]);
                                    } catch (AidBoxException ex) {
                                        Logger.getLogger(RouteGeneratorImp.class.getName()).log(Level.SEVERE, null, ex);
                                    }

                                    ((VehicleImp) tmpRoutes[i].getVehicle()).resetCurrentCapacity();

                                    try {
                                        instn.getDistance(tmpRoutes[i].getRoute()[j]);
                                        ((InstitutionImp) instn).getDuration(tmpRoutes[i].getRoute()[j]);
                                    } catch (AidBoxException ex) {
                                        Logger.getLogger(RouteGeneratorImp.class.getName()).log(Level.SEVERE, null, ex);
                                    }

                                    try {
                                        ((VehicleImp) tmpRoutes[i].getVehicle()).replaceContainer(((VehicleImp) tmpRoutes[i].getVehicle()).findContainer(tmpRoutes[i].getRoute()[j].getContainers()[k].getType()), tmpRoutes[i].getRoute()[j].getContainers()[k]);
                                    } catch (VehicleException ex) {
                                        Logger.getLogger(RouteGeneratorImp.class.getName()).log(Level.SEVERE, null, ex);
                                    }

                                    ((ReportImp) tmpRoutes[i].getReport()).setPickedContainers();
                                }
                            } else {
                                ((ReportImp) tmpRoutes[i].getReport()).setNonPickedContainers();
                            }
                        }
                    }
                }
            }
        }
    }

    /** Método que gera as rotas para a instituição, de acordo com a estratégia, e ainda imprime o relatório por rota
     * 
     * @param instn instituição recebida
     * @return o conjunto de rotas geradas para a instituição
     */
    @Override
    public Route[] generateRoutes(Institution instn) {       
        ContainerType ct = new ContainerTypeImp("perishable food");
        Route[] tmpRoutes = new RouteImp[instn.getAidBoxes().length];
        
        this.instantiateRoutes(tmpRoutes, instn);
        
        this.fillRoutes(tmpRoutes, instn);
        
        this.createVehicleReport(tmpRoutes);
        
        this.pickPerishableFoodContainers(tmpRoutes, ct, instn);
        
        this.pickRemainingContainers(tmpRoutes, ct, instn);
        
        try {
            instn.addPickingMap(new PickingMapImp(LocalDateTime.now(), tmpRoutes));
        } catch (PickingMapException ex) {
            Logger.getLogger(RouteGeneratorImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        for (int i = 0; i < tmpRoutes.length; i++) {
            System.out.println(tmpRoutes[i].getReport());
        }
        
        return tmpRoutes;
    }
 
    
}
