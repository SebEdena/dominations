package jeu;

import exceptions.DominoException;
import exceptions.TuileException;
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
    private List<IDomino> tirage;
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
            Plateau p =  new Plateau(PETIT_PLATEAU);
            try {
                p.addDomino(new Tuile(),2,2,0,null);
            } catch (TuileException | DominoException e) {
                System.out.println(e.getMessage());
            }
            joueurs.put(new Joueur(nom, Roi.getRoiInt(i), SCORE_DEFAUT),p);
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
            tirage = new ArrayList<IDomino>();
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
        retirerDominos();
        do {
            pioche();
            afficherPioche();
            if(!plusDeDominos){
                melangerRois();
                tirageJoueur();
                placementJoueur();
            }
        } while(!plusDeDominos);
        afficheScore();
        System.out.println("Fin du jeu !");
    }

    private void tirageJoueur(){
        System.out.println("Entrez le numéro pour choisir le domino");
        for (int i = 0; i < listeRois.length; i++) {
            Joueur jo = (Joueur) joueurs.get(listeRois[i]).keySet().iterator().next();
            Plateau p = (Plateau) joueurs.get(listeRois[i]).get(jo);
            jo.resetTirage();
            for (int j = 0; j < paramJeu.getNbRoiParJoueur(); j++) {
                System.out.println("Tour : " + Roi.getRoiInt(listeRois[i]));
                System.out.println("Entrez le num du domino : (entre 1 et 4)");
                int domino = scan.nextInt();
                jo.addDomino(tirage.get(domino - 1));
            }
            joueurs.get(listeRois[i]).remove(jo);
            joueurs.get(listeRois[i]).put(jo, p);
        }
    }

    private void placementJoueur(){
        for (IDomino domino : tirage) {
            for (int i = 0; i < listeRois.length; i++) {
                Joueur jo = (Joueur) joueurs.get(listeRois[i]).keySet().iterator().next();
                Plateau p = (Plateau) joueurs.get(listeRois[i]).get(jo);
                for (int j = 0; j < paramJeu.getNbRoiParJoueur(); j++) {
                    if(jo.getPioche().get(j).getIdentifiant() == domino.getIdentifiant()){
                        System.out.println("Placement : " + Roi.getRoiInt(listeRois[i]));
                        System.out.println(jo.getPioche());
                        System.out.println(p.affichePlateau(true));
                        System.out.println("Domino : "+jo.getPioche().get(j));
                        System.out.print("x : ");
                        int x = scan.nextInt();
                        System.out.print("y : ");
                        int y = scan.nextInt();
                        System.out.print("Orientation : ");
                        Orientation sens = Orientation.getOrientation(scan.next());
                        try {
                            p.addDomino(jo.getPioche().get(j),x,y,0, sens);
                        } catch (TuileException | DominoException e) {
                            System.out.println(e.getMessage());
                        }
                        System.out.println(p.affichePlateau(true));
                        joueurs.get(listeRois[i]).remove(jo);
                        joueurs.get(listeRois[i]).put(jo,p);
                        System.out.println("Fin tour de : "+joueurs.get(listeRois[i]).keySet().toArray()[0]);
                    }
                }
            }
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

    private void afficheScore(){
        List<Joueur> j = new ArrayList<>();
        List<Joueur> exaequo = new ArrayList<>();
        for (int i = 0; i < listeRois.length; i++) {
            Joueur jo = (Joueur) joueurs.get(listeRois[i]).keySet().iterator().next();
            Plateau p = (Plateau) joueurs.get(listeRois[i]).get(jo);
            jo.setScore(p.calculPoint());
            jo.setScoreDomaine(p.calculGrosDomaine());
            jo.setScoreCouronne(p.calculCouronne());
            j.add(jo);
            System.out.println(jo+" score : "+ p.calculPoint());
        }
        boolean egal = false;
        j.sort(new Comparator<Joueur>() {
            @Override
            public int compare(Joueur j1, Joueur j2) {
                if(j1.getScore() == j2.getScore())
                    if(j1.getScoreDomaine() == j2.getScoreDomaine())
                        if(j1.getScoreCouronne() == j2.getScoreCouronne()){
                            j1.setEgalite(true);
                            j2.setEgalite(true);
                            return 0;
                        }
                        else return j2.getScoreCouronne() - j1.getScoreCouronne();
                    else return j2.getScoreDomaine() - j1.getScoreDomaine();
                else return j2.getScore() - j1.getScore();
            }
        });
        if(j.get(0).getEgalite()){
            exaequo.add(j.get(0));
            for (int i = 1; i < j.size(); i++) {
                if(j.get(i).getEgalite() && j.get(i).getScoreCouronne() == j.get(0).getScoreCouronne())
                    exaequo.add(j.get(i));
            }
            StringBuilder sb = new StringBuilder("Vainqueurs : ");
            for (int i = 0; i < exaequo.size(); i++) {
                if(i<exaequo.size()-1)
                    sb.append(exaequo.get(1)+", ");
                else sb.append(exaequo.get(i));
            }
            System.out.println(sb.toString());
        }
        else System.out.println("Vainqueur : "+j.get(0));

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
