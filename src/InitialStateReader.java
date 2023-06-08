/**
 * @file InitialStateReader.java
 * @brief Classe InitialStateReader
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.ArrayList;


/**
 * @class InitialStateReader
 * @brief Classe encarregada de realitzar la lectura i organització del fitxer que conté la informació dels estats
 * inicials (FILE_NAME).
 * @author Jordi Badia
 */
public class InitialStateReader {

    // Public storage class

    /** @class InitialStateData
     * @brief Inner class d'InitialStateReader que emmagatzema els tipus primitius de les dades d'un estat inicial.
     */
    public static class InitialStateData {

        public String region_name; ///< Nom de la regió on es determina l'estat inicial.
        public ArrayList<String> virus_affectation; ///< Nom del virus que causa l'afectació.
        public ArrayList<Float> percentage_affectation;
        ///< Percentatge de la població afectada pel virus amb índex analog de \p virus_affectation.

        /** @brief Inicialitza les llistes de virus que causen afectacions i proporcions.
         * @pre True.
         * @post \p region_name i \p percentage_affectation son llistes buides.
         */
        public InitialStateData() {

            virus_affectation = new ArrayList<>();
            percentage_affectation = new ArrayList<>();

        }

    }



    // Constants

    private static final String FILE_NAME = "estatInicial.txt"; ///< Nom de l'arxiu d'on llegir les dades.



    // Private attributes

    private final ArrayList<InitialStateData> _initial_states;
    ///< Llista d'objectes amb el tipus primitius llegits dels estats inicials.



    // Constructors

    /** @brief Llegeix el fitxer FILENAME i guarda les dades llegides.
     * @pre Un \p path a un directori que contingui un fitxer anomenat \p FILE_NAME amb un format correcte.
     * @post \p _initial_states contenen les dades llegides de FILE_NAME.
     * @param path La ruta a un directori que contingui el fitxer FILE_NAME.
     * @throws FileNotFoundException si no existeix el fitxer FILE_NAME a la ruta \p path.
     */
    public InitialStateReader(String path) throws FileNotFoundException {

        Scanner read = new Scanner(new File(path + File.separator + "estatInicial.txt"));

        _initial_states = new ArrayList<>();

        readInitialState(read);

    }



    // Funcions públiques

    /** @brief Getter de la llista de dades dels estats inicials.
     * @pre True.
     * @return La llista de dades dels estats inicials.
     */
    public ArrayList<InitialStateData> affectedGroups() {
        return _initial_states;
    }



    // Funcions privades

    /** @brief Llegeix a través de \p read la informació sobre els estats inicials fins a acabar el fitxer.
     * @pre El format de l'arxiu és correcte.
     * @post \p _initial_states conté objectes amb els estats inicials organitzats.
     * @param read Scanner al fitxer FILE_NAME.
     */
    private void readInitialState(Scanner read) {

        InitialStateData new_element = new InitialStateData();

        while (read.hasNextLine()) {

            String line = read.nextLine();
            if (line.isBlank() || line.trim().charAt(0) == '#') continue;
            String[] line_elements = line.split(" ", 3);

            switch (line_elements[0].trim()) {

                case "nom_virus" -> new_element.virus_affectation.add(line_elements[1]);
                case "p_malalts" -> new_element.percentage_affectation.add(Float.parseFloat(line_elements[1])/100);
                case "regio" -> new_element.region_name = line_elements[1];
                case "virus_presents" -> { continue; }
                case "*" -> {

                    _initial_states.add(new_element);
                    new_element = new InitialStateData();

                }

                default -> throw new InputMismatchException("Lectura inesperada: " + line_elements[0]);

            }

        }

    }

}
