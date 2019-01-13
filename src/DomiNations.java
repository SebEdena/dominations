import controller.util.SceneSwitcher;
import javafx.application.Application;
import javafx.stage.Stage;

public class DomiNations extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        SceneSwitcher.getInstance().setPrimaryStage(primaryStage);
        SceneSwitcher.getInstance().init();
        SceneSwitcher.getInstance().displayScene("accueil");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
