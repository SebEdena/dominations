/**
 * Classe permettant de décrire les paramètres de jeu
 * @author Mathieu Valentin, Sébastien Viguier, Laurent Yu
 * @version 1.0
 */
package model.jeu;

public enum NbJoueur {
    jeuA2(2,24,2),
    jeuA3(3,12,1),
    jeuA4(4,0,1);

    private int nbJoueurs;
    private int nbDominosRetires;
    private int nbRoiParJoueur;

    /**
     * Contructeur de la classe d'énumération des paramètres de jeu
     * @param nb Nombre de joueur jouant dans la partie
     * @param dominos Nombre de domino à retirer du jeu au départ
     * @param nbRois Nombre de roi par joueur
     */
    NbJoueur(int nb, int dominos, int nbRois){
        this.nbJoueurs = nb;
        this.nbDominosRetires = dominos;
        this.nbRoiParJoueur = nbRois;
    }

    /**
     * Methode retournant les paramètres du jeu en fonction du nombre de joueurs
     * @param nb Le nombre de joueurs
     * @return Les paramètres du jeu
     */
    public static NbJoueur getParamsJeu(int nb){
        switch (nb){
            case 2 : return jeuA2;
            case 3 : return jeuA3;
            case 4 : return jeuA4;
            default: return null;
        }
    }

    /**
     * Methode retournant le nombre de joueurs dans la partie
     * @return Le nombre de joueur dans la partie
     */
    public int getNbJoueurs() {
        return nbJoueurs;
    }

    /**
     * Methode retourant le nombre de dominos à retirer au début de la partie
     * @return Le nombre de dominos à retirer
     */
    public int getNbDominosRetires() {
        return nbDominosRetires;
    }

    /**
     * Methode retournant le nombre de roi que possède chaque joueur
     * @return Le nombre de roi par joueur
     */
    public int getNbRoiParJoueur() {
        return nbRoiParJoueur;
    }
}
