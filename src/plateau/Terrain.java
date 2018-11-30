
package plateau;

import java.lang.String;
public enum Terrain
{
    Champs("CHAM"),
    Chateau("CHAT"),
    Foret("FORE"),
    Mer("MER-"),
    Prairie("PRAI"),
    Montagne("MONT"),
    Mine("MINE");

    private String diminutif;

    Terrain(String diminutif){
        this.diminutif = diminutif;
    }

    public static Terrain getTerrain(String nom)
    {
        switch(nom)
        {
            case "Champs" :
                return Champs;
            case "Foret" :
                return Foret;
            case "Mer" :
                return Mer;
            case "Prairie" :
                return Prairie;
            case "Montagne" :
                return Montagne;
            case "Mine" :
                return Mine;
            case "Chateau" :
                return Chateau;
            default :
                return null;
        }
    }

    public String getDiminutif(){
        return this.diminutif;
    }
}


