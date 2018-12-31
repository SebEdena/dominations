package jeu;

public enum ModeIA {

    NORMAL("Normal"),
    DIFFICILE("Difficile");

    private String libelle;

    ModeIA(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }
}
