/**
 * Interface permettant de décrire un type de domino soit domino soit tuile Château
 * @author Mathieu Valentin, Sébastien Viguier, Laurent Yu
 * @version 1.0
 */
package model.plateau;

public interface IDomino
{

    /**
     * Methode retournant les cases du domino
     * @return Les cases du domino
     */
    public Case[] getCases();

    /**
     * Methode retournant le numéro du domino
     * @return Le numéro du domino
     */
    public int getIdentifiant();

    /**
     * Methode retournant le nombre de cases du domino
     * @return Le nombre de cases du domino
     */
    public int getNbCases();

    /**
     * Methode retournant le numéro d'une case en fontion de l'autre
     * @param c Case d'un domino
     * @param oppose (true) pour la case c, (false) pour l'autre case
     * @return Le numéro de la case
     */
    public int getCaseIndex(Case c, boolean oppose);
}
