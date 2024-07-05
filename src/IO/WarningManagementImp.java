package IO;

import java.util.Arrays;

/*
* Nome: Francisco Miguel Pereira Oliveira
* Número: 8230148
* Turma: LEIT2
*/


/**
 * Classe WarningImpManagement, classe que guarda os alertas
 * @author Francisco Miguel Pereira Oliveira
 * @version 1.0
 * @since 2024-06-27
*/
public class WarningManagementImp implements WarningManagement {
    private final int MAX_WARNINGS = 5;
    
    private int numWarnings;
    
    Warning[] warnings;
    
    /**
    * Método construtor de WarningManagementImp.
    * 
    */
    public WarningManagementImp() {
        this.numWarnings = 0;
        this.warnings = new Warning[MAX_WARNINGS];
    }
    
    /**
    * Método getter para o número de alertas guardados
    * 
    * @return o número de alertas guardados
    */
    @Override
    public int getNumWarnings() {
        return this.numWarnings;
    }
    
    /**
    * Método getter para o conjunto de alertas guardados
    * 
    * @return o conjunto de alertas guardados
    */
    @Override
    public Warning[] getWarnings() {
        return this.warnings;
    }

    /**
    * Método que duplica o espaço do array warnings sempre que necessário.
    */
    private void raiseWarnings() {
        Warning[] tmp = new Warning[this.warnings.length * 2];
        
        for (int i = 0; i < this.numWarnings; i++) {
            tmp[i] = this.warnings[i];
        }
        
        this.warnings = tmp;
    }
    
    /**
    * Método para adicionar um novo warning ao conjunto.
    * 
    * @param warning alerta a adicionar
    * @return true, se o alerta for adicionado com sucesso
    * false, caso contrário
    */
    @Override
    public boolean addWarning(Warning warning) {
        for (int i = 0; i < this.numWarnings; i++) {
            if (this.warnings[i].equals(warning)) {
                return false;
            }
        }
        
        if (this.numWarnings >= this.warnings.length) {
            this.raiseWarnings();
        }
        
        this.warnings[this.numWarnings++] = warning;
        return true;
    }

    /** Método toString de WarningManagementImp
     * 
     * @return os valores de numWarnings e warnings
     */
    @Override
    public String toString() {
        return "WarningManagementImp{" + "numWarnings=" + numWarnings + ", warnings=" + Arrays.toString(warnings) + '}';
    }
    
    
}
