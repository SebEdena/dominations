
package plateau;

import java.lang.String;
public enum Terrain
{
    CHAMPS("CHAM"),
    CHATEAU("CHAT"),
    FORET("FORE"),
    MER("MER-"),
    PRAIRIE("PRAI"),
    MONTAGNE("MONT"),
    MINE("MINE");

    private String diminutif;

    Terrain(String diminutif){
        this.diminutif = diminutif;
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
}


