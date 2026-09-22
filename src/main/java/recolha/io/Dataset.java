package recolha.io;

/**
 * Os seis conjuntos de dados disponibilizados pela Web API (e pelos ficheiros de exemplo).
 *
 * @author Francisco Miguel Pereira Oliveira
 */
public enum Dataset {
    TYPES("types", "types.json"),
    CONTAINERS("containers", "containers.json"),
    AID_BOXES("aidboxes", "aidBoxes.json"),
    DISTANCES("distances", "distances.json"),
    VEHICLES("vehicles", "vehicles.json"),
    READINGS("readings", "readings.json");

    private final String endpoint;
    private final String fileName;

    Dataset(String endpoint, String fileName) {
        this.endpoint = endpoint;
        this.fileName = fileName;
    }

    /** @return último segmento do URL da API */
    public String getEndpoint() {
        return this.endpoint;
    }

    /** @return nome do ficheiro JSON local equivalente */
    public String getFileName() {
        return this.fileName;
    }
}
