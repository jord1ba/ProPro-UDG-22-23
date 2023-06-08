/**
* @file AffectedGroup.java
* @author Guillem Vidal
*/

/**
 * @brief Classe que engloba el comportament d'un grup d'afectats en una sola afectació.
 */
public class AffectedGroup implements Cloneable {

    /**
     * @brief Enumerador de l'estat de contagi del grup.
     */
    public enum ContagiousState {

        LATENCY,
        CONTAGIOUS {
            @Override
            public ContagiousState next() {
                return null;
            }
        };

        public ContagiousState next() { // són dos enums bro
            return values()[ordinal() + 1];
        }

    }

    /**
     * @brief Enumerador de l'estat de malaltia del grup.
     */
    public enum DiseaseState {

        INCUBATING,
        SYMPTOMS {
            @Override
            public DiseaseState next() { // això és llegendari
                return null;
            }
        };

        public DiseaseState next() { // ni el vscode sap què fer amb aquesta funció
            return values()[ordinal() + 1]; // com odio el java
        }

    }

    private Affectation _affectation;
    private final Virus _virus;

    private DiseaseState _disease_state;
    private ContagiousState _contagious_state;
    private boolean _dead_zone;

    private int _affected;
    private int _deaths;
    private int _final_deaths;

    private int _disease_tick;
    private int _contagious_tick;
    private int _immunity_tick;

    public DiseaseState diseaseState() { return _disease_state; }
    public ContagiousState contagiousState() { return _contagious_state; }

    public int affected() { return _affected; }
    public int deaths() { return _deaths; }

    public boolean isDead() { return _dead_zone; }

    /**
     * @return el nombre de tics que ha de durar l'estat de malaltia, segons el virus i la vacuna.
     */
    private static int diseaseNextTime(
            Virus virus, Affectation.VaccineStepper vaccine,
            DiseaseState disease_state
    ) {

        return (int)vaccine.modifier("duration",
                switch (disease_state) {
                    case INCUBATING -> virus.incubationTime();
                    case SYMPTOMS -> virus.symptomDuration();
                }
        );

    }

    /**
     * @return el nombre de tics que ha de durar l'estat de contagi, segons el virus i la vacuna.
     */
    private static int contagiousNextTime(
            Virus virus, Affectation.VaccineStepper vaccine,
            ContagiousState contagiousness_state
    ) {

        return (int)vaccine.modifier("duration",
                switch (contagiousness_state) {
                    case LATENCY -> virus.latencyTime();
                    case CONTAGIOUS -> virus.infectionDuration();
                }
        );

    }

    /**
     * @brief Constructor genèric del grup d'afectats,
     * assumeix que comencen tots els períodes des de l'inici.
     */
    public AffectedGroup(Affectation affectation, Virus virus, int affected) {
        super();

        _affectation = affectation;
        _virus = virus;

        _disease_state = DiseaseState.INCUBATING;
        _contagious_state = ContagiousState.LATENCY;
        _dead_zone = false;

        _affected = affected;
        _deaths = 0;
        _final_deaths = 0;

        _disease_tick = 0;
        _contagious_tick = 0;
        _immunity_tick = 0;

        affectation.modifiedDisease(this);
        affectation.modifiedContagious(this);

    }

    /**
     * @brief Constructor de funcionament intern. Serveix per instanciar un grup
     * que no té gent emmalaltida, sinó només contagiada.
     */
    private AffectedGroup(
            Affectation affectation, Virus virus, int affected,
            ContagiousState contagious_state, int contagious_tick
    ) {
        super();

        _affectation = affectation;
        _virus = virus;

        _disease_state = null;
        _contagious_state = contagious_state;
        _dead_zone = false;

        _affected = affected;
        _deaths = _final_deaths = 0;

        _disease_tick = 0;
        _contagious_tick = contagious_tick;
        _immunity_tick = 0;

        affectation.modifiedDisease(this);

        if (contagious_tick == 0) {

            affectation.modifiedContagious(this);

        }

    }

    /**
     * @brief Determina si ambdós grups són iguals en termes d'estats i períodes.
     * @return si ho són d'iguals.
     */
    @Override
    public boolean equals(Object obj) {

        if (this == obj)
            return true;

        if (!(obj instanceof AffectedGroup group))
            return false;

        return _affectation == group._affectation && _virus == group._virus &&
                _disease_state == group._disease_state && _contagious_state == group._contagious_state &&
                _disease_tick == group._disease_tick && _contagious_tick == group._contagious_tick &&
                _dead_zone == group._dead_zone && _immunity_tick == group._immunity_tick;
    }

    /**
     * @brief Suma dos grups d'afectats en un de sol.
     * @pre ambdós tenen els mateixos estats, afectació i virus.
     */
    public void add(AffectedGroup group) {

        _affectation.hintModified();

        _affected += group._affected;
        _deaths += group._deaths;
        _final_deaths += group._final_deaths;

    }

    /**
     * @brief Mou el grup d'afectació.
     */
    public void move(Affectation affectation) {

        _affectation.hintModified();

        _affectation = affectation;

    }

    /**
     * @brief Extreu una determinada proporció de persones del grup d'afectats.
     * @post assegura que group.add(group.subgroup(x)) == group inicial.
     * @return el subgrup generat.
     */
    public AffectedGroup subgroup(float proportion) {

        _affectation.hintModified();

        AffectedGroup sub = clone();

        sub._affected = (int)(_affected * proportion);
        _affected -= sub._affected;

        sub._deaths = (int)(_deaths * proportion);
        _deaths -= sub._deaths;

        sub._final_deaths = (int)(_final_deaths * proportion);
        _final_deaths -= sub._final_deaths;

        return sub;
    }

    /**
     * @brief Callback que es crida quan el període de malaltia canvia d'estat.
     * Bifurca els afectats quan arriben a l'estat de malaltia en dos grups,
     * el grup que esdevé malalt i el grup que no.
     */
    private void onDiseaseNext() {

        if (_disease_state == DiseaseState.SYMPTOMS) {

            float sick_proportion = _affectation.vaccine().modifier("fallSick", _virus.fallSickProbability());
            int sick = (int)(_affected * sick_proportion), healthy = _affected - sick;

            float death_rate = _affectation.vaccine().modifier("mortality", _virus.deathRate());
            _final_deaths = (int)(sick * death_rate);

            _affected = sick;

            AffectedGroup healthy_group = new AffectedGroup(
                    _affectation, _virus, healthy,
                    _contagious_state, _contagious_tick
            );

            _affectation.addGroup(healthy_group); // afegim la part del grup d'afectats que no ha emmalaltit

        }

    }

    /**
     * @brief Actualitza les morts i els afectats cada tic.
     */
    private void diseaseUpdate() {

        if (_disease_state == DiseaseState.SYMPTOMS) {

            int cur_deaths = _final_deaths * _disease_tick / _virus.symptomDuration();

            _affected -= cur_deaths - _deaths; // subgrups disminueix final_deaths
            _deaths = cur_deaths;

        }

    }

    /**
     * @brief Si escau, avança un tic en el període de malaltia.
     * @return si el període ha finalitzat.
     */
    private boolean diseaseNextStep() {

        if (_disease_state == null) return true;

        int next_time = diseaseNextTime(_virus, _affectation.vaccine(), _disease_state);

        _disease_tick++;
        if (_disease_tick >= next_time) {

            _disease_tick = 0;
            _disease_state = _disease_state.next();

            onDiseaseNext();

            _affectation.modifiedDisease(this);

        }

        diseaseUpdate();

        return _disease_state == null;
    }

    /**
     * @brief Si escau, avança un tic en el període de contagi.
     * @return si el període ha finalitzat.
     */
    private boolean contagiousNextStep() {

        if (_contagious_state == null) return true;

        int next_time = contagiousNextTime(_virus, _affectation.vaccine(), _contagious_state);

        _contagious_tick++;
        if (_contagious_tick >= next_time) {

            _contagious_tick = 0;
            _contagious_state = _contagious_state.next();

            _affectation.modifiedContagious(this);

        }

        return _contagious_state == null;
    }

    /**
     * @brief Si escalu, avança un tic en el període d'immunitat.
     * @return si el període ha finalitzat.
     */
    private boolean immunityNextStep() {

        if (_immunity_tick == 0) {

            _affectation.modifiedImmunity(this);

        }

        _dead_zone = ++_immunity_tick >= _virus.immunityDuration();

        if (_immunity_tick == _virus.immunityDuration()) {

            _affectation.modifiedImmunity(this);

        }

        return _dead_zone;

    }

    /**
     * @brief Contagious actualitza la quantitat de persones emmalaltides,
     * per tant, sempre haurà d'anar abans que Disease.
     * @post Avança als següents períodes si escau.
     * @return si han finalitzat els cicles (s'ha d'esborrar).
     */
    public boolean nextStep() {

        _affectation.hintModified();

        boolean diseaseDone    = diseaseNextStep();
        boolean contagiousDone = contagiousNextStep();

        return contagiousDone && diseaseDone && immunityNextStep();

    }

    /**
     * @return un clon del grup d'afectats.
     */
    @Override
    public AffectedGroup clone() {
        try {
            return (AffectedGroup)super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

}
