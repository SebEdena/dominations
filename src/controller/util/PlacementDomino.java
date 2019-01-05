package controller.util;

import plateau.Case;
import plateau.IDomino;
import plateau.Orientation;

public class PlacementDomino {

    private IDomino domino;

    private Integer caseId;

    private Integer row, column;

    private Orientation sens;

    public PlacementDomino(IDomino domino, Orientation sens) {
        this.domino = domino;
        this.sens = sens;
    }

    public IDomino getDomino() {
        return domino;
    }

    public int getCaseId() {
        return caseId;
    }

    public void setCaseId(Integer caseId) {
        this.caseId = caseId;
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

    public Integer getRowCase2Offset(){
        if(caseId == null){
            return 0;
        }
        return sens.getOffsetX();
    }

    public Integer getColCase2Offset(){
        if(caseId == null){
            return 0;
        }
        return sens.getOffsetY();
    }

    public Integer getRowCase2(){
        if(row == null || caseId == null){
            return null;
        }
        return row+getRowCase2Offset();
    }

    public Integer getColCase2(){
        if(column == null || caseId == null){
            return null;
        }
        return column+getColCase2Offset();
    }

    public Case getCase(boolean oppose){
        return domino.getCases()[oppose?Math.abs(caseId - 1):caseId];
    }

    public boolean isOnPlateau(){
        return row != null && column != null;
    }

    public void positionOnPlateau(int row, int col){
        this.row = row;
        this.column = col;
    }

    public void removeFromPlateau(){
        this.caseId = null;
        this.row = null;
        this.column = null;
    }
}
