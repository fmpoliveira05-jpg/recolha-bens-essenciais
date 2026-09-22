package recolha.picking;

import com.estg.core.AidBox;
import com.estg.core.Container;

/**
 * Uma troca planeada numa paragem: o veículo leva o contentor cheio e deixa no seu lugar um
 * contentor vazio que trouxe da base.
 *
 * @author Francisco Miguel Pereira Oliveira
 */
public final class Pickup {

    private final AidBox aidBox;
    private final Container collected;
    private final Container replacement;

    /**
     * @param aidBox caixa onde é feita a troca
     * @param collected contentor recolhido
     * @param replacement contentor vazio que fica no lugar do recolhido
     */
    public Pickup(AidBox aidBox, Container collected, Container replacement) {
        this.aidBox = aidBox;
        this.collected = collected;
        this.replacement = replacement;
    }

    /** @return caixa onde é feita a troca */
    public AidBox getAidBox() {
        return this.aidBox;
    }

    /** @return contentor recolhido */
    public Container getCollected() {
        return this.collected;
    }

    /** @return contentor vazio deixado na caixa */
    public Container getReplacement() {
        return this.replacement;
    }

    @Override
    public String toString() {
        return String.format("%s: recolhe %s, deixa %s", this.aidBox.getCode(), this.collected.getCode(), this.replacement.getCode());
    }
}
