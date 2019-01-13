/**
 * Classe permettant de décrire le roi
 * @author Mathieu Valentin, Sébastien Viguier, Laurent Yu
 * @version 1.0
 */
package model.jeu;

import java.io.Serializable;

public enum Roi implements Serializable
{
    Rose("Rose", "#f06292"),
    Jaune("Jaune", "#fbc02d"),
    Vert("Vert", "#8bc34a"),
    Bleu("Bleu","#0288d1");

    private String libelle;
    private String hexColor;

    /**
     * Constructeur de la classe d'énumération du roi
     * @param libelle Couleur du roi
     * @param hexColor Code couleur du roi en héxadécimal
     */
    Roi(String libelle, String hexColor){
        this.libelle = libelle;
        this.hexColor = hexColor;
    }

    /**
     * Methode retournant le roi
     * @param enumCouleur Index de la couleur
     * @return Le roi choisi
     */
    public static Roi getRoiInt(int enumCouleur)
    {
        switch(enumCouleur)
        {
            case 0 :
                return Rose;
            case 1 :
                return Jaune;
            case 2 :
                return Vert;
            case 3 :
                return Bleu;
            default :
                return null;
        }
    }

    /**
     * Methode retournant l'index du roi
     * @param roi Roi dont l'index est cherché
     * @return L'index du roi cherché
     */
    public static int getRoiIndex(Roi roi){
        for(int i = 0; i < values().length; i++){
            if(roi.equals(values()[i])) return i;
        }
        return -1;
    }

    /**
     * Methode retournant le roi en fonction d'une couleur
     * @param color Couleur du roi cherché
     * @return Roi de la couleur
     */
    public static Roi getByColor(String color){
        for(Roi r : values()){
            if(r.getColor().equals(color)) return r;
        }
        return null;
    }

    /**
     * Methode retournant la couleur du roi
     * @return La couleur du roi
     */
    public String getLibelle() {
        return libelle;
    }

    /**
     * Methode retournant la couleur du roi en héxadécimal
     * @return La couleur du roi en héxadécimal
     */
    public String getColor(){
        return hexColor;
    }

}
