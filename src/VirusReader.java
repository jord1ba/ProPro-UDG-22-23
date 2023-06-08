/**
 * @file VirusReader.java
 * @brief Classe VirusReader
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.ArrayList;


/** @class VirusReader
 * @brief Classe encarregada de realitzar la lectura i organització dels tipus primitius de les dades del fitxer que
 * conté la informació dels virus (FILE_NAME).
 * @author Jordi Badia
 */
public class VirusReader {

    // Public storage classes

    /** @class FamilyData
     * @brief Inner class de VirusReader que emmagatzema els tipus primitius de les dades d'una família de virus.
     */
    public static class FamilyData {

        public String name; ///< Nom
        public float mutate_max_var; ///< Coeficient màxim de variació en mutar

    }

    /** @class VirusData
     * @brief Inner class de VirusReader que emmagatzema els tipus primitius de les dades d'un virus.
     */
    public static class VirusData {

        public String name; ///< Nom.
        public String type; ///< Tipus (AND/ARN).
        public String family; ///< Nom de la família.
        public float fall_sick_probability; ///< Probabilitat d'emmalaltir.
        public int incubation_time; ///< Durada de la incubació.
        public int latency_time; ///< Durada de la latència.
        public int symptom_duration; ///< Durada dels símptomes (en cas de desenvolupar-los).
        public int infection_duration; ///< Durada de la infecció.
        public int immunity_duration; ///< Durada de la immunitat (un cop la infecció i els símptomes acabin).
        public float death_rate; ///< Taxa de mortalitat.
        public float spread_rate; ///< Taxa de contagi.
        public float mutate_error_probability; ///< Probabilitat de mutar per error.
        public float mutate_family_probability; ///< Probabilitat de mutar per coincidència.

    }



    // Constants

    private static final String FILE_NAME = "virus.txt"; ///< Nom de l'arxiu d'on llegir les dades.



    // Private attributes

    private final ArrayList<FamilyData> _families; ///< Llista d'objectes amb els tipus primitius llegits de famílies.
    private final ArrayList<VirusData> _viruses; ///< Llista d'objectes amb els tipus primitius llegits de virus.



    // Constructors

    /** @brief Llegeix el fitxer FILE_NAME i guarda les dades llegides.
     * @pre Un \p path a un directori que contingui un fitxer anomenat \p FILE_NAME amb un format correcte.
     * @post \p _families i \p _viruses contenen les dades llegides de FILE_NAME.
     * @param path La ruta a un directori que contingui el fitxer FILE_NAME.
     * @throws FileNotFoundException si no existeix el fitxer FILE_NAME a la ruta \p path.
     */
    public VirusReader(String path) throws FileNotFoundException {

        Scanner read = new Scanner(new File(path + File.separator + FILE_NAME));

        _families = new ArrayList<>();
        _viruses = new ArrayList<>();

        read.nextLine(); //Reads the section title ("families")

        readFamilies(read);
        readViruses(read);

    }



    // Funcions públiques

    /** @brief Getter de la llista de dades de famílies de virus.
     * @pre True.
     * @return La llista de dades de famílies de virus.
     */
    public ArrayList<FamilyData> families() {

        return _families;

    }

    /** @brief Getter de la llista de dades de virus.
     * @pre True.
     * @return La llista de dades de virus.
     */
    public ArrayList<VirusData> viruses() {

        return _viruses;

    }



    //Funcions privades

    /** @brief Llegeix a través de \p read la informació sobre les famílies fins a llegir "virus".
     * @pre El format de l'arxiu és correcte.
     * @post \p _families conté objectes amb les dades llegides organitzades.
     * @param read Scanner a l'inici del fitxer FILE_NAME.
     */
    private void readFamilies(Scanner read) {

        FamilyData new_element = new FamilyData();

        loop: while (read.hasNextLine()) {

            String line = read.nextLine();
            if (line.isBlank() || line.trim().charAt(0) == '#') continue;
            String[] line_elements = line.split(" ", 3);

            switch (line_elements[0].trim()) {

                case "nom" -> new_element.name = line_elements[1];
                case "tpc_maxim_variacio" -> new_element.mutate_max_var = Float.parseFloat(line_elements[1])/100;
                case "*" -> {

                    _families.add(new_element);
                    new_element = new FamilyData();

                }
                case "virus" -> { break loop; }

                default -> throw new InputMismatchException("Lectura inesperada: " + line_elements[0]);

            }

        }

    }

    /** @brief Llegeix a través de \p read la informació sobre els virus fins a acabar el fitxer.
     * @pre El format de l'arxiu és correcte i l'últim que ha llegit \p read és "virus".
     * @post \p _viruses conté objectes amb els virus organitzats.
     * @param read Scanner al fitxer FILE_NAME.
     */
    private void readViruses(Scanner read) {

        VirusData new_element = new VirusData();

        while (read.hasNextLine()) {

            String line = read.nextLine();
            if (line.isBlank() || line.trim().charAt(0) == '#') continue;
            String[] line_elements = line.split(" ", 3);

            switch (line_elements[0]) { //Llamp de switch

                case "nom" -> new_element.name = line_elements[1];
                case "tipus" -> new_element.type = line_elements[1];
                case "familia" -> new_element.family = line_elements[1];
                case "prob_malaltia" -> new_element.fall_sick_probability = Float.parseFloat(line_elements[1]);
                case "incubacio" -> new_element.incubation_time = Integer.parseInt(line_elements[1]);
                case "latencia" -> new_element.latency_time = Integer.parseInt(line_elements[1]);
                case "durada_malaltia" -> new_element.symptom_duration = Integer.parseInt(line_elements[1]);
                case "durada_contagi" -> new_element.infection_duration = Integer.parseInt(line_elements[1]);
                case "durada_immunitat" -> new_element.immunity_duration = Integer.parseInt(line_elements[1]);
                case "mortalitat" -> new_element.death_rate = Float.parseFloat(line_elements[1]);
                case "taxa_contagi" -> new_element.spread_rate = Float.parseFloat(line_elements[1]);
                case "prob_mutacio_copia" -> new_element.mutate_error_probability = Float.parseFloat(line_elements[1]);
                case "prob_mutacio_coincidencia" ->
                        new_element.mutate_family_probability = Float.parseFloat(line_elements[1]);
                case "*" -> {

                    _viruses.add(new_element);
                    new_element = new VirusData();

                }

                default -> throw new InputMismatchException("Lectura inesperada: " + line_elements[0]);

            }

        }

    }

}
