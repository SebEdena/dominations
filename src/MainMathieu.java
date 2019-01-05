import jeu.Jeu;

public class MainMathieu {

    public static void main(String[] args) {
        Jeu jeu  = Jeu.getInstance();
        jeu.débutPartie();
        jeu.pioche();
        jeu.afficherPioche();
        jeu.tourDeJeu();

    }

}
