/**
 * @file VirusFamily.java
 * @brief Classe VirusFamily
 */

/**
 * @class VirusFamily
 * @brief Família que agrupa diferents virus de la simulació.
 * @author Aniol Juanola
 */
public class VirusFamily {

    // ATTRIBUTES
    private final String _name; ///< Nom de la família
    private final float _mutate_max_var; ///< Valor màxim de la variació dels atributs durant la mutació.


    // CONSTRUCTORS

    /** @brief Constructor.
     * @pre name != null && 0 <= mutateMaxVar <= 1
     * @param name Nom de la família.
     * @param mutateMaxVar Valor màxim de la variació dels atributs durant la mutació.
     */
    VirusFamily(String name, float mutateMaxVar) {
        _name = name;
        _mutate_max_var = mutateMaxVar;
    }


    // GETTERS
    /** @brief Retorna el nom de la família.
     * @pre True
     * @return Retorna el nom de la família
     */
    public String name() { return _name; }

    /**
     * @brief Retorna el valor màxim de variació dels atributs dels virus.
     * @pre True
     * @return Retorna el valor màxim de variació dels atributs dels virus.
     */
    public float maximumVariation() { return _mutate_max_var; }

    /** @brief Converteix l'objecte en un String.
     * @pre True
     * @return Retorna el nom de la família.
     */
    public String toString() { return _name; }


}
