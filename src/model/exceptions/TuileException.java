/**
 * Classe permettant de l'exception TuileException
 * @author Mathieu Valentin, Sébastien Viguier, Laurent Yu
 * @version 1.0
 */
package model.exceptions;

public class TuileException extends Exception {

    /**
     * Constructeur de la classe TuileException
     * @param s Message d'erreur
     */
    public TuileException(String s) {
        super(s);
    }
}
