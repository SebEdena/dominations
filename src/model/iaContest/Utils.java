package model.iaContest;

import grooptown.ia.model.*;
import javafx.util.Pair;
import model.plateau.Case;
import model.plateau.Orientation;
import model.plateau.Terrain;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static int getPlayerNumber(GameState g, String playerName) {
        int i = 0;
        for (Kingdom k : g.getKingdoms()) {
            if (k.getPlayer().getName().equals(playerName)) return i;
            i++;
        }
        return -1;
    }

    public static int getOtherPlayer(GameState g, String playerName) {
        int i = 0;
        for (Kingdom k : g.getKingdoms()) {
            if (!k.getPlayer().getName().equals(playerName)) return i;
            i++;
        }
        return -1;
    }

    public static int sortByScore(Plateau p, Move m1, Move m2){
        if(m1.getPlacedDomino() != null) p.addPlacedDomino(m1.getPlacedDomino());
        int m1Score = Score.getTotalScore(p);
        if(m1.getPlacedDomino() != null) p.resetPlacedDomino();

        if(m2.getPlacedDomino() != null) p.addPlacedDomino(m2.getPlacedDomino());
        int m2Score = Score.getTotalScore(p);
        if(m1.getPlacedDomino() != null) p.resetPlacedDomino();

        return m2Score - m1Score;
    }

    public static boolean filterByBestScore(Plateau p, Move move, int bestScore) {
        if(move.getPlacedDomino() != null) p.addPlacedDomino(move.getPlacedDomino());
        int moveScore = Score.getTotalScore(p);
        if(move.getPlacedDomino() != null) p.resetPlacedDomino();

        return moveScore == bestScore;
    }

    public static int sortByBigDomain(Plateau p, Move m1, Move m2) {
        if(m1.getPlacedDomino() != null) p.addPlacedDomino(m1.getPlacedDomino());
        int m1Score = Score.getDomainScore(p);
        if(m1.getPlacedDomino() != null) p.resetPlacedDomino();

        if(m2.getPlacedDomino() != null) p.addPlacedDomino(m2.getPlacedDomino());
        int m2Score = Score.getDomainScore(p);
        if(m1.getPlacedDomino() != null) p.resetPlacedDomino();

        return m2Score - m1Score;
    }

    public static boolean filterByBigDomain(Plateau p, Move move, int bestScore) {
        if(move.getPlacedDomino() != null) p.addPlacedDomino(move.getPlacedDomino());
        int moveScore = Score.getDomainScore(p);
        if(move.getPlacedDomino() != null) p.resetPlacedDomino();
        return moveScore == bestScore;
    }

    public static int sortByHighestCrowns(Plateau p, Move m1, Move m2) {
        if(m1.getPlacedDomino() != null) p.addPlacedDomino(m1.getPlacedDomino());
        int m1Score = Score.getCrownScore(p);
        if(m1.getPlacedDomino() != null) p.resetPlacedDomino();

        if(m2.getPlacedDomino() != null) p.addPlacedDomino(m2.getPlacedDomino());
        int m2Score = Score.getCrownScore(p);
        if(m1.getPlacedDomino() != null) p.resetPlacedDomino();

        return m2Score - m1Score;
    }

    public static boolean filterByHighestCrowns(Plateau p, Move move, int bestScore) {
        if(move.getPlacedDomino() != null) p.addPlacedDomino(move.getPlacedDomino());
        int moveScore = Score.getCrownScore(p);
        if(move.getPlacedDomino() != null) p.resetPlacedDomino();
        return moveScore == bestScore;
    }

    private static List<Potentiel> getPotentiels(Plateau p, Domino d)
    {
        List<Potentiel> listePotentiels = new ArrayList<Potentiel>();
        Case[][] tableau = p.getTableau();
        List<Case> pileCasesVisitees = new ArrayList<Case>();
        int NB_COL_LIG = Plateau.getNbColLig();
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
                    Terrain terrain = caseTemoin.getTerrain();
                    if(d != null){
                        if(d.getTile1().getTerrain().equals(terrain.getLibelleEN())){
                            compteurCase++;
                            couronne += d.getTile1().getCrowns();
                        }
                        if(d.getTile2().getTerrain().equals(terrain.getLibelleEN())){
                            compteurCase++;
                            couronne += d.getTile2().getCrowns();
                        }
                    }
                    listePotentiels.add(new Potentiel(terrain,couronne,compteurCase,(couronne)*compteurCase));
                }
            }
        }
        return listePotentiels;
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

    public static int getSumPotentiel(List<Potentiel> liste){
        int sum = 0;
        for(Potentiel p : liste)
            sum += p.getPotentiel();
        return sum;
    }

    public static List<Pair<Integer, Integer>> getListScorePotentiel(Plateau p, List<DominoesElement> dominos) {
        List<Pair<Integer, Integer>> liste = new ArrayList<>();
        int potentielAvant = getSumPotentiel(getPotentiels(p,null));
        int score = Score.getTotalScore(p);
        for (DominoesElement d : dominos) {
            if(d.getPlayer() == null){
                liste.add(new Pair<>(d.getDomino().getNumber(),score + ( getSumPotentiel(getPotentiels(p,d.getDomino())) - potentielAvant)));
            }
        }
        return liste;
    }

    public static boolean peutJouer(String name, List<DominoesElement> dominos) {
        int nbDominos = 0;
        for (DominoesElement d : dominos) {
            if(d.getPlayer().getName().equals(name)){
                nbDominos++;
            }
        }
        return nbDominos < 2;
    }

    public static List<Integer> getListeDominoMostValuable(List<Move> copyMoves) {
        List<Integer> liste = new ArrayList<>();
        for (Move m : copyMoves){
            if(!liste.contains(m.getChosenDomino().getNumber()))
                liste.add(m.getChosenDomino().getNumber());
        }
        return liste;
    }
}
