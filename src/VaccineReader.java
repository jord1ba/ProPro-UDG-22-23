/**
 * @file VaccineReader.java
 * @brief Classe VaccineReader
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.ArrayList;


/**
 * @class VaccineReader
 * @brief Classe encarregada de realitzar la lectura i organització dels tipus primitius de les dades del fitxer que
 * conté la informació de les vacunes (FILE_NAME).
 * @author Jordi Badia
 */
public class VaccineReader {

    // Public storage class

    /** @class VaccineData
     * @brief Inner class de VaccineReader que emmagatzema els tipus primitius de les dades d'una vacuna.
     */
    public static class VaccineData {

        public String name; ///< Nom.
        public String type; ///< Tipus.
        public String virus; ///< Virus al que cura.
        public float effectiveness_ratio; ///< Ràtio d'efectivitat.
        public int delay; ///< Temps que triga la vacuna a fer efecte.
        public int effect_duration; ///< Duració de l'efecte.
        public float mortality_reduction; ///< Reducció en la taxa de mortalitat.
        public float duration_reduction; ///< Reducció en la duració.
        public float sick_probability_reduction; ///< Reducció en la probabilitat de desenvolupar la malaltia.
        public float contagiousness_reduction; ///< Reducció en el ràtio de contagi.

    }



    // Constants

    private static final String FILE_NAME = "vacunes.txt"; ///< Nom de l'arxiu d'on llegir les dades.



    // Private attributes

    private final ArrayList<VaccineData> _vaccines; ///< Llista d'objectes amb tipus primitius llegits de les vacunes.



    // Constructors

    /** @brief Llegeix el fitxer FILE_NAME i guarda les dades llegides.
     * @pre Un \p path a un directori qye contingui un fitxer anomenat \p FILE_NAME amb un format correcte.
     * @post \p _vaccines contenen les dades llegides de FILE_NAME.
     * @param path La ruta a un directori que contingui el fitxer FILE_NAME.
     * @throws FileNotFoundException si no existeix el fitxer FILE_NAME a la ruta \p path.
     */
    public VaccineReader(String path) throws FileNotFoundException {

        Scanner read = new Scanner(new File(path + File.separator + FILE_NAME));

        _vaccines = new ArrayList<>();

        read.nextLine(); //Reads the section title ("vacunes")

        readVaccines(read);

    }



    // Funcions públiques

    /** @brief Getter de la llista de dades de vacunes.
     * @pre True.
     * @return La llista de dades de vacunes.
     */
    public ArrayList<VaccineData> vaccines() {
        return _vaccines;
    }



    // Funcions privades

    /** @brief Llegeix a través de \p read la informació sobre les regions fins a acabar el fitxer.
     * @pre El format de l'arxiu és el correcte.
     * @post \p _vaccines conté objectes amb les dades llegides i organitzades.
     * @param read Scanner a l'inici del fitxer FILE_NAME.
     */
    private void readVaccines(Scanner read) {

        VaccineData new_element = new VaccineData();

        while (read.hasNextLine()) {

            String line = read.nextLine();
            if (line.isBlank() || line.trim().charAt(0) == '#') continue;
            String[] line_elements = line.split(" ", 3);

            switch (line_elements[0].trim()) {

                case "nom" -> new_element.name = line_elements[1];
                case "tipus" -> new_element.type = line_elements[1];
                case "virus_desti" -> new_element.virus = line_elements[1];
                case "efectivitat" -> new_element.effectiveness_ratio = Float.parseFloat(line_elements[1])/100;
                case "temps_activacio" -> new_element.delay = Integer.parseInt(line_elements[1]);
                case "durada" -> new_element.effect_duration = Integer.parseInt(line_elements[1]);

                case "tpc_reduccio_mortalitat" -> new_element.mortality_reduction =
                        Float.parseFloat(line_elements[1])/100;
                case "tpc_reduccio_durada" -> new_element.duration_reduction =
                        Float.parseFloat(line_elements[1])/100;
                case "tpc_reduccio_malaltia" -> new_element.sick_probability_reduction =
                        Float.parseFloat(line_elements[1])/100;
                case "tpc_reduccio_contagi" -> new_element.contagiousness_reduction =
                        Float.parseFloat(line_elements[1])/100;

                case "*" -> {

                    _vaccines.add(new_element);
                    new_element = new VaccineData();

                }

                default -> throw new InputMismatchException("Lectura inesperada: " + line_elements[0]);

            }

        }

    }

}
