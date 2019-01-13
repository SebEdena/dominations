package model.plateau;

public class PlacementDomino {

    private IDomino domino;

    private Integer caseId;

    private Integer row, column;

    private Orientation sens;

    private int[] translation;

    /**
     * Constructeur d'un placement de domino
     * @param domino Domino à placer
     * @param sens Orientation du domino
     */
    public PlacementDomino(IDomino domino, Orientation sens) {
        this.domino = domino;
        this.sens = sens;
    }

    /**
     * Methode retournant le domino
     * @return Le domino
     */
    public IDomino getDomino() {
        return domino;
    }

    /**
     * Methode retournant l'identifiant de la case du domino
     * @return L'identifiant de la case du domino
     */
    public int getCaseId() {
        return caseId;
    }

    /**
     * Methode modifiant l'identifiant de la case 1 du domino
     * @param caseId L'identifiant de la case du domino
     */
    public void setCaseId(Integer caseId) {
        this.caseId = caseId;
    }

    /**
     * Methode retournant le numéro de ligne de la case 1  du domino
     * @return Le numéro de ligne de la case 1  du domino
     */
    public int getRow() {
        return row;
    }

    /**
     * Methode retournant le numéro de colonne de la case 1  du domino
     * @return Le numéro de colonne de la case 1  du domino
     */
    public int getColumn() {
        return column;
    }

    /**
     * Methode retournant l'orientation du domino
     * @return L'orientation du domino
     */
    public Orientation getSens() {
        return sens;
    }

    /**
     * Methode modifiant l'orientation du domino
     * @param sens Le nouveau sens du domino
     */
    public void setSens(Orientation sens) {
        this.sens = sens;
    }

    /**
     * Methode retournant l'offset de ligne de la case 2  du domino
     * @return L'offset de ligne de la case 2  du domino
     */
    public Integer getRowCase2Offset(){
        if(caseId == null){
            return 0;
        }
        return sens.getOffsetX();
    }

    /**
     * Methode retournant l'offset de colonne de la case 2  du domino
     * @return L'offset de colonne de la case 2  du domino
     */
    public Integer getColCase2Offset(){
        if(caseId == null){
            return 0;
        }
        return sens.getOffsetY();
    }

    /**
     * Methode retournant le numéro de ligne de la case 2  du domino
     * @return Le numéro de ligne de la case 2  du domino
     */
    public Integer getRowCase2(){
        if(row == null || caseId == null){
            return null;
        }
        return row+getRowCase2Offset();
    }

    /**
     * Methode retournant le numéro de colonne de la case 2  du domino
     * @return Le numéro de colonne de la case 2  du domino
     */
    public Integer getColCase2(){
        if(column == null || caseId == null){
            return null;
        }
        return column+getColCase2Offset();
    }

    /**
     * Methode retournant l'autre case d'un domino
     * @param oppose (true) autre case, (false) pour la case actuelle
     * @return La case
     */
    public Case getCase(boolean oppose){
        return domino.getCases()[oppose?Math.abs(caseId - 1):caseId];
    }

    /**
     * Methode retournant le nombre case que l'on doit translater
     * @return Le nombre case que l'on doit translater
     */
    public int[] getTranslation() {
        return translation;
    }

    /**
     * Methode modifiant le nombre case que l'on doit translater
     * @param translation Le nombre case que l'on doit translater
     */
    public void setTranslation(int[] translation){
        this.translation = translation;
    }

    /**
     * Methode renseignant si une translation de plateau est nécessaire
     * @return (true) si nécesaire, (false) sinon
     */
    public boolean needsTranslation(){
        if(translation == null) return false;
        return translation[0] != 0 || translation[1] != 0;
    }

    /**
     * Methode retournant si le placement du domino est dans le plateau
     * @return (true) si le placement du domino est dans le plateau, (false) sinon
     */
    public boolean isOnPlateau(){
        return row != null && column != null;
    }

    /**
     * Methode modifiant le placement du domino dans le plateau
     * @param row Numéro de ligne
     * @param col Numéro de colonne
     */
    public void positionOnPlateau(int row, int col){
        this.row = row;
        this.column = col;
    }

    /**
     * Methode retirant le placement du domino
     */
    public void removeFromPlateau(){
        this.caseId = null;
        this.row = null;
        this.column = null;
        this.translation = null;
    }

    /**
     * Methode retournant les nouvelles limites du plateau en fonction de offsetX
     * @param xBounds les nouvelles limites du plateau en x
     * @return Les nouvelles limites du plateau en fonction de offsetX
     */
    public int[] getNewXBounds(int[] xBounds){
        xBounds[0] = Math.min(xBounds[0] + 1 + translation[0], Math.min(getRow(), getRowCase2()));
        xBounds[1] = Math.max(xBounds[1] + 1 + translation[0], Math.max(getRow(), getRowCase2()));
        return xBounds;
    }

    /**
     * Methode retournant les nouvelles limites du plateau en fonction de offsetY
     * @param yBounds les nouvelles limites du plateau en y
     * @return Les nouvelles limites du plateau en fonction de offsetY
     */
    public int[] getNewYBounds(int[] yBounds){
        yBounds[0] = Math.min(yBounds[0] + 1 + translation[1], Math.min(getColumn(), getColCase2()));
        yBounds[1] = Math.max(yBounds[1] + 1 + translation[1], Math.max(getColumn(), getColCase2()));
        return yBounds;
    }
}
