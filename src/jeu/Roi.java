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
            case "Champs" :
                return Rose;
            case "Foret" :
                return Jaune;
            case "Mer" :
                return Vert;
            case "Prairie" :
                return Bleu;
            default :
                return null;
        }
    }
}
