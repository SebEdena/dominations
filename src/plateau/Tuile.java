/**
 * Classe permettant de décrire le château
 * @author Mathieu Valentin, Sébastien Viguier, Laurent Yu
 * @version 1.0
 */
package plateau;

public class Tuile implements IDomino
{
    private Case caseTuile;

    public Tuile()
    {
        this.caseTuile = new Case(0, Terrain.CHATEAU);
    }

    public Case[] getCases()
    {
        Case[] tableauCases = new Case[1];
        tableauCases[0] = this.caseTuile;
        return tableauCases;
    }

    public int getIdentifiant()
    {
        return -1;
    }

    @Override
    public int getNbCases() {
        return 1;
    }

    @Override
    public int getCaseIndex(Case c, boolean oppose) {
        return caseTuile.equals(c)?0:-1;
    }
}
