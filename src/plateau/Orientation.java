package plateau;

public enum Orientation {

    NORD(-1, 0), EST(0, 1), SUD(1, 0), OUEST(-1, 0);

    private int offsetX, offsetY;

    Orientation(int x, int y){
        this.offsetX = x;
        this.offsetY = y;
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
}
