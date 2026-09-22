package recolha.ui;

import java.nio.file.Path;
import recolha.core.InstitutionImp;

/**
 * Ponto de entrada da aplicação de consola.
 *
 * <p>Uso: {@code java -jar recolha-bens-essenciais.jar [pasta-de-dados]}. Por omissão os
 * ficheiros JSON são procurados na pasta {@code data/}.</p>
 *
 * @author Francisco Miguel Pereira Oliveira
 */
public final class App {

    private App() {
    }

    /**
     * @param args opcionalmente, a pasta com os ficheiros JSON
     */
    public static void main(String[] args) {
        Path dataDirectory = Path.of(args.length > 0 ? args[0] : "data");
        ConsoleInput input = new ConsoleInput(System.in, System.out);
        InstitutionImp institution = new InstitutionImp("Instituição de Ajuda Humanitária");
        try {
            new MainMenu(institution, input, dataDirectory).run();
        } catch (ConsoleInput.EndOfInputException ex) {
            System.out.println();
            System.out.println("Entrada terminada. Até à próxima!");
        }
    }
}
