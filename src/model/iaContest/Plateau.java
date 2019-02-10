package model.iaContest;

import grooptown.ia.model.Kingdom;
import grooptown.ia.model.PlacedDomino;
import grooptown.ia.model.PlacedTiles;
import grooptown.ia.model.Tile;
import model.plateau.Case;
import model.plateau.Orientation;
import model.plateau.Terrain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Plateau {

    private static final int NB_COL_LIG = 9, DECAL_TABLEAU = 4;
    private int minX, minY, maxX, maxY;
    private int[] tmpBounds;
    private Case[][] tableau;
    private List<Tile> tiles;
    private PlacedDomino placedDomino = null;

    private Plateau(){
        minX = DECAL_TABLEAU;
        maxX = DECAL_TABLEAU;
        minY = DECAL_TABLEAU;
        maxY = DECAL_TABLEAU;
        tmpBounds = new int[4];
        tiles = new ArrayList<>();
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

    public Case[][] getTableau() {
        return tableau;
    }

    public List<Tile> getTiles() {
        return tiles;
    }

    public PlacedDomino getPlacedDomino() {
        return placedDomino;
    }

    private void importPlateau(Kingdom k) {
        tableau = new Case[NB_COL_LIG][NB_COL_LIG];
        for(PlacedTiles pl : k.getPlacedTiles()){
            tiles.add(pl.getTile());
            tableau[pl.getPosition().getRow() + DECAL_TABLEAU][pl.getPosition().getCol() + DECAL_TABLEAU] =
                    new Case(pl.getTile().getCrowns(), Terrain.getTerrainEN(pl.getTile().getTerrain()));
            updateBounds(pl.getPosition().getRow() + DECAL_TABLEAU, pl.getPosition().getCol() + DECAL_TABLEAU);
        }
    }

    public void addPlacedDomino(PlacedDomino placedDomino){
        this.placedDomino = placedDomino;
        tmpBounds[0] = minX;
        tmpBounds[1] = maxX;
        tmpBounds[2] = minY;
        tmpBounds[3] = maxY;

        tableau[placedDomino.getTile1Position().getRow() + DECAL_TABLEAU][placedDomino.getTile1Position().getCol() + DECAL_TABLEAU] =
                new Case(placedDomino.getDomino().getTile1().getCrowns(), Terrain.getTerrainEN(placedDomino.getDomino().getTile1().getTerrain()));
        updateBounds(placedDomino.getTile1Position().getRow() + DECAL_TABLEAU, placedDomino.getTile1Position().getCol() + DECAL_TABLEAU);

        tableau[placedDomino.getTile2Position().getRow() + DECAL_TABLEAU][placedDomino.getTile2Position().getCol() + DECAL_TABLEAU] =
                new Case(placedDomino.getDomino().getTile2().getCrowns(), Terrain.getTerrainEN(placedDomino.getDomino().getTile2().getTerrain()));
        updateBounds(placedDomino.getTile2Position().getRow() + DECAL_TABLEAU, placedDomino.getTile2Position().getCol() + DECAL_TABLEAU);
    }

    public void resetPlacedDomino(){
        tableau[placedDomino.getTile1Position().getRow() + DECAL_TABLEAU][placedDomino.getTile1Position().getCol() + DECAL_TABLEAU] = null;
        tableau[placedDomino.getTile2Position().getRow() + DECAL_TABLEAU][placedDomino.getTile2Position().getCol() + DECAL_TABLEAU] = null;
        this.placedDomino = null;
        minX = tmpBounds[0];
        maxX = tmpBounds[1];
        minY = tmpBounds[2];
        maxY = tmpBounds[3];
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