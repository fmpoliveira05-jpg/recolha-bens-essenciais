package recolha.ui;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

/**
 * Leitura validada a partir da consola.
 *
 * <p>Na versão original cada menu tinha o seu próprio {@code Scanner} e o seu próprio ciclo
 * de validação (repetido dezenas de vezes). Esta classe centraliza essa lógica e garante que,
 * se a entrada terminar (Ctrl+D / Ctrl+Z), a aplicação não fica presa num ciclo infinito.</p>
 *
 * @author Francisco Miguel Pereira Oliveira
 */
public class ConsoleInput {

    private final BufferedReader reader;
    private final PrintStream out;

    /**
     * @param in origem dos dados (normalmente {@code System.in})
     * @param out destino das mensagens (normalmente {@code System.out})
     */
    public ConsoleInput(InputStream in, PrintStream out) {
        this.reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
        this.out = out;
    }

    /** @return o destino das mensagens */
    public PrintStream out() {
        return this.out;
    }

    /**
     * Lê uma linha de texto não vazia.
     *
     * @param prompt pergunta a mostrar
     * @return o texto introduzido, sem espaços nas pontas
     * @throws EndOfInputException se a entrada terminar
     */
    public String readText(String prompt) {
        while (true) {
            String line = readLine(prompt);
            if (!line.isBlank()) {
                return line.trim();
            }
            this.out.println("Este campo é obrigatório.");
        }
    }

    /**
     * Lê um número inteiro dentro de um intervalo.
     *
     * @param prompt pergunta a mostrar
     * @param min valor mínimo aceite
     * @param max valor máximo aceite
     * @return o número introduzido
     */
    public int readInt(String prompt, int min, int max) {
        while (true) {
            String line = readLine(prompt).trim();
            try {
                int value = Integer.parseInt(line);
                if (value >= min && value <= max) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                // tratado na mensagem abaixo
            }
            this.out.printf("Introduza um número inteiro entre %d e %d.%n", min, max);
        }
    }

    /**
     * Lê um número real maior ou igual a um mínimo (aceita vírgula ou ponto decimal).
     *
     * @param prompt pergunta a mostrar
     * @param min valor mínimo aceite
     * @return o número introduzido
     */
    public double readDouble(String prompt, double min) {
        while (true) {
            String line = readLine(prompt).trim().replace(',', '.');
            try {
                double value = Double.parseDouble(line);
                if (value >= min && !Double.isNaN(value) && !Double.isInfinite(value)) {
                    return value;
                }
            } catch (NumberFormatException ignored) {
                // tratado na mensagem abaixo
            }
            this.out.printf("Introduza um número maior ou igual a %s.%n", min);
        }
    }

    private String readLine(String prompt) {
        this.out.print(prompt);
        try {
            String line = this.reader.readLine();
            if (line == null) {
                throw new EndOfInputException();
            }
            return line;
        } catch (IOException ex) {
            throw new EndOfInputException();
        }
    }

    /** Lançada quando já não há mais entrada para ler. */
    public static class EndOfInputException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        EndOfInputException() {
            super("A entrada de dados terminou.");
        }
    }
}
