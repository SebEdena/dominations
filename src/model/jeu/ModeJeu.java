

package model.jeu;

import java.util.Arrays;
import java.util.List;

import static model.jeu.NbJoueur.*;

/**
 * Enumération permettant de décrire les modes de jeu possible
 * @author Mathieu Valentin, Sébastien Viguier, Laurent Yu
 * @version 1.0
 */

public enum ModeJeu {
    STANDARD("Standard", 5, 0, 0, Arrays.asList(jeuA2, jeuA3, jeuA4)),
    GRAND_DUEL("Grand Duel",7,0,0, Arrays.asList(jeuA2)),
    EMPIRE_DU_MILIEU("Empire du Milieu", 5, 10, 0, Arrays.asList(jeuA2, jeuA3, jeuA4)),
    HARMONIE("Harmonie", 5, 0, 5, Arrays.asList(jeuA2, jeuA3, jeuA4));

    private String libelle;
    private int taillePlateau;
    private int bonusPlacementMilieu;
    private int bonusPlateauComplet;
    private List<NbJoueur> listeModesJeuAcceptes;

    /**
     * Constructeur de mode de jeu
     * @param libelle
     * @param taillePlateau
     * @param bonusPlacementMilieu
     * @param bonusPlateauComplet
     * @param listeModesJeuAcceptes
     */
    ModeJeu(String libelle, int taillePlateau, int bonusPlacementMilieu, int bonusPlateauComplet, List<NbJoueur> listeModesJeuAcceptes){
        this.libelle = libelle;
        this.taillePlateau = taillePlateau;
        this.bonusPlacementMilieu = bonusPlacementMilieu;
        this.bonusPlateauComplet = bonusPlateauComplet;
        this.listeModesJeuAcceptes = listeModesJeuAcceptes;
    }

    /**
     * Fonction statique permettant de récupèrer le mode de jeu
     * @param modeJeu
     * @return retourne le mode de jeu concerné ou null si le mode de jeu n'existe pas
     */
    public static ModeJeu getModeJeu(String modeJeu){
        for(ModeJeu m : values()){
            if(m.getLibelle().equals(modeJeu)) return m;
        }
        return null;
    }

    /**
     * Fonction permettant de récupérer le libellé d'un Mode de jeu
     * @return retourne le libellé
     */
    public String getLibelle(){
        return libelle;
    }

    /**
     * Fonction permettant de récupérer la taille du plateau
     * @return la taille
     */
    public int getTaillePlateau() {
        return taillePlateau;
    }

    /**
     * Fonction permettant de récupérer la liste des modes de jeu acceptés
     * @return la liste des modes de jeu acceptés
     */
    public List<NbJoueur> getListeModesJeuAcceptes() {
        return this.listeModesJeuAcceptes;
    }

    /**
     * Fonction permettant de récupérer le nombre de points de bonus Placement milieu
     * @return le nombre de points de bonus
     */
    public int getBonusPlacementMilieu() {
        return bonusPlacementMilieu;
    }

    /**
     * Fonction permettant de récupérer le nombre de points de bonus
     * @return le nombre de points de bonus
     */
    public int getBonusPlateauComplet() {
        return bonusPlateauComplet;
    }
}
