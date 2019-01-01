package plateau;

public interface IDomino
{

    public Case[] getCases();

    public int getIdentifiant();

    public int getNbCases();

    public int getCaseIndex(Case c, boolean oppose);
}
