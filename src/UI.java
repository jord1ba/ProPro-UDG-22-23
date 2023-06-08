/**
 * @file UI.java
 * @brief Classe UI
 */

import java.util.ArrayList;
import java.util.TreeMap;


/** @class UI
 * @brief Classe comunicadora encarregada d'actualitzar la interfície gràfica del programa.
 * @author Jordi Badia
 */
public class UI {

    // Private attributes

    private final GraphChartData data; ///< Interfície gràfica.



    // Constructors

    /** @brief Constructor principal.
     * @pre True.
     * @post this.data = \p data.
     * @param data Interfície gràfica a actualitzar.
     */
    public UI(GraphChartData data) {
        this.data = data;
    }


    /**
     * @brief Donades tres llistes de dades pels Charts i el conjunt de títolsle, actualitza cadascun dels PieCharts amb les dades corresponents
     * i habilita els botons de -> i de <- de la interfície gràfica per iterar circularment al llarg de les dades
     * enviades. Col·loca els tres charts a la primera posició de cadascuna de les arrays.
     * @pre top.size() == left.size() == right.size() && top != null && left != null && right != null
     * @param titles Array de títols
     * @param top Array de dades del PieChart superior.
     * @param left Array de dades del PieChart inferior esquerra.
     * @param right Array de dades del PieChart inferior dret.
     * @post S'han actualitzat les dades de GraphChartData i els PieCharts mostren les dades de la primera posició de
     * cadascuna de les arrays. Els botons de -> i de <- de la interfície iteren per les dades.
     */
    public void updateChartsData(ArrayList<String> titles, ArrayList<TreeMap<String, Integer>> top, ArrayList<TreeMap<String,Integer>> left, ArrayList<TreeMap<String, Integer>> right) {
        data.updateVariableTitlePool(titles);
        data.updateTopDataPool(top);
        data.updateLeftDataPool(left);
        data.updateRightDataPool(right);
        data.refreshData();
    }

    /** @brief Canvia el títol del PieChart central.
     * @pre True.
     * @post El títol del PieChart passa a ser \p s.
     * @param s Nou títol del PieChart.
     */
    public void changePieChartTitle(String s) {

        data.changePieChartTitle(s);

    }

    /** @brief Canvia el títol del PieChart esquerre.
     * @pre True.
     * @post El títol del PieChart passa a ser \p s.
     * @param s Nou títol del PieChart.
     */
    public void changeLeftPieChartTitle(String s) {

        data.changeLeftPieChartTitle(s);

    }

    /** @brief Canvia el títol del PieChart dret.
     * @pre True.
     * @post El títol del PieChart passa a ser \p s.
     * @param s Nou títol del PieChart.
     */
    public void changeRightPieChartTitle(String s) {
        data.changeRightPieChartTitle(s);
    }


    /** @brief Tanca visualment una frontera entre dues regions.
     * @pre \p r1 i \p r2 han de ser veïns.
     * @post La aresta queda de color vermell.
     * @param r1 Primera regió.
     * @param r2 Segona regió.
     */
    public void lockEdge(Region r1, Region r2) {
        data.lockEdge(r1, r2);
    }

    /** @brief Obre una frontera entre dues regions.
     * @pre \p r1 i \p r2 han de ser veïns.
     * @post La aresta queda de color blau.
     * @param r1 Primera regió.
     * @param r2 Segona regió.
     */
    public void unlockEdge(Region r1, Region r2) {
        data.unlockEdge(r1, r2);
    }

    /** @brief Mostra visualment un confinament.
     * @pre True.
     * @post La regió queda de color vermell.
     * @param r1 Regió en la que aplicar el confinament.
     */
    public void hardLock(Region r1) {
        data.hardLock(r1);
    }

    /** @brief Mostra visualment un desconfinament.
     * @pre True.
     * @post La regió queda de color verd.
     * @param r1 Regió en la que desfer el confinament.
     */
    public void hardUnlock(Region r1) {
        data.hardUnlock(r1);
    }

}
