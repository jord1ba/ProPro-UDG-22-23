/**
 * @file RegionReader.java
 * @brief Classe RegionReader
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.ArrayList;


/** @class RegionReader
 * @brief Classe encarregada de realitzar la lectura i organització dels tipus primitius de les dades del fitxer que
 * conté la informació de les regions (FILE_NAME).
 * @author Jordi Badia
 */
public class RegionReader {

    // Public storage classes

    /** @class RegionData
     * @brief Inner class de RegionReader que emmagatzema els tipus primitius de les dades d'una regió.
     */
    public static class RegionData {

        public String name; ///< Nom.
        public int inhabitants; ///< Total d'habitants.
        public int inside_mobility; ///< Mobilitat interna.

    }

    /** @class NeighbourData
     * @brief Inner class de RegionReader que emmagatzema els tipus primitius de les dades d'una relació de veïnatge.
     */
    public static class NeighbourData {

        public String region_name; ///< Nom de la regió de la qual es guarden les fronteres.
        public ArrayList<String> neighbour; ///< Nom de la regió fronterera.
        public ArrayList<Float> flow_rate; ///< Mobilitat entre \p region_name i l'índex analog de \p neighbour.

        /** @brief Inicialitza les llistes de veïns i mobilitats a llistes buides.
         * @pre True.
         * @post \p neighbour i \p flow_rate son llistes buides.
         */
        public NeighbourData() {

            neighbour = new ArrayList<>();
            flow_rate = new ArrayList<>();

        }

    }



    // Constants

    private static final String FILE_NAME = "regions.txt"; ///< Nom de l'arxiu d'on llegir les dades.



    // Private attributes

    private final ArrayList<RegionData> _regions; ///< Llista d'objectes amb els tipus primitius llegits de les regions.
    private final ArrayList<NeighbourData> _neighbours;
    ///< Llista d'objectes amb els tipus primitius llegits de les relacions de veïnatge.



    // Constructors

    /** @brief Llegeix el fitxer FILE_NAME i guarda les dades llegides.
     * @pre Un \p path a un directori que contingui un fitxer anomenat \p FILE_NAME amb un format correcte.
     * @post \p _regions i \p _neighbours contenen les dades llegides de FILE_NAME.
     * @param path La ruta a un directori que contingui el fitxer FILE_NAME.
     * @throws FileNotFoundException si no existeix el fitxer FILE_NAME a la ruta \p path.
     */
    public RegionReader(String path) throws FileNotFoundException {

        Scanner read = new Scanner(new File(path + File.separator + FILE_NAME));

        _regions = new ArrayList<>();
        _neighbours = new ArrayList<>();

        read.nextLine(); // Reads the section title ("regions")

        readRegions(read);
        readNeighbours(read);

    }



    // Funcions públiques

    /** @brief Getter de la llista de dades de regions.
     * @pre True.
     * @return La llista de dades de regions.
     */
    public ArrayList<RegionData> regions() {
        return _regions;
    }

    /** @brief Getter de la llista de dades de relacions de veïnatge.
     * @pre True.
     * @return La llista de dades de relacions de veïnatge.
     */
    public ArrayList<NeighbourData> neighbours() {
        return _neighbours;
    }



    // Funcions privades

    /** @brief Llegeix a través de \p read la informació sobre les regions fins a llegir "limits_i_mobilitat".
     * @pre El format de l'arxiu és el correcte.
     * @post \p _region conté objectes amb les dades llegides organitzades.
     * @param read Scanner a l'inici del fitxer FILE_NAME.
     */
    private void readRegions(Scanner read) {

        RegionData new_element = new RegionData();

        loop:
        while (read.hasNextLine()) {

            String line = read.nextLine();

            String[] line_elements = line.split(" ", 3);

            switch (line_elements[0].trim()) {

                case "nom" -> new_element.name = line_elements[1];
                case "habitants" -> new_element.inhabitants = Integer.parseInt(line_elements[1]);
                case "mob_interna" -> new_element.inside_mobility = Integer.parseInt(line_elements[1]);
                case "*" -> {

                    _regions.add(new_element);
                    new_element = new RegionData();

                }
                case "limits_i_mobilitat" -> { break loop; }

                default -> throw new InputMismatchException("Lectura inesperada: " + line_elements[0]);

            }

        }

    }

    /** @brief Llegeix a través de \p read la informació sobre les relacions de veïnatge fins a acabar el fitxer.
     * @pre El format de l'arxiu és correcte i l'últim que ha llegit \p read és "limits_i_mobilitat".
     * @post \p _neighbor conté objectes amb les relacions de veïnatge organitzades.
     * @param read Scanner al fitxer FILE_NAME.
     */
    private void readNeighbours(Scanner read) {

        NeighbourData new_element = new NeighbourData();

        while (read.hasNextLine()) {

            String line = read.nextLine();
            if (line.isBlank() || line.trim().charAt(0) == '#') continue;
            String[] line_elements = line.split(" ", 2);

            new_element.region_name = line_elements[0]; //Guarda el poble sobre el que donarem els veins

            while (read.hasNextLine() && !line.trim().equals("*")) {

                line = read.nextLine();

                if (line.trim().equals("*")) {

                    _neighbours.add(new_element);
                    new_element = new NeighbourData();

                }
                else {

                    line_elements = line.split(" ", 3);

                    new_element.neighbour.add(line_elements[0]);
                    new_element.flow_rate.add(Float.parseFloat(line_elements[1].replace('%','\0')) / 100);

                }

            }

        }

    }

}