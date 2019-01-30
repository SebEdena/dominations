package model.iaContest;

import grooptown.ia.PlayerConnector;
import grooptown.ia.model.AvailableMoves;
import grooptown.ia.model.GameState;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

public class DomiNationsIAClient {

    public static final String gameUUID = "ad740105-9850-449a-9fdd-89dd0ee6d2ab";
    public static final String baseUrl = "https://domi-nation.grooptown.com";
    public static final String playerName = "IA_2";

    public static PlayerConnector playerConnector;

    public static void main(String[] args) {
        PlayerConnector.baseUrl = baseUrl;
        playerConnector = new PlayerConnector(gameUUID);
        playerConnector.joinGame(playerName);

        GameState g = null;

        do {
            waitUntilItsMyTurn();
            g = PlayerConnector.getGameState(gameUUID);
            if(g.getCurrentPlayer() != null && g.getCurrentPlayer().getName().equals(playerConnector.getPlayer().getName())){
                AvailableMoves availableMoves = playerConnector.getAvailableMove();
                System.out.println(availableMoves.getMoves()[0]);
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
