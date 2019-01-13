/**
 * Classe permettant de décrire le château
 * @author Mathieu Valentin, Sébastien Viguier, Laurent Yu
 * @version 1.0
 */
package model.plateau;

public class Tuile implements IDomino
{
    private Case caseTuile;

    /**
     * Constructeur de la classe Tuile
     */
    public Tuile()
    {
        this.caseTuile = new Case(0, Terrain.CHATEAU);
    }

    /**
     * Methode retournant la case de la tuile
     * @return La case de la tuile
     */
    public Case[] getCases()
    {
        Case[] tableauCases = new Case[1];
        tableauCases[0] = this.caseTuile;
        return tableauCases;
    }

    /**
     * Methode retournant le numéro de la tuile
     * @return Le numéro de la tuile
     */
    public int getIdentifiant()
    {
        return -1;
    }

    /**
     * Methode retournant le nombre de case d'une tuile
     * @return Le nombre de case d'une tuile
     */
    @Override
    public int getNbCases() {
        return 1;
    }

    /**
     * Methode retournant le numéro d'une case d'un IDomino en fonction de l'autre
     * @param c Case d'un domino
     * @param oppose (true) pour la case c, (false) pour l'autre case
     * @return Le numéro d'une case d'un IDomino en fonction de l'autre
     */
    @Override
    public int getCaseIndex(Case c, boolean oppose) {
        return caseTuile.equals(c)?0:-1;
    }
}
