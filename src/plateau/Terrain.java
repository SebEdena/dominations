
package plateau;

import java.lang.String;
public enum Terrain
{
    CHAMPS("Champs","CHAM", "#fbc02d"),
    CHATEAU("Chateau","CHAT", "#000000"),
    FORET("Foret","FORE", "#2e7d32"),
    MER("Mer","MER-","#2196f3"),
    PRAIRIE("Prairie", "PRAI", "#8bc34a"),
    MONTAGNE("Montagne", "MONT", "#6D4C41"),
    MINE("Mine","MINE", "#607D8B");

    private String libelle;
    private String diminutif;
    private String hexColor;

    Terrain(String libelle, String diminutif, String hexColor){
        this.libelle = libelle;
        this.diminutif = diminutif;
        this.hexColor = hexColor;
    }

    public static Terrain getTerrain(String nom)
    {
        for(int i = 0; i < values().length; i++){
            if(nom.equals(values()[i].getLibelle())){
                return values()[i];
            }
        }
        return null;
    }

    public String getLibelle() { return libelle; }

    public String getDiminutif(){
        return this.diminutif;
    }

    public String getColor() { return hexColor; }
}


