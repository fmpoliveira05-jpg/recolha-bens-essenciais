package recolha.ui;

import com.estg.core.exceptions.InstitutionException;
import com.estg.core.exceptions.PickingMapException;
import com.estg.pickingManagement.PickingMap;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import recolha.core.InstitutionImp;
import recolha.io.DataSource;
import recolha.io.FileDataSource;
import recolha.io.HttpDataSource;
import recolha.io.ImporterImp;
import recolha.picking.RouteGeneratorImp;

/**
 * Menu principal da aplicação.
 *
 * @author Francisco Miguel Pereira Oliveira
 */
public class MainMenu {

    private final InstitutionImp institution;
    private final ConsoleInput input;
    private final PrintStream out;
    private final Path dataDirectory;
    private boolean dataLoaded;

    /**
     * @param institution instituição sobre a qual o menu opera
     * @param input leitura da consola
     * @param dataDirectory pasta com os ficheiros JSON
     */
    public MainMenu(InstitutionImp institution, ConsoleInput input, Path dataDirectory) {
        this.institution = institution;
        this.input = input;
        this.out = input.out();
        this.dataDirectory = dataDirectory;
    }

    /** Mostra o menu até o utilizador escolher sair. */
    public void run() {
        VehicleMenu vehicleMenu = new VehicleMenu(this.institution, this.input);
        NetworkMenu networkMenu = new NetworkMenu(this.institution, this.input);
        int option;
        do {
            this.out.println();
            this.out.println("=== Recolha de bens essenciais ===");
            this.out.println("1 - Carregar dados");
            this.out.println("2 - Veículos");
            this.out.println("3 - Caixas, contentores e leituras");
            this.out.println("4 - Distâncias");
            this.out.println("5 - Gerar rotas de recolha");
            this.out.println("6 - Ver último mapa de recolha");
            this.out.println("7 - Ver resumo da instituição");
            this.out.println("8 - Ver alertas");
            this.out.println("9 - Histórico de mapas de recolha (por datas)");
            this.out.println("0 - Sair");
            option = this.input.readInt("Opção: ", 0, 9);
            switch (option) {
                case 1:
                    loadData();
                    break;
                case 2:
                    vehicleMenu.run();
                    break;
                case 3:
                    networkMenu.runComponents();
                    break;
                case 4:
                    networkMenu.runDistances();
                    break;
                case 5:
                    generateRoutes();
                    break;
                case 6:
                    showCurrentMap();
                    break;
                case 7:
                    Printer.institution(this.out, this.institution);
                    break;
                case 8:
                    Printer.alerts(this.out, this.institution.getAlerts().getAll());
                    break;
                case 9:
                    showMapHistory();
                    break;
                default:
                    break;
            }
        } while (option != 0);
        this.out.println("Até à próxima!");
    }

    private void loadData() {
        if (this.dataLoaded) {
            this.out.println("Os dados já foram carregados nesta sessão.");
            return;
        }
        this.out.println("1 - Ficheiros locais (" + this.dataDirectory + ")");
        this.out.println("2 - Web API");
        this.out.println("0 - Voltar");
        int option = this.input.readInt("Origem: ", 0, 2);
        if (option == 0) {
            return;
        }
        DataSource source = option == 1 ? new FileDataSource(this.dataDirectory) : new HttpDataSource(HttpDataSource.DEFAULT_BASE_URL);
        ImporterImp importer = new ImporterImp(source);
        int alertsBefore = this.institution.getAlerts().count();
        try {
            importer.importData(this.institution);
            this.dataLoaded = true;
            this.out.println("Dados importados da " + source.describe() + ": " + importer.getLastSummary());
            int newAlerts = this.institution.getAlerts().count() - alertsBefore;
            if (newAlerts > 0) {
                this.out.println(newAlerts + " registo(s) inválido(s) foram ignorados; consulte os alertas (opção 8).");
            }
        } catch (IOException | InstitutionException ex) {
            this.out.println("Não foi possível importar: " + ex.getMessage());
            if (option == 2) {
                this.out.println("Sugestão: a API do ano letivo já não está disponível; use os ficheiros locais.");
            }
        }
    }

    private void generateRoutes() {
        if (this.institution.getVehicles().length == 0 || this.institution.getAidBoxes().length == 0) {
            this.out.println("São precisos veículos ativos e caixas de suprimentos para gerar rotas.");
            return;
        }
        try {
            new RouteGeneratorImp().generateRoutes(this.institution);
            Printer.pickingMap(this.out, this.institution.getCurrentPickingMap());
        } catch (IllegalStateException | PickingMapException ex) {
            this.out.println("Não foi possível gerar as rotas: " + ex.getMessage());
        }
    }

    /**
     * Lista os mapas de recolha gerados entre duas datas (inclusive).
     */
    private void showMapHistory() {
        LocalDate from = readDate("Data inicial (AAAA-MM-DD): ");
        LocalDate to = readDate("Data final (AAAA-MM-DD): ");
        PickingMap[] maps = this.institution.getPickingMaps(from.atStartOfDay(), to.atTime(LocalTime.MAX));
        if (maps.length == 0) {
            this.out.println("Não há mapas de recolha nesse intervalo.");
            return;
        }
        for (PickingMap map : maps) {
            Printer.pickingMap(this.out, map);
        }
    }

    private LocalDate readDate(String prompt) {
        while (true) {
            try {
                return LocalDate.parse(this.input.readText(prompt));
            } catch (DateTimeParseException ex) {
                this.out.println("Data inválida. Use o formato AAAA-MM-DD, por exemplo 2024-06-18.");
            }
        }
    }

    private void showCurrentMap() {
        try {
            Printer.pickingMap(this.out, this.institution.getCurrentPickingMap());
        } catch (PickingMapException ex) {
            this.out.println(ex.getMessage());
        }
    }
}
