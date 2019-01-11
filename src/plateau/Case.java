/**
 * Classe permettant de décrire une case
 * @author Mathieu Valentin, Sébastien Viguier, Laurent Yu
 * @version 1.0
 */
package plateau;

import java.io.Serializable;

public class Case implements Serializable {
    private static final String separateurPlateau = ";";
    private int nbCouronne;
    private Terrain terrain;

    public Case (int nbCouronne, Terrain ter){
        this.nbCouronne = nbCouronne;
        this.terrain = ter;
    }

    public int getNbCouronne() {
        return nbCouronne;
    }

    public void setNbCouronne(int nbCouronne) {
        this.nbCouronne = nbCouronne;
    }

    public Terrain getTerrain()
    {
        return this.terrain;
    }

    public void setTerrain(Terrain terrain)
    {
        this.terrain = terrain;
    }

    public static String getSeparateurPlateau(){
        return separateurPlateau;
    }

    public String affichagePlateau(){
        return terrain.getDiminutif() + separateurPlateau + "--C"+this.nbCouronne;
    }

    public String toString() {
        return "{" + this.terrain.name() + ", Couronnes: " + this.nbCouronne + "}";
    }
}
