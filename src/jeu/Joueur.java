package jeu;

public class Joueur {

    private Roi couleurRoi;
    private int score;

    public Joueur(Roi couleur, int score){
        this.score = score;
        couleurRoi = couleur;
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
    /*
    -- Créer joueur
    -- setRoi Roi
    -- Menu cb de joueur
    -- attribution des rois
    --
     */
}
