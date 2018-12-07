package jeu;

import plateau.*;
import util.CSVParser;

import java.io.IOException;
import java.util.*;

public class Jeu {
    private static final int SCORE_DEFAUT = 0;
    private static final int NB_JOUEURS_MIN = 2;
    private static final int NB_JOUEURS_MAX = 4;
    private static final int GRAND_PLATEAU = 7;
    private static final int PETIT_PLATEAU = 5;

    private static Jeu instance;
    private static Scanner scan = new Scanner(System.in);
    private static int nbJoueurs = 0;

    private Map<Joueur, Plateau> joueurs;
    private List<IDomino> dominosDebut;
    private List<IDomino> dominosRestants;
    private List<IDomino> tirage;

    private Jeu(){
        try {
            intitialisationJeu();
        } catch (IOException e) {
            System.err.println("Erreur lors du chargement du fichier .csv," +
                    " veuillez recommencer.");
        }
    }

    public static Jeu getInstance(){
        if (instance == null) {
            instance = new Jeu();
        }
        return instance;
    }

    private void intitialisationJeu() throws IOException {
        do {
            try{
                System.out.println("Nombre de joueurs ? (2 à 4)");
                nbJoueurs = Integer.parseInt(scan.next());
            } catch (NumberFormatException e){
                System.out.println("Erreur de saisie");
            }
        } while(nbJoueurs < NB_JOUEURS_MIN || nbJoueurs > NB_JOUEURS_MAX);
        joueurs = allocateRoi(nbJoueurs);
        dominosDebut = chargementDominos("./dominos.csv");
    }

    private Map<Joueur, Plateau> allocateRoi(int nb){
        Map<Joueur, Plateau> listeJoueurs = new HashMap<>();
        for (int i = 0; i < nb; i++) {
            System.out.println("Veuillez renseigner votre pseudo : ");
            String nom = scan.next();
            listeJoueurs.put(new Joueur(nom, Roi.getRoiInt(i), SCORE_DEFAUT), new Plateau(PETIT_PLATEAU));
            System.out.println(nom + " vous êtes le roi " + Roi.getRoiInt(i));
        }
        return listeJoueurs;
    }

    private List<IDomino> chargementDominos(String path) throws IOException {
        List<String[]> dominos = CSVParser.parse(path, ",", true);
        List<IDomino> dominosCharges = new ArrayList<>();
        for(String[] strs : dominos) {
            Case case1 = new Case(Integer.parseInt(strs[0]), Terrain.getTerrain(strs[1]));
            Case case2 = new Case(Integer.parseInt(strs[2]), Terrain.getTerrain(strs[3]));
            IDomino domino = new Domino(case1,case2,Integer.parseInt(strs[4]));
            dominosCharges.add(domino);
        }
        dominosRestants = dominosCharges;
        return dominosCharges;
    }

    private List<IDomino> pioche(){
        List<IDomino> pioche = new ArrayList<>();
        for (int i = 0; i < nbJoueurs; i++) {
            pioche.add(piocher());
        }
        return pioche;
    }

    private IDomino piocher(){
        Random mainInnocente = new Random();
        IDomino domino;
        int indexDomino = 1 + mainInnocente.nextInt(dominosRestants.size());
        if(tirage.isEmpty()){
            domino = dominosRestants.get(indexDomino);
            dominosRestants.remove(indexDomino);
            return domino;
        } else {
            boolean dejaPioche = false;
            do {
                domino = dominosRestants.get(indexDomino);
                for (IDomino d : tirage) {
                    if(d.getIdentifiant() == domino.getIdentifiant() || dejaPioche)
                        dejaPioche = true;
                    else dejaPioche = false;
                }
            } while (dejaPioche);
            dominosRestants.remove(indexDomino);
            return domino;
        }
    }
}
