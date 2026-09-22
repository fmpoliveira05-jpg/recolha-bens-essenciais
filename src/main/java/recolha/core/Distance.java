package recolha.core;

/**
 * Distância e tempo de viagem até um destino (outra caixa ou a base da instituição).
 *
 * @author Francisco Miguel Pereira Oliveira
 */
public final class Distance {

    private final String destinationCode;
    private final double meters;
    private final double minutes;

    /**
     * @param destinationCode código do destino
     * @param meters distância em metros
     * @param minutes duração da viagem em minutos
     * @throws IllegalArgumentException se o destino for vazio ou algum valor for negativo
     */
    public Distance(String destinationCode, double meters, double minutes) {
        if (destinationCode == null || destinationCode.isBlank()) {
            throw new IllegalArgumentException("A distância tem de indicar o destino.");
        }
        if (meters < 0 || minutes < 0) {
            throw new IllegalArgumentException("Distância e duração não podem ser negativas.");
        }
        this.destinationCode = destinationCode.trim();
        this.meters = meters;
        this.minutes = minutes;
    }

    /** @return código do destino */
    public String getDestinationCode() {
        return this.destinationCode;
    }

    /** @return distância em metros */
    public double getMeters() {
        return this.meters;
    }

    /** @return duração em minutos */
    public double getMinutes() {
        return this.minutes;
    }

    @Override
    public String toString() {
        return String.format("%s: %.0f m, %.0f min", this.destinationCode, this.meters, this.minutes);
    }
}
