package recolha.io;

import java.io.IOException;

/**
 * Origem dos documentos JSON. Permite que o importador trate da mesma forma os ficheiros
 * locais e a Web API (na versão original o código de leitura estava duplicado para cada caso).
 *
 * @author Francisco Miguel Pereira Oliveira
 */
public interface DataSource {

    /**
     * @param dataset conjunto de dados pretendido
     * @return o documento JSON, em texto
     * @throws IOException se não for possível obter o documento
     */
    String load(Dataset dataset) throws IOException;

    /** @return descrição curta da origem, para mostrar ao utilizador */
    String describe();
}
