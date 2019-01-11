/**
 * Enumération permettant de décrire  les orientations possibles du jeu
 * @author Mathieu Valentin, Sébastien Viguier, Laurent Yu
 * @version 1.0
 */
package plateau;

import static jdk.nashorn.internal.objects.NativeString.toUpperCase;

public enum Orientation {

    NORD(-1, 0, "N"), EST(0, 1, "E"), SUD(1, 0, "S"), OUEST(0, -1, "O");

    private int offsetX, offsetY;
    private String text;

    Orientation(int x, int y, String text){
        this.offsetX = x;
        this.offsetY = y;
        this.text = text;
    }

    public static Orientation getOrientation(String nom)
    {
        switch(nom)
        {
            case "Nord" :
                return NORD;
            case "Est" :
                return EST;
            case "Sud" :
                return SUD;
            case "Ouest" :
                return OUEST;
            default :
                return null;
        }
    }

    public int getOffsetX() {
        return offsetX;
    }

    public int getOffsetY() {
        return offsetY;
    }

    public String getText() { return text; }

    public Orientation getOppose(){
        switch (this){
            case NORD: return SUD;
            case EST: return OUEST;
            case SUD: return NORD;
            case OUEST: return EST;
            default: return null;
        }
    }

    public Orientation getRotationHoraire(){
        switch (this){
            case NORD: return EST;
            case EST: return SUD;
            case SUD: return OUEST;
            case OUEST: return NORD;
            default: return null;
        }
    }

    public Orientation getOrientationByText(String nom)
    {
        nom = nom.toUpperCase();
        switch(nom)
        {
            case "N":
                return NORD;
            case "S" :
                return SUD;
            case "O" :
                return OUEST;
            case "E" :
                return EST;
            default :
                return null;
        }
    }
}
