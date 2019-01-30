package model.iaContest;

import grooptown.ia.PlayerConnector;
import grooptown.ia.model.Game;

public class DomiNationsIAServer {

    public static void main(String[] args) {
        // With JDK inferior to 8u101 you need to disable SSL validation.
        // disableSSLValidation();
        PlayerConnector.baseUrl = "https://domi-nation.grooptown.com";
        int playerCount = 2;

        Game newGame = PlayerConnector.createNewGame(playerCount);

        System.out.println("GAME UUID :");
        System.out.println(newGame.getUuid());
    }
}
