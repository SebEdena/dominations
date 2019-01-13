package controller.util;

import controller.PartieController;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import model.joueur.Joueur;
import model.jeu.ModeJeu;
import model.jeu.NbJoueur;
import model.plateau.IDomino;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Classe du gestionnaire de scène (Singleton)
 * @author Mathieu Valentin, Sébastien Viguier, Laurent Yu
 * @version 1.0
 */
public class SceneSwitcher {

    public static final double standardWidth = 1600, standardHeight = 900;
    private static SceneSwitcher instance;

    private Map<String, Scene> scenes;
    private double windowWidth, windowHeight;
    private Scene currentPartieScene;
    private Stage primaryStage;

    /**
     * Constructeur du gestionnaire de scène
     */
    private SceneSwitcher() {
        scenes = new HashMap<>();
    }

    /**
     * Retourne l'instance du gestionnaire de scènes
     * @return l'instance du gestionnaire de scènes
     */
    public static SceneSwitcher getInstance(){
        if(instance == null){
            instance = new SceneSwitcher();
        }
        return instance;
    }

    /**
     * Ajoute l'objet Stage qui correspond à une fenêtre
     * @param primaryStage l'objet Stage
     */
    public void setPrimaryStage(Stage primaryStage){
        this.primaryStage = primaryStage;
    }

    /**
     * Initialise le gestionnaire de scènes
     * @throws IOException si le fichier .fxml des scènes n'existe pas
     */
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

    /**
     * Affiche la scène demandée
     * @param scene la chaine correspondant à la scène demandée
     * @throws Exception si la scène n'existe pas ou la scène de partie est demandée
     */
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

    /**
     * Démarre un partie
     * @param joueurs la liste des joueurs
     * @param deck la liste de dominos
     * @param nbJoueur l'énumération du nombre de joueurs
     * @param modeJeu l"énumération du mode de jeu
     * @throws Exception si le fichier .fxml de la scène n'existe pas
     */
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

    /**
     * Réinitialise le menu des paramètres
     * @throws IOException si le fichier .fxml de la scène n'existe pas
     */
    public void resetParameters() throws IOException {
        Parent menuRoot = FXMLLoader.load(getClass().getResource("/view/menu.fxml"));
        scenes.put("menu", new Scene(menuRoot, windowWidth, windowHeight));
    }
}
