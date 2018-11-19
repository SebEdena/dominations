package jeu;

public enum Roi
{
    Rose,
    Jaune,
    Vert,
    Bleu;

    public static Roi getRoi(String couleur)
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
