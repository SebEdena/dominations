/**
 * Classe permettant de décrire un domino
 * @author Mathieu Valentin, Sébastien Viguier, Laurent Yu
 * @version 1.0
 */
package model.plateau;

public class Domino implements IDomino
{
    public static final int nbCasesDomino = 2;
    private Case[] cases = new Case[nbCasesDomino];
    private int identifiant;

    /**
     * Constructeur d'un domino
     * @param c1 Case de droite
     * @param c2 Case de gauche
     * @param valeur Numéro du domino
     */
    public Domino(Case c1, Case c2, int valeur)
    {
        this.cases[0] = c1;
        this.cases[1] = c2;
        this.identifiant = valeur;
    }

    /**
     * Methode retournant les cases du domino
     * @return Les cases du domino
     */
    @Override
    public Case[] getCases()
    {
        return this.cases;
    }

    /**
     * Methode retournant le numéro du domino
     * @return Le numéro du domino
     */
    @Override
    public int getIdentifiant()
    {
        return this.identifiant;
    }

    /**
     * Methode retournant le nombre de cases du domino
     * @return Le nombre de cases du domino
     */
    @Override
    public int getNbCases() {
        return nbCasesDomino;
    }

    /**
     * Methode retournant le numéro d'une case en fontion de l'autre
     * @param c Case d'un domino
     * @param oppose (true) pour la case c, (false) pour l'autre case
     * @return Le numéro de la case
     */
    @Override
    public int getCaseIndex(Case c, boolean oppose) {
        for (int i = 0; i < cases.length; i++){
            if(c.equals(cases[i])){
                return oppose?Math.abs(i - 1):i;
            }
        }
        return -1;
    }

    /**
     * Methode retournant l'affichage d'un domino
     * @return L'affichage d'un domino
     */
    public String toString()
    {
        return "[Id: " + identifiant + ", Case 1: " + cases[0].toString() + ", Case 2: " + cases[1].toString()+ "]";
    }
}
