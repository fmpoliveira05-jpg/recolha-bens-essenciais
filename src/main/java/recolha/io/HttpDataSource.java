package recolha.io;

import com.estg.io.HTTPProvider;
import java.io.IOException;

/**
 * Obtém os documentos JSON da Web API disponibilizada pelos docentes, usando o
 * {@link HTTPProvider} fornecido nos recursos do trabalho (de utilização obrigatória).
 *
 * <p>Nota: a API original estava alojada no MongoDB Atlas Data API, serviço que a MongoDB
 * descontinuou em 2025. O URL base é configurável para poder apontar para outro servidor
 * com os mesmos endpoints.</p>
 *
 * @author Francisco Miguel Pereira Oliveira
 */
public class HttpDataSource implements DataSource {

    /** URL base usado no ano letivo 2023/24. */
    public static final String DEFAULT_BASE_URL = "https://data.mongodb-api.com/app/data-docuz/endpoint/";

    private final String baseUrl;
    private final HTTPProvider http;

    /**
     * @param baseUrl URL base, terminado ou não em "/"
     */
    public HttpDataSource(String baseUrl) {
        this.baseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
        this.http = new HTTPProvider();
    }

    @Override
    public String load(Dataset dataset) throws IOException {
        String url = this.baseUrl + dataset.getEndpoint();
        String body;
        try {
            body = this.http.getFromURL(url);
        } catch (RuntimeException ex) {
            throw new IOException("Falha no pedido a " + url + ": " + ex.getMessage(), ex);
        }
        if (body == null || body.isBlank()) {
            throw new IOException("A API não devolveu dados em " + url);
        }
        return body;
    }

    @Override
    public String describe() {
        return "API " + this.baseUrl;
    }
}
