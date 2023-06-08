/**
* @file Affectation.java
* @author Guillem Vidal
*/
import java.util.LinkedList;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * @brief Classe que engloba tots els grups d'afectats d'un sol virus en una sola regió.
 */
public class Affectation {

    /**
     * @brief Classe que encapsula el funcionament a temps real d'una vacuna.
     * És a dir, la classe Vaccine designa les seves propietats (a qui afecta,
     * amb quin efecte, etc), però es necessita saber informació actual de
     * l'aplicació d'aquesta vacuna, com la proporció de persones afectades
     * i quan fa que s'ha aplicat. Aquesta classe fa de wrapper per això.
     */
    public static class VaccineStepper {
        
        private Vaccine _vaccine;
        private float _proportion;
        private int _tick;

        public VaccineStepper(Vaccine vaccine, float proportion) {

            _vaccine = vaccine;
            _proportion = proportion;
            _tick = 0;

        }

        public Vaccine vaccine() { return _vaccine; }

        /**
         * @return si hi ha vacuna i si està activa.
         */
        public boolean isActive() {

            return _vaccine != null && _vaccine.delay() < _tick && !isOver();

        }

        /**
         * @return si la vacuna existeix i ha acabat.
         */
        public boolean isOver() {

            return _vaccine != null && _tick >= (_vaccine.delay() + _vaccine.effectDuration());

        }

        /**
         * @brief Calcula el valor modificat segons la vacuna i
         * la proporció de persones afectades. No cal comprovar
         * si la vacuna ha finalitzat o no.
         */
        public float modifier(String name, float value) {

            if (!isActive()) {

                return value;

            }

            return _vaccine.modifier(name, value) * _proportion + value * (1f - _proportion);

        }

        /**
         * @brief Avança un tic de la vacuna.
         */
        public void nextStep() {

            _tick++;

        }

    }

    /**
     * La regió que afecta l'afectació.
     */
    private final Region _region;

    /**
     * Els groups d'afectats de l'afectació.
     */
    private final LinkedList<AffectedGroup> _groups;

    /**
     * Emmagatzema els grups recentment afegits,
     * esperant a ser enllaçats amb tots els grups.
     */
    private final LinkedList<AffectedGroup> _cache_groups;

    /**
     * @brief Iterador que engloba ambdós llistes de grups,
     * per tal de simplificar la iteració quan un els vol recórrer.
     */
    private class GroupIterator implements Iterator<AffectedGroup> {

        Iterator<AffectedGroup> iter_groups, iter_cache;

        public GroupIterator() {

            iter_groups = _groups.iterator();
            iter_cache = _cache_groups.iterator();

        }

        @Override
        public boolean hasNext() {

            return iter_groups.hasNext() || iter_cache.hasNext();

        }

        @Override
        public AffectedGroup next() {

            if (iter_groups.hasNext()) {

                return iter_groups.next();

            }

            if (iter_cache.hasNext()) {

                return iter_cache.next();

            }

            throw new NoSuchElementException();
        }

    }

    /**
     * El virus de l'afectació.
     */
    private final Virus _virus;

    /**
     * La vacuna que afecta l'afectació,
     * assumim que només n'hi pot haver una.
     */
    private VaccineStepper _vaccine;

    /**
     * El nombre total de persones afectades vives.
     */
    private int _affected;

    /**
     * El nombre total de morts.
     */
    private int _deaths;

    /**
     * El nombre total de persones incubant el virus.
     */
    private int _incubating;

    /**
     * El nombre total de persones amb símptomes.
     */
    private int _symptoms;

    /**
     * El nombre total de persones en latència.
     */
    private int _latency;

    /**
     * El nombre de total de persones contagioses.
     */
    private int _contagious;

    /**
     * El nombre total de persones que han deixat de tenir símptomes,
     * però encara no són immunes.
     */
    private int _null_disease;

    /**
     * El nombre total de persones que han deixat de ser contagioses,
     * però encara no són immunes.
     */
    private int _null_contagious;

    /**
     * El nombre total de persones immunes.
     */
    private int _immunity;

    private int _in_incubating, _out_incubating;
    private int _in_latency, _out_latency;
    private int _in_symptoms, _out_symptoms;
    private int _in_contagious, _out_contagious;
    private int _in_null_disease, _out_null_disease;
    private int _in_null_contagious, _out_null_contagious;
    private int _in_immunity, _out_immunity;

    /**
     * Determina si els comptadors estan desactualitzats.
     */
    private boolean _modified_count;

    /**
     * @brief El constructor genèric d'afectació.
     */
    public Affectation(Region region, Virus virus) {
        super();

        _region = region;

        _virus = virus;
        _vaccine = new VaccineStepper(null, 0f);

        _groups = new LinkedList<>();
        _cache_groups = new LinkedList<>();

        _affected = _deaths = 0;
        _incubating = _latency = 0;
        _null_disease = _null_contagious = 0;
        _symptoms = _contagious = 0;

    }

    /**
     * @return el virus de l'afectació.
     */
    public Virus virus() { return _virus; }

    /**
     * @return el wrapper de la vacuna que afecta l'afectació.
     */
    public VaccineStepper vaccine() { return _vaccine; }

    /**
     * @return el nombre total d'afectats.
     */
    public int affected() { count(); return _affected; }

    /**
     * @return el nombre total de morts.
     */
    public int deaths() { count(); return _deaths; }

    /**
     * @return el nombre total de persones no afectades ni mortes.
     */
    public int healthy() { count(); return _region.inhabitants() - _affected - _deaths; }

    /**
     * @return el nombre total de persones incubant.
     */
    public int incubating() { count(); return _incubating; }
    public int inIncubating() { return _in_incubating; }
    public int outIncubating() { return _out_incubating; }

    /**
     * @return el nombre total de persones en latència.
     */
    public int latency() { count(); return _latency; }
    public int inLatency() { return _in_latency; }
    public int outLatency() { return _out_latency; }

    /**
     * @return el nombre total de persones amb símptomes.
     */
    public int symptom() { count(); return _symptoms; }
    public int inSymptom() { return _in_symptoms; }
    public int outSymptom() { return _out_symptoms; }

    /**
     * @return el nombre total de persones contagioses.
     */
    public int contagious() { count(); return _contagious; }
    public int inContagious() { return _in_contagious; }
    public int outContagious() { return _out_contagious; }

    /**
     * @return el nombre total de persones que han deixat de tenir símptomes,
     * però no són immunes.
     */
    public int nullDisease() { count(); return _null_disease; }
    public int inNullDisease() { return _in_null_disease; }
    public int outNullDisease() { return _out_null_disease; }

    /**
     * @return el nombre total de persones que han deixat de ser contagioses,
     * però no són immunes.
     */
    public int nullContagious() { count(); return _null_contagious; }
    public int inNullContagious() { return _in_null_contagious; }
    public int outNullContagious() { return _out_null_contagious; }

    /**
     * @return el nombre total de persones immunes.
     */
    public int immunity() { count(); return _immunity; }
    public int inImmunity() { return _in_immunity; }
    public int outImmunity() { return _out_immunity; }

    /**
     * @return els tics que resten perquè la vacuna acabi.
     */
    public int vaccineRemainingTicks() { return _vaccine._vaccine.effectDuration() - _vaccine._tick; }

    /**
     * @brief Afegeix un grup d'afectats a l'afectació.
     * @pre el grup no ha acabat tots els períodes.
     */
    public void addGroup(AffectedGroup group) {

        hintModified();

        _cache_groups.add(group);

    }

    /**
     * @brief Cerca un grup d'afectats de les mateixes característiques
     * que el grup passat per paràmetre.
     * @return si el troba, el grup, si no, null.
     */
    private AffectedGroup findGroup(AffectedGroup group) {

        for (AffectedGroup match : _groups) {

            if (group.equals(match) && match != group) {

                return match;

            }

        }

        return null;

    }

    /**
     * @brief Acaba d'afegir tots els grups que estaven preparats per afegir.
     */
    public void pushGroups() {

        for (AffectedGroup group : _cache_groups) {

            AffectedGroup dest = findGroup(group);

            if (dest != null) {

                dest.add(group);

            } else {

                _groups.add(group);

            }

        }

        _cache_groups.clear();

    }

    /**
     * @brief Compta les persones d'un grup.
     * @pre s'han reiniciat prèviament els comptadors.
     */
    private void countGroup(AffectedGroup group) {

        _deaths += group.deaths();

        if (group.isDead()) return;

        _affected += group.affected();

        AffectedGroup.DiseaseState disease_state = group.diseaseState();

        if (disease_state != null) {

            switch (disease_state) {
                case INCUBATING -> _incubating += group.affected();
                case SYMPTOMS -> _symptoms += group.affected();
            }

        }

        AffectedGroup.ContagiousState contagious_state = group.contagiousState();

        if (contagious_state != null) {

            switch (contagious_state) {
                case LATENCY -> _latency += group.affected();
                case CONTAGIOUS -> _contagious += group.affected();
            }

        }

        if (disease_state == null && contagious_state == null) {

            _immunity += group.affected();

        } else if (disease_state == null) {

            _null_disease += group.affected();

        } else if (contagious_state == null) {

            _null_contagious += group.affected();

        }

    }

    /**
     * @brief Si escau, compta totes les persones de l'afectació i les classifica.
     */
    private void count() {

        // if (!_modified_count) return;
        _modified_count = false;

        _affected = _deaths = 0;
        _incubating = _symptoms = _null_disease = 0;
        _latency = _contagious = _null_contagious = 0;
        _immunity = 0;

        for (Iterator<AffectedGroup> iterator = new GroupIterator(); iterator.hasNext();) {
            AffectedGroup group = iterator.next();

            countGroup(group);

        }

    }

    /**
     * @brief Avisa a l'afectació que el nombre d'afectats ha canviat.
     */
    public void hintModified() {

        _modified_count = true;

    }

    /**
     * @brief Transfereix el nombre de persones afectades people de l'afectació from a l'afectació to.
     * Si to és null, s'esborren els grups de from i prou.
     * @pre l'afectació from ha de tenir almenys people persones que transferir.
     * @return les transferències exitoses.
     */
    public static int transfer(Affectation from, Affectation to, int people) {

        if (to != null) {

            people = Math.min(people, to.healthy());

        }

        if (people <= 0 || from.affected() == 0) return 0;

        float proportion = (float)people / from.affected();
        proportion = Math.min(1f, proportion);

        int successful = 0, maximum = 0;

        for (Iterator<AffectedGroup> iterator = from.new GroupIterator(); iterator.hasNext();) {
            AffectedGroup group = iterator.next();

            AffectedGroup sub = group.subgroup(proportion);

            successful += sub.affected();

            if (sub.affected() > maximum) {

                maximum = sub.affected();

            }

            if (to != null) {
                sub.move(to);
                to.addGroup(sub);
            }

        }

        if (successful == people || from.affected() == 0)
            return successful; // we're done!

        /**
         * Això és una aproximació d'una distribució homogènia.
         * El que busca és repartir de manera igualitària els moviments restants.
         */
        int affected = from.affected(), numerator = (people - successful) * maximum;
        int sample = numerator / affected + (numerator % affected > 0 ? 1 : 0);

        for (Iterator<AffectedGroup> iterator = from.new GroupIterator(); iterator.hasNext() && people > successful;) {
            AffectedGroup group = iterator.next();

            int amount = Math.min(sample, group.affected());

            if (successful + amount >= people) {

                proportion = (float)(people - successful) / group.affected();

            } else {

                proportion = (float)amount / group.affected();

            }

            AffectedGroup sub = group.subgroup(proportion);

            successful += sub.affected();

            if (to != null) {
                sub.move(to);
                to.addGroup(sub);
            }

        }

        return successful;
    }

    /**
     * @brief Aplica la vacuna a l'afectació,
     * substituïnt l'anterior.
     */
    public void applyVaccine(VaccineStepper vaccine) {

        _vaccine = vaccine;

    }

    /**
     * @brief Avança steps a tots els grups d'afectats.
     * Si un grup d'afectats acaba tots els períodes,
     * esdevé el grup d'afectat mort
     * o es suma al grup d'afectats mort actual.
     */
    public void nextStep() {

        _in_incubating = _out_incubating = _in_latency = _out_latency = 0;
        _in_symptoms = _out_symptoms = _in_contagious = _out_contagious = 0;
        _in_null_disease = _out_null_disease = _in_null_contagious = _out_null_contagious = 0;
        _in_immunity = _out_immunity = 0;

        for (Iterator<AffectedGroup> iterator = _groups.iterator(); iterator.hasNext();) {
            AffectedGroup group = iterator.next();

            if (group.nextStep()) {

                AffectedGroup dead_zone = findGroup(group);

                if (dead_zone != null) {

                    dead_zone.add(group);
                    iterator.remove();

                }

            }

        }

    }

    /**
     * @brief Propaga el virus dins l'afectació.
     */
    public void propagateVirus() {

        int healthy = _region.inhabitants() - affected() - deaths();
        float spread = _vaccine.modifier("contagiousness", _virus.spreadRate());
        spread = _vaccine.modifier("virusEffectiveness", spread); // TEMP
        float contacts = spread * _region.insideMobility() * contagious() / _region.inhabitants();

        int newly_affected = Math.min(healthy, (int)(contacts * healthy));

        if (newly_affected > 0) {

            _virus.propagateVirus(_region, newly_affected);

        }

    }

    /**
     * @brief Callback que es crida des d'affected group
     * quan canvia d'estat en el període de malaltia.
     * Calcula els ins i outs.
     */
    public void modifiedDisease(AffectedGroup group) {

        AffectedGroup.DiseaseState disease_state = group.diseaseState();
        int affected = group.affected();

        if (disease_state != null) {

            switch (disease_state) {
                case INCUBATING -> _in_incubating += affected;
                case SYMPTOMS -> {
                    _out_incubating += affected;
                    _in_symptoms += affected;
                }
            }

        } else {

            _out_symptoms += affected;
            _in_null_disease += affected;

        }

    }

    /**
     * @brief Callback que es crida des d'affected group
     * quan canvia d'estat en el període de contagi.
     * Calcula els ins i outs.
     */
    public void modifiedContagious(AffectedGroup group) {

        AffectedGroup.ContagiousState contagious_state = group.contagiousState();
        int affected = group.affected();

        if (contagious_state != null) {

            switch (contagious_state) {
                case LATENCY -> _in_latency += affected;
                case CONTAGIOUS -> {
                    _out_latency += affected;
                    _in_contagious += affected;
                }
            }

        } else {

            _out_contagious += affected;
            _in_null_contagious += affected;

        }

    }

    /**
     * @brief Callback que es crida des d'affected group
     * quan canvia d'estat en el període d'immunitat.
     * Calcula els ins i outs.
     */
    public void modifiedImmunity(AffectedGroup group) {

        int affected = group.affected();

        if (!group.isDead()) {

            _out_null_disease += affected;
            _out_null_contagious += affected;
            _in_immunity += affected;

        } else {

            _out_immunity += affected;

        }

    }

}
