package jeu;

public enum ModeJeu {
    STANDARD("Standard", 5, 0, 0),
    GRAND_DUEL("Grand Duel",7,0,0),
    EMPIRE_DU_MILIEU("Empire du Milieu", 5, 10, 0),
    HARMONIE("Harmonie", 5, 0, 5);

    private String libelle;
    private int taillePlateau;
    private int bonusPlacementMilieu;
    private int bonusPlateauComplet;

    ModeJeu(String libelle, int taillePlateau, int bonusPlacementMilieu, int bonusPlateauComplet){
        this.libelle = libelle;
        this.taillePlateau = taillePlateau;
        this.bonusPlacementMilieu = bonusPlacementMilieu;
        this.bonusPlateauComplet = bonusPlateauComplet;
    }

    public String getLibelle(){
        return libelle;
    }

    public int getTaillePlateau() {
        return taillePlateau;
    }
}
