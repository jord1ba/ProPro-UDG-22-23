/**
 * @file SoftVaccine.java
 * @brief Classe SoftVaccine
 */

/**
 * @class SoftVaccine
 * @brief Vacunes que redueixen les propietats del virus.
 * @author Aniol Juanola
 */
public class SoftVaccine extends Vaccine {

    // ATTRIBUTES
    private final float _mortality_reduction; ///< Tant per cent de reducció de la taxa de mortalitat en els malalts.
    private final float _duration_reduction; ///< Tant per cent de reducció de la durada de la malaltia.
    private final float _probability_reduction; ///< Tant per cent de reducció de la probabilitat de desenvolupar la
    ///< malaltia.
    private final float _contagiousness_reduction; ///< Tant per cent de reducció de la taxa de contagi.

    // CONSTRUCTORS
    /**
     * @brief Constructor.
     * @pre
     *  - name != null
     *  - virus != null
     *  - delay > 0
     *  - effectDuration > 0
     *  - 0 <= mortalityReduction <= 1
     *  - 0 <= durationReduction <= 1
     *  - 0 <= probabilityReduction <= 1
     *  - 0 <= contagiousnessReduction <= 1
     * @param name Nom de la vacuna.
     * @param virus Virus al qual fa efecte la vacuna.
     * @param delay Duració en ticks fins que la vacuna comença a fer efecte.
     * @param effectDuration Duració en ticks de la vacuna quan és efectiva.
     * @param mortalityReduction Tant per u de reducció de la taxa de mortalitat en els malalts.
     * @param durationReduction Tant per u de reducció de la durada de la malaltia.
     * @param probabilityReduction Tant per u de reducció de la probabilitat de desenvolupar la malaltia.
     * @param contagiousnessReduction Tant per u de reducció de la taxa de contagi.
     * @post S'ha creat la vacuna amb les dades corresponents.
     */
    SoftVaccine(String name, Virus virus, int delay, int effectDuration, float mortalityReduction,
                float durationReduction, float probabilityReduction, float contagiousnessReduction) {
        _name = name;
        _virus = virus;
        _delay = delay;
        _effect_duration = effectDuration;
        _mortality_reduction = mortalityReduction;
        _duration_reduction = durationReduction;
        _probability_reduction = probabilityReduction;
        _contagiousness_reduction = contagiousnessReduction;
    }

    /** @brief Converteix l'objecte en un String.
     * @pre True
     * @return Retorna l'objecte en forma de String amb el següent format: nom (atenuadora)
     */
    public String toString() { // TODO fer la part de mostrar ticks restants, etc. per regió a l'hora de mostrar
        return _name + " (atenuadora)";
    }


    /**
     * @brief Retorna el valor value modificat segons la vacuna i el paràmetre de la vacuna que
     * es vulgui saber mitjançant el paràmetre data.
     * @param data Tipus de dada que s'està enviant per value.
     * @param value Valor de la dada previ a l'efecte de la vacuna.
     * @pre data és un dels següents valors:
     *  - virusEffectiveness
     *  - mortality
     *  - duration
     *  - fallSick
     *  - contagiousness
     * @return Retorna el paràmetre value, que és del tipus especificat a data i, segons el tipus de vacuna, es retorna
     * modificat o es retorna directament.
     */
    @Override
    public float modifier(String data, float value) {
        return switch (data) {
            case "mortality" -> _mortality_reduction * value;
            case "duration" -> _duration_reduction * value;
            case "fallSick" -> _probability_reduction * value;
            case "contagiousness" -> _contagiousness_reduction * value;
            default -> value;
        };
    }
}
