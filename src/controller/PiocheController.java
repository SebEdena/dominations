package controller;

import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

import com.jfoenix.controls.cells.editors.IntegerTextFieldEditorBuilder;
import com.sun.corba.se.impl.logging.InterceptorsSystemException;
import com.sun.xml.internal.bind.v2.model.core.ID;
import com.sun.xml.internal.ws.model.ParameterImpl;
import controller.util.ConfigStyle;
import controller.util.IndicatorFader;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import jeu.Joueur;
import jeu.Partie;
import jeu.Roi;
import plateau.*;

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

    private List<Label> jetons;
    private List<Pair<IDomino, Joueur>> choix;
    private Pair<IDomino, Joueur> choixEnCours;
    private Partie partie;
    private final int nbCaseDomino = 2;
    private static final int indexIdDomino = 0;
    private static final int indexDomino = 1;
    private static final int indexAccepteurJeton = 2;
    private ConfigStyle configStyle;
    private Node lastMovedJeton;
    private Object partieLocker;

    private final DataFormat roiFormat = new DataFormat("jeu.roi");
    private boolean finished = false;
    private IndicatorFader status;

    @FXML
    void initialize() {
        assert piocheRootNode != null : "fx:id=\"piocheRootNode\" was not injected: check your FXML file 'pioche.fxml'.";
        assert piocheDominosContainer != null : "fx:id=\"piocheDominosContainer\" was not injected: check your FXML file 'pioche.fxml'.";
        assert piocheJetonsContainer != null : "fx:id=\"piocheJetonsContainer\" was not injected: check your FXML file 'pioche.fxml'.";
        assert piocheRevertJetonButton != null : "fx:id=\"piocheRevertJetonButton\" was not injected: check your FXML file 'pioche.fxml'.";
        assert piocheValidateJetonButton != null : "fx:id=\"piocheValidateJetonButton\" was not injected: check your FXML file 'pioche.fxml'.";

        configStyle = ConfigStyle.getInstance();
        jetons = new ArrayList<>();
        choix = new ArrayList<>();

        piocheValidateJetonButton.setOnAction(event -> {
            validerAffectationDominoJoueur();
            piocheValidateJetonButton.setDisable(true);

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

    public void initContent(Partie p, IndicatorFader status, Object partieLocker){
        this.partie = p;
        this.partieLocker = partieLocker;
        this.status = status;
        double dominoInsideSpacing = configStyle.getPiocheDominoInsideSpacing();
        double caseDimension = configStyle.getCaseDimension();
        double jetonDimension = configStyle.getPiocheJetonDimension();
        double accepteurJetonDimension = configStyle.getPiocheAccepteurJetonDimension();
        for(int i = 0; i < partie.getTotalRois(); i++){
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
            configStyle.setFixedDimensions(jeton, jetonDimension, jetonDimension);
            jeton.setStyle(jeton.getStyle() + "-fx-alignment:center;-fx-text-fill:white;-fx-font-weight:bold;-fx-font-size:18px");
            jeton.setDisable(true);
            piocheJetonsContainer.getChildren().add(jeton);
            jetons.add(jeton);
            initSourceDrag(jeton);
        }

        int nbColLig = partie.getModeJeu().getTaillePlateau();
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
        fillPlateau(partie.getJoueurs().get(0).getPlateau());
    }

    private void initSourceDrag(Node node){
        node.setOnDragDetected(event -> {
            Dragboard db = node.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.put(roiFormat, getRoiFromLabel((Label) node));
            db.setContent(content);
            event.consume();
        });

        node.setOnDragDone(Event::consume);
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
                int position = positionAmongDominos(node.getParent());
                Pair<IDomino, Joueur> choisi = choix.get(position);
                Joueur j = partie.getJoueur((Roi) db.getContent(roiFormat));
                choixEnCours = new Pair<>(choisi.getKey(), j);
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    public List<Pair<IDomino, Joueur>> getChoix(){
        return choix;
    }

    public void resetPioche() {
        choix.clear();
        for(Node dominoContainer : piocheDominosContainer.getChildren()){
            VBox castedContainer = (VBox) dominoContainer;
            dominoContainer.setDisable(false);
            VBox accepteurJeton = (VBox) castedContainer.getChildren().get(indexAccepteurJeton);
            if(accepteurJeton.getChildren().size() != 0){
                Label jeton = (Label) accepteurJeton.getChildren().remove(0);
                piocheJetonsContainer.getChildren().add(jeton);
                jeton.setDisable(true);
            }
        }
    }

    public void fillPioche(List<IDomino> pioche, List<Roi> rois){
        for (int i = 0; i < piocheDominosContainer.getChildren().size(); i++){
            IDomino domino = pioche.get(i);
            Roi roi = rois.get(i);
            VBox dominoContainer = (VBox) piocheDominosContainer.getChildren().get(i);
            fillDomino(domino, dominoContainer);
            fillJeton(jetons.get(i), roi);
            choix.add(new Pair<>(domino, null));
        }
    }

    private int positionAmongDominos(Node dominoContainer){
        for(int i = 0; i < piocheDominosContainer.getChildren().size(); i++){
            if(piocheDominosContainer.getChildren().get(i).equals(dominoContainer)) return i;
        }
        return -1;
    }

    private Roi getRoiFromLabel(Label jeton){
        return Roi.getRoiInt(Integer.parseInt(jeton.getText()) - 1);
    }

    private void fillDomino(IDomino d, VBox dContainer){
        if (d instanceof Domino) {
            Case[] cases = d.getCases();
            GridPane dom = (GridPane) dContainer.getChildren().get(indexDomino);
            for (int i = 0; i < cases.length; i++) {
                Label caseDomino = (Label) dom.getChildren().get(i);
                caseDomino.setBackground(configStyle.getBackground(cases[i].getTerrain().getLibelle()));
                caseDomino.setText("" + cases[i].getNbCouronne());
            }
            ((Label) dContainer.getChildren().get(indexIdDomino)).setText(""+d.getIdentifiant());
        }
    }

    private void fillJeton(Label jeton, Roi roi){
        jeton.setBackground(configStyle.getBackground(roi.getLibelle()));
        jeton.setBorder(configStyle.getBorder(roi.getLibelle()));
        jeton.setText(""+(Roi.getRoiIndex(roi)+1));
    }

    private void fillPlateau(Plateau p){
        int pSize = p.getSize();
        for (int i = 0; i < pSize; i++) {
            for (int j = 0; j < pSize; j++) {
                fillLabelWithCase(getCaseLabel(i, j), p.getCaseAt(i, j));
            }
        }
    }

    private Label getCaseLabel(int row, int col) {
        try {
            GridPane p = (GridPane) piochePlateauContainer.getChildren().get(indexDomino);
            return (Label) p.getChildren().get(col * partie.getModeJeu().getTaillePlateau() + row);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    private void fillLabelWithCase(Label l, Case c) {
        if(l != null){
            l.setGraphic(null);
            if (c == null) {
                l.setBackground(configStyle.getBackground("empty"));
                l.setText("");
            } else {
                if(c.getTerrain().equals(Terrain.CHATEAU)){
                    l.setGraphic(new ImageView("./view/img/crown.png"));
                }else{
                    l.setText("" + c.getNbCouronne());
                }
                l.setBackground(configStyle.getBackground(c.getTerrain().getLibelle()));
            }
        }
    }

    private void validerAffectationDominoJoueur(){
        IDomino d = choixEnCours.getKey();
        int indexDomino = 0;
        for(Node dominoContainer : piocheDominosContainer.getChildren()){
            if(((Label)((VBox)dominoContainer).getChildren().get(indexIdDomino)).getText().equals(""+d.getIdentifiant())){
                break;
            }
            indexDomino++;
        }
        choix.set(indexDomino, choixEnCours);
        choixEnCours = null;
        if(piocheJetonsContainer.getChildren().size() == 0){
            synchronized (partieLocker){ partieLocker.notifyAll(); }
        }else{
            new Thread(this::nextPioche).start();
        }
    }

    public boolean isFinished() {
        return finished;
    }

    public void startPioche() {
        new Thread(()->{
            try {
                Thread.sleep(500);
                status.display("Glissez votre jeton sous le domino voulu", "Début de la pioche");
                Thread.sleep(status.getTotalTime() + 100);
                nextPioche();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void nextPioche(){
        try {
            Thread.sleep(500);
            Label jeton = (Label) piocheJetonsContainer.getChildren().get(0);
            Roi roi = Roi.getRoiInt(Integer.parseInt(jeton.getText()) - 1);
            status.display("Glissez votre jeton sous le domino voulu", partie.getJoueur(roi).getNomJoueur(), Color.web(roi.getColor()));
            Platform.runLater(()->{
                fillPlateau(partie.getJoueurs().get(Integer.parseInt(jeton.getText()) - 1).getPlateau());
                jeton.setDisable(false);
            });
        } catch (InterruptedException ignored) { }
    }
}
