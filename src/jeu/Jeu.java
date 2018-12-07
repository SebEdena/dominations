package jeu;

import plateau.Plateau;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Jeu {
    private static final int SCORE_DEFAUT = 0;
    private static final int NB_JOUEURS_MIN = 2;
    private static final int NB_JOUEURS_MAX = 4;
    private static final int GRAND_PLATEAU = 7;
    private static final int PETIT_PLATEAU = 5;

    private static Jeu instance;
    private static Scanner scan = new Scanner(System.in);
    private static Map<Joueur, Plateau> joueurs = new HashMap<>();
    private static int nbJoueurs = 0;

    private Jeu(){
        intitialisationJeu();
    }

    public static Jeu getInstance(){
        if (instance == null) {
            instance = new Jeu();
        }
        return instance;
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
        allocateRoi(nbJoueurs);
    }

    private void allocateRoi(int nb){
        for (int i = 0; i < nb; i++) {
            joueurs.put(new Joueur(Roi.getRoiInt(i), SCORE_DEFAUT), new Plateau(PETIT_PLATEAU));
            System.out.println(Roi.getRoiInt(i));
        }
    }

}
