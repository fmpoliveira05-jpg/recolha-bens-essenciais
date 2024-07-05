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
import com.estg.core.Institution;
import com.estg.core.Measurement;
import com.estg.core.exceptions.AidBoxException;
import com.estg.core.exceptions.ContainerException;
import com.estg.core.exceptions.InstitutionException;
import com.estg.core.exceptions.MeasurementException;
import com.estg.core.exceptions.VehicleException;
import com.estg.io.HTTPProvider;
import com.estg.io.Importer;
import com.estg.pickingManagement.Vehicle;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/*
* Nome: Francisco Miguel Pereira Oliveira
* Número: 8230148
* Turma: LEIT2
*/


/**
 * ImporterImp, classe que importa os dados dos 6 ficheiros JSON
 * 
 * @author Francisco Miguel Pereira Oliveira
 * @version 1.0
 * @since 2024-06-27
 */
public class ImporterImp implements Importer {
    private final int NUM_URLS = 6;
    
    private HTTPProvider http;
    
    private String[] jsonData;
    
    /**
    * Método construtor de ImporterImp.
    * 
    */
    public ImporterImp() {
        this.http = new HTTPProvider();
        this.jsonData = new String[NUM_URLS];
    }
    
    /**
     * Método para verificar se o código do container recebido como parâmetro existe em alguma aid box da instituição
     * 
     * @param instn insituição a verificar se o container existe 
     * @param container_find container a verificar se existe
     * 
     * @return i, a posição do container
     * @return -1, se não encontrar o container
     */
    private int findContainer(Institution instn, String container_find) {
        AidBox[] aid_list = instn.getAidBoxes();
        for (AidBox aidbox: aid_list) {
            Container[] container = aidbox.getContainers();
            for (int i = 0; i < container.length; i++) {
                if (container[i].getCode().equals(container_find)) {
                   return i;
                } 
            }
        }
        
        return -1;
    }
    
    /**
    * Método que lê o ficheiro "aidBoxes.json" (aid boxes e respetivos containers)
    * 
    * @param instn instituição para a qual os dados vão ser importados
    * @throws FileNotFoundException se o ficheiro não for encontrado
    * @throws IOException se o ficheiro não puder ser lido
    */
    private void getAidBoxes(Institution instn) throws FileNotFoundException, IOException {
        JSONParser jsonP = new JSONParser();
        try (FileReader reader = new FileReader("./jsonFiles/aidBoxes.json")) {
            Object obj = jsonP.parse(reader);
            JSONArray aidBoxArray = (JSONArray) obj;        

            for (Object o : aidBoxArray) {
                JSONObject readingObj = (JSONObject) o;
                String code = (String) readingObj.get("code");
                JSONArray contentor_JSON = (JSONArray) readingObj.get("containers");
                
                AidBox aid = new AidBoxImp(code);
                
                for (Object cont_obj : contentor_JSON) {
                    Container temp_container = new ContainerImp((String) cont_obj);
                    try {
                        aid.addContainer(temp_container);
                    } catch (ContainerException ex) {
                        System.out.println("Exceção: " + ex.getMessage());
                    }
                }
                
                try {
                    instn.addAidBox(aid);
                } catch (AidBoxException ex) {
                    System.out.println("Exceção: " + ex.getMessage());
                }
            }
        } catch (FileNotFoundException ex) {
            throw new FileNotFoundException("O ficheiro aidBoxes.json não foi encontrado!");
        } catch (IOException ex) {
            throw new IOException("Não foi possível ler o ficheiro aidBoxes.json!");
        } catch (ParseException ex) {
            System.out.println("Exceção: " + ex.getMessage());
        }
    }
    
    /**
    * Método que lê o ficheiro "containers.json" (aid boxes e respetivos containers)
    * 
    * @param instn instituição para a qual os dados vão ser importados
    * @throws FileNotFoundException se o ficheiro não for encontrado
    * @throws IOException se o ficheiro não puder ser lido
    */
    private void getContainers(Institution instn) throws FileNotFoundException, IOException {
        JSONParser jsonP = new JSONParser();
        try (FileReader reader = new FileReader("./jsonFiles/containers.json")) {
            Object obj = jsonP.parse(reader);
            JSONArray containerArray = (JSONArray) obj;

            for (Object o : containerArray) {
                JSONObject containerObj = (JSONObject) o;
                String code = (String) containerObj.get("code");
                long capacity = (long) containerObj.get("capacity");
                String type = (String) containerObj.get("type");

                for (AidBox aidBox : instn.getAidBoxes()) {
                    for (Container container : aidBox.getContainers()) {
                        if (container.getCode().equals(code)) {
                            ((ContainerImp) container).setCapacity((double) capacity);
                            ((ContainerImp) container).setType(new ContainerTypeImp(type));
                        }
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            throw new FileNotFoundException("O ficheiro containers.json não foi encontrado!");
        } catch (IOException ex) {
            throw new IOException("Não foi possível ler o ficheiro containers.json!");
        } catch (ParseException ex) {
            System.out.println("Exceção: " + ex.getMessage());
        }
    }
    
    /**
    * Método que lê o ficheiro "distances.json" (distâncias entre as aid boxes e entre cada aid box e a base)
    * (a base é a instituição)
    * 
    * @param instn instituição para a qual os dados vão ser importados
    * @throws FileNotFoundException se o ficheiro não for encontrado
    * @throws IOException se o ficheiro não puder ser lido
    */
    private void getDistances(Institution instn) throws FileNotFoundException, IOException {
        if (instn instanceof InstitutionImp) {
            JSONParser jsonP = new JSONParser();
            try {
                FileReader reader = new FileReader("./jsonFiles/distances.json");
                Object obj = jsonP.parse(reader); 
                JSONArray distancesArray = (JSONArray) obj;
                for (Object o: distancesArray) {
                    JSONObject readingObj = (JSONObject) o;

                    String from = (String) readingObj.get("from");
                    JSONArray aidboxesArray = (JSONArray) readingObj.get("to");

                    for (Object aid_obj : aidboxesArray) {
                        JSONObject readingObjDistance = (JSONObject) aid_obj;
                        String aidbox_cod = (String) readingObjDistance.get("name");
                        long distance_c = (long) readingObjDistance.get("distance");
                        long duration = (long) readingObjDistance.get("duration");


                        if (aidbox_cod.equals("Base")) {
                            Distance dist_base = new Distance(from, distance_c, duration);
                            ((InstitutionImp) instn).addDistance(dist_base);
                        }

                        Distance dist_temp = new Distance(aidbox_cod, distance_c, duration);
                        ((InstitutionImp) instn).addDistanceAid(from, dist_temp);                    
                    }
                }
            } catch (FileNotFoundException ex) {
                throw new FileNotFoundException("O ficheiro distances.json não foi encontrado!");
            } catch (IOException ex) {
                throw new IOException("Não foi possível ler o ficheiro distances.json!");
            } catch (ParseException ex) {
                System.out.println("Exceção: " + ex.getMessage());
            }
        } 
    }

    /**
    * Método que lê o ficheiro "readings.json" (medições dos contentores)
    * 
    * @param instn instituição para a qual os dados vão ser importados
    * @throws FileNotFoundException se o ficheiro não for encontrado
    * @throws IOException se o ficheiro não puder ser lido
    */
    private void getReadings(Institution instn) throws FileNotFoundException, IOException {
        JSONParser jsonP = new JSONParser();
        
        try (FileReader reader = new FileReader("./jsonFiles/readings.json")) {
            Object obj = jsonP.parse(reader);
            JSONArray contairarray = (JSONArray) obj;

            for (Object o : contairarray) {
                JSONObject readingObj = (JSONObject) o;

                String contentor_code = (String) readingObj.get("contentor");
                String data_string = (String) readingObj.get("data");

                Instant instant = Instant.parse(data_string);
                LocalDateTime data = instant.atOffset(ZoneOffset.UTC).toLocalDateTime();

                long valor = (long) readingObj.get("valor");

                int pos_container = findContainer(instn, contentor_code);
                
                if (pos_container != -1) {
                    try {
                        Measurement meas_temp = new MeasurementImp(data, valor);
                        for (AidBox aidbox : instn.getAidBoxes()) {
                            for (Container container : aidbox.getContainers()) {
                                if (container.getCode().equals(contentor_code)) {
                                    try {
                                        instn.addMeasurement(meas_temp, container);
                                    } catch (ContainerException ex) {
                                        Logger.getLogger(ImporterImp.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                }
                            }
                        }
                    } catch (MeasurementException ex) {
                        Logger.getLogger(ImporterImp.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    System.out.println("O container lido não existe em nenhuma aidbox da instituição");
                }
            }
        } catch (FileNotFoundException ex) {
            throw new FileNotFoundException("O ficheiro readings.json não foi encontrado!");
        } catch (IOException ex) {
            throw new IOException("Não foi possível ler o ficheiro readings.json!");
        } catch (ParseException ex) {
            System.out.println("Exceção: " + ex.getMessage());
        }
    }
    
    /**
    * Método que lê o ficheiro "types.json" (tipos de containers)
    * 
    * @param instn instituição para a qual os dados vão ser importados
    * @throws FileNotFoundException se o ficheiro não for encontrado
    * @throws IOException se o ficheiro não puder ser lido
    */
    private void getTypes(Institution instn) throws FileNotFoundException, IOException, ParseException {
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader("./jsonFiles/types.json")) {
            Object obj = jsonParser.parse(reader);
            JSONObject jsonObject = (JSONObject) obj;
            JSONArray typesArray = (JSONArray) jsonObject.get("types");

            for (Object typeObj : typesArray) {
                String typeStr = (String) typeObj;
                
                ContainerType ct = new ContainerTypeImp(typeStr);
                ((InstitutionImp)instn).addType(ct);
            }
            
        } catch (FileNotFoundException ex) {
            throw new FileNotFoundException("O ficheiro vehicles.json não foi encontrado!");
        } catch (IOException ex) {
            throw new IOException("Não foi possível ler o ficheiro vehicles.json!");
        } catch (ParseException ex) {
            System.out.println("Exceção: " + ex.getMessage());
        }
    }
    
    /**
    * Método que lê o ficheiro "vehicles.json" (veículos da instituição)
    * 
    * @param instn instituição para a qual os dados vão ser importados
    * @throws FileNotFoundException se o ficheiro não for encontrado
    * @throws IOException se o ficheiro não puder ser lido
    */
    private void getVehicles(Institution instn) throws FileNotFoundException, IOException {
        JSONParser jsonP = new JSONParser();
        try (FileReader reader = new FileReader("./jsonFiles/vehicles.json")) {
            Object obj = jsonP.parse(reader);
            JSONArray vehicleArray = (JSONArray) obj;

            for (Object o : vehicleArray) {
                JSONObject vehicleObj = (JSONObject) o;

                String code = (String) vehicleObj.get("code");

                JSONObject capacityObj = (JSONObject) vehicleObj.get("capacity");

                int numTypes = capacityObj.size();
                ContainerType[] types = new ContainerType[numTypes];
                int[] capacities = new int[numTypes];

                int index = 0;
                for (Object key : capacityObj.keySet()) {
                    String type = (String) key;
                    int capacity = ((Long) capacityObj.get(type)).intValue();
                    if (capacity > 0) {
                        types[index] = new ContainerTypeImp(type);
                        capacities[index] = capacity;
                        index++;
                    }
                }

                ContainerType[] finalTypes = new ContainerType[index];
                int[] finalCapacities = new int[index];
                for (int i = 0; i < index; i++) {
                    finalTypes[i] = types[i];
                    finalCapacities[i] = capacities[i];
                }

                Vehicle vehicle = new VehicleImp(code, finalTypes, finalCapacities);
                instn.addVehicle(vehicle);
            }
        } catch (FileNotFoundException ex) {
            throw new FileNotFoundException("O ficheiro vehicles.json não foi encontrado!");
        } catch (IOException ex) {
            throw new IOException("Não foi possível ler o ficheiro vehicles.json!");
        } catch (ParseException ex) {
            System.out.println("Exceção: " + ex.getMessage());
        } catch (VehicleException ex) {
            Logger.getLogger(ImporterImp.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
    * Método que importa o conteúdo dos 6 ficheiros JSON para o programa
    * 
    * @param instn instituição para a qual os dados vão ser importados
    * @throws FileNotFoundException se o ficheiro não for encontrado
    * @throws IOException se o ficheiro não puder ser lido
    * @throws InstitutionException se a instituição for nula
    */
    @Override
    public void importData(Institution instn) throws FileNotFoundException, IOException, InstitutionException {
        if (instn == null) {
            throw new InstitutionException("A instituição recebida está a null!");
        }
        
        this.getAidBoxes(instn);
        this.getContainers(instn);
        this.getDistances(instn);
        this.getReadings(instn);
        
        try {
            this.getTypes(instn);
        } catch (ParseException ex) {
            Logger.getLogger(ImporterImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        this.getVehicles(instn);
    }
    
    /**
    * Método que lê o url das aid boxes e interpreta os dados JSON para que possam ser utilizados no programa
    * 
    * @param instn instituição para a qual os dados vão ser importados
    */
    private void getAidBoxesAPI(Institution inst) {
        this.jsonData[0] = http.getFromURL("https://data.mongodb-api.com/app/data-docuz/endpoint/aidboxes");
        
        if (this.jsonData[0] != null) {
            JSONParser parser = new JSONParser();

            try {
                JSONArray aidBoxArray = (JSONArray) parser.parse(this.jsonData[0]);

                for (Object o : aidBoxArray) {
                    JSONObject aidboxObj = (JSONObject) o;
                    String codigo = (String) aidboxObj.get("code");
                    String zona = (String) aidboxObj.get("Zona");
                    JSONArray containersArray = (JSONArray) aidboxObj.get("containers");

                    int numContainers = containersArray.size();
                    ContainerImp[] containers = new ContainerImp[numContainers];

                    for (int i = 0; i < numContainers; i++) {
                        String codigo_cont = (String) containersArray.get(i);
                        ContainerImp temp_container = new ContainerImp(codigo_cont);
                        containers[i] = temp_container;
                    }

                    AidBoxImp aid = new AidBoxImp(codigo, zona);

                    for (int i = 0; i < numContainers; i++) {
                        try {
                            aid.addContainer(containers[i]);
                        } catch (ContainerException ex) {
                            System.out.println(ex.getMessage());
                        }
                    }

                    try {
                        inst.addAidBox(aid);
                    } catch (AidBoxException ex) {
                        System.out.println(ex.getMessage());
                    }
                    
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Falha ao importar as aid boxes da API");
        }
    }

    /**
    * Método que lê o url dos containers e interpreta os dados JSON para que possam ser utilizados no programa
    * 
    * @param instn instituição para a qual os dados vão ser importados
    */
    private void getContainersAPI(Institution inst) {
        this.jsonData[1] = http.getFromURL("https://data.mongodb-api.com/app/data-docuz/endpoint/containers");
        
        if (this.jsonData[1] != null) {
            JSONParser parser = new JSONParser();

            try {
                JSONArray containerArray = (JSONArray) parser.parse(this.jsonData[1]);

                for (Object o : containerArray) {
                    JSONObject containerObj = (JSONObject) o;
                    String code = (String) containerObj.get("code");
                    long capacity = (long) containerObj.get("capacity");
                    String type = (String) containerObj.get("type");

                    for (AidBox aidBox : inst.getAidBoxes()) {
                        for (Container container : aidBox.getContainers()) {
                            if (container.getCode().equals(code)) {
                                ((ContainerImp) container).setCapacity((double) capacity);
                                ((ContainerImp) container).setType(new ContainerTypeImp(type));
                            }
                        }
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Falha ao importar os containers da API");
        }
    }
    
    /**
    * Método que lê o url dos tipos de containers e interpreta os dados JSON para que possam ser utilizados no programa
    * 
    * @param instn instituição para a qual os dados vão ser importados
    */
    private void getTypesAPI(Institution inst) {
        this.jsonData[5] = http.getFromURL("https://data.mongodb-api.com/app/data-docuz/endpoint/types");
        
        if (this.jsonData[5] != null) {
            JSONParser parser = new JSONParser();
            
            try {
                JSONArray jsonArray = (JSONArray) parser.parse(this.jsonData[5]);
                JSONObject jsonObject = (JSONObject) jsonArray.get(0);
                JSONArray typesArray = (JSONArray) jsonObject.get("types");

                for (Object typeObj : typesArray) {
                    String typeStr = (String) typeObj;
                    ContainerType ct = new ContainerTypeImp(typeStr);
                    ((InstitutionImp) inst).addType(ct);
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Falha ao importar os tipos da API");
        }
    }
    
    /**
    * Método que lê o url das distâncias e interpreta os dados JSON para que possam ser utilizados no programa
    * 
    * @param instn instituição para a qual os dados vão ser importados
    */
    private void getDistancesAPI(Institution inst) {
        this.jsonData[3] = http.getFromURL("https://data.mongodb-api.com/app/data-docuz/endpoint/distances");
        
        if (this.jsonData[3] != null) {
            JSONParser parser = new JSONParser();

            try {
                JSONArray distancesArray = (JSONArray) parser.parse(this.jsonData[3]);

                for (Object o : distancesArray) {
                    JSONObject readingObj = (JSONObject) o;
                    String from = (String) readingObj.get("from");
                    JSONArray toArray = (JSONArray) readingObj.get("to");

                    for (Object toObj : toArray) {
                        JSONObject toEntry = (JSONObject) toObj;
                        String aidbox_cod = (String) toEntry.get("name");
                        long distance_c = (long) toEntry.get("distance");
                        long duration = (long) toEntry.get("duration");

                        if (aidbox_cod.equals("Base")) {
                            Distance dist_base = new Distance(from, distance_c, duration);
                            ((InstitutionImp) inst).addDistance(dist_base);
                        }

                        Distance dist_temp = new Distance(aidbox_cod, distance_c, duration);
                        ((InstitutionImp) inst).addDistanceAid(from, dist_temp);
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Falha ao importar as distâncias da API");
        }
    }
    
    /**
    * Método que lê o url dos veículos e interpreta os dados JSON para que possam ser utilizados no programa
    * 
    * @param instn instituição para a qual os dados vão ser importados
    */
    private void getVehiclesAPI(Institution inst) {
        this.jsonData[4] = http.getFromURL("https://data.mongodb-api.com/app/data-docuz/endpoint/vehicles");
        
        if (this.jsonData[4] != null) {
            JSONParser parser = new JSONParser();

            try {
                JSONArray vehicleArray = (JSONArray) parser.parse(this.jsonData[4]);

                for (Object o : vehicleArray) {
                    JSONObject vehicleObj = (JSONObject) o;

                    String code = (String) vehicleObj.get("code");

                    JSONObject capacityObj = (JSONObject) vehicleObj.get("capacity");

                    int numTypes = capacityObj.size();
                    ContainerType[] types = new ContainerType[numTypes];
                    int[] capacities = new int[numTypes];

                    int index = 0;
                    for (Object key : capacityObj.keySet()) {
                        String type = (String) key;
                        int capacity = ((Long) capacityObj.get(type)).intValue();
                        if (capacity > 0) {
                            types[index] = new ContainerTypeImp(type);
                            capacities[index] = capacity;
                            index++;
                        }
                    }

                    ContainerType[] finalTypes = new ContainerType[index];
                    int[] finalCapacities = new int[index];
                    for (int i = 0; i < index; i++) {
                        finalTypes[i] = types[i];
                        finalCapacities[i] = capacities[i];
                    }

                    VehicleImp vehicle = new VehicleImp(code, finalTypes, finalCapacities);

                    inst.addVehicle(vehicle);
                }
            } catch (ParseException | VehicleException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Falha ao importar os veículos da API");
        }
    }
    
    /**
    * Método que lê o url das medições e interpreta os dados JSON para que possam ser utilizados no programa
    * 
    * @param instn instituição para a qual os dados vão ser importados
    */
    private void getReadingsAPI(Institution inst) {
        int pos_container = -1;
        Measurement tmp = null;
        
        this.jsonData[5] = http.getFromURL("https://data.mongodb-api.com/app/data-docuz/endpoint/readings");
        
        if (this.jsonData[5] != null) {
            JSONParser parser = new JSONParser();

            try {
                JSONArray contairarray = (JSONArray) parser.parse(this.jsonData[5]);

                for (Object o : contairarray) {
                    JSONObject readingObj = (JSONObject) o;

                    String contentor_code = (String) readingObj.get("contentor");
                    String data_string = (String) readingObj.get("data");

                    Instant instant = Instant.parse(data_string);
                    LocalDateTime data = instant.atOffset(ZoneOffset.UTC).toLocalDateTime();

                    long valor = (long) readingObj.get("valor");

                    AidBox[] aid_list = inst.getAidBoxes();
                    for (AidBox aidbox: aid_list) {
                        Container[] container = aidbox.getContainers();
                        for (int i = 0; i < container.length; i++) {
                            if (container[i].getCode().equals(contentor_code)) {
                                pos_container = i;
                            } 
                        }
                    }

                    if (pos_container != -1) {
                        try {
                            MeasurementImp meas_temp = new MeasurementImp(data, valor);
                            for (AidBox aidbox : inst.getAidBoxes()) {
                                for (Container container : aidbox.getContainers()) {
                                    if (container.getCode().equals(contentor_code)) {
                                        inst.addMeasurement(meas_temp, container);
                                        tmp = meas_temp;
                                    }
                                }
                            }
                        } catch (ContainerException | MeasurementException ex) {
                            if (ex.getMessage().equals("O valor da medição ultrapassa a capacidade máxima do contentor!")) {
                                WarningManagement wm = new WarningManagementImp();
                                wm.addWarning(new WarningImp("Medição inválida!", tmp));
                            } else {
                                Logger.getLogger(ImporterImp.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    } else {
                        System.out.println("O container lido não existe em nenhuma aidbox da instituição");
                    }
                }
            } catch (ParseException ex) {
                ex.printStackTrace();
            }
        } else {
            System.out.println("Falha ao importar as medições da API");
        }
    }
    
    /**
    * Método que lê todos os urls e interpreta os dados JSON para que possam ser utilizados no programa
    * 
    * @param inst instituição para a qual os dados vão ser importados
    */
    public void getAllURL(Institution inst) {      
        this.getAidBoxesAPI(inst);
        this.getContainersAPI(inst);
        this.getTypesAPI(inst);
        this.getVehiclesAPI(inst);
        this.getDistancesAPI(inst);
        this.getReadingsAPI(inst);
    }
    
    
}
