/**
 * @file Launcher.java
 * @brief Classe Launcher
 */

import com.brunomnsilva.smartgraph.containers.SmartGraphDemoContainer;
import javafx.application.Application;

import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.FileNotFoundException;


/**
 * @class Launcher
 * @brief Launcher de l'aplicació. S'encarrega de gestionar tant el Thread del JavaFX com el Thread
 * de l'execució principal del programa.
 * @author Aniol Juanola
 * @author Jordi Badia
 */
public class Launcher extends Application {

    private GraphChartData data; ///< Objecte GraphChartData que conté les estructures necessàries pel graph i els PieCharts de la GUI.

    private AnchorPane pieAnchorPane; ///< AnchorPane on es col·locaran els PieCharts.


    /** @brief Genera l'AnchorPane dels PieCharts.
     * @pre Els tres PieCharts de data s'han generat.
     * @post Genera i guarda l'AnchorPane dels PieCharts en la següent distribució:
     *   - A la part superior col·loca el PieChart general, que mostra els Immunes, els No Contagiats i els Afectats.
     *   - A la part inferior esquerra col·loca el PieChart que mostra dels afectats els que estan incubant,
     *     tenen símptomes o són asimptomàtics.
     *   - A la part inferior dreta col·loca el PieChart que mostra dels afectats els que estan en estat de latència,
     *     de ser contagiosos o de ser no contagiosos.
     */
    private void generatePieAnchorPane() {
        pieAnchorPane = new AnchorPane();
        pieAnchorPane.setPrefWidth(732);
        pieAnchorPane.setPrefHeight(755);
        data.getPieChart().setLayoutX(0);
        data.getPieChart().setLayoutY(0);
        data.getLeftPieChart().setLayoutX(20);
        data.getLeftPieChart().setLayoutY(397);
        data.getRightPieChart().setLayoutX(323 + 20);
        data.getRightPieChart().setLayoutY(397);
        data.getForwardButton().setLayoutX(10);
        data.getForwardButton().setLayoutY(337+(755-397)/2);
        data.getBackwardButton().setLayoutX(10);
        data.getBackwardButton().setLayoutY(397+(755-397)/2);
        pieAnchorPane.getChildren().addAll(data.getPieChart(), data.getLeftPieChart(), data.getRightPieChart(),
                data.getForwardButton(), data.getBackwardButton());
    }

    /**
     * @brief "Main" del programa. Segueix l'estructura d'una aplicació de JavaFX.
     * @param stage Stage del JavaFX on es col·locaran els objectes gràfics.
     * @throws Exception
     * @pre Application ha preparat un stage i s'ha cridat des de Main()
     * @post Prepara l'execució de tot el programa:
     *  - Genera tots els PieCharts amb el constructor de GraphChartData.
     *  - Obté el directori dels fitxers d'entrada del projecte.
     *  - Crea l'objecte Simulation a partir dels fitxers a llegir.
     *  - Crea el Filter i la UI per la interconnexió de la GUI i la CLI.
     *  - Afegeix els vèrtexs i les arestes al graf de regions.
     *  - Crea les subescenes i panells i genera l'escena de la interfície gràfica.
     *  - Prepara els esdeveniments que reaccionen als clics de l'usuari (pels PieCharts).
     *  - Crea el Thread de la interfície de la CLI i executa el loop per l'usuari.
     */
    @Override
    public void start(Stage stage) throws Exception {
        data = new GraphChartData();

        String input_directory = Interact.getInputFilesPath();

        ///< Simulació de tot el programa.
        Simulation simulation = readFiles(input_directory);

        ///< Objecte Filter.
        Filter filter = new Filter(simulation);

        ///< Mitjà de comunicació entre la GUI i la CLI.
        UI ui = new UI(data);

        data.addVertexs(simulation.regionList().iterator());
        data.addEdges(simulation.regionList().iterator());

        ///< Subescena del Graf.
        SubScene graphSubScene = new SubScene(new SmartGraphDemoContainer(data.graphView()), 702, 755); //passar a constants

        generatePieAnchorPane();

        ///< Subescena dels PieCharts.
        SubScene chartsSubScene = new SubScene(pieAnchorPane, 632, 755);

        ///< AnchorPane on es col·locaran les Subescenes.
        AnchorPane rootAnchorPane = new AnchorPane();
        chartsSubScene.setLayoutX(640);
        rootAnchorPane.getChildren().addAll(graphSubScene, chartsSubScene);

        ///< Creació de l'escena general de la interfície gràfica.
        Scene mainScene = new Scene(rootAnchorPane, 1280, 755);

        stage = new Stage(StageStyle.DECORATED);
        stage.setResizable(false);
        stage.setTitle("Graph i dades de l'últim tick"); //títol de la UI

        Tooltip container;
        final Label caption = new Label("");

        caption.setTextFill(Color.WHITESMOKE);
        caption.setStyle("-fx-font: 12 arial;");
        container = new Tooltip();
        container.setGraphic(caption);

        stage.setScene(mainScene);
        stage.show();

        data.graphView().init(); //inicialitza la visualització del graph

        CommandLine cli = new CommandLine(ui, simulation, filter);
        Thread cliThread = new Thread(cli::processCmd);
        cliThread.setDaemon(true);
        cliThread.start(); // es crea el thread de la part de la línia d'ordres.
        data.graphView().update(); // s'actualitza la visualització del graph abans d'acabar (necessari)
    }

    /** @brief Funció principal
     * @pre True
     * @post Executa tot el programa.
     * @param args Arguments de funcionament del programa. No en té cap.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /** @brief Genera els readers donat un directori de lectura.
     * @pre input_directory és un directori que existeix en el sistema i conté:
     *  - estatInicial.txt
     *  - regions.txt
     *  - vacunes.txt
     *  - virus.txt
     * @param input_directory Directori del sistema on s'esperen els fitxers d'entrada.
     * @return Retorna un objecte de simulació amb les dades entrades i a punt per simular.
     */
    private static Simulation readFiles(String input_directory) {

        VirusReader virusReader = null;
        RegionReader regionReader = null;
        InitialStateReader initialStateReader = null;
        VaccineReader vaccineReader = null;
        try {
            virusReader = new VirusReader(input_directory);
            regionReader = new RegionReader(input_directory);
            initialStateReader = new InitialStateReader(input_directory);
            vaccineReader = new VaccineReader(input_directory);
        } catch (FileNotFoundException e) {
            System.out.println("No s'ha pogut obrir un fitxer");
            System.exit(-1);
        }

        //Crea instància de simulació
        return new Simulation(virusReader.families(), virusReader.viruses(), regionReader.regions(),
                regionReader.neighbours(), initialStateReader.affectedGroups(), vaccineReader.vaccines());

    }
}
