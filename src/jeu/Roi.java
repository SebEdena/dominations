package jeu;

public enum Roi
{
    Rose,
    Jaune,
    Vert,
    Bleu;

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

    public static Roi getRoiCol(String couleur)
    {
        switch(couleur)
        {
            case "Rose" :
                return Rose;
            case "Jaune" :
                return Jaune;
            case "Vert" :
                return Vert;
            case "Bleu" :
                return Bleu;
            default :
                return null;
        }
    }
}
