package controller;

import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.util.ResourceBundle;

import com.jfoenix.controls.JFXDialog;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;

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
    private JFXButton piocheRevertJetonButton;

    @FXML
    private JFXButton piocheValidateJetonButton;

    private JFXDialog dialog;

    @FXML
    void initialize() {
        assert piocheRootNode != null : "fx:id=\"piocheRootNode\" was not injected: check your FXML file 'pioche.fxml'.";
        assert piocheDominosContainer != null : "fx:id=\"piocheDominosContainer\" was not injected: check your FXML file 'pioche.fxml'.";
        assert piocheJetonsContainer != null : "fx:id=\"piocheJetonsContainer\" was not injected: check your FXML file 'pioche.fxml'.";
        assert piocheRevertJetonButton != null : "fx:id=\"piocheRevertJetonButton\" was not injected: check your FXML file 'pioche.fxml'.";
        assert piocheValidateJetonButton != null : "fx:id=\"piocheValidateJetonButton\" was not injected: check your FXML file 'pioche.fxml'.";


    }

    public void init(StackPane parentContainer){
        dialog = new JFXDialog();
        dialog.setContent(piocheRootNode);
        dialog.setDialogContainer(parentContainer);
        setFixedDimensions(dialog.getContent(), 1500, 800);
        dialog.setOverlayClose(false);

        /*toggleDialog.setOnAction((action)->{
            partieDialogParent.toFront();
            dialog.show(partieDialogParent);
        });

        dialog.setOnDialogClosed((action) -> {
            partieDialogParent.toBack();
        });*/
    }

    private void setFixedDimensions(Region r, double width, double height) {
        r.setPrefSize(width, height);
        r.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        r.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
    }
}
