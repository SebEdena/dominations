package controller.util;

import controller.PartieController;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import jeu.Joueur;
import jeu.ModeJeu;
import jeu.NbJoueur;
import plateau.IDomino;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SceneSwitcher {

    public static final double standardWidth = 1600, standardHeight = 900;
    private static SceneSwitcher instance;

    private Map<String, Scene> scenes;
    private double windowWidth, windowHeight;
    private Scene currentPartieScene;
    private Stage primaryStage;

    private SceneSwitcher() {
        scenes = new HashMap<>();
    }

    public static SceneSwitcher getInstance(){
        if(instance == null){
            instance = new SceneSwitcher();
        }
        return instance;
    }

    public void setPrimaryStage(Stage primaryStage){
        this.primaryStage = primaryStage;
    }

    public void init() throws IOException {
        primaryStage.setTitle("Domi Nations");
        primaryStage.setResizable(false);
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        if(bounds.getHeight() < standardHeight || bounds.getWidth() < standardWidth){
            primaryStage.setMaximized(true);
            windowHeight = bounds.getHeight();
            windowWidth = bounds.getWidth();
        } else{
            windowHeight = standardHeight;
            windowWidth = standardWidth;
        }

        Parent accueilRoot = FXMLLoader.load(getClass().getResource("/view/accueil.fxml"));
        scenes.put("accueil", new Scene(accueilRoot, windowWidth, windowHeight));

        Parent menuRoot = FXMLLoader.load(getClass().getResource("/view/menu.fxml"));
        scenes.put("menu", new Scene(menuRoot, windowWidth, windowHeight));
    }

    public void displayScene(String scene) throws Exception {
        if(!scenes.containsKey(scene)){
            if(scene.equals("partie")){
                throw new Exception("Please use startPartie to play.");
            }
            throw new Exception("This scene does not exist.");
        }else{
            primaryStage.hide();
            primaryStage.setScene(scenes.get(scene));
            primaryStage.show();
        }
    }

    public void startPartie(List<Joueur> joueurs, List<IDomino> deck, NbJoueur nbJoueur, ModeJeu modeJeu) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/partie.fxml"));
        Parent parent = loader.load();
        PartieController controller = loader.getController();
        controller.init(joueurs, deck, nbJoueur, modeJeu, windowWidth, windowHeight);
        currentPartieScene = new Scene(parent, windowWidth, windowHeight);

        primaryStage.hide();
        primaryStage.setScene(currentPartieScene);
        primaryStage.show();
        controller.jouerPartie();
    }

    public void resetParameters() throws IOException {
        Parent menuRoot = FXMLLoader.load(getClass().getResource("/view/menu.fxml"));
        scenes.put("menu", new Scene(menuRoot, windowWidth, windowHeight));
    }
}
