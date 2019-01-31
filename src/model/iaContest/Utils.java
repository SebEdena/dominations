package model.iaContest;

import grooptown.ia.PlayerConnector;
import grooptown.ia.model.GameState;
import grooptown.ia.model.Kingdom;
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
}
