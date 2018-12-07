package jeu;

public enum NbJoueur {
    jeuA2(2,24,2),
    jeuA3(3,12,1),
    jeuA4(4,0,1);

    private int nbJoueurs;
    private int nbDominosRetires;
    private int nbRoiParJoueur;

    NbJoueur(int nb, int dominos, int nbRois){
        this.nbJoueurs = nb;
        this.nbDominosRetires = dominos;
        this.nbRoiParJoueur = nbRois;
    }

    public NbJoueur getParamsJeu(int nb){
        switch (nb){
            case 2 : return jeuA2;
            case 3 : return jeuA3;
            case 4 : return jeuA4;
            default: return null;
        }
    }

    public int getNbJoueurs() {
        return nbJoueurs;
    }

    public int getNbDominosRetires() {
        return nbDominosRetires;
    }

    public int getNbRoiParJoueur() {
        return nbRoiParJoueur;
    }
}
