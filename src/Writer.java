/**
 * @file Writer.java
 * @brief Classe Writer
 */

import java.util.Iterator;
import java.util.List;

/**
 * @class Writer
 * @brief Classe encarregada de mostrar per pantalla els diferents outputs del programa.
 * @author Aniol Juanola
 */
public class Writer {

    /**
     * @brief Neteja la pantalla de la consola.
     * @pre True.
     * @post Mira quin és el sistema operatiu i executa un procés de l'intèrpret de comandes corresponent amb l'ordre
     * de netejar la consola.
     */
    public static void clearConsole() {

        try
        {
            String os = System.getProperty("os.name").toLowerCase();
            if (os.contains("windows")) {

                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();

            }
            else {

                new ProcessBuilder("bash", "-c", "clear").inheritIO().start().waitFor();

            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    /**
     * @param iterator  Iterador que es recorrerà fins a arribar al final.
     * @param numerated True si es col·locarà numeració al principi, fals si no es vol numeració.
     * @brief Mostra enumeradament o no els ítems d'un contenidor iterable.
     * @pre Els objectes que s'iteren disposen de mètode toString().
     * @post Si numerated és true, es mostren els objectes numeradament (1. a, 2. b), altrament es mostren els objectes
     * (a, b). S'imprimeix un objecte per línia.
     * @return El nombre d'elements mostrats
     */
    public static int list(Iterator<?> iterator, boolean numerated) {
        int pos = 1;
        while (iterator.hasNext()) {
            System.out.println((numerated ? pos + ". " : "") + iterator.next());
            pos++;
        }
        System.out.print("\n");
        return pos - 1;
    }

    /**
     * @param tick Número de tick del qual es mostraran les dades.
     * @param data Informació del tick actual.
     * @brief Mostra per pantalla un resum de l'estat actual i acumulat.
     * @pre True
     * @post Es mostra per pantalla amb un resum de l'estat actual i acumulat amb el format de `data.summary()` .
     */
    public static void writeSummary(int tick, Data data) {

        System.out.println("Tick: " + tick);

        System.out.println(data.summary());

    }

    /**
     * @param tick     Número de tick del qual es mostraran les dades.
     * @param dataList Llista d'informació on cada dataList correspon a un Affectation.
     * @brief Mostra per pantalla tota la informació de l'estat actual i acumulat.
     * @pre True
     * @post Es mostra per pantalla cada Affectation en detall segons el format de `data.toString()`.
     */
    public static void writeLog(int tick, List<Data> dataList) {

        System.out.println("Tick: " + tick);

        for (Data d : dataList) {

            System.out.println(d);

        }

    }

    /**
     * @param sum_or_log 's' si s'està mostrant el resum o 'l' si s'està mostrant el log.
     * @brief Mostra el menú principal.
     * @pre tick >= 0 && last_tick >= 0 && tick <= last_tick.
     * @post Es mostra el menú principal corresponent a la situació i pàgina en què es troba l'usuari
     * (amb diferents opcions segons el que l'usuari ha de poder fer).
     * @throws IllegalStateException si sum_or_log no és ni 's' ni 'l'
     */
    public static void showMainMenu(char sum_or_log) {

        if (sum_or_log != 's' && sum_or_log != 'l')
            throw new IllegalStateException();

        System.out.println("Menú principal:");
        System.out.println("1. Establir filtres");
        System.out.println("2. Generar tick següent");
        System.out.println("3. Generar més d'un tick");
        System.out.println("4. Aplicar vacuna");
        System.out.println("5. Actualitzar un confinament");
        System.out.println("6. Veure " + (sum_or_log == 's' ? "log" : "resum"));
        System.out.println("7. Consultar llistes");
        System.out.println("8. Sortir");

    }

    /**
     * @brief Mostra el menú de llistar informació.
     * @pre True
     * @post Mostra per pantalla el menú de llistar informació.
     */
    public static void showListMenu() {

        System.out.println("Menú de llistes:");
        System.out.println("1. Llistar regions");
        System.out.println("2. Llistar virus");
        System.out.println("3. Llistar famílies");
        System.out.println("4. Llistar vacunes");
        System.out.println("0. Tornar al menú principal");

    }

    /**
     * @param filter Filtre de les dades.
     * @brief Mostra el menú de filtres.
     * @pre filter != null
     * @post Mostra el menú dels filtres, especificant quina opció s'ha escollit si s'escau.
     */
    public static void showFilterMenu(Filter filter) {

        System.out.println("Menú de filtres:");
        System.out.println("1. Modificar virus: " + (filter.virus() == null ? "sense filtre" : filter.virus().name()));
        System.out.println("2. Modificar regió: " + (filter.region() == null ? "sense filtre" : filter.region().name()));
        System.out.println("3. Esborrar filtres");
        System.out.println("0. Tornar al menú principal");

    }

    /** @brief Mostra el menú de confinaments
     * @pre True
     * @post Mostra el menú de confinaments
     */
    public static void showLockdownMenu() {

        System.out.println("Menú de confinaments:");
        System.out.println("1. Aplicar confinament");
        System.out.println("2. Extingir confinament");
        System.out.println("3. Aplicar tancament");
        System.out.println("4. Extingir tancament");
        System.out.println("0. Tornar al menú principal");

    }

}