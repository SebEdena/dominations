package jeu;

import com.sun.xml.internal.bind.v2.model.core.ID;
import exceptions.DominoException;
import exceptions.TuileException;
import javafx.util.Pair;
import plateau.*;
import util.CSVParser;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

public class Jeu {
    private static final int SCORE_DEFAUT = 0;
    private static final int NB_JOUEURS_MIN = 2;
    private static final int NB_JOUEURS_MAX = 4;
    private static final ModeJeu MODE_JEU_DEFAUT = ModeJeu.STANDARD;

    private static Jeu instance;

    private Scanner scan = new Scanner(System.in);
    private List<IDomino> dominosDebut;
    private Partie partie;

    private Jeu(){
        try {
            intitialisationJeu();
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement du fichier .csv," +
                    " veuillez recommencer.");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
    }

    public static Jeu getInstance(){
        if (instance == null) {
            instance = new Jeu();
        }
        return instance;
    }

    private void intitialisationJeu() throws Exception {
        System.out.println("Bienvenue sur Dominations quel mode de jeu souhaitez-vous ?");
        for(int compteur = 0; compteur < ModeJeu.values().length; compteur++)
        {
            System.out.println(""+ (compteur) + " - " + ModeJeu.values()[compteur].toString());
        }
        int modeJeu = 0;
        do {
            try{
                modeJeu = Integer.parseInt(scan.next());
            } catch (NumberFormatException e){
                System.out.println("Erreur de saisie");
            }
            if(modeJeu < 0 || modeJeu > ModeJeu.values().length - 1)
            {
                System.out.println("Veuillez saisir un bon numéro");
            }
        } while(modeJeu < 0 || modeJeu > ModeJeu.values().length - 1);
        System.out.println("Vous avez sélectionné le mode de jeu : " + ModeJeu.values()[modeJeu].toString());
        int nbJoueurs = 0;
        do {
            try{
                System.out.println("Nombre de joueurs ? (2 à 4)");
                nbJoueurs = Integer.parseInt(scan.next());
            } catch (NumberFormatException e){
                System.out.println("Erreur de saisie");
            }
        } while(nbJoueurs < NB_JOUEURS_MIN || nbJoueurs > NB_JOUEURS_MAX);
        NbJoueur paramJeu = NbJoueur.getParamsJeu(nbJoueurs);
        List<Joueur> joueurs = allocateRoi(paramJeu, ModeJeu.values()[modeJeu]);
        dominosDebut = chargementDominos("./dominos.csv");
        partie = new Partie(joueurs, dominosDebut, paramJeu, ModeJeu.values()[modeJeu]);
    }

    private List<Joueur> allocateRoi(NbJoueur nbJoueur, ModeJeu modeJeu) throws Exception {
        List<Joueur> joueurs = new ArrayList<>();
        for (int i = 0; i < nbJoueur.getNbJoueurs(); i++) {
            System.out.println("Joueur "+ (i+1) +" veuillez renseigner votre pseudo ou écrivez ia pour créer un bot");
            String nom = scan.next();
            if(nom.equals("ia"))
            {
                System.out.println("IA Créé");
                Joueur j = ModeIA.getIAClasse(ModeIA.SIMPLE,("IA " + Roi.getRoiInt(i).getLibelle() + ""), Roi.getRoiInt(i), nbJoueur, modeJeu, SCORE_DEFAUT);
                joueurs.add(j);
                System.out.println(j.getNomJoueur() + " / vous êtes le roi " + Roi.getRoiInt(i));
            }
            else
            {
                Joueur j = new Joueur(nom, Roi.getRoiInt(i), nbJoueur, modeJeu, SCORE_DEFAUT);
                joueurs.add(j);
                System.out.println(nom + " vous êtes le roi " + Roi.getRoiInt(i));
            }
        }
        return joueurs;
    }

    public static List<IDomino> chargementDominos(String path) throws IOException {
        List<String[]> dominos = CSVParser.parse(path, ",", true);
        List<IDomino> dominosCharges = new ArrayList<>();
        for(String[] strs : dominos) {
            Case case1 = new Case(Integer.parseInt(strs[0]), Terrain.getTerrain(strs[1]));
            Case case2 = new Case(Integer.parseInt(strs[2]), Terrain.getTerrain(strs[3]));
            IDomino domino = new Domino(case1,case2,Integer.parseInt(strs[4]));
            dominosCharges.add(domino);
        }
        return dominosCharges;
    }

    public void tourDeJeu(){
        do {
            List<IDomino> pioche = partie.pioche();
            afficherPioche(pioche);
            if(!partie.partieFinie()){
                List<Roi> rois = partie.melangerRois();
                tirageJoueur(rois);
                placementJoueur();
            }
        } while(!partie.partieFinie());
        afficheScore();
        System.out.println("Fin du jeu !");
    }

    private void tirageJoueur(List<Roi> rois){
        System.out.println("Entrez le numéro pour choisir le domino");
        List<IDomino> cartesSurBoard = new ArrayList<IDomino>(partie.getTirage()); //en faisant le remove des domino du tirage tu casses ta fonction de getTourOrder. je t'ai fait un petit clonage pour éviter cela
        for (Roi roi : rois) {
            Joueur joueur = partie.getJoueur(roi);
            if(joueur.isIA())
            {
                try
                {
                    System.out.println("Tour : " + roi.toString());
                    int numeroTire = joueur.pickInPioche(cartesSurBoard,partie.getJoueurs());
                    System.out.println("IA a choisi le domino : " + cartesSurBoard.get(numeroTire).getIdentifiant());
                    joueur.addDomino(cartesSurBoard.remove(numeroTire));
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
            else
            {
                System.out.println("Tour : " + roi.toString());
                System.out.println("Entrez le num du domino : (entre 1 et " + (cartesSurBoard.size()) + ")");
                int domino = scan.nextInt();
                System.out.println("Domino tiré : " + cartesSurBoard.get(domino - 1).getIdentifiant());
                joueur.addDomino(cartesSurBoard.remove(domino - 1));
            }
        }
    }

    private void placementJoueur(){
        List<Pair<IDomino, Joueur>> assortiment = partie.getTourOrder();
        for (Pair<IDomino, Joueur> paire : assortiment) {
            IDomino domino = paire.getKey();
            Joueur joueur = paire.getValue();
            if(joueur.isIA())
            {
                try
                {
                    System.out.println("IA Placement : " + joueur.getCouleurRoi().getLibelle());
                    PlacementDomino p = joueur.pickPossibilite(domino);
                    joueur.getPlateau().addDomino(p.getDomino(),p.getRow(),p.getColumn(),p.getCaseId(),p.getSens());
                    System.out.println("IA a décidé de placer son domino en x : " + p.getRow() + "/ y : " + p.getColumn() + " / sens : " + p.getSens().getText());
                    System.out.println("Domino concerné : " + p.getDomino().toString());
                    System.out.println(joueur.getPlateau().affichePlateau(true));
                    //Thread.sleep(1000);
                }
                catch(Exception e)
                {
                    System.out.println("IA n'a pas pu placer son domino ! Aucune possibilité");
                    System.out.println("Domino concerné : " + domino.toString());
                    System.out.println(joueur.getPlateau().affichePlateau(true));
                }
            }
            else
            {
                System.out.println("Placement : " + joueur.getCouleurRoi().getLibelle());
                System.out.println(joueur.getPioche());
                System.out.println(joueur.getPlateau().affichePlateau(true));
                System.out.println("Domino : "+ domino);
                System.out.print("x : ");
                int x = scan.nextInt();
                System.out.print("y : ");
                int y = scan.nextInt();
                System.out.print("Orientation : ");
                Orientation sens = Orientation.getOrientation(scan.next());
                try {
                    joueur.getPlateau().addDomino(domino,x,y,0, sens);
                } catch (TuileException | DominoException e) {
                    System.out.println(e.getMessage());
                }
                System.out.println(joueur.getPlateau().affichePlateau(true));
                System.out.println("Fin tour de : "+ joueur.getCouleurRoi().getLibelle());
            }
        }
    }

    public void afficherPioche(List<IDomino> pioche){
        if(pioche != null){
            for (IDomino domino : pioche) {
                System.out.println("[ "+domino.getIdentifiant()+" ]");
            }
            // Mettre une pause
            for (IDomino domino : pioche) {
                System.out.println("[ "+domino.getCases()[0]+","+domino.getCases()[1]+" ]");
            }
        }
    }

    private void afficheScore(){
        List<Joueur> scores = partie.calculScores();
        List<Joueur> exaequo = new ArrayList<>();
        if(scores.get(0).getEgalite()){
            exaequo.add(scores.get(0));
            for (int i = 1; i < scores.size(); i++) {
                if(scores.get(i).getEgalite() && scores.get(i).getScoreCouronne() == scores.get(0).getScoreCouronne())
                    exaequo.add(scores.get(i));
            }
            StringBuilder sb = new StringBuilder("Vainqueurs : ");
            for (int i = 0; i < exaequo.size(); i++) {
                if(i<exaequo.size()-1)
                    sb.append(exaequo.get(1)+", ");
                else sb.append(exaequo.get(i));
            }
            System.out.println(sb.toString());
        }
        else
        {
            StringBuilder sb = new StringBuilder("Participants : " + "\n");
            for (Joueur j : scores)
            {
                sb.append("Joueur : " + j.getNomJoueur() + " / score : " + j.getScore() + "\n");
            }
            sb.append("Vainqueur : " + scores.get(0));
            System.out.println(sb.toString());
        }

    }

    /*
     * Tour 1
     * pioche de la tuile de départ
     *      -> créer tuile de départ
     * puis re pioche des 1ere tuiles
     * Afficher pioche (les dominos face numérotée et rangés par ordre croissant) OK
     * Si peux pas poser domino -> le défausser
     *
     *
     *
     *
     * Tour 1 ex:
     * tour 1a pour pioche pour tuile de départ ON ZAPPE
     * tour 1b pour pioche 1er domino
     *
     * Tour 2 ex:
     * tour pour pioche 2e domino
     *
     * Tour 3 ex:
     * tour pour pioche 3e domino
     *
     * Tour 4
     * ...
     *
     * pioche(tuile);
     * do {
     * pioche(dominos);
     * foreach -> joueurs
     * j.jouer();
     * } while (plusDeDomino);
     */
}
