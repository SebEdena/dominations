package plateau;



public class Domino
{
    private static final int nbCasesDomino = 2;
    private Case[] cases = new Case[nbCasesDomino];
    private int identifiant;

    public Domino(Case c1, Case c2, int valeur)
    {
        this.cases[0] = c1;
        this.cases[1] = c2;
        this.identifiant = valeur;
    }

    public String toString()
    {
        return identifiant + " / " + cases[0].toString() + " / " + cases[1].toString();
    }
}
