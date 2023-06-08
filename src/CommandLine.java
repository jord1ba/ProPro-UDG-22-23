/**
 * @file CommandLine.java
 * @brief Classe CommandLine
 */

/** @class CommandLine
 * @brief Classe encarregada de gestionar la línia d'ordres en el seu thread individual.
 * Conté el bucle principal del programa amb les diferents opcions i formes que l'usuari té per interactuar
 * amb el programa.
 * @author Jordi Badia
 */
public class CommandLine {

    // Constants

    private static final int MENU_OPTIONS = 8; ///< Nombre d'opcions del menú principal.
    private static final int FILTER_OPTIONS = 3; ///< Nombre d'opcions del menú de filtres.
    private static final int LOCKDOWN_OPTIONS = 4; ///< Nombre d'opcions del menú de confinaments.
    private static final int LIST_OPTIONS = 4; ///< Nombre d'opcions del menú de llistar.



    //Private attributes

    private static UI ui; ///< Classe UI que permet la interacció de CommandLine amb el Thread dels Charts i el Graph.
    private static Simulation simulation; ///< Objecte simulation que realitza la simulació.
    private static Filter filter; ///< Objecte Filter que filtra la informació generada per \p simulation.

    private static Output view; ///< Guarda el tipus de vista que s'està mostrant en tot moment per pantalla.
    private static Menu menu; ///< Guarda el menú que es mostrarà en tot moment per pantalla.



    // Constructors

    /** @brief Inicialitza l'objecte actual.
     * @pre True.
     * @post Els atributs privats apunten a \p u, \p s, \p f i es mostra el menú principal i el resum de la situació inicial.
     * @param u UI inicialitzada a partir de \p s.
     * @param s Simulation inicialitzada.
     * @param f Filter inicialitzat a partir de \p s.
     */
    public CommandLine(UI u, Simulation s, Filter f) {

        ui = u;
        simulation = s;
        filter = f;

        view = Output.SUMMARY_VIEW;
        menu = Menu.MAIN;

    }



    // Funcions públiques

    /** @brief Funció que conté el bucle principal del progrma.
     * @pre \p simulation inicialitzada sense errors i \p filter i \p ui inicialitzats a partir del primer.
     * @post Mostra per pantalla el que l'usuari soliciti fins que decideixi acabar, llegeix una nova opció i l'executa.
     * Opcions:
     * 1. Establir filtres: obre el menú de filtres i permet modificar-los
     * 2. Generar tick següent: genera el següent tick
     * 3. Generar més d'un tick: rep un enter entre 1 i 20 i genera tants ticks com l'enter entrat.
     * 4. Aplicar vacuna: l'usuari tria una regió i una vacuna, així com la proporció de la població a la que aplicar-la.
     * 5. Actualitzat un confinament: s'obre el menú de confinaments i l'usuari pot aplicar-ne i treure'n, així com tancaments.
     * 6. Veure log o resum: alterna vistes de resum i log cada cop que és activada (es recomana log per seguiments concrets).
     * 7. Consultar llistes: sobre el menú de llistes i es poden visualitzar aquelles que es vulguin (virus, vacunes, regions i famílies)
     * 8. Sortir: tanca l'execució del programa exitosament.
     */
    public void processCmd() {

        //Bucle principal
        while (true) {

            printOutput();

            int option = Interact.getOption("Opció: ", MENU_OPTIONS, false);

            switch (option) {

                case 1 -> setFilters();
                case 2 -> nextTick();
                case 3 -> moreThanOneTicks();
                case 4 -> applyVaccine();
                case 5 -> updateLockdown();
                case 6 -> toggleData();
                case 7 -> listMenu();
                case 8 -> System.exit(0);

            }

        }

    }



    //Funcions privades

    /** @brief Mostra per consola a través de Writer els continguts pertinents.
     * @pre True.
     * @post Mostra el tipus de vist i menú que toquin segons \p view i \p menu i actualitza els PieCharts.
     */
    private static void printOutput() {

        char sum_or_log = 'x';

        Writer.clearConsole();

        switch (view) {

            case SUMMARY_VIEW -> {
                Writer.writeSummary(simulation.getTick(), filter.summary());
                sum_or_log = 's';
            }
            case LOG_VIEW -> {
                Writer.writeLog(simulation.getTick(),filter.log());
                sum_or_log = 'l';
            }
            case REGION_LIST -> Writer.list(simulation.regionList().iterator(), false);
            case VIRUS_LIST -> Writer.list(simulation.virusList().iterator(), false);
            case FAMILY_LIST -> Writer.list(simulation.familyList().iterator(), false);
            case VACCINE_LIST -> Writer.list(simulation.vaccineList().iterator(), false);

        }

        switch (menu) {

            case MAIN -> Writer.showMainMenu(sum_or_log);
            case FILTER -> Writer.showFilterMenu(filter);
            case LIST -> Writer.showListMenu();
            case LOCKDOWN -> Writer.showLockdownMenu();

        }

        sendDataToUI(simulation.getTick(),filter.region(),filter.virus());

    }

    /** @brief Desplega el menú de filtres i permet actualitzar-los.
     * @pre True.
     * @post Obre el menú de filtres i llegeix l'opció elegida per l'usuari.
     * Opcions:
     * 1. Modificar virus: permet a l'usuari seleccionar un virus com a filtre o treure l'actual
     * 2. Modificar regió: permet a l'usuari seleccionar una regió com a filtre o treure l'actual.
     * 3. Esborrar filtres: suprimeix els filtres actuals.
     * 0. Tornar al menú principal: torna al menú principal
     */
    private static void setFilters() {

        view = Output.BLANK;
        menu = Menu.FILTER;

        printOutput();

        int option = Interact.getOption("Opció: ", FILTER_OPTIONS, true);

        switch (option) {
            case 0 -> {
                view = Output.SUMMARY_VIEW;
                menu = Menu.MAIN;
                return;
            }
            case 1 -> updateVirusFilter();
            case 2 -> updateRegionFilter();
            case 3 -> {
                filter.deleteRegionFilter();
                filter.deleteVirusFilter();
            }
        }

        setFilters();

    }

    /** @brief Si encara és útil, genera el següent tick
     * @pre True.
     * @post Si encara hi ha immunes o infectats permet generar el següent tick i actualitzar la informació.
     * En cas contrari no fa res.
     */
    private static void nextTick() {

        if (filter.contagiousPopulation()) {

            simulation.simulateNextTick();

            filter.updateDataNextTick();

        }

    }

    /** @brief Si encara és útil, genera tants ticks com determini l'usuari.
     * @pre True.
     * @post Llegeix un nombre entre 1 i 20 i genera tants ticks (mentre valgui la pena) com l'enter llegit.
     */
    private static void moreThanOneTicks() {

        int ticks_to_generate = Interact.getOption("Nombre de ticks a generar (1-20, 0 per sortir): ",
                20, true);

        for (int i = 0; i < ticks_to_generate; i++)
            nextTick();

    }

    /** @brief Demana a l'usuari les dades per aplicar una vacuna i l'aplica
     * @pre True.
     * @post Demana a l'usuari que seleccioni una regió, una vacuna i una proporció de la població a la que aplicar-la i la aplica.
     */
    private static void applyVaccine() {

        view = Output.BLANK;
        menu = Menu.BLANK;

        printOutput();
        Vaccine vaccine = null;
        float percentage = 0f;

        Region region = Interact.getElement(simulation.regionList(), "Número de la regió on aplicar la vacuna " +
                "(0 per tornar al menú): ", true);
        if (region != null)
            vaccine = Interact.getElement(simulation.vaccineList(), "Número de la vacuna a aplicar " +
                "(0 per tornar al menú): ", true);
        if (vaccine != null)
            percentage = Interact.getRate("Entra el percentatge de població al que aplicar la vacuna " +
                "(0-100, 0 per tornar al menú): ", 100) / 100;
        if (percentage != 0f)
            simulation.applyVaccine(vaccine, region, percentage);

        view = Output.SUMMARY_VIEW;
        menu = Menu.MAIN;

    }

    /** @brief Actualitza confinaments i tancaments
     * @pre True.
     * @post Mostra el menú de confinaments que permet a l'usuari aplicar i treure tancaments i confinaments.
     * Opcions:
     * 1. Aplicar confinament: permet confinar una regió
     * 2. Extingir confinament: permet extingir el confinament d'una regió
     * 3. Aplicar tancament: permet modificar la mobilitat entre dues regions
     * 4. Extingir tancament: permet extingir un tancament
     * 0. Tornar al menú principal: torna al menú principal.
     */
    private static void updateLockdown() {

        view = Output.BLANK;
        menu = Menu.LOCKDOWN;

        printOutput();

        int option = Interact.getOption("Opció: ", LOCKDOWN_OPTIONS, true);

        Region r1 = null, r2 = null;

        if (option > 0) {

            r1 = Interact.getElement(simulation.regionList(), "Número de la regió on aplicar un " +
                    "tancament/confinament (0 per tornar al menú): ", true);

            if (r1 == null) {
                updateLockdown();
                return;
            }

            if (option > 2)  {
                r2 = Interact.getElement(r1.neighbours(), "Número de la segona regió amb la que realitzar un " +
                        "tancament (0 per tornar al menú): ", true);
                if (r2 == null) {
                    updateLockdown();
                    return;
                }
            }

        }

        switch (option) {

            case 0 -> {
                view = Output.SUMMARY_VIEW;
                menu = Menu.MAIN;
                return;
            }
            case 1 -> {
                float new_ratio = Interact.getRate("Nou ratio (0-100, 100 per tornar): ", 100) / 100;

                if (new_ratio == 1f) break;

                simulation.setHarshLockdown(r1, true, new_ratio);
                ui.hardLock(r1);
            }
            case 2 -> {
                simulation.setHarshLockdown(r1, false, 0);
                ui.hardUnlock(r1);
            }
            case 3 -> {
                simulation.setBorderState(r1, r2, false);
                ui.lockEdge(r1,r2);
            }
            case 4 -> {
                simulation.setBorderState(r1, r2, true);
                ui.unlockEdge(r1,r2);
            }

        }

        updateLockdown();

    }

    /** @brief Alterna entre mostrar el resum i el log.
     * @pre True.
     * @post Si s'està mostrant el resum passa a mostrar el log i viceversa.
     * - Resum: dades importants
     * - Log: tota la informació generada
     */
    private static void toggleData() {

        if (view == Output.SUMMARY_VIEW)
            view = Output.LOG_VIEW;
        else
            view = Output.SUMMARY_VIEW;

    }

    /** @brief Mostra el menú de llistes dels elements de la simulació.
     * @pre True.
     * @post Mostra el menú de llistes, llegeix l'opció triada per l'usuari i mostra una llista d'elements de la simulació.
     * Opcions:
     * 1. Llistar regions
     * 2. Llistar virus
     * 3. Llistar famílies
     * 4. Llistar vacunes
     * 0. Tornar al menú principal
     */
    private static void listMenu() {

        if (view == Output.SUMMARY_VIEW || view == Output.LOG_VIEW)
            view = Output.BLANK;
        menu = Menu.LIST;

        printOutput();

        int option = Interact.getOption("Opció: ", LIST_OPTIONS, true);

        switch (option) {

            case 0 -> {
                menu = Menu.MAIN;
                view = Output.SUMMARY_VIEW;
                return;
            }
            case 1 -> view = Output.REGION_LIST;
            case 2 -> view = Output.VIRUS_LIST;
            case 3 -> view = Output.FAMILY_LIST;
            case 4 -> view = Output.VACCINE_LIST;

        }

        listMenu();

    }

    /** @brief envia la informació generada pels filtres a la UI per fer els PieCharts.
     * @pre \p tick >= 0.
     * @post Actualitza els PieChart amb els filtres de \p filter.
     * @param tick Tick que s'està mostrant a la interfície.
     * @param region Regió sobre la qual es mostra informació (pot ser null).
     * @param virus Virus sobre el qual es mostra informació (pot ser null).
     */
    private static void sendDataToUI(int tick, Region region, Virus virus) {

        ui.changePieChartTitle("Tick " + tick + (filter.contagiousPopulation() ? "" : " (situació final)") +
                "\nFiltre de regió: " + (region == null ? "cap" : region.name()) + "\nFiltre de virus: " +
                (virus == null ? "cap" : virus.name()));

        if (!filter.contagiousPopulation()) {
            ui.changeLeftPieChartTitle("Infectats erradicats");
            ui.changeRightPieChartTitle("Infectats erradicats");
        }
        else {
            ui.changeLeftPieChartTitle(filter.summary().contagiated() != 0 ? "Malalts" : "Cap malalt");
            ui.changeRightPieChartTitle(filter.summary().contagiated() != 0 ? "Infectats" : "Cap infectat");
        }

        ui.updateChartsData(filter.getTitlesArray(), filter.generalData(), filter.diseaseData(), filter.contagiousData());

    }

    // Extracció de funcions

    /** @brief Actualitza el filtre de virus.
     * @pre True.
     * @post Si hi havia un filtre, el treu. Si no n'hi havia, deixa triar-ne un a l'usuari.
     */
    private static void updateVirusFilter() {

        if (filter.virus() != null)
            filter.deleteVirusFilter();
        else {

            Virus virus = Interact.getElement(simulation.virusList(), "Número del virus a posar com a filtre " +
                            "(0 per tornar al menú): ",
                    true);

            if (virus == null) return;

            filter.updateFilter(virus);

        }

    }

    /** @brief Actualitza el filtre de regions.
     * @pre True.
     * @post Si hi havia filtre, el treu. Si no n'hi havia, deixa triar-ne un a l'usuari.
     */
    private static void updateRegionFilter() {

        if (filter.region() != null)
            filter.deleteRegionFilter();
        else {

            Region region = Interact.getElement(simulation.regionList(), "Número de la regió a posar com a " +
                    "filtre (0 per tornar al menú): ", true);
            filter.updateFilter(region);

        }

    }

    //Private enums

    /// Enumeració de sortides
    private enum Output {

        SUMMARY_VIEW, ///< Mostra el resum
        LOG_VIEW, ///< Mostra el log
        REGION_LIST, ///< Mostra la llista de regions
        VIRUS_LIST, ///< Mostra la llista de virus
        FAMILY_LIST, ///< Mostra la llista de famílies
        VACCINE_LIST, ///< Mostra la llista de vacunes
        BLANK, ///< No mostra res

    }

    /// Enumeració de menús
    private enum Menu {

        MAIN, ///< Menú principal
        FILTER, ///< Menú de filtres
        LIST, ///< Menú de llistes
        LOCKDOWN, ///< Menú de confinaments
        BLANK ///< No mostris cap menú

    }

}
