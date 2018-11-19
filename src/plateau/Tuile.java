package plateau;

public class Tuile implements IDomino
{
    private Case caseTuile;

    public Tuile(Case c1)
    {
        this.caseTuile = c1;
    }

    public Case[] getCase()
    {
        Case[] tableauCases = new Case[1];
        tableauCases[0] = this.caseTuile;
        return tableauCases;
    }

    public int getIdentifiant()
    {
        return -1;
    }
}
