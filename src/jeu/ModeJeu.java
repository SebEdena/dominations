package jeu;

import java.util.Arrays;
import java.util.List;

import static jeu.NbJoueur.*;

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

    ModeJeu(String libelle, int taillePlateau, int bonusPlacementMilieu, int bonusPlateauComplet, List<NbJoueur> listeModesJeuAcceptes){
        this.libelle = libelle;
        this.taillePlateau = taillePlateau;
        this.bonusPlacementMilieu = bonusPlacementMilieu;
        this.bonusPlateauComplet = bonusPlateauComplet;
        this.listeModesJeuAcceptes = listeModesJeuAcceptes;
    }

    public static ModeJeu getModeJeu(String modeJeu){
        for(ModeJeu m : values()){
            if(m.getLibelle().equals(modeJeu)) return m;
        }
        return null;
    }

    public String getLibelle(){
        return libelle;
    }

    public int getTaillePlateau() {
        return taillePlateau;
    }

    public List<NbJoueur> getListeModesJeuAcceptes() {
        return this.listeModesJeuAcceptes;
    }

    public int getBonusPlacementMilieu() {
        return bonusPlacementMilieu;
    }

    public int getBonusPlateauComplet() {
        return bonusPlateauComplet;
    }
}
