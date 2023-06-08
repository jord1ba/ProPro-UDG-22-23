/** @file GraphChartData.java
 * @brief Classe GraphChartData
 */

import javafx.application.Platform;
import com.brunomnsilva.smartgraph.graph.*;
import com.brunomnsilva.smartgraph.graphview.SmartCircularSortedPlacementStrategy;
import com.brunomnsilva.smartgraph.graphview.SmartGraphPanel;
import com.brunomnsilva.smartgraph.graphview.SmartGraphProperties;
import java.util.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.chart.*;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

/** @class GraphChartData
 * @brief Classe encarregada de gestionar tota la interacció externa amb les dades del Graf i dels Charts.
 * Cada mètode modificador s'assegura que s'executi des del Thread del JavaFX (altrament no es pot modificar
 * i es llança una excepció).
 * @author Aniol Juanola
 */
public class GraphChartData {

    private final Graph<String, String> graph = new GraphEdgeList<>(); ///< Objecte del graf.

    private final HashMap<Region, HashMap<Region, Edge<String, String>>> edgeMap = new HashMap<>();
    ///< Hashmap per enllaçar les regions amb les arestes del graf.

    private static final String customGraphProperties =
            "edge.label = false" + "\n" +
            "edge.arrow = false" + "\n" +
            "layout.repulsive-force = 40000" + "\n" +
            "layout.attraction-force = 20";
    ///< Propietats visuals del graf, modificables per obtenir físiques i visualitzacions diferents.

    private final SmartGraphProperties graphProperties; ///< Objecte que conté les propietats del graf.

    private final SmartGraphPanel<String, String> graphView; ///< Panell de JavaFX que conté el Graf.

    private final Button forwardButton = new Button(); ///< Botó que permet avançar en el pool de gràfics dels filtres
    ///seleccionats.
    private final Button backwardButton = new Button(); ///< Botó que permet retrocedir en el pool de gràfics dels
    /// filtres seleccionats.

    /** @brief Prepara el Graph de les regions i els PieCharts per a ser mostrats.
     * @pre True
     * @post S'han inicialitzat els grafs i els PieCharts correctament.
     */
    public GraphChartData() {

        //Creació de les propietats i del visor del graf.
        graphProperties = new SmartGraphProperties(customGraphProperties);
        graphView = new SmartGraphPanel<>(graph, graphProperties, new SmartCircularSortedPlacementStrategy());
        graphView.setAutomaticLayout(true);

        //PieChartGeneral
        pieChartData = FXCollections.observableArrayList(); //vector de dades buit
        pieChart.setData(pieChartData); //establir la data del piechart
        pieChart.setTitle("Títol del PieChart"); //establir títol del piechart
        pieChart.setStartAngle(90); //angle inicial (vertical)
        pieChart.setClockwise(true); //ordre horari de generació del chart.
        pieChart.setLabelsVisible(false); //amaguem les Labels de les zones.
        pieChart.setPrefSize(pieChartWidth, pieChartHeight); //Mida per defecte per cabre a la finestra.
        pieChart.setAnimated(false); //desactivar animacions per estalviar bugs.

        //PieChart esquerra
        leftPieChartData = FXCollections.observableArrayList();
        leftPieChart.setData(leftPieChartData);
        leftPieChart.setStartAngle(90);
        leftPieChart.setClockwise(true);
        leftPieChart.setLabelsVisible(false);
        leftPieChart.setTitle("Incubacions / símptomes");
        leftPieChart.setPrefSize(bottomPieChartWidth, bottomPieChartHeight);
        leftPieChart.setAnimated(false);

        //PieChart dret
        rightPieChartData = FXCollections.observableArrayList();
        rightPieChart.setData(rightPieChartData);
        rightPieChart.setStartAngle(90);
        rightPieChart.setClockwise(true);
        rightPieChart.setLabelsVisible(false);
        rightPieChart.setTitle("Latencia / contagiosos");
        rightPieChart.setPrefSize(bottomPieChartWidth, bottomPieChartHeight);
        rightPieChart.setAnimated(false);

        //BOTONS
        forwardButton.setText("→");
        backwardButton.setText("←");

        //event handlers del click dels botons
        forwardButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                index = ++index % topDataPool.size(); //els 3 tenen el mateix size
                refreshData();
            }
        });

        backwardButton.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent mouseEvent) {
                index = --index < 0 ? topDataPool.size() - 1 : index; //els 3 tenen el mateix size
                refreshData();
            }
        });
    }

    /** @brief Mètode que assegura que el Runnable
     * @param action Funció o grup de funcions que s'executarà en el Thread del JavaFX.
     * @pre Action és correcta.
     * @post Executa l'acció action en el Thread del JavaFX. Si es crida el mètode des d'un Thread extern,
     * l'acció s'empila en una cua gestionada pel Thread del JavaFX per a l'execució en un futur indeterminat.
     * És important que el funcionament del programa base NO depengui d'aquest mètode perquè assegura que s'executarà,
     * però no assegura ni en quin moment ni si ho acabarà fent en un temps "raonable".
     */
    private void ensureFXThread(Runnable action) {
        if (Platform.isFxApplicationThread()) {
            action.run();
        } else {
            Platform.runLater(action);
        }
    }

    /** @brief Retorna el Panel del graph.
     * @pre True.
     * @return Retorna el Panel del graph.
     */
    public SmartGraphPanel<String, String> graphView() { return graphView; }

    /** @brief Donat un iterador de Regions, afegeix totes les regions com a vèrtexs del graf.
     * @pre True.
     * @param iterator Iterador d'una Collection de Regions.
     * @post Afegeix totes les regions de l'iterador com a vèrtexs del graf.
     */
    public void addVertexs(Iterator<Region> iterator) {
        ensureFXThread(() -> {
            while (iterator.hasNext()) {
                Region r = iterator.next();
                graph.insertVertex(r.name());
            }
        });
    }

    /** @brief Donat un iterador de Regions, afegeix totes les relacions de veïnes com a arestes al graf.
     * @pre Les regions veïnes de cada regió ja existeixen en el graf com a vèrtex.
     * @post Afegeix totes les veïnes de cada regió de l'iterador com a arestes del graf.
     * @param iterator Iterador d'una Collection de Regions.
     */
    public void addEdges(Iterator<Region> iterator) {
        ensureFXThread(() -> {
            while (iterator.hasNext()) {
                Region r = iterator.next();
                try {
                    for (Region j : r.neighbours()) {
                        edgeMap.computeIfAbsent(r, k -> new HashMap<>());
                        edgeMap.computeIfAbsent(j, k -> new HashMap<>());
                        if (edgeMap.get(r).get(j) == null && edgeMap.get(j).get(r) == null)
                            edgeMap.get(r).put(j, graph.insertEdge(r.name(), j.name(), r.name() + j.name()));
                    }
                } catch (InvalidEdgeException e) { e.printStackTrace(); }
            }
        });
    }

    /** @brief Canvia el color de l'aresta que connecta r1 i r2 a vermell (bloquejat).
     * @pre L'aresta entre r1 i r2 existeix. r1 i r2 es troben en el graf.
     * @param r1 Primera regió.
     * @param r2 Segona regió.
     * @post El color de l'aresta que connecta r1 i r2 serà vermell (tancat).
     */
    public void lockEdge(Region r1, Region r2) {
        if (edgeMap.get(r1).get(r2) == null) {
            if (edgeMap.get(r2).get(r1) == null) {
                throw new IllegalArgumentException("L'aresta entre les regions seleccionades (" + r1.name()
                        + " i " + r2.name() + ") no existeix.");
            }
            ensureFXThread(() -> {
                graphView.getStylableEdge(edgeMap.get(r2).get(r1)).setStyleClass("locked-edge");

                graphView.update();
            });
        }
        else {
            ensureFXThread(() -> {
                graphView.getStylableEdge(edgeMap.get(r1).get(r2)).setStyleClass("locked-edge");

                graphView.update();
            });
        }
    }

    /** @brief Canvia el color de l'aresta que connecta r1 i r2 a blau (obert).
     * @pre L'aresta entre r1 i r2 existeix. r1 i r2 es troben en el graf.
     * @param r1 Primera regió.
     * @param r2 Segona regió.
     * @post El color de l'aresta que connecta r1 i r2 serà el per defecte (obert).
     */
    public void unlockEdge(Region r1, Region r2) {
        if (edgeMap.get(r1).get(r2) == null) {
            if (edgeMap.get(r2).get(r1) == null) {
                throw new IllegalArgumentException("L'aresta entre les regions seleccionades (" + r1.name()
                        + " i " + r2.name() + ") no existeix.");
            }
            ensureFXThread(() -> {
                graphView.getStylableEdge(edgeMap.get(r2).get(r1)).setStyleClass("edge");
                graphView.getStylableVertex(r1.name()).setStyleClass("vertex");
                graphView.getStylableVertex(r2.name()).setStyleClass("vertex");
                graphView.update();
            });
        }
        else {
            ensureFXThread(() -> {
                graphView.getStylableEdge(edgeMap.get(r1).get(r2)).setStyleClass("edge");
                graphView.getStylableVertex(r1.name()).setStyleClass("vertex");
                graphView.getStylableVertex(r2.name()).setStyleClass("vertex");
                graphView.update();
            });
        }
    }

    /** @brief Bloqueja la regió estrictament.
     * @pre neighbourIterator només conté Regions que tenen una aresta amb la Regió r1. r1 es troba al graf.
     * @param r1 Regió que es vol bloquejar.
     * @post La regió queda pintada de vermell.
     */
    public void hardLock(Region r1) {
        ensureFXThread(() -> {
            graphView.getStylableVertex(r1.name()).setStyleClass("locked-vertex");
            graphView.update();
        });
    }

    /** @brief Desbloqueja la regió.
     * @pre neighbourIterator només conté Regions que tenen una aresta amb la Regió r1. r1 es troba al graf.
     * @param r1 Regió que es vol desbloquejar.
     * @post La regió queda del color original.
     */
    public void hardUnlock(Region r1) {
        ensureFXThread(() -> {
            graphView.getStylableVertex(r1.name()).setStyleClass("vertex");
            graphView.update();
        });
    }


    //========================================================================

    private ArrayList<TreeMap<String, Integer>> topDataPool; ///< Conjunt de dades del PieChart superior.

    /** @brief Substitueix el pool de dades del PieChart superior.
     * @pre newData != null
     * @param newData Nou pool de dades pel PieChart superior.
     * @post topDataPool és newData i l'índex = 0.
     */
    public void updateTopDataPool(ArrayList<TreeMap<String, Integer>> newData) {
        topDataPool = newData;
        index = 0;
    }

    private final ObservableList<PieChart.Data> pieChartData; ///< Dades del PieChart general.

    private final PieChart pieChart = new PieChart(); ///< Objecte del PieChart general.

    private static final int pieChartWidth = 672; ///< Amplada del PieChart general.
    private static final int pieChartHeight = 371; ///< Alçada del PieChart general.

    //======================================================

    private ArrayList<TreeMap<String, Integer>> leftDataPool; ///< Conjunt de dades del PieChart inferior esquerra.

    private ArrayList<TreeMap<String, Integer>> rightDataPool; ///< Conjunt de dades del PieChart inferior dret.

    private int index = 0; ///< Índex de la posició de les diferents dades dels PieCharts (és compartit).

    private ArrayList<String> variableTitlePool = new ArrayList<>(); ///< Conjunt de tots els títols.

    private String mainTitle; ///< Títol que es conserva independentment del conjunt de dades mostrat.

    /** @brief Neteja les dades dels PieCharts i les actualitza per les marcades per la posició de la variable índex.
     * @pre 0 <= index < topDataPool.size() && topDataPool.size() == leftDataPool.size() == rightDataPool.size()
     * @post Els tres PieCharts mostren les dades de les posicions índex de cada un dels pools de dades.
     */
    public void refreshData() {
        clearPieChart();
        clearRightPieChart();
        clearLeftPieChart();

        changePieChartTitle(mainTitle);

        //top
        for (Map.Entry<String, Integer> d : topDataPool.get(index).entrySet()) {
            addDataToPieChart(d.getKey(), d.getValue());
        }

        //left
        for (Map.Entry<String, Integer> d : leftDataPool.get(index).entrySet()) {
            addDataToLeftPieChart(d.getKey(), d.getValue());
        }

        //right
        for (Map.Entry<String, Integer> d : rightDataPool.get(index).entrySet()) {
            addDataToRightPieChart(d.getKey(), d.getValue());
        }
    }


    /** @brief Substitueix el pool de títols.
     * @pre newTitles != null.
     * @post variableTitlePool és newTitles i l'índex = 0.
     * @param newTitles Nou pool de títols.
     */
    public void updateVariableTitlePool(ArrayList<String> newTitles) {
        variableTitlePool = newTitles;
        index = 0;
    }

    /** @brief Substitueix el pool de dades del PieChart inferior esquerra.
     * @pre newData != null
     * @param newData Nou pool de dades pel PieChart inferior esquerra.
     * @post leftDataPool és newData i l'índex = 0.
     */
    public void updateLeftDataPool(ArrayList<TreeMap<String, Integer>> newData) {
        leftDataPool = newData;
        index = 0;
    }

    /** @brief Substitueix el pool de dades del PieChart inferior dret.
     * @pre newData != null
     * @param newData Nou pool de dades pel PieChart inferior dret.
     * @post rightDataPool és newData i l'índex = 0.
     */
    public void updateRightDataPool(ArrayList<TreeMap<String, Integer>> newData) {
        rightDataPool = newData;
        index = 0;
    }

    private final ObservableList<PieChart.Data> leftPieChartData; ///< Dades del PieChart inferior esquerra.
    private final ObservableList<PieChart.Data> rightPieChartData; ///< Dades del PieChart inferior dreta.

    private final PieChart leftPieChart = new PieChart(); ///< Objecte del PieChart inferior esquerra.
    private final PieChart rightPieChart = new PieChart(); ///< Objecte del PieChart inferior dreta.

    private static final int bottomPieChartWidth = 323; ///< Amplada dels PieCharts inferiors.
    private static final int bottomPieChartHeight = 283; ///< Alçada dels PieCharts inferiors.


    /** @brief Afegeix una dada al PieChart general.
     * @pre String s no buit. i > 0.
     * @param s String que conté la categoria del PieChart.
     * @param i Valor de la categoria s.
     * @post S'afegeix la categoria s amb valor i al PieChart general. Es redimensiona el PieChart i es col·loca a
     * l'esquerra de l'eix vertical (en última posició).
     */
    public void addDataToPieChart(String s, int i) {
        ensureFXThread(() -> {
            pieChartData.add( new PieChart.Data(s, i));
            pieChart.setData(pieChartData);
        });
    }

    /** @brief Afegeix una dada al PieChart inferior esquerre.
     * @pre String s no buit. i > 0.
     * @param s String que conté la categoria del PieChart.
     * @param i Valor de la categoria s.
     * @post S'afegeix la categoria s amb valor i al PieChart inferior esquerre. Es redimensiona el PieChart i es
     * col·loca a l'esquerra de l'eix vertical (en última posició).
     */
    public void addDataToLeftPieChart(String s, int i) {
        ensureFXThread(() -> {
            leftPieChartData.add( new PieChart.Data(s, i));
            leftPieChart.setData(leftPieChartData);
        });
    }

    /** @brief Afegeix una dada al PieChart inferior dret.
     * @pre String s no buit. i > 0.
     * @param s String que conté la categoria del PieChart.
     * @param i Valor de la categoria s.
     * @post S'afegeix la categoria s amb valor i al PieChart inferior dret. Es redimensiona el PieChart i es col·loca
     * a l'esquerra de l'eix vertical (en última posició).
     */
    public void addDataToRightPieChart(String s, int i) {
        ensureFXThread(() -> {
            rightPieChartData.add( new PieChart.Data(s, i));
            rightPieChart.setData(rightPieChartData);
        });
    }

    /** @brief Esborra totes les dades del PieChart general.
     * @pre True
     * @post Esborra totes les dades del PieChart general.
     */
    public void clearPieChart() {
        ensureFXThread(() -> {
            pieChartData.clear();
            pieChart.setData(pieChartData);
        });
    }

    /** @brief Esborra totes les dades del PieChart inferior esquerre.
     * @pre True
     * @post Esborra totes les dades del PieChart inferior esquerre.
     */
    public void clearLeftPieChart() {
        ensureFXThread(() -> {
            leftPieChartData.clear();
            leftPieChart.setData(leftPieChartData);
        });
    }

    /** @brief Esborra totes les dades del PieChart inferior dret.
     * @pre True
     * @post Esborra totes les dades del PieChart inferior dret.
     */
    public void clearRightPieChart() {
        ensureFXThread(() -> {
            rightPieChartData.clear();
            rightPieChart.setData(rightPieChartData);
        });
    }

    /** @brief Canvia el títol del PieChart.
     * @pre True
     * @param s Nou títol del PieChart.
     * @post El títol del PieChart general és "s".
     */
    public void changePieChartTitle(String s) {
        mainTitle = s;
        ensureFXThread(() -> {
            pieChart.setTitle(variableTitlePool.isEmpty() ? "" : s + "\n" + variableTitlePool.get(index));
        });
    }

    /** @brief Canvia el títol del PieChart.
     * @pre True
     * @param s Nou títol del PieChart.
     * @post El títol del PieChart inferior esquerre és "s".
     */
    public void changeLeftPieChartTitle(String s) {
        ensureFXThread(() -> {
            leftPieChart.setTitle(s);
        });
    }

    /** @brief Canvia el títol del PieChart.
     * @pre True
     * @param s Nou títol del PieChart.
     * @post El títol del PieChart inferior dret és "s".
     */
    public void changeRightPieChartTitle(String s) {
        ensureFXThread(() -> {
            rightPieChart.setTitle(s);
        });
    }

    /** @brief Mètode per obtenir l'objecte del PieChart.
     * @pre True
     * @return Retorna l'objecte del PieChart general.
     */
    public PieChart getPieChart() { return pieChart; }

    /** @brief Mètode per obtenir l'objecte del PieChart.
     * @pre True
     * @return Retorna l'objecte del PieChart inferior esquerre.
     */
    public PieChart getLeftPieChart() { return leftPieChart; }

    /** @brief Mètode per obtenir l'objecte del PieChart.
     * @pre True
     * @return Retorna l'objecte del PieChart inferior dret.
     */
    public PieChart getRightPieChart() { return rightPieChart; }

    public Button getForwardButton() { return forwardButton; }

    public Button getBackwardButton() { return backwardButton; }


}