package model.iaContest;

import model.plateau.Terrain;

public class Potentiel
{
    private int nbCouronne;
    private Terrain terrain;
    private int nbCases;
    private int potentiel;

    public Potentiel(Terrain terrain, int couronne, int compteurCase, int value)
    {
        nbCouronne = 0;
        nbCases = 0;
        this.terrain = terrain;
        potentiel = value;
    }

    public Potentiel(Terrain terrain, int nbCou, int nbCas)
    {
        nbCouronne = nbCou;
        nbCases = nbCas;
        this.terrain = terrain;
        potentiel = 0;
    }
    
    public int getNbCouronne() {
        return nbCouronne;
    }

    public void setNbCouronne(int nbCouronne) {
        this.nbCouronne = nbCouronne;
    }

    public Terrain getTerrain() {
        return terrain;
    }

    public void setTerrain(Terrain terrain) {
        this.terrain = terrain;
    }

    public int getNbCases() {
        return nbCases;
    }

    public void setNbCases(int nbCases) {
        this.nbCases = nbCases;
    }

    public int getPotentiel() {
        return potentiel;
    }

    public void setPotentiel(int potentiel) {
        this.potentiel = potentiel;
    }

    @Override
    public String toString() {
        return "[" + terrain.getLibelle() + " | Couronnes : " + nbCouronne + " | Cases : " + nbCases + "]";
    }
}
