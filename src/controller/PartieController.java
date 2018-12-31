package controller;

import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.util.ResourceBundle;

import controller.util.IndicatorFader;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;

public class PartieController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private GridPane partieIndicator;

    @FXML
    private Label partieTitleIndicator;

    @FXML
    private Label partieTextIndicator;

    @FXML
    private JFXButton toggle;

    private IndicatorFader status;

    @FXML
    void initialize() {
        assert partieIndicator != null : "fx:id=\"partieIndicator\" was not injected: check your FXML file 'partie.fxml'.";
        assert partieTitleIndicator != null : "fx:id=\"partieTitleIndicator\" was not injected: check your FXML file 'partie.fxml'.";
        assert partieTextIndicator != null : "fx:id=\"partieTextIndicator\" was not injected: check your FXML file 'partie.fxml'.";
        assert toggle != null : "fx:id=\"toggle\" was not injected: check your FXML file 'partie.fxml'.";

        status = new IndicatorFader(partieIndicator, partieTitleIndicator, partieTextIndicator, 3000, 500);

        toggle.setOnAction(event -> {
            status.display("Hello", "What's your name ?");
        });
    }
}
