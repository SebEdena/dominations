package controller;

import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import controller.util.SceneSwitcher;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import model.joueur.Joueur;
import model.jeu.NbJoueur;

/**
 * Classe du controlleur de la sous-scène d'affichage de scores
 * @author Mathieu Valentin, Sébastien Viguier, Laurent Yu
 * @version 1.0
 */
public class ScoresController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Label scoresVainqueursLabel;

    @FXML
    private VBox scoresContainer;

    @FXML
    private GridPane scoresRow1;

    @FXML
    private GridPane scoresRow2;

    @FXML
    private GridPane scoresRow3;

    @FXML
    private GridPane scoresRow4;

    @FXML
    private JFXButton scoresHomeButton;

    @FXML
    private JFXButton scoresSettingsButton;

    private static final int classementIndex = 0;
    private static final int nomIndex = 1;
    private static final int pointsIndex = 2;

    private List<GridPane> scores;

    /**
     * Fonction appelée automatiquement à l'instantiation de la classe
     */
    @FXML
    void initialize() {
        assert scoresVainqueursLabel != null : "fx:id=\"scoresVainqueursLabel\" was not injected: check your FXML file 'scores.fxml'.";
        assert scoresContainer != null : "fx:id=\"scoresContainer\" was not injected: check your FXML file 'scores.fxml'.";
        assert scoresRow1 != null : "fx:id=\"scoresRow1\" was not injected: check your FXML file 'scores.fxml'.";
        assert scoresRow2 != null : "fx:id=\"scoresRow2\" was not injected: check your FXML file 'scores.fxml'.";
        assert scoresRow3 != null : "fx:id=\"scoresRow3\" was not injected: check your FXML file 'scores.fxml'.";
        assert scoresRow4 != null : "fx:id=\"scoresRow4\" was not injected: check your FXML file 'scores.fxml'.";
        assert scoresHomeButton != null : "fx:id=\"scoresHomeButton\" was not injected: check your FXML file 'scores.fxml'.";
        assert scoresSettingsButton != null : "fx:id=\"scoresSettingsButton\" was not injected: check your FXML file 'scores.fxml'.";

        scores = Arrays.asList(scoresRow1, scoresRow2, scoresRow3, scoresRow4);

        scoresHomeButton.setOnAction(event -> {
            try {
                SceneSwitcher.getInstance().resetParameters();
                SceneSwitcher.getInstance().displayScene("accueil");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        scoresSettingsButton.setOnAction(event -> {
            try {
                SceneSwitcher.getInstance().displayScene("menu");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Prépare l'affichage des scores
     * @param scores la liste des joueurs ordonnée par score
     * @param nbJoueurs l'énumération du nombre de joueurs
     */
    public void prepareDisplay(List<Joueur> scores, NbJoueur nbJoueurs){
        List<List<Joueur>> formattedScore = formatScores(scores);
        int scoreCursor = 1;
        StringBuilder vainqueurs = new StringBuilder();
        try{
            scoresContainer.getChildren().remove(nbJoueurs.getNbJoueurs() + 1, scoresContainer.getChildren().size());
        }catch(IndexOutOfBoundsException ignored){ }

        for(int i = 0; i < formattedScore.size(); i++){
            for(Joueur j : formattedScore.get(i)) {
                if(i == 0) vainqueurs.append(j.getNomJoueur()).append(", ");
                GridPane score = (GridPane) scoresContainer.getChildren().get(scoreCursor);
                ((Label) score.getChildren().get(classementIndex)).setText("" + (i + 1) + ".");
                ((Label) score.getChildren().get(nomIndex)).setText(j.getNomJoueur());
                ((Label) score.getChildren().get(pointsIndex)).setText("" + j.getScore() + " pt" + ((j.getScore() == 1)?"":"s"));
                scoreCursor++;
            }
            if(i == 0){
                if(formattedScore.get(i).size() > 1){
                    vainqueurs.append("vous êtes les vainqueurs !");
                } else {
                    vainqueurs.append("vous êtes le vainqueur !");
                }
            }
        }
        scoresVainqueursLabel.setText(vainqueurs.toString());
    }

    /**
     * Fomate le tableau des scores en un format plus facile à réaliser
     * @param joueurs la liste des joueurs ordonnée par scores
     * @return la liste formatée de joueurs avec en indice des sous-listes le classement
     */
    private List<List<Joueur>> formatScores(List<Joueur> joueurs){
        List<List<Joueur>> formattedScores = new ArrayList<>();
        for(int i = 0; i < joueurs.size(); i++){
            Joueur joueur1 = joueurs.get(i);
            formattedScores.add(i, new ArrayList<>());
            formattedScores.get(i).add(joueur1);
            if(joueur1.getEgalite()){
                for(int j = i+1; j < joueurs.size(); j++){
                    Joueur joueur2 = joueurs.get(j);
                    if(joueur2.getEgalite() && joueur1.getScoreCouronne() == joueur2.getScoreCouronne()){
                        formattedScores.get(i).add(joueur2);
                        formattedScores.add(j, new ArrayList<>());
                        i = j;
                    }else{
                        break;
                    }
                }
            }
        }
        return formattedScores;
    }
}
