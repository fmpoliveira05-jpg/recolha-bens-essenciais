package recolha.ui;

import com.estg.core.AidBox;
import com.estg.core.Container;
import com.estg.core.exceptions.AidBoxException;
import com.estg.core.exceptions.ContainerException;
import com.estg.core.exceptions.MeasurementException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import recolha.core.AidBoxImp;
import recolha.core.ContainerImp;
import recolha.core.ContainerTypeImp;
import recolha.core.Distance;
import recolha.core.InstitutionImp;
import recolha.core.MeasurementImp;

/**
 * Gestão manual da rede de recolha: caixas, contentores, leituras e distâncias.
 *
 * @author Francisco Miguel Pereira Oliveira
 */
class NetworkMenu {

    private final InstitutionImp institution;
    private final ConsoleInput input;
    private final PrintStream out;

    NetworkMenu(InstitutionImp institution, ConsoleInput input) {
        this.institution = institution;
        this.input = input;
        this.out = input.out();
    }

    void runComponents() {
        int option;
        do {
            this.out.println();
            this.out.println("--- Caixas, contentores e leituras ---");
            this.out.println("1 - Adicionar caixa de suprimentos");
            this.out.println("2 - Instalar contentor numa caixa");
            this.out.println("3 - Registar leitura de um contentor");
            this.out.println("0 - Voltar");
            option = this.input.readInt("Opção: ", 0, 3);
            try {
                switch (option) {
                    case 1:
                        addAidBox();
                        break;
                    case 2:
                        addContainer();
                        break;
                    case 3:
                        addMeasurement();
                        break;
                    default:
                        break;
                }
            } catch (AidBoxException | ContainerException | MeasurementException ex) {
                this.out.println(ex.getMessage());
            }
        } while (option != 0);
    }

    void runDistances() {
        int option;
        do {
            this.out.println();
            this.out.println("--- Distâncias ---");
            this.out.println("1 - Distância entre a base e uma caixa");
            this.out.println("2 - Distância entre duas caixas");
            this.out.println("0 - Voltar");
            option = this.input.readInt("Opção: ", 0, 2);
            try {
                if (option == 1) {
                    AidBox box = askAidBox("Código da caixa: ");
                    this.institution.addBaseDistance(askDistance(box.getCode()));
                    this.out.println("Distância registada.");
                } else if (option == 2) {
                    AidBox from = askAidBox("Caixa de origem: ");
                    AidBox to = askAidBox("Caixa de destino: ");
                    if (from.equals(to)) {
                        this.out.println("A origem e o destino são a mesma caixa (distância 0).");
                    } else if (from instanceof AidBoxImp) {
                        ((AidBoxImp) from).addDistance(askDistance(to.getCode()));
                        this.out.println("Distância registada.");
                    }
                }
            } catch (AidBoxException ex) {
                this.out.println(ex.getMessage());
            }
        } while (option != 0);
    }

    private void addAidBox() throws AidBoxException {
        String code = this.input.readText("Código da caixa: ");
        String zone = this.input.readText("Zona: ");
        boolean added = this.institution.addAidBox(new AidBoxImp(code, zone));
        this.out.println(added ? "Caixa adicionada." : "Já existe uma caixa com esse código.");
    }

    private void addContainer() throws AidBoxException, ContainerException {
        AidBox box = askAidBox("Código da caixa: ");
        String code = this.input.readText("Código do contentor: ");
        if (this.institution.findContainer(code) != null) {
            this.out.println("Já existe um contentor com esse código.");
            return;
        }
        ContainerTypeImp type = new ContainerTypeImp(this.input.readText("Tipo de bens: "));
        double capacity = this.input.readDouble("Capacidade (kg): ", 0.1);
        box.addContainer(new ContainerImp(code, type, capacity));
        this.institution.addType(type);
        this.out.println("Contentor instalado.");
    }

    private void addMeasurement() throws ContainerException, MeasurementException {
        String code = this.input.readText("Código do contentor: ");
        Container container = this.institution.findContainer(code);
        if (container == null) {
            throw new ContainerException("Não existe nenhum contentor instalado com o código " + code + ".");
        }
        double value = this.input.readDouble("Peso lido (kg): ", 0);
        this.institution.addMeasurement(new MeasurementImp(LocalDateTime.now(), value), container);
        this.out.println("Leitura registada.");
    }

    private AidBox askAidBox(String prompt) throws AidBoxException {
        String code = this.input.readText(prompt);
        AidBox box = this.institution.findAidBox(code);
        if (box == null) {
            throw new AidBoxException("Não existe nenhuma caixa com o código " + code + ".");
        }
        return box;
    }

    private Distance askDistance(String destinationCode) {
        double meters = this.input.readDouble("Distância (metros): ", 0);
        double minutes = this.input.readDouble("Duração (minutos): ", 0);
        return new Distance(destinationCode, meters, minutes);
    }
}
