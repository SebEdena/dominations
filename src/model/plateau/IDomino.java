/**
 * Interface permettant de décrire un type de domino soit domino soit tuile Château
 * @author Mathieu Valentin, Sébastien Viguier, Laurent Yu
 * @version 1.0
 */
package model.plateau;

public interface IDomino
{

    public Case[] getCases();

    public int getIdentifiant();

    public int getNbCases();

    public int getCaseIndex(Case c, boolean oppose);
}
