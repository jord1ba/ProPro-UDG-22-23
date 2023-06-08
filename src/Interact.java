/**
 * @file Interact.java
 * @brief Classe Interact
 */

import java.util.Scanner;
import java.util.List;


/**
 * @class Interact
 * @brief Classe encarregada d'interactuar amb l'usuari per consola. Mostra dades si cal i llegeix l'input de l'usuari.
 * @author Jordi Badia
 */
public class Interact {

    // Private attributes
    private static final Scanner stdin = new Scanner(System.in); ///< Canal de lectura per teclat.



    // No hi ha constructors, ja que la classe és estàtica i en cap moment es realitzen instàncies



    // Funcions públiques

    /** @brief Demana, llegeix i retorna una ruta al directori on hi ha els fitxers d'entrada.
     * @pre True.
     * @return Un String que conté la entrada de l'usuari.
     */
    public static String getInputFilesPath() {

        System.out.println("Si us plau, entreu el directori on es troben els fitxers d'entrada:");
        return stdin.nextLine();

    }

    /** @brief Demana a l'usuari que triï una opció (o entri un enter) per mitjà de \p text, els llegeix i el retorna.
     * Si l'enter entrat per l'usuari no compleix els requisits establerts pels paràmetres (o de format o és <0), se
     * seguirà demanant fins que es realitzi una entrada vàlida.
     * @pre \p max_option > 0 si \p !allow_zero o \p max_option >= 0 si \p allow_zero.
     * @param text Text que es mostrarà per consola abans de realitzar la lectura.
     * @param max_option Enter màxim que podrà entrar l'usuari.
     * @param allow_zero Si és true el nombre mínim permès serà 0, en cas contrari serà 1.
     * @return L'enter entrat per l'usuari.
     */
    public static int getOption(String text, int max_option, boolean allow_zero) {

        int option;

        try {

            System.out.print(text);
            option = Integer.parseInt(stdin.nextLine());
            if ((!allow_zero && option == 0) || option < 0 || option > max_option) throw new NumberFormatException();

        } catch (NumberFormatException e) {
            System.out.print("[No existeix aquesta opció] ");
            return getOption(text, max_option, allow_zero);
        }

        return option;

    }

    /** @brief Demana a l'usuari que entri una ràtio (o entri un float) per mitjà de \p text i el retorna.
     * @pre \p max_option >= 0. Si el float entrat per l'usuari no compleix els requisits establerts pels paràmetres
     * (o de format o és <0), se seguirà demanant fins que es realitzi una entrada vàlida.
     * @param text Text que es mostrarà per consola abans de realitzar la lectura
     * @param max_option Float màxim que podrà entrar l'usuari.
     * @return El float entrat per l'usuari
     */
    public static float getRate(String text, float max_option) {

        float option;

        try {

            System.out.print(text);
            option = Float.parseFloat(stdin.nextLine());
            if (option < 0 || option > max_option) throw new NumberFormatException();

        } catch (NumberFormatException e) {
            System.out.println("[Format o valor incorrecte] ");
            return getRate(text, max_option);
        }

        return option;

    }

    /** @brief Mostra una llista d'elements numerada i en retorna el corresponent al número entrat per l'usuari.
     * @pre \p list amb un element o més.
     * @param list Llista d'elements que es mostraran numerats per pantalla, i dels quals un pot ser escollit per
     * l'usuari.
     * @param text Text que es mostrarà per consola abans de realitzar la lectura
     * @param null_on_zero Si és true, s'acceptarà l'input 0 i es retornarà null si s'entra. Si és false, no s'acceptarà
     * el 0 com a input vàlid.
     * @return Si \p null_on_zero == true null si l'opció entrada per l'usuari és 0, altrament retorna la referència a
     * l'element corresponent al número entrat per l'usuari. Si \p null_on_zero == false no s'acceptarà el 0 com a
     * entrada vàlida.
     * @param <T> Pensat per ser o bé Virus, o bé una Region, o bé una Vaccine, però funcionaria per qualsevol llista
     * de qualsevol mena.
     */
    public static <T> T getElement(List<T> list, String text, boolean null_on_zero) {

        int total_elements = Writer.list(list.iterator(), true);
        int index = Interact.getOption(text, total_elements, null_on_zero);

        return index == 0 ? null : list.get(index - 1);

    }

}
