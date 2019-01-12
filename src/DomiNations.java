import jeu.Jeu;

public class DomiNations {

    /**
     * Methode permettant de lancer le jeu en mode console
     * @param args Liste des paramètres transmis lors de l'éxécution de la methode
     * @see Jeu#getInstance
     * @see Jeu#tourDeJeu
     */
    public static void main(String[] args) {
        Jeu jeu  = Jeu.getInstance();
        jeu.tourDeJeu();

    }

}
