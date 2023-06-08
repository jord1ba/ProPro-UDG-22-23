/**
* @file Region.java
* @author Guillem Vidal
*/
import java.util.*;

/**
 * @brief Classe que conté tots els mètodes i atributs per gestionar una regió.
 */
public class Region {

    /**
     * @brief Classe que encapsula el funcionament del veïnat.
     * Emmagatzema la proporció de moviment, el moviment realitzat
     * i si la frontera està oberta o tancada.
     */
    public static class Neighbour {

        /**
         * El flux de persones en aquesta frontera.
         */
        public float flow_rate;

        /**
         * El nombre de persones que s'han mogut en el tic actual.
         */
        public int movement;

        /**
         * Si la frontera està oberta o tancada.
         */
        public boolean is_open;

        public Neighbour(Neighbour neighbour) {
            flow_rate = neighbour.flow_rate;
            movement = neighbour.movement;
            is_open = neighbour.is_open;
        }

        public Neighbour(float flow_rate, boolean is_open) {
            this.flow_rate = flow_rate;
            this.movement = 0;
            this.is_open = is_open;
        }

    }

    /**
     * Mapeja les regions veïnes al seu objecte neighbour.
     */
	private final HashMap<Region, Neighbour> _neighbours;

    /**
     * Mapeja els virus a les seves afectacions.
     */
    private final HashMap<Virus, Affectation> _affectations;

    /**
     * Llista de vacunes que afecten la regió.
     */
    private final List<Affectation.VaccineStepper> _vaccines;

    /**
     * Llista d'afectacions afegides en aquest tic.
     */
    private List<Affectation> _recent_affectations;

    /**
     * Aprofito la classe que ha d'emmagatzemar el mateix,
     * per determinar el harsh lockdown.
     */
    private final Neighbour _mobility_state;

    /**
     * Nom de la regió.
     */
    private final String _name;

    /**
     * La mobilitat a dins de la regió.
     */
    private final int _inside_mobility;

    /**
     * Habitants actual de la regió.
     */
    private int _inhabitants;

    /**
     * Els habitants abans de retornar-los.
     */
    private int _pre_rollback_inhabitants;

    /**
     * Habitants natals de la regió.
     */
    private final int _natal;

    /**
     * Habitants natals de la regió que són fora.
     */
    private int _abroad;

    /**
     * @brief Constructor genèric de la regió.
     */
    public Region(String name, int inside_mobility, int inhabitants) {

        _name = name;
        _inside_mobility = inside_mobility;
        _natal = _pre_rollback_inhabitants = _inhabitants = inhabitants;

        _neighbours = new HashMap<>();
        _affectations = new HashMap<>();
        _vaccines = new LinkedList<>();
        _recent_affectations = new LinkedList<>();

        _mobility_state = new Neighbour(1f, true);

    }

    /**
     * @return habitants de la regió.
     */
    public int inhabitants() {

        return _inhabitants;

    }

    /**
     * @return els habitants que són estrangers.
     */
    public int foreign() {

        return _pre_rollback_inhabitants - _natal + _abroad;

    }

    /**
     * @return la mobilitat a dins de la regió.
     */
    public float insideMobility() { return _inside_mobility; }

    /**
     * @brief Si no existeix crea l'afectació
     * @return l'afectació assignada al virus
     */
    public Affectation affectation(Virus virus) {

        Affectation affectation = _affectations.get(virus);

        if (affectation == null) {

            affectation = new Affectation(this, virus);

            _affectations.put(virus, affectation);

            _recent_affectations.add(affectation);

            checkVaccines(affectation);
            
        }

        return affectation;

    }

    /**
     * @brief Funciona com el pop d'un stack,
     * cada crida a aquesta funció esborra les entrades
     * que són retornades.
     * @return les afectacions afegides aquest tic.
     */
    public List<Affectation> recentlyAddedAffectations() {

        List<Affectation> result = _recent_affectations;;

        _recent_affectations = new LinkedList<>();

        return result;

    }

    /**
     * @brief Filtra les afectacions per família.
     * @return les coincidències d'afectacions segons la família.
     */
    public List<Affectation> affectationsByFamily(VirusFamily family) {

        List<Affectation> matches = new ArrayList<>();
        
        for (Affectation affectation : _affectations.values()) {

            if (affectation.virus().family() == family) {

                matches.add(affectation);

            }

        }

        return matches;
    }

    public String toString() {

        return _name + " (" + _inhabitants + " inhab.)";

    }

    /**
     * @return els veïns de la regió.
     */
    public ArrayList<Region> neighbours() {

        return new ArrayList<>(_neighbours.keySet());

    }

    /**
     * @brief Afegeix un veí a la regió.
     */
    public void addNeighbour(Region region, Neighbour neighbour) {

        _neighbours.put(region, neighbour);

    }

    /**
     * @return el nom de la regió.
     */
	public String name() { return _name; }

    /**
     * @brief Aplica la vacuna a la regió.
     */
    public void addVaccine(Vaccine vaccine, float proportion) {

        Affectation.VaccineStepper vaccineStepper = new Affectation.VaccineStepper(vaccine, proportion);

        _vaccines.add(vaccineStepper);

        for (Map.Entry<Virus, Affectation> entry : _affectations.entrySet()) {

            if (vaccine.isEffective(entry.getKey())) {

                entry.getValue().applyVaccine(vaccineStepper);

            }

        }

    }

    /**
     * @brief Comprova si hi ha alguna vacuna que afectaria aquesta afectació,
     * i si es dona el cas, l'aplica. Alhora, si la vacuna ha deixat de tenir
     * efecte, la treu de la llista de vacunes.
     * @pre assumeix que no hi pot haver més d'una vacuna que el pugui afectar!
     */
    private void checkVaccines(Affectation affectation) {

        Iterator<Affectation.VaccineStepper> iterator;
        for (iterator = _vaccines.iterator(); iterator.hasNext();) {
            Affectation.VaccineStepper vaccine = iterator.next();

            if (vaccine.isOver()) {

                iterator.remove();
                continue;

            }

            if (vaccine.vaccine().isEffective(affectation.virus())) {

                affectation.applyVaccine(vaccine);
                break;

            }

        }

    }

    /**
     * @brief Estableix l'estat de la frontera amb la regió region:
     * si state és true, les fronteres estan obertes, si no, tancades.
     */
    public void setBorderState(Region region, boolean state) {

        _neighbours.get(region).is_open = state;

    }

    public boolean isOnLockdown() {

        return !_mobility_state.is_open;

    }

    /**
     * @brief Determina el confinament intern, si state és fals,
     * la mobilitat es redueix segons new_rate.
     */
    public void setHarshLockdown(boolean state, float new_rate) {

        _mobility_state.is_open = state;

        if (!state) {
            
            _mobility_state.flow_rate = new_rate;

        }

    }

    /**
     * @brief Avança un tic a les vacunes i, si escau, les esborra.
     */
    private void nextStepVaccines() {

        Iterator<Affectation.VaccineStepper> iterator;
        for (iterator = _vaccines.iterator(); iterator.hasNext();) {
            Affectation.VaccineStepper vaccine = iterator.next();

            vaccine.nextStep();

            if (vaccine.isOver()) {



                iterator.remove();

            }

        }

    }

    /**
     * @brief Transfereix una proporció de persones afectades de la regió from a la regió to.
     * @return les transferències exitoses.
     */
    private static int transfer(Region from, Region to, int total_movement) {

        int original_inhabitants = from._inhabitants;

        from._inhabitants -= total_movement;
        to._inhabitants += total_movement;

        for (Affectation affectation : from._affectations.values()) {

            int people = affectation.affected() * total_movement / original_inhabitants;

            if (people > 0) {

                Affectation towards = to.affectation(affectation.virus());
                Affectation.transfer(affectation, towards, people);

                towards.pushGroups();

            }

        }

        return total_movement;
    }

    /**
     * @brief Transfereix una proporció de persones afectades de la regió from a la regió to.
     * @return les transferències exitoses.
     */
    private static int transfer(Region from, Region to, float proportion) {

        int total_movement = (int)(from._inhabitants * proportion);

        return transfer(from, to, total_movement);
    }

    /**
     * @brief Mou les persones a les regions veïnes.
     */
    public void movements() {

        nextStepVaccines();

        _abroad = 0;

        if (isOnLockdown())
            return; // harsh lockdown, cap moviment

        for (Map.Entry<Region, Neighbour> entry : _neighbours.entrySet()) {

            if (entry.getValue().is_open && !entry.getKey().isOnLockdown()) {

                int movement = transfer(this, entry.getKey(), entry.getValue().flow_rate);

                entry.getValue().movement = movement;

                _abroad += movement;

            } else {

                entry.getValue().movement = 0;

            }

        }

    }

    /**
     * @brief Propaga els virus en aquesta regió.
     */
    public void propagate() {

        List<Affectation> affectations = new ArrayList<>(_affectations.values());

        for (Affectation affectation : affectations) {

            affectation.nextStep();
            
            affectation.propagateVirus();

        }

    }

    /**
     * @brief Retorna els habitants que s'han mogut.
     */
    public void rollbacks() {

        _pre_rollback_inhabitants = _inhabitants;

        if (isOnLockdown())
            return; // harsh lockdown, cap moviment

        for (Map.Entry<Region, Neighbour> entry : _neighbours.entrySet()) {

            int movement = entry.getValue().movement;

            if (entry.getValue().is_open && movement > 0) {

                transfer(entry.getKey(), this, movement);

                entry.getValue().movement = 0;

            }

        }

    }

    /**
     * @brief Acaba d'afegir els grups a les afectacions.
     */
    public void infect() {

        for (Affectation affectation : _affectations.values()) {

            affectation.pushGroups();

        }

    }

    /**
     * @brief Distribueix la proporció de persones malaltes uniformement
     * en la duració dels símptomes del virus en concret.
     */
    public void distributeAffectedGroup(Virus virus, float sick_percentage) {

        Affectation affectation = affectation(virus);

        int symptom_duration = virus.symptomDuration();
        int incubation_time = virus.incubationTime();

        int affected = (int)(sick_percentage * _inhabitants);
        int sick_div = affected / symptom_duration;
        int sick_min = symptom_duration - affected % symptom_duration;

        for (int i = 0; i < symptom_duration; i++) {

            /*
             *  La divisió d'affected / symptom_duration, al ser una divisió entera, es pot deixar n persones
             *  pel camí, on n és symptom_duration. Aleshores, mitjançant sick_min sumem les persones individuals
             *  que queden a cada grup per omplir-ho al màxim. Si no ho fes així, els grups no sumarien affected.
             */
            int cur_affected = sick_div + (i >= sick_min ? 1 : 0);

            AffectedGroup group = new AffectedGroup(affectation, virus, cur_affected);

            affectation.addGroup(group);

            affectation.pushGroups();
            affectation.nextStep();

        }

        for (int i = 1; i < incubation_time; i++) {

            affectation.nextStep(); // avançar els tics per saltar-se el període d'incubació

        }

        affectation.propagateVirus(); // primera propagació ha d'ocórrer ara

    }

}
