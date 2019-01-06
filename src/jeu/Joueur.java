package jeu;

import plateau.IDomino;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Joueur {

    private String nomJoueur;
    private Roi couleurRoi;
    private int score;
    private List<IDomino> pioche;
    private int scoreCouronne;
    private int scoreDomaine;
    private boolean egalite;

    public Joueur(String nom, Roi couleur, int score){
        nomJoueur = nom;
        this.score = score;
        couleurRoi = couleur;
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
}
