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

    Roi(String libelle, String hexColor){
        this.libelle = libelle;
        this.hexColor = hexColor;
    }

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

    public static int getRoiIndex(Roi roi){
        for(int i = 0; i < values().length; i++){
            if(roi.equals(values()[i])) return i;
        }
        return -1;
    }

    public static Roi getByColor(String color){
        for(Roi r : values()){
            if(r.getColor().equals(color)) return r;
        }
        return null;
    }

    public String getLibelle() {
        return libelle;
    }

    public String getColor(){
        return hexColor;
    }

}
