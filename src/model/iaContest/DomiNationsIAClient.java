package model.iaContest;

import grooptown.ia.PlayerConnector;
import grooptown.ia.model.AvailableMoves;
import grooptown.ia.model.GameState;
import grooptown.ia.model.Move;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Arrays;
import java.util.List;

public class DomiNationsIAClient {

    public static final String gameUUID = "14ca3028-afa2-4800-bba2-6aa98fa80087";
    public static final String baseUrl = "https://domi-nation.grooptown.com";
    public static final String playerName = "IA_2";

    public static PlayerConnector playerConnector;

    public static void main(String[] args) {
        PlayerConnector.baseUrl = baseUrl;
        playerConnector = new PlayerConnector(gameUUID);
        playerConnector.joinGame(playerName);

        int playerNumber = -1;
        GameState g = null;

        do {
            if(waitUntilItsMyTurn()){
                g = PlayerConnector.getGameState(gameUUID);
                if(playerNumber < 0) playerNumber = Utils.getPlayerNumber(g, playerName);
                if(g.getCurrentPlayer() != null && g.getCurrentPlayer().getName().equals(playerConnector.getPlayer().getName())){
                    List<Move> availableMoves = Arrays.asList(playerConnector.getAvailableMove().getMoves());
                    System.out.println("TOUR : " + g.getTurn());
                    Plateau p = Plateau.fromKingdom(g.getKingdoms()[playerNumber]);
                    System.out.println("CASES : " + p.getCases().size());
                    if(availableMoves.get(0).getPlacedDomino() != null) p.addPlacedDomino(availableMoves.get(0).getPlacedDomino());
                    System.out.println("SCORE : " + Score.getTotalScore(p));
                    playerConnector.playMove(0);
                    System.out.println();
                }
            } else{
                System.out.println("Game Over.");
                break;
            }
        } while (true);
    }

    public static boolean waitUntilItsMyTurn(){
        while (true) {
            try {
                GameState gameState = PlayerConnector.getGameState(gameUUID);
                if(gameState.isGameOver()) return false;
                if (gameState.getCurrentPlayer() != null && gameState.getCurrentPlayer().getName().equals(playerConnector.getPlayer().getName())) {
                    return true;
                }
            } catch (HttpClientErrorException e) {
                if (e.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                    System.out.println("Game has not started yet.");
                }
            }
            try { Thread.sleep(500); } catch(InterruptedException ignored){}
        }
    }
}
