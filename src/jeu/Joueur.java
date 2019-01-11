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

    public Joueur(String nom, Roi couleur, NbJoueur nbJoueur, ModeJeu modeJeu, int score) throws DominoException, TuileException {
        nomJoueur = nom;
        this.score = score;
        this.nbRois = nbJoueur.getNbRoiParJoueur();
        couleurRoi = couleur;
        plateau = new Plateau(modeJeu.getTaillePlateau());
        plateau.addDomino(new Tuile(),2,2,0,null);
    }

    public String getNomJoueur() {
        return nomJoueur;
    }

    public void setNomJoueur(String nomJoueur) {
        this.nomJoueur = nomJoueur;
    }

    public Roi getCouleurRoi() {
        return couleurRoi;
    }

    public void setCouleurRoi(Roi couleurRoi) {
        this.couleurRoi = couleurRoi;
    }

    public int getNbRois(){
        return nbRois;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void addDomino(IDomino domino){
        pioche.add(domino);
        pioche.sort(new Comparator<IDomino>() {
            @Override
            public int compare(IDomino o1, IDomino o2) {
                return o1.getIdentifiant()-o2.getIdentifiant();
            }
        });
    }

    public List<IDomino> getPioche(){
        return pioche;
    }

    public void resetTirage(){
        pioche = new ArrayList<>();
    }

    @Override
    public String toString(){
        return "Roi "+this.couleurRoi+" "+this.nomJoueur;
    }

    public void setScoreCouronne(int calculCouronne) {
        this.scoreCouronne = calculCouronne;
    }

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
        this.setScore(plateau.calculPoint() + this.BonusPoint(mode));
        this.setScoreDomaine(plateau.calculGrosDomaine());
        this.setScoreCouronne(plateau.calculCouronne());
    }

    private int BonusPoint(ModeJeu mode)
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
