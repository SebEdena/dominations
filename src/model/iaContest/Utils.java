package model.iaContest;

import grooptown.ia.PlayerConnector;
import grooptown.ia.model.GameState;
import grooptown.ia.model.Kingdom;
import grooptown.ia.model.Move;
import grooptown.ia.model.Player;

public class Utils {

    public static int getPlayerNumber(GameState g, String playerName) {
        int i = 0;
        for (Kingdom k : g.getKingdoms()) {
            if (k.getPlayer().getName().equals(playerName)) return i;
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
}
