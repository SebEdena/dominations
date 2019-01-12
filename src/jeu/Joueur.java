/**
 * Classe permettant de décrire un joueur
 * @author Mathieu Valentin, Sébastien Viguier, Laurent Yu
 * @version 1.0
 */
package jeu;

import com.sun.org.apache.xpath.internal.operations.Mod;
import exceptions.DominoException;
import exceptions.TuileException;
import javafx.application.Platform;
import plateau.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

public class Joueur {

    private String nomJoueur;
    private Roi couleurRoi;
    private int nbRois;
    private int score;
    private List<IDomino> pioche;
    private int scoreCouronne;
    private int scoreDomaine;
    private boolean egalite;
    private Plateau plateau;

    /**
     * Constructeur d'un joueur
     * @param nom Pseudo du joueur
     * @param couleur Roi attribué au joueur
     * @param nbJoueur Paramètre du jeu
     * @param modeJeu Mode du jeu
     * @param score Score de départ du joueur
     * @throws DominoException
     * @throws TuileException
     * @see NbJoueur#getNbRoiParJoueur
     * @see ModeJeu#getTaillePlateau
     * @see Plateau#addDomino
     */
    public Joueur(String nom, Roi couleur, NbJoueur nbJoueur, ModeJeu modeJeu, int score) throws DominoException, TuileException {
        nomJoueur = nom;
        this.score = score;
        this.nbRois = nbJoueur.getNbRoiParJoueur();
        couleurRoi = couleur;
        plateau = new Plateau(modeJeu.getTaillePlateau());
        plateau.addDomino(new Tuile(),2,2,0,null);
    }

    /**
     * Methode retournant le nom du joueur
     * @return Le nom du joueur
     */
    public String getNomJoueur() {
        return nomJoueur;
    }

    /**
     * Methode modifiant le nom du joueur
     * @param nomJoueur Le nouveau nom du joueur
     */
    public void setNomJoueur(String nomJoueur) {
        this.nomJoueur = nomJoueur;
    }

    /**
     * Methode retournant le roi du joueur
     * @return Le roi du joueur
     */
    public Roi getCouleurRoi() {
        return couleurRoi;
    }

    /**
     * Methode modifiant le roi du joueur
     * @param couleurRoi Le nouveau roi du joueur
     */
    public void setCouleurRoi(Roi couleurRoi) {
        this.couleurRoi = couleurRoi;
    }

    /**
     * Methode retournant le nombre de roi que le joueur possède
     * @return Le nombre de roi du joueur
     */
    public int getNbRois(){
        return nbRois;
    }

    /**
     * Methode retournant le score du joueur
     * @return Le score du joueur
     */
    public int getScore() {
        return score;
    }

    /**
     * Methode modifiant le score du joueur
     * @param score Nouveau score du joueur
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Methode ajoutant un domino pioché aux dominos tirés par le joueur
     * @param domino Domino pioché
     */
    public void addDomino(IDomino domino){
        pioche.add(domino);
        // Tri des domino par ordre croissant de numéro
        pioche.sort(new Comparator<IDomino>() {
            @Override
            public int compare(IDomino o1, IDomino o2) {
                return o1.getIdentifiant()-o2.getIdentifiant();
            }
        });
    }

    /**
     * Methode retournant les dominos piochés par le joueur
     * @return Les dominos piochés du joueur
     */
    public List<IDomino> getPioche(){
        return pioche;
    }

    /**
     * Methode d'initialisation des dominos piochés par le joueur
     */
    public void resetTirage(){
        pioche = new ArrayList<>();
    }

    /**
     * Methode permettant l'affichage du joueur
     * @return La couleur et le nom du joueur
     */
    @Override
    public String toString(){
        return "Roi "+this.couleurRoi+" "+this.nomJoueur;
    }

    /**
     * Methode modifiant le score des couronnes du joueur
     * @param calculCouronne Le nouveau score des couronnes du joueur
     */
    public void setScoreCouronne(int calculCouronne) {
        this.scoreCouronne = calculCouronne;
    }

    /**
     * Methode modifiant le score de la taille du domaine du joueur
     * @param calculGrosDomaine Le nouveau score de la taille du domaine du joueur
     */
    public void setScoreDomaine(int calculGrosDomaine) {
        this.scoreDomaine = calculGrosDomaine;
    }

    public int getScoreDomaine() {
        return this.scoreDomaine;
    }

    public int getScoreCouronne() {
        return this.scoreCouronne;
    }

    public boolean getEgalite() {
        return this.egalite;
    }

    public void setEgalite(boolean b) {
        this.egalite = b;
    }

    public Plateau getPlateau() {
        return plateau;
    }

    public boolean piocheContainsDomino(IDomino d){
        return pioche.contains(d);
    }

    public void calculScore(ModeJeu mode) {
        this.setScore(plateau.calculPoint() + this.bonusPoint(mode));
        this.setScoreDomaine(plateau.calculGrosDomaine());
        this.setScoreCouronne(plateau.calculCouronne());
    }

    private int bonusPoint(ModeJeu mode)
    {
        switch(mode)
        {
            case STANDARD:
                return 0;
            case EMPIRE_DU_MILIEU:
                return this.getPlateau().getCaseAt(2,2).getTerrain().equals(Terrain.CHATEAU)?10:0;
            case HARMONIE:
                Plateau plateau = this.getPlateau();
                for(int i = 0; i < mode.getTaillePlateau(); i++)
                {
                    for(int j = 0; i < mode.getTaillePlateau(); j++)
                    {
                        if(plateau.getCaseAt(i,j) == null)
                        {
                            return 0;
                        }
                    }
                }
                return 5;
            default:
                return 0;
        }
    }

    public boolean isIA(){
        return false;
    }

    public int pickInPioche(List<IDomino> cartesSurBoard, List<Joueur> joueursAdverses) throws Exception
    {
        throw new Exception("impposible de faire cela avec un joueur");
    }

    public PlacementDomino pickPossibilite(IDomino domino) throws Exception
    {
        throw new Exception("impposible de faire cela avec un joueur");
    }
}
