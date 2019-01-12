/**
 * Classe permettant de décrire une partie de jeu
 * @author Mathieu Valentin, Sébastien Viguier, Laurent Yu
 * @version 1.0
 */
package jeu;

import javafx.util.Pair;
import plateau.*;
import java.util.*;

public class Partie {

    private List<Joueur> joueurs;
    private List<IDomino> dominosRestants;
    private List<IDomino> tirage;
    private NbJoueur nbJoueur;
    private ModeJeu modeJeu;

    /**
     * Constructeur d'une partie de jeu
     * @param joueurs Liste des joueurs qui jouent
     * @param deckDominos Liste des dominos utilisés
     * @param nbJoueur Paramètres de jeu
     * @param modeJeu Mode de jeu
     */
    public Partie(List<Joueur> joueurs, List<IDomino> deckDominos, NbJoueur nbJoueur, ModeJeu modeJeu) {
        this.nbJoueur = nbJoueur;
        this.modeJeu = modeJeu;
        this.dominosRestants = deckDominos;
        this.joueurs = joueurs;
        retirerDominos();
    }

    /**
     * Methode retournant l'état de la partie
     * @return L'état de la partie (finie ou en cours)
     */
    public boolean partieFinie(){
        return dominosRestants.size() == 0 && tirage == null; //vérifier que le tirage n'est pas vide aussi. c'est bien is null car à chaque fois on clear le tirage
    }

    /**
     * Methode retournant les paramètres de jeu
     * @return Paramètres de jeu
     */
    public NbJoueur getParamJeu() {
        return nbJoueur;
    }

    /**
     * Mathode de recherche du joueur qui a le roi
     * @param roi Roi recherché
     * @return Le joueur qui a le roi
     */
    public Joueur getJoueur(Roi roi){
        for (Joueur j : joueurs){
            if(j.getCouleurRoi().equals(roi)) return j;
        }
        return null;
    }

    /**
     *
     * @param i
     * @return
     * @throws ArrayIndexOutOfBoundsException
     */
    public IDomino getDominoPioche(int i) throws ArrayIndexOutOfBoundsException {
        return tirage.remove(i);
    }

    /**
     * Methode retirant des dominos aléatoirement aux dominos du départ
     * @see NbJoueur#getNbDominosRetires
     */
    private void retirerDominos(){
        // Récupération du nombre de domino à retirer e, fonction du nombre de joueur et du mode de jeu utilisé
        if(!modeJeu.getLibelle().equals("Grand Duel")) {
            int nbDominos = nbJoueur.getNbDominosRetires();
            Random rand = new Random();
            // Suppression aléatoire des dominos
            for (int i = 0; i < nbDominos; i++) {
                int index = rand.nextInt(dominosRestants.size());
                dominosRestants.remove(index);
            }
        }
    }

    /**
     * Methode permettant de mélanger les rois
     * @return La liste des rois mélangée
     * @see Joueur#getNbRois
     * @see Joueur#getCouleurRoi
     */
    public List<Roi> melangerRois(){
        List<Roi> rois = new ArrayList<>();
        // Passage par List<> pour utiliser la méthode shuffle()
        for(Joueur j : joueurs){
            for(int i = 0; i < j.getNbRois(); i++){
                rois.add(j.getCouleurRoi());
            }
        }
        Collections.shuffle(rois);
        return rois;
    }

    /**
     * Methode de pioche de domino utilisés pendant un tour de jeu
     * @return La liste des dominos piochés pour le tour
     * @see Joueur#resetTirage
     * @see NbJoueur#getNbRoiParJoueur
     * @see NbJoueur#getNbJoueurs
     * @see IDomino#getIdentifiant
     */
    public List<IDomino> pioche(){
        // Initialisation des pioches de chaque joueur
        for (Joueur j : joueurs){
            j.resetTirage();
        }
        Random mainInnocente = new Random();
        tirage = null;
        // Cas ou il reste des dominos à piocher
        if(!dominosRestants.isEmpty()){
            Collections.shuffle(dominosRestants);
            tirage = new ArrayList<IDomino>();
            // Pioche des dominos en fonction du nombre de joueur et du nombre de roi par joueur
            for (int i = 0; i < nbJoueur.getNbRoiParJoueur()*nbJoueur.getNbJoueurs(); i++) {
                int indexDomino = mainInnocente.nextInt(dominosRestants.size());
                tirage.add(dominosRestants.remove(indexDomino));
            }
            // Tri des dominos piochés par ordre croissant
            tirage.sort(Comparator.comparingInt(IDomino::getIdentifiant));
        }
        return tirage;
    }

    /**
     * Methode retournant l'ordre de jeu des joueurs avec le domino pioché par le joueur
     * @return L'ordre de jeu des joueurs avec le domino pioché par le joueur
     * @see Joueur#piocheContainsDomino
     */
    public List<Pair<IDomino, Joueur>> getTourOrder(){
        // Cas ou il reste des dominos en jeu (pioche non vide)
        if(tirage != null && tirage.size() != 0){
            List<Pair<IDomino, Joueur>> order = new ArrayList<>();
            // Ajout de joueurs et des dominos tirés par les joueurs en fonction
            // de l'ordre des dominos triés par ordre croissant
            for(IDomino d : tirage){
                boolean playerFound = false;
                for(int i = 0; i < joueurs.size() && !playerFound; i++){
                    if(joueurs.get(i).piocheContainsDomino(d)){
                        order.add(new Pair<>(d, joueurs.get(i)));
                        playerFound = true;
                    }
                }
                if(!playerFound) throw new IllegalStateException("Pas de joueur pour ce domino !");
            }
            return order;
        }else{
            return null;
        }
    }

    /**
     * Methode retournant la liste des joueurs triés par score décroissant
     * @return La liste des joueurs triés
     * @see Joueur#calculScore
     * @see Joueur#getScore
     * @see Joueur#getScoreDomaine
     * @see Joueur#getScoreCouronne
     * @see Joueur#setEgalite
     */
    public List<Joueur> calculScores(){
        List<Joueur> resultats = new ArrayList<>();
        // Ajout des scores de chaque joueur
        for(Joueur j : joueurs){
            j.calculScore(this.modeJeu);
            resultats.add(j);
        }
        // Tri des scores par ordre croissant
        // Si égalité :
        //  - Tri des scores des domaines par ordre croissant
        //    Si égalité :
        //      - Tri des scores des couronnes par ordre croissant
        //        Si égalité :
        //          - Les deux joueurs comparés sont exaequos
        resultats.sort((j1, j2) -> {
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
        });
        return resultats;
    }

    /**
     * Methode retournat le nombre de dominos piochés
     * @return Nombre de dominos piochés
     */
    public int getNbDominoPioche()
    {
        return tirage.size();
    }

    /**
     * Methode retournant la pioche
     * @return La pioche
     */
    public List<IDomino> getTirage() {
        return tirage;
    }

    /**
     * Methode retournant la liste des joueurs
     * @return La liste des joueurs
     */
    public List<Joueur> getJoueurs(){
        return joueurs;
    }
}
