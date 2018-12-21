package jeu;

public class Joueur {

    private String nomJoueur;
    private Roi couleurRoi;
    private int score;

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

    @Override
    public String toString(){
        return "Roi "+this.couleurRoi;
    }
}
