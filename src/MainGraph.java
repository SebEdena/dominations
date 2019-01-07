import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class MainGraph extends Application {

    private int standardWidth = 1600, standardHeight = 900;

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("/view/menu.fxml"));
        primaryStage.setTitle("Hello World");
        Scene s = new Scene(root, standardWidth, standardHeight);

        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        if(bounds.getHeight() < standardHeight ||
                bounds.getWidth() < standardWidth){
            primaryStage.setMaximized(true);
        }

        primaryStage.setScene(s);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
