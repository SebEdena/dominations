package plateau;

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
}
