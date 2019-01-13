/**
 * Classe permettant de décrire le mode de jeu
 * @author Mathieu Valentin, Sébastien Viguier, Laurent Yu
 * @version 1.0
 */
package model.jeu;

import java.util.Arrays;
import java.util.List;

import static model.jeu.NbJoueur.*;

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
     * Contructeur de la classe d'énumération du mode de jeu
     * @param libelle Nom du mode de jeu
     * @param taillePlateau Taille du plateau de jeu
     * @param bonusPlacementMilieu Nombre de points bonus si le chateau est au milieu du plateau
     * @param bonusPlateauComplet Nombre de points bonus si toutes les cases sont remplies dans un plateau
     * @param listeModesJeuAcceptes Liste des modes de jeu existants
     */
    ModeJeu(String libelle, int taillePlateau, int bonusPlacementMilieu, int bonusPlateauComplet, List<NbJoueur> listeModesJeuAcceptes){
        this.libelle = libelle;
        this.taillePlateau = taillePlateau;
        this.bonusPlacementMilieu = bonusPlacementMilieu;
        this.bonusPlateauComplet = bonusPlateauComplet;
        this.listeModesJeuAcceptes = listeModesJeuAcceptes;
    }

    /**
     * Methode retournant le mode de jeu
     * @param modeJeu Nom du mode de jeu
     * @return Le mode de jeu
     */
    public static ModeJeu getModeJeu(String modeJeu){
        for(ModeJeu m : values()){
            if(m.getLibelle().equals(modeJeu)) return m;
        }
        return null;
    }

    /**
     * Methode retournant le nom du mode de jeu
     * @return Le nom du mode de jeu
     */
    public String getLibelle(){
        return libelle;
    }

    /**
     * Methode retournant la taille du plateau
     * @return La taille du plateau
     */
    public int getTaillePlateau() {
        return taillePlateau;
    }

    /**
     * Methode retournant la liste des mode de jeu existants
     * @return La liste de modes de jeu existants
     */
    public List<NbJoueur> getListeModesJeuAcceptes() {
        return this.listeModesJeuAcceptes;
    }

    /**
     * Methode retournant le bonus de points si le chateau st au milieu du plateau
     * @return Le bonus de points si le chateau st au milieu du plateau
     */
    public int getBonusPlacementMilieu() {
        return bonusPlacementMilieu;
    }

    /**
     * Methode retournant le bonus si toutes les cases sont remplies dans un plateau
     * @return Le bonus si toutes les cases sont remplies dans un plateau
     */
    public int getBonusPlateauComplet() {
        return bonusPlateauComplet;
    }
}
