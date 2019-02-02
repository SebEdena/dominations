package model.iaContest;

import grooptown.ia.model.*;
import model.plateau.Case;
import model.plateau.Orientation;
import model.plateau.Terrain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Plateau {

    private static final int NB_COL_LIG = 9, DECAL_TABLEAU = 4, FULL_KINGDOM = 5;
    private int minX, minY, maxX, maxY;
    private int[] tmpBounds;
    private Case[][] tableau;
    private List<Case> cases;
    private Position castlePosition;
    private PlacedDomino placedDomino = null;

    private Plateau(){
        minX = DECAL_TABLEAU;
        maxX = DECAL_TABLEAU;
        minY = DECAL_TABLEAU;
        maxY = DECAL_TABLEAU;
        tmpBounds = new int[4];
        cases = new ArrayList<>();
        castlePosition = new Position();
        tableau = new Case[NB_COL_LIG][NB_COL_LIG];
    }

    public static Plateau fromKingdom(Kingdom k){
        Plateau plateau = new Plateau();
        plateau.importPlateau(k);
        return plateau;
    }

    public static int getNbColLig() {
        return NB_COL_LIG;
    }

    public static int getDecalTableau() {
        return DECAL_TABLEAU;
    }

    public static int getFullKingdom() {
        return FULL_KINGDOM;
    }

    public Case[][] getTableau() {
        return tableau;
    }

    public List<Case> getCases() {
        return cases;
    }

    public PlacedDomino getPlacedDomino() {
        return placedDomino;
    }

    public int getMinX() {
        return minX;
    }

    public int getMinY() {
        return minY;
    }

    public int getMaxX() {
        return maxX;
    }

    public int getMaxY() {
        return maxY;
    }

    public Position getCastlePosition() {
        return castlePosition;
    }

    private void importPlateau(Kingdom k) {
        for(PlacedTiles pl : k.getPlacedTiles()){
            Case caseDom = new Case(pl.getTile().getCrowns(), Terrain.getTerrainEN(pl.getTile().getTerrain()));
            cases.add(caseDom);
            tableau[pl.getPosition().getRow() + DECAL_TABLEAU][pl.getPosition().getCol() + DECAL_TABLEAU] = caseDom;
            updateBounds(pl.getPosition().getRow() + DECAL_TABLEAU, pl.getPosition().getCol() + DECAL_TABLEAU);
            if(caseDom.getTerrain().equals(Terrain.CHATEAU)){
                castlePosition.setRow(pl.getPosition().getRow() + DECAL_TABLEAU);
                castlePosition.setCol(pl.getPosition().getCol() + DECAL_TABLEAU);
            }
        }
    }

    public void addPlacedDomino(PlacedDomino placedDomino){
        this.placedDomino = placedDomino;
        tmpBounds[0] = minX;
        tmpBounds[1] = maxX;
        tmpBounds[2] = minY;
        tmpBounds[3] = maxY;

        Case case1 = new Case(placedDomino.getDomino().getTile1().getCrowns(), Terrain.getTerrainEN(placedDomino.getDomino().getTile1().getTerrain()));
        Case case2 = new Case(placedDomino.getDomino().getTile2().getCrowns(), Terrain.getTerrainEN(placedDomino.getDomino().getTile2().getTerrain()));

        tableau[placedDomino.getTile1Position().getRow() + DECAL_TABLEAU][placedDomino.getTile1Position().getCol() + DECAL_TABLEAU] = case1;
        updateBounds(placedDomino.getTile1Position().getRow() + DECAL_TABLEAU, placedDomino.getTile1Position().getCol() + DECAL_TABLEAU);
        cases.add(case1);

        tableau[placedDomino.getTile2Position().getRow() + DECAL_TABLEAU][placedDomino.getTile2Position().getCol() + DECAL_TABLEAU] = case2;
        updateBounds(placedDomino.getTile2Position().getRow() + DECAL_TABLEAU, placedDomino.getTile2Position().getCol() + DECAL_TABLEAU);
        cases.add(case2);
    }

    public void resetPlacedDomino(){
        tableau[placedDomino.getTile1Position().getRow() + DECAL_TABLEAU][placedDomino.getTile1Position().getCol() + DECAL_TABLEAU] = null;
        tableau[placedDomino.getTile2Position().getRow() + DECAL_TABLEAU][placedDomino.getTile2Position().getCol() + DECAL_TABLEAU] = null;
        this.placedDomino = null;
        minX = tmpBounds[0];
        maxX = tmpBounds[1];
        minY = tmpBounds[2];
        maxY = tmpBounds[3];
        cases = cases.subList(0, cases.size() - 2);
    }

    public int getXLength() {
        return maxX - minX + 1;
    }

    public int getYLength() {
        return maxY - minY + 1;
    }

    private void updateBounds(int row, int col){
        minX = Math.min(row, minX);
        maxX = Math.max(row, maxX);
        minY = Math.min(col, minY);
        maxY = Math.max(col, maxY);
    }
}
