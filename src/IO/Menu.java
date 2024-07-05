package IO;

import Core.InstitutionImp;
import com.estg.core.Institution;
import com.estg.core.exceptions.VehicleException;
import java.util.InputMismatchException;
import java.util.Scanner;

/*
* Nome: Francisco Miguel Pereira Oliveira
* Número: 8230148
* Turma: LEIT2
*/


/**
 * Main Class Menu, classe que contém o menu principal do programa
 * @author Francisco Miguel Pereira Oliveira
 * @version 1.0
 * @since 2024-06-27
*/
public class Menu {

    /**
     * Menu principal do programa
     * 
     * @param args
     * @throws VehicleException
     */
    public static void main(String[] args) throws VehicleException {
        Institution institution = new InstitutionImp("Base");
        Scanner scanner = new Scanner(System.in);
        MenuOption op = new MenuOption(institution);
        int option = -1;
        boolean read_data = false;

        do {
            System.out.println("Main\tMenu\n");
            System.out.println("0-\tSair");
            System.out.println("1-\tMenu veículos");
            System.out.println("2-\tMenu componentes");
            System.out.println("3-\tMenu distâncias");
            System.out.println("4-\tGerar rotas");
            System.out.println("5-\tExibir instituição");
            System.out.println("6-\tCarregar dados");
            System.out.print("Introduza uma opção --> ");
            
            try {
                option = scanner.nextInt();
            } catch (InputMismatchException ex) {
                scanner.next();
            }

            switch (option) {
                case 0: {
                    break;
                } case 1: {
                    System.out.println("Menu de veículos!");
                    op.menuVehicles();
                    break;
                } case 2: {
                    System.out.println("Menu de componentes!");
                    op.menuComponents();
                    break;
                } case 3: {
                    System.out.println("Menu de distâncias!");
                    op.menuDistances();
                } case 4: {
                    if (institution.getVehicles().length > 0 && institution.getAidBoxes().length > 0) {
                        System.out.println("Gerar rotas!");
                        op.generateRoutes();
                    } else {
                        System.out.println("Impossível gerar rotas. Verifique se a instituição tem aid boxes e veículos disponíveis!");
                    }
                    break;
                } case 5: {
                    System.out.println("Dados da instituição!");
                    op.info_inst();
                    break;
                } case 6: {
                    if (read_data == false) {
                        read_data = op.load_data(read_data);   
                        System.out.println("Sucesso!");
                    } else {
                        System.out.println("Já foram carregados dados 1 vez!");
                    }
                    break;
                } default: {
                    System.out.println("Opção Inválida!");
                    break;
                }
            }
        } while (option != 0);
        
        scanner.close();
    }   
    
    
}
