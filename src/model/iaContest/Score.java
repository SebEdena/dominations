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
        PlacedDomino pld = p.getPlacedDomino();
        for(Tile t : p.getTiles()){
            crownScore += t.getCrowns();
        }
        if(pld != null){
            crownScore += pld.getDomino().getTile1().getCrowns() + pld.getDomino().getTile2().getCrowns();
        }
        return crownScore;
    }

    public static int getCentralCastleBonus(Plateau p){
        if(p.getXLength() >= 4 || p.getYLength() >= 4){
            int x = p.getXLength(), y = p.getYLength();
            int xOff = x - 3, yOff = y - 3;
            return 0;
        }else{
            return 10;
        }
    }

    public static int getFullKingdomBonus(Plateau p){
        return 0;
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
