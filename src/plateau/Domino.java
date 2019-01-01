package plateau;


import java.util.Map;

public class Domino implements IDomino
{
    public static final int nbCasesDomino = 2;
    private Case[] cases = new Case[nbCasesDomino];
    private int identifiant;

    public Domino(Case c1, Case c2, int valeur)
    {
        this.cases[0] = c1;
        this.cases[1] = c2;
        this.identifiant = valeur;
    }

    @Override
    public Case[] getCases()
    {
        return this.cases;
    }

    @Override
    public int getIdentifiant()
    {
        return this.identifiant;
    }

    @Override
    public int getNbCases() {
        return nbCasesDomino;
    }

    @Override
    public int getCaseIndex(Case c, boolean oppose) {
        for (int i = 0; i < cases.length; i++){
            if(c.equals(cases[i])){
                return oppose?Math.abs(i - 1):i;
            }
        }
        return -1;
    }

    public String toString()
    {
        return "[Id: " + identifiant + ", Case 1: " + cases[0].toString() + ", Case 2: " + cases[1].toString()+ "]";
    }
}
