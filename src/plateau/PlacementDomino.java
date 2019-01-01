package plateau;

public class PlacementDomino {

    IDomino domino;

    Integer caseId;

    Integer row, column;

    Orientation sens;

    boolean onPlateau;

    public PlacementDomino(IDomino domino, int caseId, int row, int column, Orientation sens) {
        this.domino = domino;
        this.caseId = caseId;
        this.row = row;
        this.column = column;
        this.sens = sens;
        this.onPlateau = false;
    }

    public PlacementDomino(IDomino domino, Orientation sens) {
        this.domino = domino;
        this.sens = sens;
    }

    public IDomino getDomino() {
        return domino;
    }

    public void setDomino(IDomino domino) {
        this.domino = domino;
    }

    public int getCaseId() {
        return caseId;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public Orientation getSens() {
        return sens;
    }

    public void setSens(Orientation sens) {
        this.sens = sens;
    }

    public boolean isOnPlateau(){
        return caseId != null && row != null && column != null;
    }

    public void positionOnPlateau(int caseId, int row, int col){
        this.caseId = caseId;
        this.row = row;
        this.column = col;
    }

    public void removeFromPlateau(){
        this.caseId = null;
        this.row = null;
        this.column = null;
    }
}
