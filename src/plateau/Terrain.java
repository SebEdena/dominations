
package plateau;

import java.lang.String;
public enum Terrain
{
    CHAMPS("CHAM", "#fbc02d"),
    CHATEAU("CHAT", "#ff7043"),
    FORET("FORE", "#2e7d32"),
    MER("MER-","#2196f3"),
    PRAIRIE("PRAI", "#8bc34a"),
    MONTAGNE("MONT", "#795548"),
    MINE("MINE", "#757575");

    private String diminutif;
    private String hexColor;

    Terrain(String diminutif, String hexColor){
        this.diminutif = diminutif;
        this.hexColor = hexColor;
    }

    public static Terrain getTerrain(String nom)
    {
        switch(nom)
        {
            case "Champs" :
                return CHAMPS;
            case "Foret" :
                return FORET;
            case "Mer" :
                return MER;
            case "Prairie" :
                return PRAIRIE;
            case "Montagne" :
                return MONTAGNE;
            case "Mine" :
                return MINE;
            case "Chateau" :
                return CHATEAU;
            default :
                return null;
        }
    }

    public String getDiminutif(){
        return this.diminutif;
    }

    public String getColor() { return hexColor; }
}


