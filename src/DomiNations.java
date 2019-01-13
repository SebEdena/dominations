/**
 * Classe permettant de lancer le jeu avec l'interface graphique
 * @author Mathieu Valentin, Sébastien Viguier, Laurent Yu
 * @version 1.0
 */
import controller.util.SceneSwitcher;
import javafx.application.Application;
import javafx.stage.Stage;

public class DomiNations extends Application {

    /**
     * Methode permettant de lancer le jeu avec l'interface graphique
     * @see SceneSwitcher#getInstance
     * @see SceneSwitcher#init
     * @see SceneSwitcher#displayScene
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        SceneSwitcher.getInstance().setPrimaryStage(primaryStage);
        SceneSwitcher.getInstance().init();
        SceneSwitcher.getInstance().displayScene("accueil");
    }

    /**
     * Methode permettant de lancer le jeu
     */
    public static void main(String[] args) {
        launch(args);
    }
}
