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

    public static final String gameUUID = "2b911dd8-40c4-4ddb-967c-58381f8d1c2a";
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
            waitUntilItsMyTurn();
            g = PlayerConnector.getGameState(gameUUID);
            if(playerNumber < 0) playerNumber = Utils.getPlayerNumber(g, playerName);
            if(g.getCurrentPlayer() != null && g.getCurrentPlayer().getName().equals(playerConnector.getPlayer().getName())){
                List<Move> availableMoves = Arrays.asList(playerConnector.getAvailableMove().getMoves());
                System.out.println(availableMoves);

                playerConnector.playMove(0);
            }
        } while (!g.isGameOver());
    }

    public static void waitUntilItsMyTurn(){
        while (true) {
            try {
                GameState gameState = PlayerConnector.getGameState(gameUUID);
                if (gameState.getCurrentPlayer().getName().equals(playerConnector.getPlayer().getName())) {
                    return;
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
