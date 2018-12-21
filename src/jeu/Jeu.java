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

    private Scanner scan = new Scanner(System.in);
    private Map<Joueur, Plateau> joueurs;
    private List<IDomino> dominosDebut;
    private List<IDomino> dominosRestants;
    private List<IDomino> tirage = new ArrayList<>();
    private int[] listeRois;
    private boolean plusDeDominos;
    private NbJoueur paramJeu;

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
        int nbJoueurs = 0;
        do {
            try{
                System.out.println("Nombre de joueurs ? (2 à 4)");
                nbJoueurs = Integer.parseInt(scan.next());
            } catch (NumberFormatException e){
                System.out.println("Erreur de saisie");
            }
        } while(nbJoueurs < NB_JOUEURS_MIN || nbJoueurs > NB_JOUEURS_MAX);
        paramJeu = NbJoueur.getParamsJeu(nbJoueurs);
        joueurs = allocateRoi();
        dominosDebut = chargementDominos("./dominos.csv");
    }

    private Map<Joueur, Plateau> allocateRoi(){
        Map<Joueur, Plateau> listeJoueurs = new HashMap<>();
        listeRois = new int[paramJeu.getNbJoueurs()];
        for (int i = 0; i < paramJeu.getNbJoueurs(); i++) {
            System.out.println("Joueur "+ (i+1) +" veuillez renseigner votre pseudo : ");
            String nom = scan.next();
            listeJoueurs.put(new Joueur(nom, Roi.getRoiInt(i), SCORE_DEFAUT), new Plateau(PETIT_PLATEAU));
            listeRois[i] = i;
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
        plusDeDominos = false;
        return dominosCharges;
    }

    public void débutPartie(){
        retirerDominos();
    }

    private void retirerDominos(){
        int nbDominos = NbJoueur.getParamsJeu(paramJeu.getNbJoueurs()).getNbDominosRetires();
        Random rand = new Random();
        for (int i = 0; i < nbDominos; i++) {
            int index = rand.nextInt(dominosRestants.size());
            dominosRestants.remove(index);
        }
    }

    private void mélangerRois(){
        Collections.shuffle(Arrays.asList(listeRois));
    }

    public void pioche(){
        if(!dominosRestants.isEmpty()){
            Collections.shuffle(dominosRestants);
            for (int i = 0; i < paramJeu.getNbRoiParJoueur()*paramJeu.getNbJoueurs(); i++) {
                IDomino d = piocher();
                tirage.add(d);
            }
        } else plusDeDominos = true;

    }

    private IDomino piocher(){
        Random mainInnocente = new Random();
        IDomino domino;
        int indexDomino = mainInnocente.nextInt(dominosRestants.size());
        domino = dominosRestants.get(indexDomino);
        dominosRestants.remove(indexDomino);
        return domino;
    }

    /*
     * Tour 1
     * pioche de la tuile de départ
     *      -> créer tuile de départ
     * puis re pioche des 1ere tuiles
     * Afficher pioche (les dominos face numérotée et rangés par ordre croissant)
     * Si peux pas poser domino -> le défausser
     *
     *
     *
     *
     * Tour 1 ex:
     * tour 1a pour pioche pour tuile de départ
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
