package recolha.core;

import com.estg.core.ContainerType;
import java.util.Locale;

/**
 * Tipo de bens que um contentor guarda (ex.: "perishable food", "clothing").
 *
 * <p>Os tipos não são fixos: chegam da API e podem mudar ao longo do tempo. Por isso são
 * representados por texto e não por um {@code enum}. O nome é normalizado (sem espaços nas
 * pontas e em minúsculas) para que "Clothing" e "clothing " sejam o mesmo tipo.</p>
 *
 * @author Francisco Miguel Pereira Oliveira
 */
public final class ContainerTypeImp implements ContainerType {

    /** Nome do tipo que, segundo o enunciado, é recolhido independentemente da lotação. */
    public static final String PERISHABLE_FOOD = "perishable food";

    private final String name;

    /**
     * @param name nome do tipo
     * @throws IllegalArgumentException se o nome for nulo ou vazio
     */
    public ContainerTypeImp(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("O tipo de contentor não pode ser vazio.");
        }
        this.name = name.trim().toLowerCase(Locale.ROOT);
    }

    /** @return o nome normalizado do tipo */
    public String getName() {
        return this.name;
    }

    /**
     * @param type tipo a verificar (pode ser de outra implementação)
     * @return {@code true} se o tipo corresponder a alimentos perecíveis
     */
    public static boolean isPerishable(ContainerType type) {
        return type != null && PERISHABLE_FOOD.equals(nameOf(type));
    }

    /**
     * Obtém o nome de qualquer {@link ContainerType}, mesmo que não seja desta implementação.
     *
     * @param type tipo
     * @return nome normalizado
     */
    public static String nameOf(ContainerType type) {
        if (type instanceof ContainerTypeImp) {
            return ((ContainerTypeImp) type).name;
        }
        return type.toString().trim().toLowerCase(Locale.ROOT);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ContainerTypeImp)) {
            return false;
        }
        return this.name.equals(((ContainerTypeImp) obj).name);
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public String toString() {
        return this.name;
    }
}
