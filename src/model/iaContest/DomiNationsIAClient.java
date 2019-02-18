package model.iaContest;

import grooptown.ia.PlayerConnector;
import grooptown.ia.model.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DomiNationsIAClient {

    public static final String gameUUID = "3bcfa637-0aed-451f-ac84-49d6484ff72e";
    public static final String baseUrl = "https://domi-nation.grooptown.com";
    public static final String playerName = "IA_2";

    public static PlayerConnector playerConnector;



    public static void main(String[] args) {
        PlayerConnector.baseUrl = baseUrl;

        playerConnector = new PlayerConnector(gameUUID);
        playerConnector.joinGame(playerName);

        int playerNumber = -1, otherPlayerNumber = -1;
        GameState g = null;

        do {
            if(waitUntilItsMyTurn()){
                g = PlayerConnector.getGameState(gameUUID);
                //g.getPreviousDraft().getDominoes()[0].getPlayer();
                if(playerNumber < 0) playerNumber = Utils.getPlayerNumber(g, playerName);
                if(otherPlayerNumber < 0) otherPlayerNumber = Utils.getOtherPlayer(g, playerName);
                if(g.getCurrentPlayer() != null && g.getCurrentPlayer().getName().equals(playerConnector.getPlayer().getName())){
                    List<Move> availableMoves = Arrays.asList(playerConnector.getAvailableMove().getMoves());
                    List<Move> copyMoves = new ArrayList<>(availableMoves);
                    Plateau p = Plateau.fromKingdom(g.getKingdoms()[playerNumber]);
                    Plateau pOther = Plateau.fromKingdom(g.getKingdoms()[otherPlayerNumber]);
                    PlacedDomino tmpPlacedDomino = null;

                    int potentielAvant = Utils.getSumPotentiel(Utils.getPotentiels(p,null));
                    System.out.print("Potentiel avant : ");
                    System.out.println(potentielAvant);
                    //Filtrage par les scores plateau
                    copyMoves = copyMoves.stream().sorted((o1, o2) -> Utils.sortByScore(p, o1, o2)).collect(Collectors.toList());
                    tmpPlacedDomino = copyMoves.get(0).getPlacedDomino();
                    if(tmpPlacedDomino != null) p.addPlacedDomino(tmpPlacedDomino);
                    int bestScore = Score.getTotalScore(p);
                    if(tmpPlacedDomino != null) p.resetPlacedDomino();
                    copyMoves = copyMoves.stream().filter(move -> Utils.filterByBestScore(p, move, bestScore)).collect(Collectors.toList());
                    System.out.println();
                    //Filtrage par les plus gros domaines
                    copyMoves = copyMoves.stream().sorted((o1, o2) -> Utils.sortByBigDomain(p, o1, o2)).collect(Collectors.toList());
                    tmpPlacedDomino = copyMoves.get(0).getPlacedDomino();
                    if(tmpPlacedDomino != null) p.addPlacedDomino(tmpPlacedDomino);
                    int bestDomain = Score.getDomainScore(p);
                    if(tmpPlacedDomino != null) p.resetPlacedDomino();
                    copyMoves = copyMoves.stream().filter(move -> Utils.filterByBigDomain(p, move, bestDomain)).collect(Collectors.toList());

                    //Filtrage par le plus grand nombre de couronnes
                    copyMoves = copyMoves.stream().sorted((o1, o2) -> Utils.sortByHighestCrowns(p, o1, o2)).collect(Collectors.toList());
                    tmpPlacedDomino = copyMoves.get(0).getPlacedDomino();
                    if(tmpPlacedDomino != null) p.addPlacedDomino(tmpPlacedDomino);
                    int bestCrowns = Score.getCrownScore(p);
                    if(tmpPlacedDomino != null) p.resetPlacedDomino();
                    copyMoves = copyMoves.stream().filter(move -> Utils.filterByHighestCrowns(p, move, bestCrowns)).collect(Collectors.toList());

                    // Récupération des dominos restants
                    List<DominoesElement> dominos = Arrays.asList(g.getCurrentDraft().getDominoes());
                    System.out.println(dominos);

                    // Calcul du potentiel de l'autre joueur
                    for (DominoesElement d : dominos) {
                        if(d.getPlayer() == null) {
                            System.out.println("--------------------------");
                            System.out.println(Utils.getSumPotentiel(Utils.getPotentiels(pOther, d.getDomino()))-
                                    Utils.getSumPotentiel(Utils.getPotentiels(pOther, null)));
                            System.out.println("--------------------------");
                        }
                    }

                    System.out.println("TOUR : " + g.getTurn());
                    System.out.println("BEST SCORE : " + bestScore);
                    System.out.println("BEST DOMAIN : " + bestDomain);
                    System.out.println("BEST CROWNS : " + bestCrowns);
                    /*System.out.println("CASES : " + p.getCases().size());
                    if(availableMoves.get(0).getPlacedDomino() != null) p.addPlacedDomino(availableMoves.get(0).getPlacedDomino());
                    System.out.println("SCORE : " + Score.getTotalScore(p));*/
                    System.out.print("Potentiel après : ");
                    System.out.println(Utils.getSumPotentiel(Utils.getPotentiels(p,copyMoves.get(0).getChosenDomino())));
                    System.out.println("___________________________________________________________________________________________");
                    playerConnector.playMove(copyMoves.get(0).getNumber());
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
