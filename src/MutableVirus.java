/**
 * @file MutableVirus.java
 * @brief Classe MutableVirus
 */

import java.util.HashMap;
import java.util.Map;

/**
 * @class MutableVirus
 * @brief Extensió de la classe Virus que permet al Virus mutar.
 * @author Aniol Juanola
 */
public class MutableVirus extends Virus {

    //ATTRIBUTES

    /**
     * Mapa auxiliar per a saber quants virus existeixen derivats de l'inicial.
     */
    private static final Map<MutableVirus, Integer> _mutation_count = new HashMap<>();

    private final MutableVirus _v1; ///< Primer parentesc del virus (pot ser null).
    private final MutableVirus _v2; ///< Segon parentesc del virus (si vé d'una mutació familiar, pot ser null).
    private final float _mutate_error_probability; ///< Probabilitat que es produeixi una mutació per error de còpia.
    private final float _mutate_family_probability; ///< Probabilitat que es produeixi una mutació per coincidència
    ///< de dos virus de la mateixa família.

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
     *  - 0 <= mutateErrorProbability <= 1
     *  - 0 <= mutateFamilyProbability <= 1
     *  - parent1 != parent2
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
     * @param mutateErrorProbability Probabilitat que el virus muti per error de còpia.
     * @param mutateFamilyProbability Probabilitat que el virus muti per coincidència de virus de la mateixa família.
     * @param parent1 Primer pare del virus. Pot provenir de mutació per error o de mutació per família. Null si cap.
     * @param parent2 Segon pare del virus. Pot provenir només de mutació per família. Null si cap.
     * @post S'ha creat el virus amb les dades corresponents.
     */
    MutableVirus(String name, VirusFamily family, float fallSickProbability, float deathRate,
                 float spreadRate, int symptomDuration, int incubationTime,
                 int latencyTime, int immunityDuration, int infectionDuration,
                 float mutateErrorProbability, float mutateFamilyProbability, MutableVirus parent1,
                 MutableVirus parent2) {
        super(name, family, fallSickProbability, deathRate, spreadRate, symptomDuration, incubationTime, latencyTime,
                immunityDuration, infectionDuration);
        _mutate_error_probability = mutateErrorProbability;
        _mutate_family_probability = mutateFamilyProbability;
        _v1 = parent1;
        _v2 = parent2;
    }

    /** @brief Genera un float entre min (exclusiu) i max (inclusiu)
     * @pre True
     * @param min Valor mínim que es generarà (inclòs)
     * @param max Valor màxim que es generarà (no inclòs)
     * @return Retorna un float generat aleatòriament que compleix min <= valor < max
     */
    private float generateRandomFloat(float min, float max) {
        return (float) ((Math.random() * (max - min)) + min);
    }

    /** @brief Retorna el pare d'error de còpia.
     * @pre True
     * @return Retorna el pare d'error de còpia.
     */
    private MutableVirus getErrorCopyParent() {
        if (_v1 == null)
            return this;

        else
            return _v1.getErrorCopyParent();
    }

    /** @brief Genera el nom del nou virus a causa d'error de còpia.
     * @pre True
     * @return Retorna el nom en format: NOM_ORIGINAL+(quantitat de còpies existents)
     */
    private String generateMutateCopyErrorName() {
        MutableVirus parent = getErrorCopyParent();

        Integer count = _mutation_count.get(parent);

        if (count == null) {
            count = 0;
        }

        _mutation_count.put(parent, ++count);

        return parent._name + count;
    }

    /**
     * @brief Determina el funcionament en cas de coincidència de virus de la mateixa família en un grup de persones.
     * En la classe derivada mutable, aquesta funció genera un virus nou per coincidència en una part del solapament,
     * en l'altre actua de la mateixa manera que la classe base.
     * @pre \p my_affectation és l'afectació del virus this i my_affectation és diferent de \p affectation.
     * @param my_affectation l'afectació del virus this.
     * @param affectation l'afectació del virus amb què s'han solapat.
     * @param overlap tant per u de coincidència.
     */
    @Override
    protected void onVirusOverlap(Region region, Affectation my_affectation, Affectation affectation, float overlap) {

        float proportion;
        if (affectation.virus() instanceof MutableVirus)
            proportion = overlap * _mutate_family_probability;
        else
            proportion = 0f;

        int total = (int)(overlap * region.inhabitants());

        overlap -= proportion;

        super.onVirusOverlap(region, my_affectation, affectation, overlap);

        int mutations = total - (int)(overlap * region.inhabitants());
        if (mutations > 0) {

            MutableVirus newMutableVirus = createMutationByFamily(this, (MutableVirus)affectation.virus());

            Affectation.transfer(my_affectation, region.affectation(newMutableVirus), mutations);
            Affectation.transfer(affectation, null, mutations);

        }

    }

    /** @brief Propaga el virus en una regió específica.
     * @pre affected > 0
     * @param r Regió on s'inserirà l'AffectedGroup.
     * @param affected Nombre de persones infectades del virus en el tick inicial.
     * @post S'ha propagat el virus actual en la regió r amb el nombre affected de nous afectats. Addicionalment,
     * es calculen i afegeixen les mutacions per error de còpia i error de família.
     */
    @Override
    public void propagateVirus(Region r, int affected) {

        int mutationErrors = (int) (affected * _mutate_error_probability);
        affected -= mutationErrors;

        super.propagateVirus(r, affected);

        if (mutationErrors > 0) {

            MutableVirus newMutableVirus = createMutationByError();

            // és l'equivalent de fer super.propagateVirus però pel nou virus
            newMutableVirus.generateAffectedGroup(r, mutationErrors);
            newMutableVirus.checkVirusOverlap(r, mutationErrors);

        }

    }
    
    /** @brief Crea una mutació per família donats dos MutableVirus.
     * @pre v1.family() == v2.family()
     * @param v1 Primer virus de la mutació.
     * @param v2 Segon virus de la mutació.
     * @return Retorna el nou MutableVirus generat per la recombinació aleatòria de v1 i v2.
     */
    private static MutableVirus createMutationByFamily(MutableVirus v1, MutableVirus v2) {

        float[] randomVals = new float[10];

        for (int i = 0; i < 10; i++) {
            randomVals[i] = (float) Math.random(); //generem 10 valors aleatoris pels 10 paràmetres
        }

        MutableVirus newMutableVirus = new MutableVirus(
                v1.name() + '_' + v2.name(),
                v1._family, //podria ser v2._family
                randomVals[0]*v1._fall_sick_probability + (1 - randomVals[0]) * v2._fall_sick_probability,
                randomVals[1]*v1._death_rate + (1 - randomVals[1]) * v2._death_rate,
                randomVals[2]*v1._spread_rate + (1 - randomVals[2]) * v2._spread_rate,
                Math.round( randomVals[3]*v1._symptom_duration + (1 - randomVals[3]) * v2._symptom_duration),
                Math.round( randomVals[4]*v1._incubation_time + (1 - randomVals[4]) * v2._incubation_time),
                Math.round( randomVals[5]*v1._latency_time + (1 - randomVals[5]) * v2._latency_time),
                Math.round( randomVals[6]*v1._immunity_duration + (1 - randomVals[6]) * v2._immunity_duration),
                Math.round( randomVals[7]*v1._infection_duration + (1 - randomVals[7]) * v2._infection_duration),
                randomVals[8]*v1._mutate_error_probability + (1 - randomVals[8]) * v2._mutate_error_probability,
                randomVals[9]*v1._mutate_family_probability + (1 - randomVals[9]) * v2._mutate_family_probability,
                null, null
                );

        Simulation.addVirus(newMutableVirus);

        return newMutableVirus;
    }

    /** @brief Crea una mutació per error de còpia.
     * @pre True
     * @return Retorna el nou MutableVirus generat aleatòriament per l'error de còpia.
     */
    private MutableVirus createMutationByError() {
        //SUPOSAREM QUE EL PARÀMETRE ALEATORI ÉS EL MATEIX PER TOTS ELS VALORS DEL VIRUS.
        float multiplier = 1f + generateRandomFloat(-_family.maximumVariation(), _family.maximumVariation());


        MutableVirus newMutation = new MutableVirus(generateMutateCopyErrorName(), _family,
                _fall_sick_probability * multiplier, _death_rate * multiplier,
                _spread_rate * multiplier, Math.round(_symptom_duration * multiplier),
                Math.round(_incubation_time * multiplier), Math.round(_latency_time * multiplier),
                Math.round(_immunity_duration * multiplier), Math.round(_infection_duration * multiplier),
                Math.round(_mutate_error_probability * multiplier), Math.round(_mutate_family_probability * multiplier),
                this, null);

        Simulation.addVirus(newMutation);

        return newMutation;
    }


    /** @brief Retorna si el virus V és una variant del virus actual.
     * @pre True
     * @param v Virus a comprovar si és una variant.
     * @return Retorna si el virus V és una variant del virus actual.
     */
    @Override
    public boolean isParent(Virus v) {
        return super.isParent(v) || (_v1 != null && _v1.isParent(v)) || (_v2 != null && _v2.isParent(v));
    }
}
