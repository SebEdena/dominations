package model.iaContest;

import grooptown.ia.model.PlacedDomino;
import grooptown.ia.model.Tile;
import model.plateau.Case;
import model.plateau.Orientation;
import model.plateau.Terrain;

import java.util.ArrayList;
import java.util.List;

public class Score {

    public static int getTotalScore(Plateau p){
        return getBasicScore(p) + getCentralCastleBonus(p) + getFullKingdomBonus(p);
    }

    public static int getBasicScore(Plateau p){
        int sommePoints = 0, NB_COL_LIG = Plateau.getNbColLig();
        Case[][] tableau = p.getTableau();
        List<Case> pileCasesVisitees = new ArrayList<Case>();
        for(int i = 0; i < NB_COL_LIG; i++)
        {
            for(int j = 0 ; j < NB_COL_LIG; j++)
            {
                if(tableau[i][j] != null && !pileCasesVisitees.contains(tableau[i][j]) &&
                        !tableau[i][j].getTerrain().equals(Terrain.CHATEAU)) //si la case (i;j) n'est pas vide et qu'il n'a jamais été parcouru et qu'il n'est pas une tuile
                {
                    Case caseTemoin = tableau[i][j];
                    List<Case> casesSimilaires = new ArrayList<Case>();
                    rechercheCaseSimilaire(p,i,j, pileCasesVisitees, caseTemoin, casesSimilaires);

                    int couronne = 0;
                    int compteurCase = 0;
                    for(Case c : casesSimilaires)
                    {
                        compteurCase++;
                        couronne = couronne + c.getNbCouronne();
                    }
                    sommePoints = sommePoints + compteurCase * couronne;
                    //System.out.println(sommePoints);
                }
            }
        }
        return sommePoints;
    }

    public static int getDomainScore(Plateau p){
        int grosDomaine = 0, NB_COL_LIG = Plateau.getNbColLig();
        Case[][] tableau = p.getTableau();
        List<Case> pileCasesVisitees = new ArrayList<Case>();
        for(int i = 0; i < NB_COL_LIG; i++)
        {
            for(int j = 0 ; j < NB_COL_LIG; j++)
            {
                if(tableau[i][j] != null && !pileCasesVisitees.contains(tableau[i][j]) &&
                        !tableau[i][j].getTerrain().equals(Terrain.CHATEAU))
                {
                    Case caseTemoin = tableau[i][j];
                    List<Case> casesSimilaires = new ArrayList<Case>();
                    rechercheCaseSimilaire(p,i,j, pileCasesVisitees, caseTemoin, casesSimilaires);

                    int couronne = 0;
                    int compteurCase = 0;
                    for(Case c : casesSimilaires)
                    {
                        compteurCase++;
                        couronne = couronne + c.getNbCouronne();
                    }
                    if(grosDomaine < compteurCase)
                    {
                        grosDomaine = compteurCase;
                    }
                }
            }
        }
        return grosDomaine;
    }

    public static int getCrownScore(Plateau p){
        int crownScore = 0;
        for(Case c : p.getCases()){
            crownScore += c.getNbCouronne();
        }
        return crownScore;
    }

    public static int getCentralCastleBonus(Plateau p){
        int xOff = Math.max(p.getXLength() - 3, 0);
        int yOff = Math.max(p.getYLength() - 3, 0);
        int xMin = p.getMinX();
        int xMax = p.getMaxX();
        int yMin = p.getMinY();
        int yMax = p.getMaxY();
        int x = p.getCastlePosition().getRow();
        int y = p.getCastlePosition().getCol();
        boolean xCenter = false, yCenter = false;

        if(x >= xMin + xOff && x <= xMax - xOff) xCenter = true;
        if(y >= yMin + yOff && y <= yMax - yOff) yCenter = true;

        if(xCenter && yCenter){
            return 10;
        }else{
            return 0;
        }
    }

    public static int getFullKingdomBonus(Plateau p){
        if(p.getCases().size() == Plateau.getFullKingdom() * Plateau.getFullKingdom()){
            return 5;
        }else{
            return 0;
        }
    }

    private static void rechercheCaseSimilaire(Plateau p, int x, int y, List<Case> pileCasesVisitees, Case caseTemoin, List<Case> casesAdjacentes)
    {
        Case[][] tableau = p.getTableau();
        int NB_COL_LIG = Plateau.getNbColLig();
        casesAdjacentes.add(caseTemoin);
        pileCasesVisitees.add(caseTemoin);
        List<Case> casesTrouvees = new ArrayList<Case>();

        for(Orientation o : Orientation.values()) // à chaque orientation on vérifie s'il y a une case de même domaine et non comptabilisée
        {
            if(x + o.getOffsetX() >= 0 && x + o.getOffsetX() < NB_COL_LIG &&
                    y + o.getOffsetY() >= 0 && y + o.getOffsetY() < NB_COL_LIG &&
                    tableau[x + o.getOffsetX()][y + o.getOffsetY()] != null &&
                    tableau[x + o.getOffsetX()][y + o.getOffsetY()].getTerrain().equals(caseTemoin.getTerrain()) &&
                    !pileCasesVisitees.contains(tableau[x + o.getOffsetX()][y + o.getOffsetY()]))
            {
                casesTrouvees.add(tableau[x + o.getOffsetX()][y + o.getOffsetY()]);
                pileCasesVisitees.add(tableau[x + o.getOffsetX()][y + o.getOffsetY()]);
                rechercheCaseSimilaire(p,x + o.getOffsetX(), y + o.getOffsetY(),
                        pileCasesVisitees,tableau[x + o.getOffsetX()][y + o.getOffsetY()],casesAdjacentes);
            }
        }
    }
}
