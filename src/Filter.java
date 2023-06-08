/**
 * @file Filter.java
 * @brief Classe Filter
 */

import com.sun.source.tree.Tree;

import java.lang.reflect.Array;
import java.util.*;


/**
 * @class Filter
 * @brief Classe encarregada de guardar un conjunt d'informació i filtrar-la per diferents criteris com virus, regió,
 * els dos o cap.
 * @author Jordi Badia
 */
public class Filter {

    // Private attributes

    //Llistes auxiliars (es guarden per quan apareguin virus nous, llegir-los i poder generar la Data per cada regió).
    private final List<Virus> _virus_list;
    private final List<Region> _region_list;

    // Atributs importants
    private final Map<Virus, Map<Region, Data>> _dataVR;
    ///< Mapa de mapes que indexen objectes de tipus Data primer per VIRUS i després per REGIÓ.
    private final Map<Region, Map<Virus, Data>> _dataRV;
    ///< Mapa de mapes que indexen objectes de tipus Data primer per REGIÓ i després per VIRUS.
    private Region _region_filter; ///< Regió pel qual es filtra (null si no hi ha filtre).
    private Virus _virus_filter; ///< Virus pel qual es filtra (null si no hi ha filtre).
    private int _last_virus_index; ///< Índex de l'últim virus aparegut a _virus_list (per controlar les mutacions).



    // Constructors

    /** @brief Constructor principal que crea a partir de les llistes de regions i virus de \p simulation, les
     * estructures que guarden Data.
     * @pre \p simulation té inicialitzats les estructures que guarden regions i virus.
     * @post \p _dataVR i \p _dataRV guarden Data de només aquelles combinacions de regió i virus que tenen alguna
     * afectació, a més guarden les referències a les llistes de \p simulation i l'índex de l'últim virus.
     * @param simulation Simulació d'on treure les regions i els virus.
     */
    public Filter(Simulation simulation) {

        _region_filter = null;
        _virus_filter = null;
        _dataVR = new HashMap<>();
        _dataRV = new HashMap<>();
        _virus_list = simulation.virusList();
        _region_list = simulation.regionList();
        _last_virus_index = _virus_list.size();

        for (Virus virus : _virus_list)
            _dataVR.put(virus, new HashMap<>());

        for (Region region : _region_list) {

            HashMap<Virus, Data> tmp = new HashMap<>();

            for (Affectation affectation : region.recentlyAddedAffectations()) {

                Virus virus = affectation.virus();
                Data new_data = new Data(region, virus);

                _dataVR.get(virus).put(region, new_data);
                tmp.put(virus, new_data);

            }

            _dataRV.put(region, tmp);

        }

    }



    // Funcions públiques

    /** @brief Getter del filtre de regió.
     * @pre True.
     * @return La regió establerta com a filtre (null si no n'hi ha).
     */
    public Region region() {

        return _region_filter;

    }

    /** @brief Getter del filtre de virus.
     * @pre True.
     * @return El virus establert com a filtre (null si no n'hi ha).
     */
    public Virus virus() {

        return _virus_filter;

    }

    /** @brief Actualitza els objectes Data d'aquest objecte al nou tick generat, i afegeix el de les noves afectacions.
     * @pre Des de l'última crida d'aquesta funció, la simulació de la qual llegeix aquest Filtre ha generat un nou tick.
     * @post S'actualitzen tots els objectes de Data de \p _dataVR (i, per tant, de \p _dataRV), es llegeixen i
     * carreguen els nous virus apareguts per mutació i es generen nous objectes Data per les afectacions aparegudes.
     */
    public void updateDataNextTick() {

        // Actualització de la Data present
        for (Map<Region, Data> map : _dataVR.values())

            for (Data data : map.values())

                data.updateData();


        // Ampliació amb els nous virus apareguts per mutació
        for (int i = _last_virus_index; i < _virus_list.size(); i++)

            _dataVR.put(_virus_list.get(i), new HashMap<>());

        _last_virus_index = _virus_list.size();


        // Inserció de Data a les estructures per noves afectacions
        for (Region region : _region_list) {

            for (Affectation affectation : region.recentlyAddedAffectations()) {

                Virus virus = affectation.virus();
                Data new_data = new Data(region, virus);

                _dataVR.get(virus).put(region, new_data);
                _dataRV.get(region).put(virus, new_data);

            }

        }

    }

    /** @brief Setter del filtre per regió.
     * @pre \p virus no pot ser null.
     * @post \p _region_filter pren el valor de \p region.
     * @param region Regió per la qual es filtrarà (no pot ser null).
     */
    public void updateFilter(Region region) {

        _region_filter = region;

    }

    /** @brief Setter del filtre per virus.
     * @pre \p virus no pot ser null.
     * @post \p _virus_filter pren el valor de \p virus.
     * @param virus Virus pel qual es filtrarà (no pot ser null).
     */
    public void updateFilter(Virus virus) {

        _virus_filter = virus;

    }

    /** @brief Setter per suprimir el filtre per regió (n'hi hagi o no).
     * @pre True.
     * @post _region_filter és null.
     */
    public void deleteRegionFilter() {

        _region_filter = null;

    }

    /** @brief Setter per suprimir el filtre per virus (n'hi hagi o no).
     * @pre True.
     * @post _virus_filter és null.
     */
    public void deleteVirusFilter() {

        _virus_filter = null;

    }

    /** @brief Genera un objecte de tipus Data amb el conjunt de totes les dades resultants d'aplicar els filtres.
     * @pre True.
     * @return Un objecte de tipus Data amb la suma de tots els objectes Data del filtre que compleixin amb els filtres
     * establerts. Si cap compleix, es retorna un Data on tots els valors són 0.
     */
    public Data summary() {

        return new Data(_region_filter, _virus_filter, log().iterator());

    }

    /** @brief Retorna una llista d'objectes de tipus Data continguts en aquest objecte que compleixin amb els filtres
     * establerts.
     * @pre True.
     * @return Una llista d'objectes de tipus Data que compleixen amb els filtres establerts.
     */
    public List<Data> log() {

        ArrayList<Data> res = new ArrayList<>();

        if (_virus_filter != null && _region_filter != null) { //Filtre per virus i regions

            Data to_add = _dataVR.get(_virus_filter).get(_region_filter);
            res.add(to_add != null ? to_add : new Data(_region_filter,_virus_filter));

        }
        else if (_virus_filter != null) { //Filtre per virus

            res.addAll(_dataVR.get(_virus_filter).values());

        }
        else if (_region_filter != null) { //Filtre per regions

            res.addAll(_dataRV.get(_region_filter).values());

        }
        else { //Sense filtres

            for (Map<Region, Data> virusData : _dataVR.values())

                res.addAll(virusData.values());

        }

        return res;

    }

    /** @brief Retorna el conjunt de títols dels elements del log().
     * @pre True.
     * @return Un ArrayList de String amb el conjunt de títols dels elements de log().
     */
    public ArrayList<String> getTitlesArray() {

        ArrayList<String> res = new ArrayList<>();

        for (Data data : log())
            res.add(data.virus() + " a " + data.region());

        return res;
    }

    /** @brief Retorna el conjunt d'informació genèrica dels elements del log()
     * @pre True.
     * @return Un ArrayList de TreeMap de String i Integers amb la informació genèrica dels elements del log().
     */
    public ArrayList<TreeMap<String, Integer>> generalData() {

        ArrayList<TreeMap<String, Integer>> res = new ArrayList<>();

        for (Data data : log())
            res.add(data.generalData());

        return res;

    }

    /** @brief Retorna el conjunt d'informació dels malalts dels elements del log()
     * @pre True.
     * @return Un ArrayList de TreeMap de String i Integers amb la informació dels malalts dels elements del log().
     */
    public ArrayList<TreeMap<String, Integer>> diseaseData() {

        ArrayList<TreeMap<String, Integer>> res = new ArrayList<>();

        for (Data data : log())
            res.add(data.diseaseData());

        return res;

    }

    /** @brief Retorna el conjunt d'informació dels contagiosos dels elements del log()
     * @pre True.
     * @return Un ArrayList de TreeMap de String i Integers amb la informació dels contagiosos dels elements del log().
     */
    public ArrayList<TreeMap<String, Integer>> contagiousData() {

        ArrayList<TreeMap<String, Integer>> res = new ArrayList<>();

        for (Data data : log())
            res.add(data.contagiousData());

        return res;

    }

    /** @brief Retorna si encara queda algú contagiat o immune a alguna malaltia independentment dels filtres establerts.
     * @pre True.
     * @return Un booleà que és true si encara hi ha contagiats o immunes i false en cas contrari.
     */
    public boolean contagiousPopulation() {

        // Guarda els filtres i els desactiva
        Region old_region_filter = _region_filter;
        Virus old_virus_filter = _virus_filter;
        _region_filter = null;
        _virus_filter = null;

        boolean res = summary().virusPresence() != 0;

        // Reactiva els filtres que hi havia en cridar la funció
        _region_filter = old_region_filter;
        _virus_filter = old_virus_filter;

        return res;

    }

}
