package jeu;

import java.util.Arrays;
import java.util.List;

import static jeu.NbJoueur.*;

public enum ModeJeu {
    STANDARD("Standard", 5, 0, 0, Arrays.asList()),
    GRAND_DUEL("Grand Duel",7,0,0, Arrays.asList(jeuA3,jeuA4)),
    EMPIRE_DU_MILIEU("Empire du Milieu", 5, 10, 0, Arrays.asList()),
    HARMONIE("Harmonie", 5, 0, 5, Arrays.asList());

    private String libelle;
    private int taillePlateau;
    private int bonusPlacementMilieu;
    private int bonusPlateauComplet;
    private List<NbJoueur> listerefuse;

    ModeJeu(String libelle, int taillePlateau, int bonusPlacementMilieu, int bonusPlateauComplet, List<NbJoueur> listerefuse){
        this.libelle = libelle;
        this.taillePlateau = taillePlateau;
        this.bonusPlacementMilieu = bonusPlacementMilieu;
        this.bonusPlateauComplet = bonusPlateauComplet;
        this.listerefuse = listerefuse;
    }

    public String getLibelle(){
        return libelle;
    }

    public int getTaillePlateau() {
        return taillePlateau;
    }

    public List<NbJoueur> getListerefuse() {
        return this.listerefuse;
    }
}
