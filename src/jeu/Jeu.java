package jeu;

import plateau.Plateau;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
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
        for (int i = 0; i < 15; i++) {
            if(nbJoueurs  == NB_JOUEURS_MIN){
                String rois;
                rois = randomRoiADeux();
                System.out.println(rois);
                allocateRoiA2(rois);

            } else {

            }
        }
    }

    private String randomRoiADeux(){
        int indexRoi1, indexRoi2;
        indexRoi1 = new Random().nextInt(4);
        do {
            indexRoi2 = new Random().nextInt(4);
        } while (indexRoi1 == indexRoi2);
        return Roi.getRoiInt(indexRoi1)+"/"+Roi.getRoiInt(indexRoi2);
    }


    private void allocateRoiA2(String rois){
        String roi1, roi2;
        int parser = rois.indexOf("/");
        roi1 = rois.substring(0, parser);
        roi2 = rois.substring(parser + 1);
        System.out.println(roi1);
        System.out.println(roi2);
        joueurs.put(new Joueur(Roi.getRoiCol(roi1), SCORE_DEFAUT), new Plateau(PETIT_PLATEAU));
        joueurs.put(new Joueur(Roi.getRoiCol(roi2), SCORE_DEFAUT), new Plateau(PETIT_PLATEAU));
    }

    private void jeuDe3a4() {

    }

    private void jeuA2() {
    }

}
