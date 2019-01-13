

package model.jeu;

import model.exceptions.DominoException;
import model.exceptions.TuileException;
import model.joueur.AbstractIA;
import model.joueur.DifficileIA;
import model.joueur.NormalIA;
import model.joueur.SimpleIA;

/**
 * Enumération permettant de décrire les modes d'IA possible
 * @author Mathieu Valentin, Sébastien Viguier, Laurent Yu
 * @version 1.0
 */

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
                return new NormalIA(nom, couleur, nbJoueur, modeJeu, score);
            case DIFFICILE:
                return new DifficileIA(nom, couleur, nbJoueur, modeJeu, score);
            default:
                return null;
        }
    }

    public static ModeIA getModeIA(String modeIA){
        for(ModeIA m : values()){
            if(m.getLibelle().equals(modeIA)) return m;
        }
        return null;
    }
}
