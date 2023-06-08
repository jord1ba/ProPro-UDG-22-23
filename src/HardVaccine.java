/**
 * @file HardVaccine.java
 * @brief Classe HardVaccine
 */

/**
 * @class HardVaccine
 * @brief Vacunes que disminueixen la capacitat de transmissió del virus.
 * @author Aniol Juanola
 */
public class HardVaccine extends Vaccine {

    // ATTRIBUTES
    private final float _effectiveness_ratio;
    ///< Modificador dels paràmetres d'un virus: indica el nou percentatge de gent que no agafarà el virus gràcies a la vacuna.


    // CONSTRUCTORS
    /**
     * @brief Constructor.
     * @pre
     *  - name != null
     *  - virus != null
     *  - delay > 0
     *  - effectDuration > 0
     *  - 0 <= effectivenessRatio <= 1
     * @param name Nom de la vacuna.
     * @param virus Virus al qual fa efecte la vacuna.
     * @param delay Duració en ticks fins que la vacuna comença a fer efecte.
     * @param effectDuration Duració en ticks de la vacuna quan és efectiva.
     * @param effectivenessRatio El nou percentatge de gent que no agafarà el virus gràcies a la vacuna.
     * @post S'ha creat la vacuna amb les dades corresponents.
     */
    HardVaccine(String name, Virus virus, int delay, int effectDuration, float effectivenessRatio) {
        _name = name;
        _virus = virus;
        _delay = delay;
        _effect_duration = effectDuration;
        _effectiveness_ratio = effectivenessRatio;
    }
	
    /** @brief Converteix l'objecte en un String.
     * @pre True
     * @return Retorna l'objecte en forma d'String amb el següent format: nom (inhibidora)
     */
    public String toString() { // TODO fer la part de mostrar ticks restants, etc. per regió a l'hora de mostrar
        return _name + " (inhibidora)";
    }

    /**
     * @brief Retorna el valor value modificat segons la vacuna i el paràmetre de la vacuna que
     * es vulgui saber mitjançant el paràmetre data.
     * @param data Tipus de dada que s'està enviant per value.
     * @param value Valor de la dada previ a l'efecte de la vacuna.
     * @pre \p data és un dels següents valors:
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
        return data.equals("virusEffectiveness") ? (1f - _effectiveness_ratio) * value : value;
    }
}
