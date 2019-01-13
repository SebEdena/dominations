/**
 * Classe permettant de décrire une case
 * @author Mathieu Valentin, Sébastien Viguier, Laurent Yu
 * @version 1.0
 */
package model.plateau;

import java.io.Serializable;

public class Case implements Serializable {
    private static final String separateurPlateau = ";";
    private int nbCouronne;
    private Terrain terrain;

    /**
     * Constructeur d'une case
     * @param nbCouronne Nombre de couronnes contenues dans une case
     * @param ter Terrain de la case
     */
    public Case (int nbCouronne, Terrain ter){
        this.nbCouronne = nbCouronne;
        this.terrain = ter;
    }

    /**
     * Methode retournant le nombre de couronne dans la case
     * @return Le nombre de couronne dans la case
     */
    public int getNbCouronne() {
        return nbCouronne;
    }

    /**
     * Methode attribuant un nombre de couronnes à une case
     * @param nbCouronne Nombre de couronnes
     */
    public void setNbCouronne(int nbCouronne) {
        this.nbCouronne = nbCouronne;
    }

    /**
     * Methode retournant le terrain de la case
     * @return Le terrain de la case
     */
    public Terrain getTerrain()
    {
        return this.terrain;
    }

    /**
     * Methode attribuant un terrain à une case
     * @param terrain Terrain de la case
     */
    public void setTerrain(Terrain terrain)
    {
        this.terrain = terrain;
    }

    /**
     * Methode retournant le séparateur de case
     * @return Le séparateur de case
     */
    public static String getSeparateurPlateau(){
        return separateurPlateau;
    }

    /**
     * Methode retournant le plateau
     * @return Le plateau
     */
    public String affichagePlateau(){
        return terrain.getDiminutif() + separateurPlateau + "--C"+this.nbCouronne;
    }

    /**
     * Methode retournant l'affichage d'une case
     * @return L'affichage d'une case
     */
    public String toString() {
        return "{" + this.terrain.name() + ", Couronnes: " + this.nbCouronne + "}";
    }
}
