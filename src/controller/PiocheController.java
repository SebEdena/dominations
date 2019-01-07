package controller;

import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.util.ResourceBundle;

import controller.util.ConfigStyle;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;

public class PiocheController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private StackPane piocheRootNode;

    @FXML
    private HBox piocheDominosContainer;

    @FXML
    private HBox piocheJetonsContainer;

    @FXML
    private VBox piochePlateauContainer;

    @FXML
    private JFXButton piocheRevertJetonButton;

    @FXML
    private JFXButton piocheValidateJetonButton;

    private final int nbCaseDomino = 2;
    private ConfigStyle configStyle;

    @FXML
    void initialize() {
        assert piocheRootNode != null : "fx:id=\"piocheRootNode\" was not injected: check your FXML file 'pioche.fxml'.";
        assert piocheDominosContainer != null : "fx:id=\"piocheDominosContainer\" was not injected: check your FXML file 'pioche.fxml'.";
        assert piocheJetonsContainer != null : "fx:id=\"piocheJetonsContainer\" was not injected: check your FXML file 'pioche.fxml'.";
        assert piocheRevertJetonButton != null : "fx:id=\"piocheRevertJetonButton\" was not injected: check your FXML file 'pioche.fxml'.";
        assert piocheValidateJetonButton != null : "fx:id=\"piocheValidateJetonButton\" was not injected: check your FXML file 'pioche.fxml'.";

        configStyle = ConfigStyle.getInstance();

    }

    public void initContent(int nbRois, int nbColLig){
        double dominoInsideSpacing = configStyle.getPiocheDominoInsideSpacing();
        double caseDimension = configStyle.getPetiteCaseDimension();
        double jetonDimension = configStyle.getPiocheJetonDimension();
        double accepteurJetonDimension = configStyle.getPiocheAccepteurJetonDimension();
        for(int i = 0; i < nbRois; i++){
            VBox domino = new VBox();
            domino.setSpacing(dominoInsideSpacing);
            domino.setAlignment(Pos.CENTER);

            Label dominoInt = new Label();
            dominoInt.setText("X");
            dominoInt.setStyle(dominoInt.getStyle() + "-fx-text-fill:black;-fx-font-weight:bold;-fx-font-size:18px");

            GridPane dominoDisplay = new GridPane();
            setFixedDimensions(dominoDisplay, caseDimension*2, caseDimension);
            for(int j = 0; j < nbCaseDomino; j++){
                Label dominoContent = new Label();
                dominoContent.setText("X");
                setFixedDimensions(dominoContent, caseDimension, caseDimension);
                dominoContent.setStyle(dominoContent.getStyle() + "-fx-border-color:black;-fx-text-fill:white;-fx-font-weight:bold;-fx-font-size:18px");
                dominoContent.setAlignment(Pos.CENTER);
                dominoDisplay.add(dominoContent, j, 0);
            }

            VBox accepteurJeton = new VBox();
            accepteurJeton.setAlignment(Pos.CENTER);
            accepteurJeton.setStyle(accepteurJeton.getStyle() + "-fx-border-color:black");
            setFixedDimensions(accepteurJeton, accepteurJetonDimension, accepteurJetonDimension);

            domino.getChildren().addAll(dominoInt, dominoDisplay, accepteurJeton);

            piocheDominosContainer.getChildren().add(domino);

            Label jeton = new Label();
            setFixedDimensions(jeton, jetonDimension, jetonDimension);
            jeton.setStyle(jeton.getStyle() + "-fx-border-radius:100%;-fx-border-color:black;-fx-border-width:4px;-fx-text-fill:white;-fx-font-weight:bold;-fx-font-size:18px");
            piocheJetonsContainer.getChildren().add(jeton);
        }

        GridPane plateau = new GridPane();
        plateau.setStyle(plateau.getStyle() + "-fx-border-color:black;");
        setFixedDimensions(plateau, caseDimension * nbColLig, caseDimension * nbColLig);
        for (int i = 0; i < nbColLig; i++) {
            for (int j = 0; j < nbColLig; j++) {
                Label label = new Label();
                setFixedDimensions(label, caseDimension, caseDimension);
                label.setAlignment(Pos.CENTER);
                label.setStyle("-fx-font-size:18px;-fx-font-weight:bold;-fx-text-fill:white;-fx-border-color:black;");
                label.setBackground(configStyle.getBackground("empty"));
                plateau.add(label, i, j);
            }
        }
        piochePlateauContainer.getChildren().add(plateau);
    }

    private void setFixedDimensions(Region r, double width, double height) {
        r.setPrefSize(width, height);
        r.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        r.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
    }
}
