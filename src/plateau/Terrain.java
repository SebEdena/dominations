
package plateau;

import java.lang.String;
public enum Terrain
{
    Champs,
    Chateau,
    Foret,
    Mer,
    Prairie,
    Montagne,
    Mine;

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
}


