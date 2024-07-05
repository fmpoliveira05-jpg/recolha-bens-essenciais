package IO;

import Core.AidBoxImp;
import Core.ContainerImp;
import Core.ContainerTypeImp;
import Core.Distance;
import Core.InstitutionImp;
import Core.MeasurementImp;
import PickingManagement.VehicleImp;
import com.estg.core.AidBox;
import com.estg.core.Container;
import com.estg.core.ContainerType;
import com.estg.core.exceptions.AidBoxException;
import com.estg.core.exceptions.ContainerException;
import com.estg.core.exceptions.InstitutionException;
import com.estg.core.exceptions.MeasurementException;
import com.estg.core.exceptions.VehicleException;
import com.estg.pickingManagement.Vehicle;
import PickingManagement.RouteGeneratorImp;
import com.estg.core.Institution;
import com.estg.io.Importer;
import com.estg.pickingManagement.RouteGenerator;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.Scanner;

/*
* Nome: Francisco Miguel Pereira Oliveira
* Número: 8230148
* Turma: LEIT2
*/

/**
 * Classe MenuOption, classe que contém todas as opções do menu do programa
 * @author Francisco Miguel Pereira Oliveira
 * @version 1.0
 * @since 2024-06-27
*/
public class MenuOption {   
    private Institution institution;
    
    private BufferedReader reader;
  
    /**
    * Método construtor da classe MenuOption
    * 
    * @param institution instituição a receber a alteração
    */ 
    public MenuOption(Institution institution) {
        this.institution = institution;
        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }
    
    /**
     * Método que permite criar um veículo novo para a instituição.
     */
    private void add_vehicle() throws VehicleException {
        String code = "";
        String type_vehicle = "";
        int maxTypes = 0;
        int[] capacities;
        ContainerType[] types_vehicle;

        try {          
            do {
                System.out.print("Introduza o código do veículo: ");
                code = reader.readLine();
            } while (code.equals(""));

            while (maxTypes <= 0) {
                System.out.print("Insira quantos tipos de containers vazios o veículo vai transportar: ");
                try {
                    maxTypes = Integer.parseInt(reader.readLine());
                    if (maxTypes > 0) {
                        break;
                    }
                } catch (NumberFormatException nfe) {
                    System.out.println("Não introduza letras!");
                }
            }

            types_vehicle = new ContainerTypeImp[maxTypes];
            capacities = new int[maxTypes];

            for (int i = 0; i < maxTypes; i++) {
                System.out.print("Insira o tipo: ");
                type_vehicle = reader.readLine();
                types_vehicle[i] = new ContainerTypeImp(type_vehicle);
            }

            for (int i = 0; i < maxTypes; i++) {
                while (capacities[i] <= 0) {
                    System.out.print("Insira o número máximo de containers a transportar para cada tipo: ");
                    try {
                        capacities[i] = Integer.parseInt(reader.readLine());
                        if (capacities[i] > 0) {
                            break;
                        }
                    } catch (NumberFormatException nfe) {
                        System.out.println("Não introduza letras!");
                    }
                }
            }

            this.institution.addVehicle(new VehicleImp(code, types_vehicle, capacities));
            
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    /**
     * Método para selecionar um veículo
     * 
     * @return vei, se a matrícula introduzida pelo utilizador pertence a um veículo da instituição
     * @retun null, se a matrícula introduzida pelo utilizador não pertencer a nenhum veículo
     */
    private Vehicle select_vei() {
        String code = "";
        Vehicle vei = null;

        try {
            do {
                System.out.print("Introduza o código do veículo: ");
                code = this.reader.readLine();
            } while (code.equals(""));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            return null;
        }

        for (Vehicle vhcl : this.institution.getVehicles()) {
            if (vhcl instanceof VehicleImp && ((VehicleImp) vhcl).getCode().equals(code)) {
                vei = vhcl;
                break;
            }
        }

        if (vei == null) {
            for (Vehicle vhcl2 : ((InstitutionImp) this.institution).getDisabledVehicles()) {
                if (vhcl2 instanceof VehicleImp && ((VehicleImp) vhcl2).getCode().equals(code)) {
                    vei = vhcl2;
                    break;
                }
            }
        }

        if (vei != null) {
            System.out.println("Veículo encontrado!");
        } else {
            System.out.println("Veículo não encontrado...");
        }

        return vei;
}
    
    /**
     * Menu de veículos
     * 
     * @throws VehicleException
     */
    public void menuVehicles() throws VehicleException {
        int op = -1;
        Vehicle vei = null; 
        Scanner scanner = new Scanner(System.in);
                
        do {
            System.out.println("1-\tAdicionar veículo");
            System.out.println("2-\tDesativar veículo");
            System.out.println("3-\tAtivar veículo");
            System.out.println("4-\tListar todos os veículos ativos");
            System.out.println("0-\tVoltar ao menu principal");
            System.out.print("Escolha uma opção: ");
            
            try {
                op = scanner.nextInt();
            } catch (InputMismatchException ex) {
                scanner.next(); 
            }
           
            switch (op) {
                case 0: {
                    break;
                }
                case 1: {
                    this.add_vehicle();
                    break;
                } case 2: {
                    if (this.institution.getVehicles().length > 0) {
                        vei = this.select_vei();

                        if (vei != null) {
                            this.institution.disableVehicle(vei);
                        }
                    } else {
                        System.out.println("Todos os veículos estão inativos ou não existem veículos!");
                    }
                    break;
                } case 3: {
                    if (((InstitutionImp)this.institution).getDisabledVehicles().length > 0) {
                        vei = this.select_vei();

                        if (vei != null) {
                            this.institution.enableVehicle(vei);
                        }
                    } else {
                        System.out.println("Todos os veículos estão ativos ou não existem veículos!");
                    } 
                    break;
                } case 4: {
                    if (this.institution.getVehicles().length > 0) {
                        System.out.println(Arrays.toString(this.institution.getVehicles()));
                    } else {
                        System.out.println("Não existem veículos ativos!");
                    }
                    break;
                } default: {
                    System.out.println("Opção Inválida!");
                    break;
                }
            }                                  
        } while(op != 0);
    }
    
    /**
     * Menu de componentes (para adicionar uma aid box, measurement ou container).
     * 
     */
    public void menuComponents() {
        int op = -1;
        Scanner scanner = new Scanner(System.in);
                
        do {
            System.out.println("1-\tAdicionar aid box");
            System.out.println("2-\tAdicionar measurement");
            System.out.println("3-\tAdicionar container");
            System.out.println("0-\tVoltar ao menu principal");
            System.out.print("Escolha uma opção: ");
            
            try {
                op = scanner.nextInt();
            } catch (InputMismatchException ex) {
                scanner.next(); 
            }
           
            switch (op) {
                case 0: {
                    break;
                }
                case 1: {
                    System.out.println("Adicionar aid box!");
                    this.addAidBox();
                    break;
                } case 2: {
                    if (((InstitutionImp) institution).getNumContainers() > 0) {
                        System.out.println("Adicionar measurement!"); 
                        this.addMeasurement();
                    } else {
                        System.out.println("Não é possível adicionar uma medição, pois ainda não existem containers!");
                    }
                    break;
                } case 3: {
                    if (institution.getAidBoxes().length > 0) {
                        System.out.println("Adicionar container!");
                        this.addContainer();
                    } else {
                        System.out.println("Adicione primeiro uma aid box para poder adicionar um container!");
                    }
                    break;
                } default: {
                    System.out.println("Opção Inválida!");
                    break;
                }
            }                                  
        } while(op != 0);
    }
    
    /**
     * Menu de distâncias (para adicionar uma distância da instituição a uma aid box ou entre aid boxes).
     * 
     */
    public void menuDistances() {
        int op = -1;
        Scanner scanner = new Scanner(System.in);
                
        do {
            System.out.println("1-\tAdicionar institution distance");
            System.out.println("2-\tAdicionar aid box distance");
            System.out.println("0-\tVoltar ao menu principal");
            System.out.print("Escolha uma opção: ");
            
            try {
                op = scanner.nextInt();
            } catch (InputMismatchException ex) {
                scanner.next(); 
            }
           
            switch (op) {
                case 0: {
                    break;
                }
                case 1: {
                    if (institution.getAidBoxes().length > 0) {
                        System.out.println("Adicionar institution distance!");
                        this.addInstitutionDistance();
                    } else {
                        System.out.println("Não é possível adicionar uma distância, pois ainda não existem aid boxes!");
                    }
                    break;
                } case 2: {
                    if (institution.getAidBoxes().length > 1) {
                        System.out.println("Adicionar aid box distance!");
                        this.addAidBoxDistance();
                    } else {
                        System.out.println("Não é possível adicionar uma distância, pois devem existir, pelo menos, duas aid boxes!");
                    }
                    break;
                } default: {
                    System.out.println("Opção Inválida!");
                    break;
                }
            }                                  
        } while(op != 0);
    }

    /**
     * Método para adicionar um novo container a uma aid box da instituição.
     */
    public void addContainer() {
        int pos = -1;
        String cod_aid = "";
        String code_container = "";
        double capacity = 0;
        String type = "";
        ContainerType ct = null;

        try {
            do {
                System.out.print("Introduza o código da aid box para a qual o container será enviado: ");
                cod_aid = reader.readLine();
            } while (cod_aid.equals(""));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            return;
        }

        pos = ((InstitutionImp) this.institution).findAidBox(cod_aid);
        if (pos == -1) {
            System.out.println("Aid box não encontrada...");
            return;
        }

        try {
            do {
                System.out.print("Introduza o código do container: ");
                code_container = reader.readLine();
            } while (code_container.equals(""));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            return;
        }

        try {
            System.out.print("Introduza o tipo de container pretendido: ");
            type = reader.readLine();
            ct = new ContainerTypeImp(type);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            return;
        }

        while (capacity <= 0) {
            try {
                System.out.print("Introduza o valor para a capacidade máxima do container: ");
                capacity = Double.parseDouble(reader.readLine());
                if (capacity > 0) {
                    break;
                } else {
                    System.out.println("O valor deve ser superior a 0!");
                }
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
                return;
            } catch (NumberFormatException nfe) {
                System.out.println("Não introduza letras!");
            }
        }

        try {
            this.institution.getAidBoxes()[pos].addContainer(new ContainerImp(code_container, ct, capacity));
            System.out.println("O container foi adicionado com sucesso!");
        } catch (ContainerException ex) {
            System.out.println(ex.getMessage());
        }
}
    
    
    /**
     * Método que procura por um container em todas as aid boxes da instituição.
     * 
     * @param cod_container código do container a ser procurado
     * 
     * @return container, caso o container a procurar exista em alguma aid box da instituição
     * @return null, caso o container não seja encontrado
     */
    private Container find_container(String cod_container) {
        for (AidBox aid: institution.getAidBoxes()) {
            for (Container container: aid.getContainers()) {
                if (container.getCode().equals(cod_container)) {
                    return container;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Método para adicionar uma nova medição a um container da instituição.
     */
    public void addMeasurement() {
        Container container = null;
        String cod_container = "";
        double value_med = 0;

        System.out.print("Introduza o código do container: ");

        try {
            cod_container = reader.readLine();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            return;
        }

        for (AidBox aid : institution.getAidBoxes()) {
            for (Container cntnr : aid.getContainers()) {
                if (cntnr.getCode().equals(cod_container)) {
                    container = cntnr;
                }
            }
        }

        if (container == null) {
            System.out.println("Container não encontrado...");
            return;
        }

        while (true) {
            System.out.print("Introduza o valor da medição: ");

            try {
                value_med = Double.parseDouble(reader.readLine());
            } catch (NumberFormatException nfe) {
                System.out.println("Não introduza letras!");
                continue;
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
                return;
            }

            if (value_med <= 0) {
                System.out.println("O valor da medição deve ser superior a 0!");
                continue;
            } else if (container.getCapacity() < value_med) {
                System.out.println("O valor introduzido é maior do que a capacidade máxima do container!");
                continue;
            }

            break;
        }

        try {
            this.institution.addMeasurement(new MeasurementImp(LocalDateTime.now(), value_med), container);
            System.out.println("A medição foi adicionada ao container!");
        } catch (ContainerException | MeasurementException ex) {
            System.out.println(ex.getMessage());
        }
    }   
    
    /**
     * Método para adicionar uma nova distância de uma aid box à instituição.
     */
    public void addInstitutionDistance()  {
        int pos_aidbox = -1;
        String cod_aidbox = "";
        double dist = 0.0;
        double duration = 0.0;
        
        System.out.print("Introduza o código da aid box: ");

        try {
            cod_aidbox = reader.readLine();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            return;
        }

        pos_aidbox = ((InstitutionImp)this.institution).findAidBox(cod_aidbox);

        if (pos_aidbox == -1) {
            System.out.println("Aid box não encontrada...");
            return;
        }
        
        System.out.print("Introduza uma distância: ");
        
        while (dist <= 0) {
            try {
                dist = Double.parseDouble(reader.readLine());
                if (dist > 0) {
                        break;
                    } else {
                        System.out.println("A distância deve ser superior a 0!");
                    }
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
                return;
            } catch (NumberFormatException nfe) {
                    System.out.println("Não introduza letras!");
            }
        }
        
        System.out.print("Introduza uma duração: ");
        
        while (duration <= 0) {
            try {
                duration = Double.parseDouble(reader.readLine());
                if (duration > 0) {
                        break;
                    } else {
                        System.out.println("A duração deve ser superior a 0!");
                    }
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
                return;
            } catch (NumberFormatException nfe) {
                    System.out.println("Não introduza letras!");
            }
        }
        
        ((InstitutionImp)this.institution).addDistance(new Distance(cod_aidbox, dist, duration));
    }
    
    /**
     * Método para adicionar uma nova distância entre duas aid boxes da instituição.
     */
    public void addAidBoxDistance()  {
        int pos_aidbox = -1;
        int pos_aidbox2 = -1;
        String cod_aidbox = "";
        String cod_aidbox2 = "";
        double dist = 0.0;
        double duration = 0.0;
        
        System.out.print("Introduza o código da primeira aid box: ");

        try {
            cod_aidbox = reader.readLine();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            return;
        }

        pos_aidbox = ((InstitutionImp)this.institution).findAidBox(cod_aidbox);

        if (pos_aidbox == -1) {
            System.out.println("Aid box não encontrada...");
            return;
        }
        
        System.out.print("Introduza o código da segunda aid box: ");

        try {
            cod_aidbox2 = reader.readLine();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            return;
        }
        
        pos_aidbox2 = ((InstitutionImp)this.institution).findAidBox(cod_aidbox2);
        
        if (pos_aidbox2 == -1) {
            System.out.println("Aid box não encontrada...");
            return;
        }
        
        if (pos_aidbox == pos_aidbox2) {
            ((AidBoxImp)((InstitutionImp)this.institution).getAidBoxes()[pos_aidbox]).addDistance(new Distance(cod_aidbox2, 0, 0));
            System.out.println("A distância e a duração foram colocadas a 0 porque trata-se da mesma aid box!");
            return;
        }
        
        System.out.print("Introduza uma distância: ");
        
        while (dist <= 0) {
            try {
                dist = Double.parseDouble(reader.readLine());
                if (dist > 0) {
                        break;
                    } else {
                        System.out.println("A distância deve ser superior a 0!");
                    }
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
                return;
            } catch (NumberFormatException nfe) {
                    System.out.println("Não introduza letras!");
            }
        }
        
        System.out.print("Introduza uma duração: ");
        
        while (duration <= 0) {
            try {
                duration = Double.parseDouble(reader.readLine());
                if (duration > 0) {
                        break;
                    } else {
                        System.out.println("A duração deve ser superior a 0!");
                    }
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
                return;
            } catch (NumberFormatException nfe) {
                    System.out.println("Não introduza letras!");
            }
        }
        ((AidBoxImp)((InstitutionImp)this.institution).getAidBoxes()[pos_aidbox]).addDistance(new Distance(cod_aidbox2, dist, duration));
    }
    
    /**
     * Método para adicionar uma nova aid box à instituição.
     */
    public void addAidBox() {
        String code = "";
        String zone = "";                        
        
        try {            
            do {
                System.out.print("Introduza o código: ");
                code = reader.readLine();
            } while (code.equals(""));

            do {
                System.out.print("Introduza a zona: ");
                zone = reader.readLine();
            } while (zone.equals(""));
            
            try {
                institution.addAidBox(new AidBoxImp(code, zone));
            } catch (AidBoxException ex) {
                System.out.println(ex.getMessage());
            }
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    /**
     * Método para adicionar um novo picking map à instituição.
     * 
     */
    public void generateRoutes() {
        RouteGenerator routegen = new RouteGeneratorImp();

        System.out.println(Arrays.toString(routegen.generateRoutes(this.institution)));
    }
    
    
    /**
     * Método para carregar todos os dados do JSON ou da Web para a instituição.
     * 
     * Este método tem um menu com 3 opções:
     * 1 - Fazer pedido web, carrega todos os dados da web para a instituição
     * 2 - Ler ficheiro JSON, carrega os dados dos 3 ficheiros JSON para a instituição 
     * 0 - Voltar ao menu principal, retorna o utilizador para o menu principal sem carregar nenhum dado
     * 
     * @param read_data, dados lidos
     * @return true, se conseguir carregar os dados do JSON ou da Web para a instituição,
     * false, caso não consiga ler ou o utilizador queira voltar para o menu principal
     */
    public boolean load_data(boolean read_data) {
        int op = -1;
        ImporterImp impt = null;
        Scanner scanner = new Scanner(System.in);
        Importer imp = new ImporterImp();
        
        do {
            System.out.println("1-\tFazer pedidos web para os endereços");
            System.out.println("2-\tImportar JSON files pelos caminhos relativos");
            System.out.println("0-\tVoltar ao menu principal");
            System.out.print("Introduza um número: ");
            
            try {
                op  = scanner.nextInt();
            } catch (InputMismatchException ex) {
                System.out.println("Caractere inválido!");
                scanner.next(); 
            }
            
            if (op < 0 | op > 2) {
                System.out.println("Intervalo fora do alcance!");
            }
        } while (op < 0 | op > 2);
        
        switch (op) {
            case 0: {
                break;
            }
            case 1: {
                ((ImporterImp)imp).getAllURL(this.institution);
                return true;             
            } case 2: {
                impt = new ImporterImp();
                
                try {
                    impt.importData(this.institution);
                } catch (IOException | InstitutionException ex) {
                    System.out.println(ex.getMessage());
                }
                return true;
            } default: {
                System.out.println("Opção Inválida!");
                break;
            }
        }
        
        return false;
    }
    
    /**
     * Método que vai buscar toda a informação da instituição.
     */
    public void info_inst() {
        System.out.println(this.institution.toString());
    }
}
