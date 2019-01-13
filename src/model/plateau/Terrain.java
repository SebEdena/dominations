/**
 * Enumération permettant de décrire  les types de terrains possible du jeu
 * @author Mathieu Valentin, Sébastien Viguier, Laurent Yu
 * @version 1.0
 */
package model.plateau;

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

    /**
     * Constructeur de la classe d'énumération du terrain
     * @param libelle
     * @param diminutif
     * @param hexColor
     */
    Terrain(String libelle, String diminutif, String hexColor){
        this.libelle = libelle;
        this.diminutif = diminutif;
        this.hexColor = hexColor;
    }

    /**
     * Methode retournant le terrain en fonction du nom du terrain
     * @param nom Le nom du terrain
     * @return Le terrain
     */
    public static Terrain getTerrain(String nom)
    {
        for(int i = 0; i < values().length; i++){
            if(nom.equals(values()[i].getLibelle())){
                return values()[i];
            }
        }
        return null;
    }

    /**
     * Methode retournant le nom du terrain
     * @return Le nom du terrain
     */
    public String getLibelle() { return libelle; }

    /**
     * Methode retournant le diminutif du nom du terrain
     * @return Le diminutif du nom du terrain
     */
    public String getDiminutif(){
        return this.diminutif;
    }

    /**
     * Methode retournant la couleur du terrain (en héxadécimal)
     * @return La couleur du terrain (en héxadécimal)
     */
    public String getColor() { return hexColor; }
}


