package recolha.io;

import com.estg.core.AidBox;
import com.estg.core.Container;
import com.estg.core.ContainerType;
import com.estg.core.Institution;
import com.estg.core.exceptions.AidBoxException;
import com.estg.core.exceptions.ContainerException;
import com.estg.core.exceptions.InstitutionException;
import com.estg.core.exceptions.MeasurementException;
import com.estg.core.exceptions.VehicleException;
import com.estg.io.Importer;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import recolha.alerts.AlertManager;
import recolha.core.AidBoxImp;
import recolha.core.ContainerImp;
import recolha.core.ContainerTypeImp;
import recolha.core.Distance;
import recolha.core.InstitutionImp;
import recolha.core.MeasurementImp;
import recolha.picking.VehicleImp;
import recolha.util.DynamicArray;

/**
 * Importa os seis conjuntos de dados (tipos, contentores, caixas, distâncias, veículos e
 * leituras) para uma instituição.
 *
 * <p>Os dados são lidos por uma ordem que garante que tudo aquilo de que um registo depende
 * já existe: primeiro os contentores, depois as caixas que os referem, e só no fim as
 * leituras. Qualquer registo inválido (contentor inexistente, leitura acima da capacidade,
 * campo em falta...) não interrompe a importação: é ignorado e fica guardado como alerta,
 * como pede o enunciado.</p>
 *
 * @author Francisco Miguel Pereira Oliveira
 */
public class ImporterImp implements Importer {

    private static final String BASE_CODE = "Base";

    private final DataSource source;
    private ImportSummary lastSummary = new ImportSummary();

    /**
     * @param source origem dos documentos JSON (ficheiros ou API)
     */
    public ImporterImp(DataSource source) {
        this.source = source;
    }

    /** @return contagens da última importação */
    public ImportSummary getLastSummary() {
        return this.lastSummary;
    }

    /**
     * @param institution instituição a preencher (tem de ser uma {@link InstitutionImp})
     * @throws IOException se algum dos documentos não puder ser obtido
     * @throws InstitutionException se a instituição for nula ou de outro tipo
     */
    @Override
    public void importData(Institution institution) throws IOException, InstitutionException {
        if (!(institution instanceof InstitutionImp)) {
            throw new InstitutionException("É necessária uma instância de InstitutionImp para importar os dados.");
        }
        InstitutionImp inst = (InstitutionImp) institution;
        ImportSummary summary = new ImportSummary();

        importTypes(inst, summary);
        DynamicArray<Container> containers = importContainers(inst, summary);
        importAidBoxes(inst, containers, summary);
        registerSpares(inst, containers, summary);
        importDistances(inst, summary);
        importVehicles(inst, summary);
        importReadings(inst, summary);

        this.lastSummary = summary;
    }

    // ---------------------------------------------------------------- cada conjunto de dados

    private void importTypes(InstitutionImp inst, ImportSummary summary) throws IOException {
        Object json = parse(Dataset.TYPES);
        // O ficheiro tem {"types": [...]}, a API devolvia [{"types": [...]}].
        if (json instanceof JSONArray && !((JSONArray) json).isEmpty()) {
            json = ((JSONArray) json).get(0);
        }
        if (!(json instanceof JSONObject) || !(((JSONObject) json).get("types") instanceof JSONArray)) {
            inst.getAlerts().add("Formato inesperado no documento de tipos", json);
            return;
        }
        for (Object type : (JSONArray) ((JSONObject) json).get("types")) {
            if (type instanceof String && !((String) type).isBlank() && inst.addType(new ContainerTypeImp((String) type))) {
                summary.types++;
            }
        }
    }

    private DynamicArray<Container> importContainers(InstitutionImp inst, ImportSummary summary) throws IOException {
        DynamicArray<Container> containers = new DynamicArray<>();
        for (JSONObject record : objects(Dataset.CONTAINERS, inst.getAlerts())) {
            String code = text(record, "code");
            String type = text(record, "type");
            Double capacity = number(record, "capacity");
            if (code == null || type == null || capacity == null) {
                inst.getAlerts().add("Contentor com campos em falta", record);
                continue;
            }
            try {
                ContainerType containerType = new ContainerTypeImp(type);
                inst.addType(containerType);
                containers.add(new ContainerImp(code, containerType, capacity));
                summary.containers++;
            } catch (ContainerException ex) {
                inst.getAlerts().add(ex.getMessage(), record);
            }
        }
        return containers;
    }

    private void importAidBoxes(InstitutionImp inst, DynamicArray<Container> containers, ImportSummary summary) throws IOException {
        for (JSONObject record : objects(Dataset.AID_BOXES, inst.getAlerts())) {
            String code = text(record, "code");
            if (code == null) {
                inst.getAlerts().add("Caixa de suprimentos sem código", record);
                continue;
            }
            String zone = text(record, "zone") != null ? text(record, "zone") : text(record, "Zona");
            AidBoxImp box = new AidBoxImp(code, zone);
            Object list = record.get("containers");
            if (list instanceof JSONArray) {
                for (Object entry : (JSONArray) list) {
                    String containerCode = entry instanceof JSONObject ? text((JSONObject) entry, "code") : String.valueOf(entry);
                    Container container = containers.find(c -> c.getCode().equals(containerCode));
                    if (container == null) {
                        inst.getAlerts().add("A caixa " + code + " refere um contentor que não existe: " + containerCode, record);
                        continue;
                    }
                    try {
                        box.addContainer(container);
                    } catch (ContainerException ex) {
                        inst.getAlerts().add(ex.getMessage(), container);
                    }
                }
            }
            try {
                if (inst.addAidBox(box)) {
                    summary.aidBoxes++;
                }
            } catch (AidBoxException ex) {
                inst.getAlerts().add(ex.getMessage(), box);
            }
        }
    }

    /** Os contentores que não estão em nenhuma caixa ficam na base, vazios, prontos para trocas. */
    private static void registerSpares(InstitutionImp inst, DynamicArray<Container> containers, ImportSummary summary) {
        for (int i = 0; i < containers.size(); i++) {
            if (inst.addSpareContainer(containers.get(i))) {
                summary.spareContainers++;
            }
        }
    }

    private void importDistances(InstitutionImp inst, ImportSummary summary) throws IOException {
        for (JSONObject record : objects(Dataset.DISTANCES, inst.getAlerts())) {
            String from = text(record, "from");
            Object destinations = record.get("to");
            if (from == null || !(destinations instanceof JSONArray)) {
                inst.getAlerts().add("Registo de distâncias inválido", record);
                continue;
            }
            for (Object item : (JSONArray) destinations) {
                if (!(item instanceof JSONObject)) {
                    continue;
                }
                JSONObject destination = (JSONObject) item;
                String to = text(destination, "name");
                Double meters = number(destination, "distance");
                Double minutes = number(destination, "duration");
                if (to == null || meters == null || minutes == null || meters < 0 || minutes < 0) {
                    inst.getAlerts().add("Distância inválida a partir de " + from, destination);
                    continue;
                }
                if (storeDistance(inst, from, to, meters, minutes)) {
                    summary.distances++;
                }
            }
        }
    }

    private static boolean storeDistance(InstitutionImp inst, String from, String to, double meters, double minutes) {
        if (from.equals(to)) {
            return false;
        }
        if (BASE_CODE.equalsIgnoreCase(to)) {
            inst.addBaseDistance(new Distance(from, meters, minutes));
            return true;
        }
        if (BASE_CODE.equalsIgnoreCase(from)) {
            inst.addBaseDistance(new Distance(to, meters, minutes));
            return true;
        }
        AidBox origin = inst.findAidBox(from);
        if (origin instanceof AidBoxImp && inst.findAidBox(to) != null) {
            ((AidBoxImp) origin).addDistance(new Distance(to, meters, minutes));
            return true;
        }
        inst.getAlerts().add("Distância entre caixas desconhecidas: " + from + " → " + to, null);
        return false;
    }

    private void importVehicles(InstitutionImp inst, ImportSummary summary) throws IOException {
        for (JSONObject record : objects(Dataset.VEHICLES, inst.getAlerts())) {
            String code = text(record, "code");
            Object capacity = record.get("capacity");
            if (code == null || !(capacity instanceof JSONObject)) {
                inst.getAlerts().add("Veículo com campos em falta", record);
                continue;
            }
            JSONObject capacities = (JSONObject) capacity;
            DynamicArray<ContainerType> types = new DynamicArray<>();
            DynamicArray<Integer> counts = new DynamicArray<>();
            for (Object key : capacities.keySet()) {
                Double value = number(capacities, String.valueOf(key));
                if (value != null && value > 0) {
                    types.add(new ContainerTypeImp(String.valueOf(key)));
                    counts.add(value.intValue());
                }
            }
            int[] capacityArray = new int[counts.size()];
            for (int i = 0; i < capacityArray.length; i++) {
                capacityArray[i] = counts.get(i);
            }
            try {
                if (inst.addVehicle(new VehicleImp(code, types.toArray(ContainerType[]::new), capacityArray))) {
                    summary.vehicles++;
                }
            } catch (VehicleException | IllegalArgumentException ex) {
                inst.getAlerts().add(ex.getMessage(), record);
            }
        }
    }

    private void importReadings(InstitutionImp inst, ImportSummary summary) throws IOException {
        for (JSONObject record : objects(Dataset.READINGS, inst.getAlerts())) {
            String containerCode = text(record, "contentor");
            String date = text(record, "data");
            Double value = number(record, "valor");
            if (containerCode == null || date == null || value == null) {
                inst.getAlerts().add("Leitura com campos em falta", record);
                continue;
            }
            Container container = inst.findContainer(containerCode);
            if (container == null) {
                inst.getAlerts().add("Leitura de um contentor que não está instalado em nenhuma caixa", record);
                continue;
            }
            try {
                LocalDateTime instant = Instant.parse(date).atOffset(ZoneOffset.UTC).toLocalDateTime();
                if (inst.addMeasurement(new MeasurementImp(instant, value), container)) {
                    summary.readings++;
                }
            } catch (DateTimeParseException ex) {
                inst.getAlerts().add("Data de leitura inválida", record);
            } catch (MeasurementException | ContainerException ex) {
                inst.getAlerts().add(ex.getMessage(), record);
            }
        }
    }

    // ---------------------------------------------------------------- auxiliares de JSON

    private Object parse(Dataset dataset) throws IOException {
        String text = this.source.load(dataset);
        try {
            return new JSONParser().parse(text);
        } catch (ParseException ex) {
            throw new IOException("O documento " + dataset.getFileName() + " não é JSON válido (" + ex + ").", ex);
        }
    }

    /** Devolve os objetos de um documento que deve ser uma lista; o resto gera alertas. */
    private JSONObject[] objects(Dataset dataset, AlertManager alerts) throws IOException {
        Object json = parse(dataset);
        DynamicArray<JSONObject> result = new DynamicArray<>();
        if (!(json instanceof JSONArray)) {
            alerts.add("O documento " + dataset.getFileName() + " devia ser uma lista", json);
            return new JSONObject[0];
        }
        for (Object item : (JSONArray) json) {
            if (item instanceof JSONObject) {
                result.add((JSONObject) item);
            } else {
                alerts.add("Elemento inesperado em " + dataset.getFileName(), item);
            }
        }
        return result.toArray(JSONObject[]::new);
    }

    private static String text(JSONObject object, String key) {
        Object value = object.get(key);
        return value instanceof String && !((String) value).isBlank() ? ((String) value).trim() : null;
    }

    private static Double number(JSONObject object, String key) {
        Object value = object.get(key);
        return value instanceof Number ? ((Number) value).doubleValue() : null;
    }

    /** Contagens de registos importados com sucesso. */
    public static final class ImportSummary {
        private int types;
        private int containers;
        private int spareContainers;
        private int aidBoxes;
        private int distances;
        private int vehicles;
        private int readings;

        /** @return tipos novos registados */
        public int getTypes() {
            return this.types;
        }

        /** @return contentores lidos */
        public int getContainers() {
            return this.containers;
        }

        /** @return contentores que ficaram de reserva na base */
        public int getSpareContainers() {
            return this.spareContainers;
        }

        /** @return caixas adicionadas */
        public int getAidBoxes() {
            return this.aidBoxes;
        }

        /** @return distâncias registadas */
        public int getDistances() {
            return this.distances;
        }

        /** @return veículos adicionados */
        public int getVehicles() {
            return this.vehicles;
        }

        /** @return leituras registadas */
        public int getReadings() {
            return this.readings;
        }

        @Override
        public String toString() {
            return String.format("%d tipos, %d contentores (%d de reserva), %d caixas, %d distâncias, %d veículos, %d leituras",
                    this.types, this.containers, this.spareContainers, this.aidBoxes, this.distances, this.vehicles, this.readings);
        }
    }
}
