/**
 * @file Simulation.java
 * @brief Classe Simulation
 */

import java.util.*;

/**
 * @class Simulation
 * @brief Simulació del territori, virus, vacunes, etc. S'encarrega de gestionar els ticks, els nous virus i mutacions,
 * les vacunes i els confinaments.
 * @author Aniol Juanola
 */
public class Simulation {

    private static Territory _territory; ///< Territori que conté totes les regions de la simulació.
    private static List<Virus> _viruses; ///< Llista de virus de la simulació.
    private static List<VirusFamily> _families; ///< Llista de families de virus de la simulació.
    private static List<Vaccine> _vaccines; ///< Llista de vacunes de la simulació.

    private static int _tick; ///< Últim tick de la simulació.

    //CONSTRUCTOR

    /**
     * @brief Constructor de la simulació.
     * @pre families != null, viruses != null, regions != null, neighbours != null, initialStates != null, vaccines != null
     * @param families Col·lecció de les dades llegides de les famílies de virus.
     * @param viruses Col·lecció de les dades llegides dels virus.
     * @param regions Col·lecció de les dades llegides de les regions.
     * @param neighbours Col·lecció de les dades llegides dels veïns de les regions.
     * @param initialStates Col·lecció de les dades llegides de l'estat inicial de la simulació.
     * @param vaccines Col·lecció de les dades llegides de les vacunes.
     * @post Amb les dades dels diferents paràmetres s'instancien tots els nous Objectes del tipus respectiu,
     * es guarden a les respectives estructures de dades i s'inicialitza el tick inicial de la simulació a 0.
     */
    public Simulation(ArrayList<VirusReader.FamilyData> families, ArrayList<VirusReader.VirusData> viruses,
                      ArrayList<RegionReader.RegionData> regions, ArrayList<RegionReader.NeighbourData> neighbours,
                      ArrayList<InitialStateReader.InitialStateData> initialStates, ArrayList<VaccineReader.VaccineData> vaccines) {

        _families = assemblyFamilies(families);
        _viruses = assemblyViruses(viruses);
        _vaccines = assemblyVaccines(vaccines);
        _territory = new Territory(assemblyRegions(regions, neighbours, initialStates));

        _tick = 0;

    }

    //GETTERS

    /** @brief Getter de la llista de virus.
     * @pre True
     * @return Retorna la llista de virus.
     */
    public List<Virus> virusList() { return _viruses; }

    /**
     * @brief Getter de la llista de regions.
     * @pre True
     * @return Retorna la llista de regions.
     */
    public List<Region> regionList() { return _territory.regionList(); }

    /**
     * @brief Getter de la llista de famílies de virus.
     * @pre True
     * @return Retorna la llista de famílies de virus.
     */
    public List<VirusFamily> familyList() { return _families; }

    /** @brief Getter de la llista de vacunes.
     * @pre True
     * @return Retorna la llista de vacunes.
     */
    public List<Vaccine> vaccineList() { return _vaccines; }


    /** @brief Afegeix el virus v a la llista de virus.
     * @pre v != null
     * @param v Virus a inserir a la llista.
     * @post v s'ha afegit a la llista de virus de la simulació.
     */
    public static void addVirus(Virus v) {
        if (!_viruses.contains(v))
            _viruses.add(v);
    }


    //MÈTODES PÚBLICS

    //calcula cada tick i gestiona les operacions que s'han de fer a escala de simulació

    /** @brief Calcula el següent tick.
     * @pre True
     * @post S'ha avançat un tick s'han calculat els moviments entre regions, les mutacions dels virus i s'han propagat.
     */
    public void simulateNextTick() {

        _territory.propagateViruses();
        _tick++;
    }

    /**
     * @brief Retorna el tick actual.
     * @pre True
     * @return Retorna el tick actual.
     */
    public int getTick() {

        return _tick;

    }

    //estableix l'estat del confinament entre dues regions

    /** @brief Estableix l'estat de confinament de la frontera entre dues regions.
     * @pre region1 != null && region2 != null
     * @param region1 Regió 1
     * @param region2 Regió 2
     * @param state Estat de la regió.
     */
    public void setBorderState(Region region1, Region region2, boolean state) {
        region1.setBorderState(region2, state);
        region2.setBorderState(region1, state);
    }

    /**
     * @brief Estableix el confinament estricte de la regió a l'estat desitjat.
     * @pre new_rate >= 0 && new_rate <= 1 && region pertany a la llista de regions de la simulació.
     * @param region Regió que es vol confinar estrictament.
     * @param state Estat del confinament.
     * @param new_rate Nou ratio de moviments de dins de la regió.
     * @post Si state == true, region queda bloquejada amb el ratio intern de mobilitat new_rate. Altrament, region
     * queda desbloquejada (si arestes de la regió estaven tancades es mantindran així).
     */
    public void setHarshLockdown(Region region, boolean state, float new_rate) {
        region.setHarshLockdown(state, new_rate);
    }

    /**
     * @brief Aplica una vacuna a un percentatge de població d'una regió.
     * @pre vac pertany a _vaccines && region pertany a _regions && percentatge >= 0 && percentatge <= 1
     * @param vac Vacuna a aplicar.
     * @param region Regió on s'aplica la vacuna.
     * @param percentage Percentatge de població que serà vacunada.
     * @post S'ha aplicat la vacuna vac al percentatge % d'habitants de region.
     */
    public void applyVaccine(Vaccine vac, Region region, float percentage) {
        region.addVaccine(vac, percentage);
    }


    //MÈTODES PRIVATS

    /** @brief A partir de les dades de les families crea la llista d'objectes de tipus VirusFamily.
     * @pre True
     * @param input Conté les dades de totes les families a inserir.
     * @return Retorna la llista de VirusFamily amb les dades d'input.
     * @author Jordi Badia
     */
    private ArrayList<VirusFamily> assemblyFamilies(ArrayList<VirusReader.FamilyData> input) {

        ArrayList<VirusFamily> res = new ArrayList<>();

        for (VirusReader.FamilyData data : input) {

            res.add(new VirusFamily(data.name, data.mutate_max_var));

        }

        return res;

    }

    /** @brief A partir de les dades dels virus crea la llista d'objectes de tipus Virus o MutableVirus.
     * @pre True
     * @param input Conté les dades de tots els virus a inserir.
     * @return Retorna la llista de Virus amb les dades d'input.
     * @author Jordi Badia
     */
    private ArrayList<Virus> assemblyViruses(ArrayList<VirusReader.VirusData> input) {

        ArrayList<Virus> res = new ArrayList<>();

        for (VirusReader.VirusData data : input) {

            //Busca la familia
            VirusFamily virus_family = findFamilyByName(data.family, _families);

            Virus new_virus; //Virus a inserir

            if (data.type.equals("ADN"))

                new_virus = new Virus(data.name, virus_family, data.fall_sick_probability, data.death_rate,
                        data.spread_rate, data.symptom_duration, data.incubation_time, data.latency_time,
                        data.immunity_duration, data.infection_duration);

            else if (data.type.equals("ARN")) {
                new_virus = new MutableVirus(data.name, virus_family, data.fall_sick_probability, data.death_rate,
                        data.spread_rate, data.symptom_duration, data.incubation_time, data.latency_time,
                        data.immunity_duration, data.infection_duration, data.mutate_error_probability,
                        data.mutate_family_probability, null, null);

            } else {

                throw new InputMismatchException("El virus " + data.name + " no és ni de tipus ADN ni de tipus ARN");

            }

            res.add(new_virus);

        }

        return res;

    }

    /** @brief A partir de les dades de les vacunes crea la llista d'objectes de tipus Vaccine.
     * @pre True
     * @param input Conté les dades de totes les vacunes a inserir.
     * @return Retorna la llista de Vaccines amb les dades d'input.
     * @author Jordi Badia
     */
    private ArrayList<Vaccine> assemblyVaccines(ArrayList<VaccineReader.VaccineData> input) {

        ArrayList<Vaccine> res = new ArrayList<>();

        for (VaccineReader.VaccineData data : input) {

            Virus virus = findVirusByName(data.virus, _viruses);

            Vaccine new_vaccine; //Vacuna a inserir

            if (data.type.equals("inhibidora")) {

                new_vaccine = new HardVaccine(data.name, virus, data.delay, data.effect_duration,
                        data.effectiveness_ratio);

            }
            else if (data.type.equals("atenuadora")) {

                new_vaccine = new SoftVaccine(data.name, virus, data.delay, data.effect_duration,
                        data.mortality_reduction, data.duration_reduction, data.sick_probability_reduction,
                        data.contagiousness_reduction);

            } else {

                throw new InputMismatchException("La vacuna" + data.name +
                        " no és ni de tipus atenuadora ni de tipus inhibidora");

            }

            res.add(new_vaccine);

        }

        return res;

    }

    /** @brief A partir de les dades de les families crea la llista d'objectes de tipus Region.
     * @pre True
     * @param regions Conté les dades de totes les regions a inserir.
     * @param neighbours Conté les dades del veïnatge de les regions inserides.
     * @param initialStates Conté les dades de l'estat inicial de les regions en la simulació.
     * @return Retorna la llista de Region amb les dades d'input.
     * @author Jordi Badia
     */
    private ArrayList<Region> assemblyRegions(ArrayList<RegionReader.RegionData> regions,
                                              ArrayList<RegionReader.NeighbourData> neighbours,
                                              ArrayList<InitialStateReader.InitialStateData> initialStates) {

        ArrayList<Region> res = new ArrayList<>(); //Estructura a retornar

        //Inserta les regions cridant els contructors per totes les dades
        for (RegionReader.RegionData data : regions) {

            res.add(new Region(data.name, data.inside_mobility, data.inhabitants));

        }

        //Inserta les relacions de veïnatge a la regió pertinent
        for (RegionReader.NeighbourData data : neighbours) {

            Region region = findRegionByName(data.region_name, res);

            ArrayList<String> neighbour_names = data.neighbour;
            ArrayList<Float> neighbour_mobilities = data.flow_rate;

            for (int i = 0; i < neighbour_names.size(); i++) {

                Region neighbour = findRegionByName(neighbour_names.get(i), res);
                region.addNeighbour(neighbour, new Region.Neighbour(neighbour_mobilities.get(i), true));

            }

        }

        //Inserta els grups d'afectats a la regió pertinent
        for (InitialStateReader.InitialStateData data : initialStates) {

            Region region = findRegionByName(data.region_name, res);

            ArrayList<String> present_viruses = data.virus_affectation;
            ArrayList<Float> sick_percentage = data.percentage_affectation;

            for (int i = 0 ; i < present_viruses.size(); i++) {

                Virus virus = findVirusByName(present_viruses.get(i), _viruses);
                region.distributeAffectedGroup(virus, sick_percentage.get(i));

            }

        }

        return res;

    }

    /** @brief Retorna l'objecte VirusFamily de la llista de families donat el nom de la família.
     * @pre families conté una família amb nom == name && name != ""
     * @param name Nom de la família a buscar.
     * @param families Llista que conté la família a buscar
     * @return Retorna l'objecte VirusFamily de la família amb nom name de la llista families.
     */
    private VirusFamily findFamilyByName(String name, List<VirusFamily> families) {

        VirusFamily virus_family = null;
        int i = 0;

        while (i < families.size() && virus_family == null) {
            VirusFamily fam = families.get(i);
            if (fam.name().equals(name))
                virus_family = fam;
            i++;
        }
        if (virus_family == null) throw new NoSuchElementException("No existeix la familia " + name);

        return virus_family;

    }

    /** @brief Retorna l'objecte Region de la llista de regions donat el nom de la regió.
     * @pre regions conté una regió amb nom == name && name != ""
     * @param name Nom de la regió a buscar.
     * @param regions Llista que conté la regió a buscar
     * @return Retorna l'objecte Region de la regió amb nom name de la llista regions.
     */
    private Region findRegionByName(String name, List<Region> regions) {
        Region region = null;
        int i = 0;

        while (i < regions.size() && region == null) {
            Region reg = regions.get(i);
            if (reg.name().equals(name))
                region = reg;
            i++;
        }
        if (region == null) throw new NoSuchElementException("No existeix la regió " + name);

        return region;
    }

    /** @brief Retorna l'objecte Virus de la llista de virus donat el nom del virus.
     * @pre viruses conté un virus amb nom == name && name != ""
     * @param name Nom del virus a buscar.
     * @param viruses Llista que conté el virus a buscar
     * @return Retorna l'objecte Virus amb nom name de la llista viruses.
     */
    private Virus findVirusByName(String name, List<Virus> viruses) {
        Virus virus = null;
        int i = 0;

        while (i < viruses.size() && virus == null) {
            Virus vir = viruses.get(i);
            if (vir.name().equals(name))
                virus = vir;
            i++;
        }
        if (virus == null) throw new NoSuchElementException("No existeix el virus " + name);

        return virus;
    }

} 
