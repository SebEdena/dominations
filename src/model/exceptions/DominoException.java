/**
 * Classe permettant de l'exception DomiException
 * @author Mathieu Valentin, Sébastien Viguier, Laurent Yu
 * @version 1.0
 */
package model.exceptions;

public class DominoException extends Exception {
    /**
     * Constructeur de la classe DominoException
     * @param s Message d'erreur
     */
    public DominoException(String s) {
        super(s);
    }
}
