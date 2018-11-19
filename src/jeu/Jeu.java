package jeu;

import plateau.Plateau;

import java.util.Map;
import java.util.Scanner;

public class Jeu {
    private static final int SCORE_DEFAUT = 0;
    private static final int NB_JOUEURS_MIN = 2;
    private static final int NB_JOUEURS_MAX = 4;

    private static final int GRAND_PLATEAU = 7;
    private static final int PETIT_PLATEAU = 5;

    private static Scanner scan = new Scanner(System.in);

    private Map<Joueur, Plateau> joueurs;
    private int nbJoueurs;

    private Jeu(){
        int nb = intitialisationJeu();
    }

    private int intitialisationJeu() {
        int nombre = 0;
        do {
            try{
                System.out.println("Nombre de joueurs ? (2 à 4)");
                nombre = Integer.parseInt(scan.next());
            } catch (Exception e){
                System.out.println(e.getMessage());
            }
        } while(nombre < NB_JOUEURS_MIN || nombre > NB_JOUEURS_MAX);
        return nbJoueurs;
    }

    private void jeuDe3a4() {

    }

    private void jeuA2() {
    }

}
