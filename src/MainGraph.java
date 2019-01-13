import controller.util.SceneSwitcher;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainGraph extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        /*Parent root = FXMLLoader.load(getClass().getResource("/view/partie.fxml"));
        primaryStage.setTitle("Hello World");
        Scene s = new Scene(root, standardWidth, standardHeight);

        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        if(bounds.getHeight() < standardHeight ||
                bounds.getWidth() < standardWidth){
            primaryStage.setMaximized(true);
        }

        primaryStage.setScene(s);
        primaryStage.setResizable(false);
        primaryStage.show();*/
        SceneSwitcher.getInstance().setPrimaryStage(primaryStage);
        SceneSwitcher.getInstance().init();
        SceneSwitcher.getInstance().displayScene("accueil");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
