package controller;

import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.util.ResourceBundle;

import controller.util.SceneSwitcher;
import javafx.fxml.FXML;
import javafx.scene.Cursor;

public class AccueilController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private JFXButton accueilPlayButton;

    @FXML
    void initialize() {
        assert accueilPlayButton != null : "fx:id=\"accueilPlayButton\" was not injected: check your FXML file 'accueil.fxml'.";

        accueilPlayButton.setCursor(Cursor.HAND);
        accueilPlayButton.setOnAction(event -> {
            try { SceneSwitcher.getInstance().displayScene("menu"); } catch (Exception e) { System.err.println(e.getMessage()); }
        });
    }
}