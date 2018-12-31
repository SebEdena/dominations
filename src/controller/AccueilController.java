package controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.scene.image.ImageView;

public class AccueilController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ImageView accueilPlayButton;

    @FXML
    void initialize() {
        assert accueilPlayButton != null : "fx:id=\"accueilPlayButton\" was not injected: check your FXML file 'accueil.fxml'.";

    }
}