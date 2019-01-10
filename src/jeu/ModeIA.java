package jeu;

import exceptions.DominoException;
import exceptions.TuileException;

public enum ModeIA {
    SIMPLE("Simple"),
    NORMAL("Normal"),
    DIFFICILE("Difficile");

    private String libelle;

    ModeIA(String libelle) {
        this.libelle = libelle;
    }

    public String getLibelle() {
        return libelle;
    }

    public static AbstractIA getIAClasse(ModeIA niveau, String nom, Roi couleur, NbJoueur nbJoueur, ModeJeu modeJeu, int score) throws DominoException, TuileException
    {
        switch(niveau)
        {
            case SIMPLE:
                return new SimpleIA(nom, couleur, nbJoueur, modeJeu, score);
            case NORMAL:
                return null;
            case DIFFICILE:
                return null;
            default:
                return null;
        }
    }
}
