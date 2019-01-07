package controller;

import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.util.ResourceBundle;

import controller.util.ConfigStyle;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import jeu.Roi;
import plateau.Case;

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
    private Node lastMovedJeton;

    private final DataFormat roiFormat = new DataFormat("jeu.roi");

    @FXML
    void initialize() {
        assert piocheRootNode != null : "fx:id=\"piocheRootNode\" was not injected: check your FXML file 'pioche.fxml'.";
        assert piocheDominosContainer != null : "fx:id=\"piocheDominosContainer\" was not injected: check your FXML file 'pioche.fxml'.";
        assert piocheJetonsContainer != null : "fx:id=\"piocheJetonsContainer\" was not injected: check your FXML file 'pioche.fxml'.";
        assert piocheRevertJetonButton != null : "fx:id=\"piocheRevertJetonButton\" was not injected: check your FXML file 'pioche.fxml'.";
        assert piocheValidateJetonButton != null : "fx:id=\"piocheValidateJetonButton\" was not injected: check your FXML file 'pioche.fxml'.";

        configStyle = ConfigStyle.getInstance();

        piocheValidateJetonButton.setOnAction(event -> {

        });

        piocheRevertJetonButton.setOnAction(event -> {
            if(lastMovedJeton != null){
                VBox accepter = (VBox) lastMovedJeton.getParent();
                Node dominoContainer = accepter.getParent();
                accepter.getChildren().remove(0);
                piocheJetonsContainer.getChildren().add(0, lastMovedJeton);
                lastMovedJeton = null;
                dominoContainer.setDisable(false);
                piocheValidateJetonButton.setDisable(true);
            }
        });
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
            configStyle.setFixedDimensions(dominoDisplay, caseDimension*2, caseDimension);
            for(int j = 0; j < nbCaseDomino; j++){
                Label dominoContent = new Label();
                dominoContent.setText("X");
                configStyle.setFixedDimensions(dominoContent, caseDimension, caseDimension);
                dominoContent.setStyle(dominoContent.getStyle() + "-fx-border-color:black;-fx-text-fill:white;-fx-font-weight:bold;-fx-font-size:18px");
                dominoContent.setAlignment(Pos.CENTER);
                dominoDisplay.add(dominoContent, j, 0);
            }

            VBox accepteurJeton = new VBox();
            accepteurJeton.setAlignment(Pos.CENTER);
            accepteurJeton.setStyle(accepteurJeton.getStyle() + "-fx-border-color:black");
            configStyle.setFixedDimensions(accepteurJeton, accepteurJetonDimension, accepteurJetonDimension);
            initTargetDrag(accepteurJeton);

            domino.getChildren().addAll(dominoInt, dominoDisplay, accepteurJeton);

            piocheDominosContainer.getChildren().add(domino);

            Label jeton = new Label();
            //jeton.setDisable(true);
            configStyle.setFixedDimensions(jeton, jetonDimension, jetonDimension);
            jeton.setStyle(jeton.getStyle() + "-fx-border-radius:100%;-fx-border-color:black;-fx-border-width:4px;-fx-text-fill:white;-fx-font-weight:bold;-fx-font-size:18px");
            piocheJetonsContainer.getChildren().add(jeton);
            initSourceDrag(jeton);
        }

        GridPane plateau = new GridPane();
        plateau.setStyle(plateau.getStyle() + "-fx-border-color:black;");
        configStyle.setFixedDimensions(plateau, caseDimension * nbColLig, caseDimension * nbColLig);
        for (int i = 0; i < nbColLig; i++) {
            for (int j = 0; j < nbColLig; j++) {
                Label label = new Label();
                configStyle.setFixedDimensions(label, caseDimension, caseDimension);
                label.setAlignment(Pos.CENTER);
                label.setStyle("-fx-font-size:18px;-fx-font-weight:bold;-fx-text-fill:white;-fx-border-color:black;");
                label.setBackground(configStyle.getBackground("empty"));
                plateau.add(label, i, j);
            }
        }
        piochePlateauContainer.getChildren().add(plateau);
    }

    private void initSourceDrag(Node node){
        node.setOnDragDetected(event -> {
            Dragboard db = node.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.put(roiFormat, Roi.Rose);
            db.setContent(content);
            event.consume();
        });

        node.setOnDragDone(event -> {
            event.consume();
        });
    }

    private void initTargetDrag(VBox node){
        node.setOnDragOver(event -> {
            if (event.getGestureSource() != node &&
                    event.getDragboard().hasContent(roiFormat) &&
                    node.getChildren().size() == 0) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        node.setOnDragEntered(event -> {
            node.setBackground(configStyle.getBackground("hover"));
            event.consume();
        });

        node.setOnDragExited(event -> {
            node.setBackground(configStyle.getBackground("empty"));
            event.consume();
        });

        node.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;
            if (db.hasContent(roiFormat) && node.getChildren().size() == 0) {
                success = true;
                Node jeton = piocheJetonsContainer.getChildren().remove(0);
                node.getChildren().add(jeton);
                node.getParent().setDisable(true);
                piocheValidateJetonButton.setDisable(false);
                lastMovedJeton = jeton;
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }
}
