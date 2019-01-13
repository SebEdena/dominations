/**
 * Enumération permettant de décrire  les orientations possibles du model.jeu
 * @author Mathieu Valentin, Sébastien Viguier, Laurent Yu
 * @version 1.0
 */
package model.plateau;

public enum Orientation {

    NORD(-1, 0, "N"), EST(0, 1, "E"), SUD(1, 0, "S"), OUEST(0, -1, "O");

    private int offsetX, offsetY;
    private String text;

    /**
     * Constructeur de la classe d'énumération
     * @param x Numéro de la ligne
     * @param y Numéro de la colonne
     * @param text Direction du domino
     */
    Orientation(int x, int y, String text){
        this.offsetX = x;
        this.offsetY = y;
        this.text = text;
    }

    /**
     * Methode retournant l'orientation
     * @param nom Nom de l'orientation
     * @return L'orientation
     */
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

    /**
     * Methode retourant l'offset de la ligne
     * @return L'offset de la ligne
     */
    public int getOffsetX() {
        return offsetX;
    }

    /**
     * Methode retournant l'offset de la colonne
     * @return L'offset de la colonne
     */
    public int getOffsetY() {
        return offsetY;
    }

    /**
     * Methode retournant le nom de l'orientation
     * @return Le nom de l'orientation
     */
    public String getText() { return text; }

    /**
     * Methode retournant l'orientation opposée de l'orientation actuelle
     * @return L'orientation opposée de l'orientation actuelle
     */
    public Orientation getOppose(){
        switch (this){
            case NORD: return SUD;
            case EST: return OUEST;
            case SUD: return NORD;
            case OUEST: return EST;
            default: return null;
        }
    }

    /**
     * Methde retournant l'orientation suivante selon une rotation horaire
     * @return L'orientation suivante selon une rotation horaire
     */
    public Orientation getRotationHoraire(){
        switch (this){
            case NORD: return EST;
            case EST: return SUD;
            case SUD: return OUEST;
            case OUEST: return NORD;
            default: return null;
        }
    }

    /**
     * Methode retournant l'orientation en fonction d'une lettre
     * @param nom Premiere lettre de l'orientation
     * @return L'orientation en fonction d'une lettre
     */
    public static Orientation getOrientationByText(String nom)
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
