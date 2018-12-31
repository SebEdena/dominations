package jeu;

public enum ModeJeu {
    STANDARD("Standard");

    private String libelle;

    ModeJeu(String libelle){
        this.libelle = libelle;
    }

    public String getLibelle(){
        return libelle;
    }
}
