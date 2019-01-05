package jeu;

import plateau.*;
import util.CSVParser;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

public class Jeu {
    private static final int SCORE_DEFAUT = 0;
    private static final int NB_JOUEURS_MIN = 2;
    private static final int NB_JOUEURS_MAX = 4;
    private static final int GRAND_PLATEAU = 7;
    private static final int PETIT_PLATEAU = 5;

    private static Jeu instance;

    private Scanner scan = new Scanner(System.in);
    private Map<Integer,Map> joueurs;
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

    private Map<Integer,Map> allocateRoi(){
        Map<Integer,Map> listeJoueurs = new HashMap<>();
        listeRois = new int[paramJeu.getNbJoueurs()];
        for (int i = 0; i < paramJeu.getNbJoueurs(); i++) {
            Map<Joueur, Plateau> joueurs = new HashMap<>();
            System.out.println("Joueur "+ (i+1) +" veuillez renseigner votre pseudo : ");
            String nom = scan.next();
            joueurs.put(new Joueur(nom, Roi.getRoiInt(i), SCORE_DEFAUT), new Plateau(PETIT_PLATEAU));
            listeJoueurs.put(i,joueurs);
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

    private void melangerRois(){
        List<Integer> rois = new ArrayList<>();
        for (int i = 0; i < listeRois.length; i++) {
            rois.add(listeRois[i]);
        }
        Collections.shuffle(rois);
        int i = 0;
        for (Integer roi : rois) {
            listeRois[i] = roi;
            i++;
        }
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

    public void tourDeJeu(){
        melangerRois();
        System.out.println("Entrez le numéro pour choisir le domino");
        for (int i = 0; i < listeRois.length; i++) {
            Joueur jo = (Joueur) joueurs.get(listeRois[i]).keySet().iterator().next();
            Plateau p = (Plateau) joueurs.get(listeRois[i]).get(jo);
            jo.resetTirage();
            for (int j = 0; j < paramJeu.getNbRoiParJoueur(); j++) {
                System.out.println("Tour : "+Roi.getRoiInt(listeRois[i]));
                System.out.println("Entrez le num du domino : (entre 1 et 4)");
                int domino = scan.nextInt();
                jo.addDomino(tirage.get(domino-1));
            }
            System.out.println(jo.getPioche());
            //Poser Domino ou défausser
            joueurs.get(listeRois[i]).remove(jo);
            joueurs.get(listeRois[i]).put(jo,p);
            System.out.println("Fin tour de : "+joueurs.get(listeRois[i]).keySet().toArray()[0]);
        }
    }

    public void afficherPioche(){
        if(!plusDeDominos){
            tirage.sort(new Comparator<IDomino>() {
                @Override
                public int compare(IDomino o1, IDomino o2) {
                    return o1.getIdentifiant()-o2.getIdentifiant();
                }
            });
            for (IDomino domino : tirage) {
                System.out.println("[ "+domino.getIdentifiant()+" ]");
            }
            // Mettre une pause
            for (IDomino domino : tirage) {
                System.out.println("[ "+domino.getCases()[0]+","+domino.getCases()[1]+" ]");
            }
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
