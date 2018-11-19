package plateau;



public class Domino implements IDomino
{
    private static int nbCasesDomino = 2;
    private Case[] cases = new Case[nbCasesDomino];
    private int identifiant;

    public Domino(Case c1, Case c2, int valeur)
    {
        this.cases[0] = c1;
        this.cases[1] = c2;
        this.identifiant = valeur;
    }

    public Case[] getCase()
    {
        return this.cases;
    }

    public int getIdentifiant()
    {
        return this.identifiant;
    }

    public String toString()
    {
        return identifiant + " / " + cases[0].toString() + " / " + cases[1].toString();
    }
}
