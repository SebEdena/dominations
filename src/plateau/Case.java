package plateau;

public class Case {
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

    public String toString()
    {
        return this.nbCouronne + " / " + this.terrain.name();
    }

}
