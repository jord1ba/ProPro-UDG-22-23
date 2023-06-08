/**
 * @file Virus.java
 * @brief Classe Virus
 */

import java.util.List;

/**
 * @class Virus
 * @brief Conté tota la informació relacionada amb els virus i permet gestionar-ne el comportament i la propagació.
 * @author Aniol Juanola
 */
public class Virus implements Comparable<Virus> {

    //ATTRIBUTES
    protected String _name; ///< Nom del virus.
    protected float _fall_sick_probability; ///< Probabilitat d'agafar el virus.
    protected float _death_rate; ///< Taxa de mortalitat.
    protected float _spread_rate; ///< Taxa de contagi.
    protected int _symptom_duration; ///< Durada en ticks dels símptomes.
    protected int _incubation_time; ///< Durada en ticks que es tarda a incubar el virus.
    protected int _latency_time; ///< Durada en ticks del període de latència.
    protected int _immunity_duration; ///< Durada en ticks del període d'immunitat.
    protected int _infection_duration; ///< Durada en ticks de la infecció.
    protected VirusFamily _family; ///< Família a la qual pertany el virus.


    //CONSTRUCTOR

    ///Constructor base amb totes les dades.

    /** @brief Constructor.
     * @pre
     *  - name != null
     *  - family != null
     *  - 0 <= fallSickProbability <= 1
     *  - 0 <= deathRate <= 1
     *  - 0 <= spreadRate <= 1
     *  - symptomDuration > 0
     *  - latencyTime > 0
     *  - immunityDuration > 0
     *  - infectionDuration > 0
     * @param name Nom del virus
     * @param family Família a la qual pertany el virus.
     * @param fallSickProbability Probabilitat d'agafar el virus.
     * @param deathRate Taxa de mortalitat.
     * @param spreadRate Taxa de contagi.
     * @param symptomDuration Durada en ticks dels símptomes.
     * @param incubationTime Durada en ticks que es tarda a incubar el virus.
     * @param latencyTime Durada en ticks del període de latència.
     * @param immunityDuration Durada en ticks del període d'immunitat.
     * @param infectionDuration Durada en ticks de la infecció.
     * @post S'ha creat el virus amb les dades corresponents.
     */
    Virus(String name, VirusFamily family, float fallSickProbability, float deathRate,
          float spreadRate, int symptomDuration, int incubationTime,
          int latencyTime, int immunityDuration, int infectionDuration) {
        _name = name;
        _family = family;
        _fall_sick_probability = fallSickProbability;
        _death_rate = deathRate;
        _spread_rate = spreadRate;
        _symptom_duration = symptomDuration;
        _incubation_time = incubationTime;
        _latency_time = latencyTime;
        _immunity_duration = immunityDuration;
        _infection_duration = infectionDuration;
    }

    /** @brief Converteix l'objecte en un String.
     * @pre True
     * @return Retorna l'objecte en format "nom (família)".
     */
    public String toString() {
        return _name + " (" + _family + ")";
    }

    //GETTERS

    /** @brief Retorna el nom del virus.
     * @pre True
     * @return Retorna el nom del virus.
     */
    public String name() { return _name; }

    /** @brief Retorna la probabilitat d'agafar el virus.
     * @pre True
     * @return Retorna la probabilitat d'agafar el virus.
     */
    public float fallSickProbability() { return _fall_sick_probability; }

    /** @brief Retorna la taxa de mortalitat del virus.
     * @pre True
     * @return Retorna la taxa de mortalitat del virus.
     */
    public float deathRate() { return _death_rate; }

    /** @brief Retorna la taxa de contagi del virus.
     * @pre True
     * @return Retorna la taxa de contagi del virus.
     */
    public float spreadRate() { return _spread_rate; }

    /** @brief Retorna la durada en ticks dels símptomes.
     * @pre True
     * @return Retorna la durada en ticks dels símptomes.
     */
    public int symptomDuration() { return _symptom_duration; }

    /** @brief Retorna la durada en ticks que es tarda a incubar el virus.
     * @pre True
     * @return Retorna la durada en ticks que es tarda a incubar el virus.
     */
    public int incubationTime() { return _incubation_time; }

    /** @brief Retorna la durada en ticks del període de latència.
     * @pre True
     * @return Retorna la durada en ticks del període de latència.
     */
    public int latencyTime() { return _latency_time; }

    /** @brief Retorna la durada en ticks del període d'immunitat.
     * @pre True
     * @return Retorna la durada en ticks del període d'immunitat.
     */
    public int immunityDuration() { return _immunity_duration; }

    /** @brief Retorna la durada en ticks de la infecció.
     * @pre True
     * @return Retorna la durada en ticks de la infecció.
     */
    public int infectionDuration() { return _infection_duration; }

    /** @brief Retorna la família a la qual pertany el virus.
     * @pre True
     * @return Retorna la família a la qual pertany el virus.
     */
    public VirusFamily family() { return _family; }

    //FUNCTIONS

    /** @brief Genera un nou AffectedGroup a la regió especificada.
     * @pre affected > 0
     * @param r Regió on s'inserirà l'AffectedGroup.
     * @param affected Nombre de persones infectades del virus en el tick inicial.
     * @post S'ha afegit a la regió r un AffectedGroup que conté affected afectats del virus en el primer tick.
     */
    protected void generateAffectedGroup(Region r, int affected) {

        Affectation affectation = r.affectation(this);

        AffectedGroup newGroup = new AffectedGroup(affectation, this, affected);

        affectation.addGroup(newGroup);
    }

    /**
     * @brief Determina el funcionament en cas de coincidència de virus de la mateixa família en un grup de persones.
     * En la classe base virus, aquesta funció mira quin és el virus més fort, i aquest és l'únic que sobreviu.
     * @pre \p my_affectation és l'afectació del virus this i \p my_affectation és diferent de \p affectation.
     * @param my_affectation l'afectació del virus this.
     * @param affectation l'afectació del virus amb què s'han superposat.
     * @param overlap tant per u de coincidència.
     */
    protected void onVirusOverlap(Region region, Affectation my_affectation, Affectation affectation, float overlap) {

        int shared = (int)(overlap * region.inhabitants());

        // esborrem la part que se solapa de l'afectació més fluixa,
        // perquè l'afectació més forta ja conté la part de la població
        // llavors no cal una transferència cap a cap afectació.

        if (strongestVirus(affectation.virus())) {

            Affectation.transfer(affectation, null, shared);

        } else {

            Affectation.transfer(my_affectation, null, shared);

        }

    }

    /**
     * @brief Comprova si hi ha coincidències de virus de la mateixa família en un grup de persones.
     * @param affected el grup de persones del virus this.
     */
    protected void checkVirusOverlap(Region region, int affected) {

        Affectation my_affectation = region.affectation(this);
        List<Affectation> affectations = region.affectationsByFamily(_family);

        for (Affectation affectation : affectations) {

            if (my_affectation == affectation) continue;

            float prop1 = (float)affected / region.inhabitants(); prop1 = Math.min(prop1, 1f);
            float prop2 = (float)affectation.affected() / region.inhabitants(); prop2 = Math.min(prop2, 1f);
            float shared = prop1 * prop2;

            if (shared > 0f) {

                onVirusOverlap(region, my_affectation, affectation, shared);

            }

        }

    }

    /** @brief Propaga el virus en una regió específica.
     * @pre affected > 0
     * @param r Regió on s'inserirà l'AffectedGroup.
     * @param affected Nombre de persones infectades del virus en el tick inicial.
     * @post S'ha propagat el virus actual en la regió r amb el nombre affected de nous afectats.
     */
    public void propagateVirus(Region r, int affected) {
        
        generateAffectedGroup(r, affected);
    
        checkVirusOverlap(r, affected);
        
    }

    /** @brief Compara dos objectes virus pel nom.
     * @pre virus != null
     * @param virus Objecte a ser comparat.
     * @return Retorna que dos virus són iguals si el seu nom és igual.
     */
	@Override
	public int compareTo(Virus virus) {

		return _name.compareTo(virus._name);

	}

    /** @brief Retorna si v és pare del virus actual.
     * @pre v != null
     * @param v Possible pare del virus.
     * @return Retorna true si v és pare del virus actual (primari o secundari).
     */
    public boolean isParent(Virus v) {
        return v.equals(this);
    }

    /** @brief Retorna si el virus actual és més fort que un altre virus de la mateixa família.
     * @pre v és de la mateixa família que el virus actual
     * @return Retorna si el virus actual és més fort que el virus V segons les següents prioritats:
     *  - Major taxa de contagi.
     *  - Probabilitat de desenvolupar la malaltia.
     *  - Taxa de mortalitat.
     *  - Ordre alfabètic.
     */
    public boolean strongestVirus(Virus v) {
        if (_family != v._family) {
            throw new IllegalArgumentException("El virus V no és de la mateixa família que el virus actual");
        }

        return _spread_rate > v._spread_rate ||
                _spread_rate == v._spread_rate && _fall_sick_probability > v._fall_sick_probability ||
                _fall_sick_probability == v._fall_sick_probability && _death_rate > v._death_rate ||
                _death_rate == v._death_rate && _name.compareTo(v._name) > 0;
    }
}
