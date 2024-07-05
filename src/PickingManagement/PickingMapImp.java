package PickingManagement;

import com.estg.pickingManagement.PickingMap;
import com.estg.pickingManagement.Route;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

/*
* Nome: Francisco Miguel Pereira Oliveira
* Número: 8230148
* Turma: LEIT2
*/


/**
 * PickingMapImp, classe que representa os mapas de rotas
 * 
 * @author Francisco Miguel Pereira Oliveira
 * @version 1.0
 * @since 2024-06-27
 */
public class PickingMapImp implements PickingMap { 
    private LocalDateTime date;
    
    private Route[] routes;
    
    /**
    * Método construtor de PickingMapImp.
    * 
    * @param date data de registo do picking map
    * @param routes conjunto de rotas do picking map
    */   
    public PickingMapImp(LocalDateTime date, Route[] routes) {
        this.date = date;
        this.routes = routes;
    }
    
    /**
    * Método getter para a data do picking map
    * 
    * @return a data do picking map
    */
    @Override
    public LocalDateTime getDate() {
        return this.date;
    }

    /**
    * Método getter para o conjunto de rotas do picking map
    * 
    * @return o conjunto de rotas do picking map
    */
    @Override
    public Route[] getRoutes() {
        return this.routes;
    }

    /** Método equals de PickingMapImp
     * 
     * Compara um picking map com o objeto recebido
     * 
     * @param obj o objeto a ser comparado com um picking map
     * @return true, se os objetos forem iguais,
     * false, caso contrário
     * 
     */
    @Override    
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PickingMapImp other = (PickingMapImp) obj;
        if (!Objects.equals(this.date, other.date)) {
            return false;
        }
        return Arrays.deepEquals(this.routes, other.routes);
    }

    /**
     * Método toString de PickingMapCla, que retorna a representação em string de um picking map.
     *
     * @return uma string que representa um picking map.
     */
    @Override
    public String toString() {
        return "PickingMapImp{" + "date=" + date + ", routes=" + Arrays.toString(routes) + '}';
    }
    
    
}
