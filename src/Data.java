/**
 * @file Data.java
 * @brief Classe Data
 */

import java.util.Iterator;
import java.util.TreeMap;


/** @class Data
 * @brief Classe encarregada de guardar les dades d'una afectació causada per un virus en una regió, o bé
 * la suma d'un conjunt de Data (en aquest cas \p _virus i/o \p _region serien nulls).
 * @author Jordi Badia
 */
public class Data {

    // Private general attributes
    private final Virus _virus; ///< Virus sobre el que tracten les dades de l'objecte (pot ser null).
    private final Region _region; ///< Regió sobre la qual tracten les dades de l'objecte (pot ser null).
    private Vaccine _vaccine;
    ///< Si \p _virus i \p _region no són nulls i hi ha vacuna a l'afectació corresponent, la guarda. Null altrament.
    private int _vaccine_remaining_tick; ///< Si \p _vaccine no és null, guarda el ticks restants d'aquesta

    // Private specific attributes
    private long _total_inhabitants; ///< Total d'habitants representats per l'objecte.
    /*
    Si _region != null, tindran el mateix valor que inhabitants, si no tindrà la suma de tots els inhabitants que
    l'han formada.
    */

    // Dades acumulades
    private int _total_symptoms; ///< Total de simptomàtics apareguts al llarg de la simulació.
    private int _total_contagious; ///< Total de contagiosos apareguts al llarg de la simulació.
    private int _total_deaths; ///< Total de morts al llarg de la simulació.

    //Dades relatives

    // Sobre incubats
    private int _incubating; ///< Gent incubant en aquest moment.
    private int _in_incubating; ///< Gent que comença a incubar en aquest moment.
    private int _out_incubating; ///< Gent que ha deixat d'incubar.

    // Sobre malalts
    private int _symptom; ///< Malalts en aquest moment.
    private int _in_symptom; ///< Nous malalts.
    private int _out_symptom; ///< Gent que ha deixat d'estar malalta.

    //Sobre latències
    private int _latency; ///< Gent en estat de latència en aquest moment.
    private int _in_latency; ///< Gent que comença l'estat de latència en aquest moment.
    private int _out_latency; ///< Gent que deixa d'estar en latència.

    // Sobre contagiosos
    private int _contagious; ///< Contagiosos en aquest moment.
    private int _in_contagious; ///< Nous contagiosos.
    private int _out_contagious; ///< Gent que deixa de ser contagiosa.

    //Sobre immunitat
    private int _immunity; ///< Immunes en aquest moment
    private int _in_immunity; ///< Gent que comença a estar immunes en aquest moment
    private int _out_immunity; ///< Gent que deixa de ser immune

    //Sobre nulls malalts
    private int _null_disease; ///< Gent que ha agafat el virus, però no ha emmalaltit i encara no és immune.
    private int _in_null_disease; ///< Nova gent que no és ni immune ni té símptomes.
    private int _out_null_disease; ///< Gent que ha passat a estat d'immunitat

    //Sobre nulls contagiosos
    private int _null_contagious; ///< Gent que ja no és contagiosa, però encara té símptomes.
    private int _in_null_contagious; ///< Nova gent que encara no és contagiosa, però encara té símptomes.
    private int _out_null_contagious; ///< Gent que era contagiosa, però encara tenia símptomes i ja no.

    //Sobre morts
    private int _in_deaths; ///< Nous morts.



    // Constructors

    /** @brief Constructor que rep un virus i una regió afectada per aquest i carrega i guarda les dades de l'afectació.
     * @pre \p region és afectada per \p virus. Cap dels dos és null.
     * @post Es carreguen totes les dades de l'afectació, inclosos els habitants i la possible vacuna.
     * @param region Regió afectada pel virus.
     * @param virus Virus que afecta la regió.
     */
    public Data(Region region, Virus virus) {

        _region = region;
        _virus = virus;

        _total_inhabitants = region.inhabitants();

        Affectation affectation = region.affectation(virus);

        if (affectation != null) {

            _vaccine = affectation.vaccine().vaccine();
            _vaccine_remaining_tick = _vaccine == null ? 0 : affectation.vaccineRemainingTicks();

            _total_symptoms = affectation.symptom();
            _total_contagious = affectation.contagious();
            _total_deaths = affectation.deaths();

            _incubating = affectation.incubating();
            _symptom = affectation.symptom();
            _latency = affectation.latency();
            _contagious = affectation.contagious();
            _immunity = affectation.immunity();
            _null_contagious = affectation.nullContagious();
            _null_disease = affectation.nullDisease();

            //La resta de paràmetres haurien de ser 0 en l'estat inicial

        }

    }

    /** @brief Constructor que rep un iterator d'una collection de Data i realitza i guarda la suma dels valors de cada
     * un dels objectes de la collection.
     * @pre True.
     * @post Aquest objecte guarda la suma de tots els objectes Data de la collection de \p iterator i si \p region i
     * \p virus no són null, guarda la possible vacuna.
     * @param region La regió que representa el conjunt de Data de \p iterator (pot ser null).
     * @param virus El virus que representa el conjunt de Data de \p iterator (pot ser null).
     * @param iterator Iterator de l'estructura de la collection amb els objectes Data a sumar.
     */
    public Data(Region region, Virus virus, Iterator<Data> iterator) {

        _region = region;
        _virus = virus;

        if (_region != null && _virus != null) {

            Affectation affectation = _region.affectation(_virus);

            _vaccine = affectation.vaccine().vaccine();
            _vaccine_remaining_tick = _vaccine == null ? 0 : affectation.vaccineRemainingTicks();

        }

        while (iterator.hasNext())

            this.addData(iterator.next());

    }

    /** @brief Getter de la regió a la que corresponen les dades de l'objecte actual.
     * @pre True.
     * @return La regió a la que corresponen les dades de l'objecte actual.
     */
    public Region region() {
        return _region;
    }

    /** @brief Getter del virus al que corresponen les dades de l'objecte actual.
     * @pre True.
     * @return El virus al qual corresponen les dades de l'objecte actual.
     */
    public Virus virus() {
        return _virus;
    }


    /** @brief Retorna la ràtio de noves infeccions per tots els infectats.
     * @pre True.
     * @return 0 si no hi ha infectats, altrament retorna la ràtio de transmissió.
     */
    private float transmissionRatio() {

        return (_contagious + _latency ) == 0 ? 0 : (float)_in_latency / (_contagious + _latency);

    }

    /** @brief Retorna el total d'infectats en durant tota la simulació.
     * @pre True.
     * @return El total de contagiosos durant tota la simulació + gent en estat de latència
     */
    private int totalContagiated() {

        return _total_contagious + _latency;

    }

    /** @brief Retorna el total d'infectats actual (sense els immunes).
     * @pre True.
     * @return Gent en latència + contagiosos + null_contagious.
     */
    public int contagiated() {
        return _latency + _contagious + _null_contagious;
    }

    /** @brief Retorna el total d'infectats actual (inclosos els immunes).
     * @pre True.
     * @return Gent en latència + contagiosos + null_contagious + immunes.
     */
    public int virusPresence() {
        return contagiated() + _immunity;
    }

    /** @brief Actualitza la informació actual per la generada en el següent tick.
     * @pre S'ha generat un tick des de l'última crida a aquesta funció i \p _region i \p _virus no son null.
     * @post S'actualitzen les dades de l'objecte actual per l'afectació que determinen la regió i el virus,
     * actualitzant els comptadors històrics i la vacuna.
     */
    public void updateData() {

        Affectation affectation = _region.affectation(_virus);

        if (affectation != null) {

            _vaccine = affectation.vaccine().vaccine();
            _vaccine_remaining_tick = _vaccine == null ? 0 : affectation.vaccineRemainingTicks();

            _incubating = affectation.incubating();
            _in_incubating = affectation.inIncubating();
            _out_incubating = affectation.outIncubating();

            _symptom = affectation.symptom();
            _in_symptom = affectation.inSymptom();
            _out_symptom = affectation.outSymptom();

            _latency = affectation.latency();
            _in_latency = affectation.inLatency();
            _out_latency = affectation.outLatency();

            _contagious = affectation.contagious();
            _in_contagious = affectation.inContagious();
            _out_contagious = affectation.outContagious();

            _immunity = affectation.immunity();
            _in_immunity = affectation.inImmunity();
            _out_immunity = affectation.outImmunity();

            _in_deaths = affectation.deaths() - _total_deaths;

            _null_disease = affectation.nullDisease();
            _in_null_disease = affectation.inNullDisease();
            _out_null_disease = affectation.outNullDisease();

            _null_contagious = affectation.nullContagious();
            _in_null_contagious = affectation.inNullContagious();
            _out_null_contagious = affectation.outNullContagious();

            _total_symptoms += _in_symptom;
            _total_contagious += _in_contagious;
            _total_deaths = affectation.deaths();

        }

    }

    /** @brief Suma les dades de \p data a les de l'objecte actual
     * @pre True.
     * @post Les dades de l'objecte actual han estat incrementades amb les de \p dada.
     * @param data Data del qual es trauran els valors per incrementar els de l'objecte actual.
     */
    private void addData(Data data) {
        
        _total_symptoms += data._total_symptoms;
        _total_contagious += data._total_contagious;
        _total_deaths += data._total_deaths;
        
        _incubating += data._incubating;
        _in_incubating += data._in_incubating;
        _out_incubating += data._out_incubating;

        _symptom += data._symptom;
        _in_symptom += data._in_symptom;
        _out_symptom += data._out_symptom;

        _latency += data._latency;
        _in_latency += data._in_latency;
        _out_latency += data._out_latency;

        _contagious += data._contagious;
        _in_contagious += data._in_contagious;
        _out_contagious += data._out_contagious;

        _immunity += data._immunity;
        _in_immunity += data._in_immunity;
        _out_immunity += data._out_immunity;

        _in_deaths += data._in_deaths;

        _null_disease += data._null_disease;
        _in_null_disease += data._in_null_disease;
        _out_null_disease += data._out_null_disease;

        _null_contagious += data._null_contagious;
        _in_null_contagious += data._in_null_contagious;
        _out_null_contagious += data._out_null_contagious;

        _total_inhabitants += data._total_inhabitants;

    }

    /** @brief Retorna en format String totes les dades de l'objecte actual ordenades.
     * @pre True.
     * @return Un String amb totes les dades ordenades.
     */
    public String toString() {

        Affectation affectation = _region.affectation(_virus);

        _vaccine = affectation.vaccine().vaccine();
        _vaccine_remaining_tick = _vaccine == null ? 0 : affectation.vaccineRemainingTicks();

        return _virus.name() + " a " + _region.name() + ":\n" +
                (_vaccine == null ? "" : "\tVacuna aplicada: " + _vaccine + " (" + _vaccine_remaining_tick +
                " ticks restants)\n") +
                "\tHabitants: " + _region.inhabitants() + "\n" +
                "\tTuristes: " + _region.foreign() + "\n" +
                "\tDades acumulades:\n" +
                "\t\tTotal de malalts al llarg de l'execució: " + _total_symptoms + " (+" + _in_symptom + ")\n" +
                "\t\tTotal de contagiosos al llarg de l'execució: " + _total_contagious + " (+" + _in_contagious + ")\n" +
                "\t\tTotal de contagiats al llarg de l'execució: " + totalContagiated() + " (+" + _in_latency + ")\n" +
                "\t\tTotal de morts al llarg de l'execució: " + _total_deaths + " (+" + _in_deaths + ")\n" +
                "\tDades del tick:\n" +
                "\t\tTaxa de transmissió: " + transmissionRatio() + "\n" +
                "\t\tGrup de simptomàtics:\n" +
                "\t\t\tIncubant: " + _incubating + " (+" + _in_incubating + ") (-" + _out_incubating + ")\n" +
                "\t\t\tSimptomàtics: " + _symptom + " (+" + _in_symptom + ") (-" + _out_symptom + ")\n" +
                "\t\t\tSense símptomes: " + _null_disease + " (+" + _in_null_disease + ") (-" + _out_null_disease + ")\n" +
                "\t\tGrup de contagiosos:" + "\n" +
                "\t\t\tLatència: " + _latency + " (+" + _in_latency + ") (-" + _out_latency + ")\n" +
                "\t\t\tContagiosos: " + _contagious + " (+" + _in_contagious + ") (-" + _out_contagious + ")\n" +
                "\t\t\tNo contagiosos: " + _null_contagious + " (+" + _in_null_contagious + ") (-" + _out_null_contagious + ")\n" +
                "\t\tGrup d'immunes:" + "\n" +
                "\t\t\tImmunes: " + _immunity + " (+" + _in_immunity + ") (-" + _out_immunity+ ")\n";

    }

    /** @brief Retorna en format String les dades més rellevants de l'objecte actual.
     * @pre True.
     * @return Un String amb les dades més rellevants adaptades a si hi ha o no determinades regió i/o virus.
     */
    public String summary() {

        String res = "";

        if (_virus != null && _region != null) {

            Affectation affectation = _region.affectation(_virus);

            _vaccine = affectation.vaccine().vaccine();
            _vaccine_remaining_tick = _vaccine == null ? 0 : affectation.vaccineRemainingTicks();

            res += "Presència del virus " + _virus.name() + " a la regió " + _region.name() + ":\n" +
            (_vaccine == null ? "" : "\tVacuna aplicada: " + _vaccine + " (" + _vaccine_remaining_tick +
                    " ticks restants)\n");
        }
        else if (_virus != null)
            res += "Presència del virus " + _virus.name() + ":\n";
        else if (_region != null)
            res += "Dades de la regió " + _region.name() + ":\n";
        else
            res += "Dades sense filtres:\n";

        if (_region != null) {
            res += "\tHabitants: " + _region.inhabitants() + "\n";
            res += "\tTuristes: " + _region.foreign() + "\n";
        }

        res += "\tSituació en aquest moment:\n" +
                "\t\tNombre de malalts: " + _symptom + " (+" + _in_symptom + ")\n" +
                "\t\tMorts des del pas anterior: " + _in_deaths + "\n" +
                "\t\tNous contagis: " + _in_latency + "\n" +
                "\t\tTaxa de transmissió: " + transmissionRatio() + "\n" +
                "\tEvolució des de l'inici:\n" +
                "\t\tTotal de contagis: " + totalContagiated() + "\n" +
                "\t\tTotal de morts: " + _total_deaths + "\n" +
                "\t\tTotal de malalts: " + _total_symptoms + "\n";

        return res;

    }

    /** @brief Retorna un TreeMap de la informació general de l'objecte actual. Indexa enters per concepte (String).
     * @pre True.
     * @return Un TreeMap amb els immunes, contagiats, morts i no contagiats, indexant enters amb noms.
     */
    public TreeMap<String,Integer> generalData() {

        TreeMap<String,Integer> res = new TreeMap<>();

        res.put("Immunes", _immunity);
        res.put("Contagiats", contagiated());
        res.put("Morts", _total_deaths);

        int non_contagious = (int)(_total_inhabitants - contagiated() - _total_deaths - _immunity);
        res.put("No contagiats", Math.max(non_contagious, 0));

        return res;

    }

    /** @brief Retorna un TreeMap de la informació dels malalts de l'objecte actual. Indexa enters per concepte (String).
     * @pre True.
     * @return Un TreeMap amb els incubadors, simptomàtics i sense símptomes, indexant enters amb noms.
     */
    public TreeMap<String,Integer> diseaseData() {

        TreeMap<String,Integer> res = new TreeMap<>();

        if (contagiated() == 0) return res;

        res.put("Incubació", _incubating);
        res.put("Simptomàtics", _symptom);
        res.put("Sense símptomes", _null_disease);

        return res;

    }

    /** @brief Retorna un TreeMap de la informació dels contagiosos de l'objecte actual. Indexa enters per concepte (String).
     * @pre True.
     * @return Un TreeMap amb els que estan en latència, contagiosos i no contagiosos.
     */
    public TreeMap<String,Integer> contagiousData() {

        TreeMap<String,Integer> res = new TreeMap<>();

        if (contagiated() == 0) return res;

        res.put("Latència", _latency);
        res.put("Contagiosos", _contagious);
        res.put("No contagiosos", _null_contagious);

        return res;

    }

}
