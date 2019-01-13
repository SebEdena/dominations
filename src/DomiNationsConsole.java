/**
 * Classe permettant de lancer le jeu en mode console
 * @author Mathieu Valentin, Sébastien Viguier, Laurent Yu
 * @version 1.0
 */
import model.jeu.Jeu;

public class DomiNationsConsole {

    /**
     * Methode permettant de lancer le model.jeu en mode console
     * @param args Liste des paramètres transmis lors de l'éxécution de la methode
     * @see Jeu#getInstance
     * @see Jeu#tourDeJeu
     */
    public static void main(String[] args) {
        Jeu jeu  = Jeu.getInstance();
        jeu.tourDeJeu();

    }

}
