/**
 * @file Vaccine.java
 * @brief Classe abstracta Vaccine
 */

/**
 * @class Vaccine
 * @brief Vacunes que disminueixen l'efectivitat dels virus.
 * @author Aniol Juanola
 */
public abstract class Vaccine {

    // ATTRIBUTES

    protected String _name; ///< Nom de la vacuna.
    protected int _delay; ///< Duració en ticks fins que la vacuna comença a fer efecte.
    protected int _effect_duration; ///< Duració en ticks de la vacuna quan és efectiva.

    protected Virus _virus; ///< Virus al qual fa efecte la vacuna.
    
    /** @brief Retorna el virus al qual la vacuna està adreçada.
     * @pre True
     * @return Retorna el virus al qual la vacuna està adreçada.
     */
    public Virus addressedVirus() { return _virus; }

    /** @brief Retorna el nom de la vacuna.
     * @pre True
     * @return Retorna el nom de la vacuna.
     */
    public String name() { return _name; }

    /** @brief Retorna el nombre de ticks que tarda la vacuna a fer efecte.
     * @pre True
     * @return Retorna el nombre de ticks que tarda la vacuna a fer efecte.
     */
    public int delay() { return _delay; }

    /** @brief Retorna el nombre de ticks que tindrà efecte la vacuna.
     * @pre True
     * @return Retorna el nombre de ticks que tindrà efecte la vacuna.
     */
    public int effectDuration() { return _effect_duration; }

    /**@pre True
     * @param v Virus que pot ser adreçat o no per la vacuna.
     * @return Retorna si la vacuna actual tindrà efecte sobre el virus v.
     */
    public boolean isEffective(Virus v)  {
        return v.isParent(_virus);
    }

    /** @brief Converteix l'objecte en un String.
     * @pre True
     * @return Retorna l'objecte en forma de String amb el següent format: nom (suavitza els efectes / evita contreure la malaltia).
     */
    public abstract String toString();

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
    public abstract float modifier(String data, float value);
    
}
