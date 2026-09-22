package recolha.ui;

import com.estg.core.ContainerType;
import com.estg.core.exceptions.VehicleException;
import com.estg.pickingManagement.Vehicle;
import java.io.PrintStream;
import recolha.core.ContainerTypeImp;
import recolha.core.InstitutionImp;
import recolha.picking.VehicleImp;

/**
 * Gestão da frota: adicionar, ativar, desativar e listar veículos.
 *
 * @author Francisco Miguel Pereira Oliveira
 */
class VehicleMenu {

    private final InstitutionImp institution;
    private final ConsoleInput input;
    private final PrintStream out;

    VehicleMenu(InstitutionImp institution, ConsoleInput input) {
        this.institution = institution;
        this.input = input;
        this.out = input.out();
    }

    void run() {
        int option;
        do {
            this.out.println();
            this.out.println("--- Veículos ---");
            this.out.println("1 - Adicionar veículo");
            this.out.println("2 - Desativar veículo");
            this.out.println("3 - Ativar veículo");
            this.out.println("4 - Listar veículos");
            this.out.println("0 - Voltar");
            option = this.input.readInt("Opção: ", 0, 4);
            try {
                switch (option) {
                    case 1:
                        addVehicle();
                        break;
                    case 2:
                        this.institution.disableVehicle(askVehicle());
                        this.out.println("Veículo desativado.");
                        break;
                    case 3:
                        this.institution.enableVehicle(askVehicle());
                        this.out.println("Veículo ativado.");
                        break;
                    case 4:
                        Printer.vehicles(this.out, "Veículos ativos", this.institution.getVehicles());
                        Printer.vehicles(this.out, "Veículos desativados", this.institution.getDisabledVehicles());
                        break;
                    default:
                        break;
                }
            } catch (VehicleException ex) {
                this.out.println(ex.getMessage());
            }
        } while (option != 0);
    }

    private Vehicle askVehicle() throws VehicleException {
        String code = this.input.readText("Código do veículo: ");
        Vehicle vehicle = this.institution.findVehicle(code);
        if (vehicle == null) {
            throw new VehicleException("Não existe nenhum veículo com o código " + code + ".");
        }
        return vehicle;
    }

    private void addVehicle() throws VehicleException {
        String code = this.input.readText("Código do veículo: ");
        int numTypes = this.input.readInt("Quantos tipos de contentor transporta? ", 1, 20);
        ContainerType[] types = new ContainerType[numTypes];
        int[] capacities = new int[numTypes];
        for (int i = 0; i < numTypes; i++) {
            types[i] = new ContainerTypeImp(this.input.readText("Tipo " + (i + 1) + ": "));
            capacities[i] = this.input.readInt("Número máximo de contentores deste tipo: ", 1, 100);
        }
        try {
            boolean added = this.institution.addVehicle(new VehicleImp(code, types, capacities));
            this.out.println(added ? "Veículo adicionado." : "Já existe um veículo com esse código.");
        } catch (IllegalArgumentException ex) {
            this.out.println(ex.getMessage());
        }
    }
}
