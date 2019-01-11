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

    /**
     * Constructeur du jeu (pattern singleton)
     */
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

    /**
     * Methode permettant de retourner l'instance du jeu en cours
     * @return instance du jeu en cours
     */
    public static Jeu getInstance(){
        if (instance == null) {
            instance = new Jeu();
        }
        return instance;
    }

    /**
     * Methode d'initialisation des paramètres généraux du jeu comprenant :
     *  - Le mode de jeu
     *  - Le nombre de joueurs
     *  - Les paramètres du jeu
     *  - Les dominos à utiliser
     *  - La liste des rois
     *  - La partie de jeu
     * @see #chargementDominos
     * @see #allocateRoi
     * @throws Exception
     */
    private void intitialisationJeu() throws Exception {
        System.out.println("Bienvenue sur Dominations quel mode de jeu souhaitez-vous ?");
        // Affichage des différents modes de jeu supportés
        for(int compteur = 0; compteur < ModeJeu.values().length; compteur++)
        {
            System.out.println(""+ (compteur) + " - " + ModeJeu.values()[compteur].toString());
        }
        int modeJeu = -1;
        // Récupération du mode choisi
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
        // Récupération du nombre de joueur pour la partie
        int nbJoueurs = 0;
        do {
            try{
                System.out.println("Nombre de joueurs ? (2 à 4)");
                nbJoueurs = Integer.parseInt(scan.next());
            } catch (NumberFormatException e){
                System.out.println("Erreur de saisie");
            }
        } while(nbJoueurs < NB_JOUEURS_MIN || nbJoueurs > NB_JOUEURS_MAX);
        scan.nextLine();
        // Récupération de l'énum des paramètres du jeu
        NbJoueur paramJeu = NbJoueur.getParamsJeu(nbJoueurs);
        // Récupération de la liste des joueurs
        List<Joueur> joueurs = allocateRoi(paramJeu, ModeJeu.values()[modeJeu]);
        // Récupération de la liste des dominos du jeu
        dominosDebut = chargementDominos("./dominos.csv");
        // Création de la partie de jeu
        partie = new Partie(joueurs, dominosDebut, paramJeu, ModeJeu.values()[modeJeu]);
    }

    /**
     * Methode qui créée les joueurs en leur attribuant un ou deux roi(s) de couleur.
     * Il y a aussi la possibilité de créer des IA
     * @param nbJoueur Le nombre de joueurs participant au jeu
     * @param modeJeu Le mode de jeu choisi
     * @return La liste des joueurs inscris dans le jeu
     * @throws Exception
     * @see ModeIA#getIAClasse
     * @see Joueur#getNomJoueur
     */
    private List<Joueur> allocateRoi(NbJoueur nbJoueur, ModeJeu modeJeu) throws Exception {
        List<Joueur> joueurs = new ArrayList<>();
        // Création de chaque roi et de chaque joueur
        for (int i = 0; i < nbJoueur.getNbJoueurs(); i++) {
            System.out.println("Joueur "+ (i+1) +" veuillez renseigner votre pseudo ou écrivez ia pour créer un bot");
            String nom = scan.nextLine();
            // Si c'est un IA
            if(nom.equals("ia"))
            {
                // Choix du niveau de difficulté
                System.out.println("Veuillez choisir un niveau IA :");
                for(int compteur = 0; compteur < ModeIA.values().length; compteur++)
                {
                    System.out.println(""+ (compteur) + " - " + ModeIA.values()[compteur].getLibelle());
                }
                int modeIA = -1;
                do {
                    try{
                        modeIA = Integer.parseInt(scan.next());
                    } catch (NumberFormatException e){
                        System.out.println("Erreur de saisie");
                    }
                    if(modeIA < 0 || modeIA > ModeIA.values().length - 1)
                    {
                        System.out.println("Veuillez saisir un bon numéro");
                    }
                } while(modeIA < 0 || modeIA > ModeIA.values().length - 1);
                System.out.println("IA Créé avec comme niveau : " + ModeIA.values()[modeIA].getLibelle());
                scan.nextLine(); // pour flush
                // Saisie du nom de l'IA
                System.out.println("veuillez saisir un nom pour ce bot : ");
                String nomIA = scan.nextLine();
                // Cast de l'IA en joueur
                Joueur j = ModeIA.getIAClasse(ModeIA.values()[modeIA],nomIA, Roi.getRoiInt(i), nbJoueur, modeJeu, SCORE_DEFAUT);
                // Ajout dans la liste des joueurs
                joueurs.add(j);
                System.out.println(j.getNomJoueur() + " / vous êtes le roi " + Roi.getRoiInt(i));
            }
            // Cas d'un joueur normal
            else
            {
                // Création du joueur
                Joueur j = new Joueur(nom, Roi.getRoiInt(i), nbJoueur, modeJeu, SCORE_DEFAUT);
                // Ajout dans la liste des joueurs
                joueurs.add(j);
                System.out.println(nom + " vous êtes le roi " + Roi.getRoiInt(i));
            }
        }
        return joueurs;
    }

    /**
     * Methode de chargement des dominos à partir d'un fichier csv et les transformes en objet IDomino
     * @param path Chemin d'accès à la liste des dominos
     * @return La liste des dominos chargés
     * @throws IOException
     * @see CSVParser#parse
     */
    private List<IDomino> chargementDominos(String path) throws IOException {
        // Récupération des dominos parsés
        List<String[]> dominos = CSVParser.parse(path, ",", true);
        List<IDomino> dominosCharges = new ArrayList<>();
        // Création des dominos et de leurs cases
        for(String[] strs : dominos) {
            Case case1 = new Case(Integer.parseInt(strs[0]), Terrain.getTerrain(strs[1]));
            Case case2 = new Case(Integer.parseInt(strs[2]), Terrain.getTerrain(strs[3]));
            IDomino domino = new Domino(case1,case2,Integer.parseInt(strs[4]));
            dominosCharges.add(domino);
        }
        return dominosCharges;
    }

    /**
     * Methode permettant de dérouler le jeu tant qu'il reste des dominos à joueur
     * Un tour est composé en plusieurs étapes :
     *  - tirage des dominos de la pioche
     *  - afficher la pioche
     *  - mélanger les rois
     *  - tirage des dominos par les joueurs
     *  - placement des dominos par les joueurs
     *  - afficher le score final
     * @see Partie#pioche
     * @see #afficherPioche
     * @see Partie#melangerRois
     * @see #tirageJoueur
     * @see #placementJoueur
     * @see Partie#partieFinie
     * @see #afficheScore
     */
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

    /**
     * Methode permetant au joueur ou a l'IA de tirer un domino suivant la liste des rois préalablement mélangée
     * @see Partie#getTirage
     * @see Partie#getJoueur
     * @see IDomino#getIdentifiant
     * @see Joueur#pickInPioche
     * @see Joueur#addDomino
     * @param rois Liste des rois en jeu
     */
    private void tirageJoueur(List<Roi> rois){
        System.out.println("Entrez le numéro pour choisir le domino");
        List<IDomino> cartesSurBoard = new ArrayList<IDomino>(partie.getTirage()); //en faisant le remove des domino du tirage tu casses ta fonction de getTourOrder. je t'ai fait un petit clonage pour éviter cela
        for (Roi roi : rois) {
            // Récupération du joueur du roi tiré (par ordre croissant)
            Joueur joueur = partie.getJoueur(roi);
            // Cas d'un joueur IA
            if(joueur.isIA())
            {
                try
                {
                    System.out.println("Tour : " + roi.toString());
                    // Pioche un domino depuis la pioche
                    int numeroTire = joueur.pickInPioche(cartesSurBoard,partie.getJoueurs());
                    System.out.println("IA a choisi le domino : " + cartesSurBoard.get(numeroTire).getIdentifiant());
                    // Ajout du domino dans les dominos tirés par l'IA
                    joueur.addDomino(cartesSurBoard.remove(numeroTire));
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
            // Cas d'un joueur normal
            else
            {
                int numDomino = -1;
                System.out.println("Tour : " + roi.toString());
                System.out.println("Entrez le num du domino : (entre 0 et " + (cartesSurBoard.size() - 1) + ")");
                // Saisie du domino pioché par le joueur
                for (int compteur = 0; compteur < cartesSurBoard.size();compteur++)
                {
                    System.out.println(compteur + " - domino : " + cartesSurBoard.get(compteur).toString());
                }
                do {
                    try{
                        numDomino = Integer.parseInt(scan.nextLine());
                    } catch (NumberFormatException e){
                        System.out.println("Erreur de saisie");
                    }
                    if(numDomino < 0 || numDomino > (cartesSurBoard.size() - 1))
                    {
                        System.out.println("Veuillez saisir un bon numéro");
                    }
                } while(numDomino < 0 || numDomino > (cartesSurBoard.size() - 1));
                System.out.println("Domino tiré : " + cartesSurBoard.get(numDomino).getIdentifiant());
                // Ajout du domino dans les dominos tirés par le joueur
                joueur.addDomino(cartesSurBoard.remove(numDomino));
            }
        }
    }

    /**
     * Methode permettant aux joueurs de placer leurs dominos piochés en fonction de leur ordre de tirage
     * @see Partie#getTourOrder
     * @see Pair#getKey
     * @see Pair#getValue
     * @see Joueur#isIA
     * @see Roi#getLibelle
     * @see Joueur#pickPossibilite
     * @see Plateau#addDomino
     * @see PlacementDomino#getRow
     * @see Domino#toString
     * @see Plateau#affichePlateau
     * @see Orientation#getOrientationByText
     */
    private void placementJoueur(){
        List<Pair<IDomino, Joueur>> assortiment = partie.getTourOrder();
        for (Pair<IDomino, Joueur> paire : assortiment) {
            IDomino domino = paire.getKey();
            Joueur joueur = paire.getValue();
            // Cas d'un joueur IA
            if(joueur.isIA())
            {
                try
                {
                    System.out.println("IA Placement : " + joueur.getCouleurRoi().getLibelle());
                    // Récupération d'un emplacement possible du domino sur son plateau
                    PlacementDomino p = joueur.pickPossibilite(domino);
                    // Ajout sur le plateau
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
            // Cas d'un joueur normal
            else
            {
                System.out.println("Placement : " + joueur.getCouleurRoi().getLibelle());
                System.out.println(joueur.getPioche());
                System.out.println(joueur.getPlateau().affichePlateau(true));
                System.out.println("Domino : "+ domino);
                boolean numValide = false;
                int x = 0, y = 0;
                // Saisie des coordonnées du domino à placer (ligne,colonne,orientation)
                while(!numValide)
                {
                    try{
                        System.out.print("x : ");
                        x = Integer.parseInt(scan.nextLine());
                        numValide = true;
                    } catch (Exception e){
                        System.out.println("Erreur de saisie");
                        scan.nextLine();
                    }
                }
                numValide = false;
                while(!numValide)
                {
                    try{
                        System.out.print("y : ");
                        y = Integer.parseInt(scan.nextLine());
                        numValide = true;
                    } catch (Exception e){
                        System.out.println("Erreur de saisie");
                        scan.nextLine();
                    }
                }
                Orientation sens = null;
                while(sens == null)
                {
                    try{
                        System.out.print("Orientation : ");
                        sens = Orientation.getOrientationByText(scan.nextLine());
                    }
                    catch(Exception e)
                    {
                        System.out.println("Erreur de saisie");
                    }
                }
                // Essai de l'ajout au plateau, sinon le domino est défaussé
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

    /**
     * Methode affichant en console la pioche en deux parties :
     *  - D'abord les numéros des dominos par ordre croissant
     *  - Ensuite en affichant les faces avec les terrains
     * @param pioche La pioche de domino à afficher
     * @see Domino#getIdentifiant
     * @see Domino#getCases
     */
    public void afficherPioche(List<IDomino> pioche){
        if(pioche != null){
            // Affichage des numéros des dominos
            for (IDomino domino : pioche) {
                System.out.println("[ "+domino.getIdentifiant()+" ]");
            }
            //Thread.sleep(1000);
            // Affichage des faces avec les terrains des mêmes dominos
            for (IDomino domino : pioche) {
                System.out.println("[ "+domino.getCases()[0]+","+domino.getCases()[1]+" ]");
            }
        }
    }

    /**
     * Methode affichant les scores des joueurs en fonction :
     *  - Du score de chaque joueur
     *  - De plusieurs joueurs qui sont exaequos
     * @see Partie#calculScores
     * @see Joueur#getEgalite
     * @see Joueur#getScoreCouronne
     * @see Joueur#getNomJoueur
     * @see Joueur#getScore
     */
    private void afficheScore(){
        // Calcul et tri des scores des joueurs (ordre décroissant)
        List<Joueur> scores = partie.calculScores();
        List<Joueur> exaequo = new ArrayList<>();
        // Recherche d'une égalité pour les vainqueurs
        if(scores.get(0).getEgalite()){
            exaequo.add(scores.get(0));
            // Recherche des vainqueurs exaequos
            for (int i = 1; i < scores.size(); i++) {
                if(scores.get(i).getEgalite() && scores.get(i).getScoreCouronne() == scores.get(0).getScoreCouronne())
                    exaequo.add(scores.get(i));
            }
            // Affichage des vainqueurs
            StringBuilder sb = new StringBuilder("Vainqueurs : ");
            for (int i = 0; i < exaequo.size(); i++) {
                if(i<exaequo.size()-1)
                    sb.append(exaequo.get(1)+", ");
                else sb.append(exaequo.get(i));
            }
            System.out.println(sb.toString());
        }
        // Cas sans exaequos
        else
        {
            // Affichage des scores des joueurs
            StringBuilder sb = new StringBuilder("Participants : " + "\n");
            for (Joueur j : scores)
            {
                sb.append("Joueur : " + j.getNomJoueur() + "/ Roi : " + j.getCouleurRoi().getLibelle() + " / score : " + j.getScore() + "\n");
            }
            // Affichage du vainqueur
            sb.append("Vainqueur : " + scores.get(0));
            System.out.println(sb.toString());
        }

    }
}
