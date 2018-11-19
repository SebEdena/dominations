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
    private int nbJoueurs = 0;

    public Jeu(){
        intitialisationJeu();
    }

    private void intitialisationJeu() {
        do {
            try{
                System.out.println("Nombre de joueurs ? (2 à 4)");
                nbJoueurs = Integer.parseInt(scan.next());
            } catch (NumberFormatException e){
                System.out.println("Erreur de saisie");
            }
        } while(nbJoueurs < NB_JOUEURS_MIN || nbJoueurs > NB_JOUEURS_MAX);
        for (int i = 0; i < nbJoueurs; i++) {
            Roi roi = setCouleurRoi();
            if(nbJoueurs < 3)
                joueurs.put(new Joueur(roi, SCORE_DEFAUT), new Plateau(GRAND_PLATEAU));
            else joueurs.put(new Joueur(roi, SCORE_DEFAUT), new Plateau(PETIT_PLATEAU));
        }
    }

    private Roi setCouleurRoi() {
        // TO DO
        return null;
    }

    private void jeuDe3a4() {

    }

    private void jeuA2() {
    }

}
