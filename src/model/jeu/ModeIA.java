/**
 * Classe permettant de décrire le mode de l'IA
 * @author Mathieu Valentin, Sébastien Viguier, Laurent Yu
 * @version 1.0
 */
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

    /**
     * Contructeur de la classe d'énumération du mode de l'IA
     * @param libelle Difficulté de l'IA
     */
    ModeIA(String libelle) {
        this.libelle = libelle;
    }

    /**
     * Methode retournant la difficulté de l'IA
     * @return La difficulté de l'IA
     */
    public String getLibelle() {
        return libelle;
    }

    /**
     * Methode retournant créant un IA et retournant son instance
     * @param niveau Difficulté de l'IA
     * @param nom Pseudo de l'IA
     * @param couleur Roi attribé à l'IA
     * @param nbJoueur Paramètres du jeu
     * @param modeJeu Mode de jeu
     * @param score Score de départ
     * @return L'instance de l'IA créée
     * @throws DominoException
     * @throws TuileException
     */
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

    /**
     * Methode retournant la difficulté de l'IA
     * @param modeIA Mode de l'IA
     * @return La difficulté de l'IA
     */
    public static ModeIA getModeIA(String modeIA){
        for(ModeIA m : values()){
            if(m.getLibelle().equals(modeIA)) return m;
        }
        return null;
    }
}
