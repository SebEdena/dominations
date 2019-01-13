package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.sun.rowset.internal.Row;
import controller.util.ConfigStyle;
import controller.util.IndicatorFader;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Parent;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.util.Pair;
import jeu.*;
import plateau.PlacementDomino;
import exceptions.DominoException;
import exceptions.TuileException;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import plateau.*;
import util.CSVParser;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class PartieController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private GridPane partieRootNode;

    @FXML
    private GridPane partieIndicator;

    @FXML
    private Label partieTitleIndicator;

    @FXML
    private Label partieTextIndicator;

    @FXML
    private VBox leftContent;

    @FXML
    private VBox rightContent;

    @FXML
    private Label partieTourJoueurLabel;

    @FXML
    private GridPane partieDomino;

    @FXML
    private GridPane partiePlateauContainer;

    @FXML
    private JFXButton partieRevertDominoButton;

    @FXML
    private JFXButton partieDropDominoButton;

    @FXML
    private JFXButton partieValidateButton;

    @FXML
    private StackPane partiePiocheParent;

    @FXML
    private StackPane pioche;

    @FXML
    private PiocheController piocheController;


    private JFXDialog dialog;

    private ConfigStyle configStyle;

    private IndicatorFader status;

    private GridPane plateauDisplay;

    private int colRowSize;

    private List<IDomino> deckDominos = new ArrayList<>();

    private List<String[]> dominosPlacement = null;

    private List<GridPane> miniPlateaux = null;

    private PlacementDomino placement = null;

    private Joueur joueurActuel = null;

    private IDomino dominoActuel = null;

    private Partie partie;

    private final Object partieLocker = new Object();

    private double windowWidth, windowHeight, offsetDialog = 100;

    private final DataFormat caseDominoFormat = new DataFormat("plateau.Case");

    @FXML
    void initialize() {
        assert partieIndicator != null : "fx:id=\"partieG2Indicator\" was not injected: check your FXML file 'partie.fxml'.";
        assert partieTitleIndicator != null : "fx:id=\"partieTitleIndicator\" was not injected: check your FXML file 'partie.fxml'.";
        assert partieTextIndicator != null : "fx:id=\"partiematicoTextIndicator\" was not injected: check your FXML file 'partie.fxml'.";
        assert leftContent != null : "fx:id=\"leftContent\" was not injected: check your FXML file 'partie.fxml'.";
        assert rightContent != null : "fx:id=\"rightContent\" was not injected: check your FXML file 'partie.fxml'.";
        assert partieTourJoueurLabel != null : "fx:id=\"partieUYurJoueurLabel\" was not injected: check your FXML file 'partie.fxml'.";
        assert partieDomino != null : "fx:id=\"partieDomino\" was not injected: check your FXML file 'partie.fxml'.";
        assert partiePlateauContainer != null : "fx:id=\"partiePlateauContainer\" was not injected: check your FXML file 'partie.fxml'.";

        status = new IndicatorFader(partieIndicator, partieTitleIndicator, partieTextIndicator, 3000, 500);
        configStyle = ConfigStyle.getInstance();
        leftContent.setSpacing(configStyle.getPiocheDominoInsideSpacing());
        rightContent.setSpacing(configStyle.getPiocheDominoInsideSpacing());
        partieValidateButton.setDisable(true);
        partieDropDominoButton.setDisable(false);
    }

    public void init(List<Joueur> joueurs, List<IDomino> deck, NbJoueur nbJoueur, ModeJeu modeJeu) {
        /*try {
            List<IDomino> deck = Jeu.chargementDominos("./dominos.csv");
            ModeJeu modeJeu = ModeJeu.STANDARD;
            NbJoueur nbJoueur = NbJoueur.jeuA4;
            List<Joueur> joueurs = new ArrayList<>();
            joueurs.add(new Joueur("Seb", Roi.getRoiInt(0), nbJoueur, modeJeu, 0));
            joueurs.add(new Joueur("Laurent", Roi.getRoiInt(1), nbJoueur, modeJeu, 0));
            joueurs.add(new Joueur("Mathieu", Roi.getRoiInt(2), nbJoueur, modeJeu, 0));
            joueurs.add(ModeIA.getIAClasse(ModeIA.SIMPLE,"Kabir", Roi.getRoiInt(3), nbJoueur, modeJeu, 0));
            partie = new Partie(joueurs, deck, nbJoueur, modeJeu);
        } catch (Exception e){
            e.printStackTrace();
        }*/

        partie = new Partie(joueurs, deck, nbJoueur, modeJeu);
        initDialog();
        initPlateau(modeJeu.getTaillePlateau() + 2);
        initMiniPlateau();
        for(int i = 0; i < miniPlateaux.size(); i++){
            fillMiniPlateau(partie.getJoueurs().get(i));
        }

        partieDomino.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.SECONDARY)) {
                partieDomino.setRotate(Math.floor(((int)partieDomino.getRotate() + 90) % 360));
                for (Node child : partieDomino.getChildren()) {
                    child.setRotate(-(int)partieDomino.getRotate());
                }
            }
        });

        partieRevertDominoButton.setOnAction(event -> {
            resetDomino();
        });

        partieValidateButton.setOnAction(event -> {
            try {
                validateDominoPlacement(placement);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        partieDropDominoButton.setOnAction(event -> {
            try {
                partieDomino.setVisible(false);
                synchronized (partieLocker) { partieLocker.notifyAll(); }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        initDominoSourceDrag();
        initDominoTargetDrag();
    }

    public void jouerPartie() {
        new Thread(() -> {
            try{
                piocheController.initContent(partie, status, partieLocker);
                Thread.sleep(500);
                Platform.runLater(() -> status.display("Débutons la partie, que le meilleur gagne!", "C'est parti !"));
                Thread.sleep(status.getTotalTime());
                int i = 1;
                while (i > 0 && true) {
                    i--;
                    List<IDomino> tirage = partie.pioche();
                    if(partie.partieFinie()) break;

                    preparePioche(tirage, partie.melangerRois());
                    Platform.runLater(this::showPiocheDialog);
                    Platform.runLater(piocheController::startPioche);
                    synchronized(partieLocker) { partieLocker.wait(); }

                    closePiocheDialog();
                    Thread.sleep(500);

                    List<Pair<IDomino, Joueur>> choix = piocheController.getChoix();
                    for(Pair<IDomino, Joueur> combi : choix){
                        IDomino d = combi.getKey();
                        Joueur j = combi.getValue();
                        Platform.runLater(()->{
                            joueurActuel = j;
                            fillDomino(d);
                            fillPlateau(j.getPlateau(), j);
                            fillJoueurLabel(j);
                            status.display("Choisissez quoi faire de votre domino", j.getNomJoueur(), Color.web(j.getCouleurRoi().getColor()));
                        });
                        if(j.isIA()) simulePlacementDomino();
                        synchronized(partieLocker) { partieLocker.wait(); }
                    }
                    Platform.runLater(() -> {
                        partieTourJoueurLabel.setVisible(false);
                        partieDropDominoButton.setDisable(true);
                        partieValidateButton.setDisable(true);
                        partiePlateauContainer.setVisible(false);
                    });
                    Thread.sleep(500);
                    if(partie.hasNextTurn()) {
                        status.display("Préparez-vous à piocher.", "Fin du tour");
                        Thread.sleep(status.getTotalTime() + 100);
                    }
                }
                List<Joueur> results = partie.calculScores();
                Platform.runLater(() -> displayResultsDialog(results));
                System.out.println(results);
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }).start();
    }

    private void initDialog(){
        dialog = new JFXDialog();
        dialog.toBack();
        dialog.setContent(pioche);
        dialog.setDialogContainer(partiePiocheParent);
        configStyle.setFixedDimensions(dialog.getContent(), 1500, 800);
        dialog.setOverlayClose(false);
    }

    private void initPlateau(int nbColLig) {
        double caseDimension = configStyle.getCaseDimension();
        this.colRowSize = nbColLig;
        plateauDisplay = new GridPane();
        plateauDisplay.setStyle(plateauDisplay.getStyle() + "-fx-border-color:black;");
        configStyle.setFixedDimensions(plateauDisplay, caseDimension * nbColLig, caseDimension * nbColLig);

        for (int i = 0; i < nbColLig; i++) {
            for (int j = 0; j < nbColLig; j++) {
                Label label = new Label();
                configStyle.setFixedDimensions(label, caseDimension, caseDimension);
                label.setAlignment(Pos.CENTER);
                label.setStyle("-fx-font-size:18px;-fx-font-weight:bold;-fx-text-fill:white;-fx-border-color:black;");
                label.setBackground(configStyle.getBackground("empty"));
                plateauDisplay.add(label, i, j);
            }
        }
        partiePlateauContainer.getChildren().add(plateauDisplay);
    }

    private void initDominoSourceDrag() {
        for (Node caseDom : partieDomino.getChildren()) {
            caseDom.setOnDragDetected(event -> {
                if (placement.getDomino() != null) {
                    Dragboard db = caseDom.startDragAndDrop(TransferMode.MOVE);
                    ClipboardContent content = new ClipboardContent();
                    int pickedCase = getCaseDominoIndex(caseDom);
                    placement.setCaseId(pickedCase);
                    content.put(caseDominoFormat, placement.getCase(false));
                    updateOrientationDomino(placement, pickedCase);
                    db.setContent(content);
                }
                event.consume();
            });

            caseDom.setOnDragDone(event -> {
                if (placement.isOnPlateau() || event.isDropCompleted()) {
                    partieDomino.setVisible(false);
                }
                event.consume();
            });
        }
    }

    private void initDominoTargetDrag() {
        for (Node child : plateauDisplay.getChildren()) {
            child.setOnDragOver(event -> {
                if (event.getGestureSource() != child &&
                        event.getDragboard().hasContent(caseDominoFormat)) {
                    if(joueurActuel.getPlateau().placementValide(placement.getDomino(), getRow(child) - 1, getCol(child) - 1, placement.getCaseId(), placement.getSens()) == null){
                        event.acceptTransferModes(TransferMode.MOVE);
                    }
                }
                event.consume();
            });

            child.setOnDragEntered(event -> {
                Label lCase2 = getCaseLabel(plateauDisplay, getRow(child), getCol(child), placement.getSens());
                if (event.getGestureSource() != child &&
                        event.getDragboard().hasContent(caseDominoFormat)) {
                    if(joueurActuel.getPlateau().placementValide(placement.getDomino(), getRow(child) - 1, getCol(child) - 1, placement.getCaseId(), placement.getSens()) == null){
                        Case c = (Case) event.getDragboard().getContent(caseDominoFormat);
                        fillLabelWithCase((Label) child, c, false);
                        fillLabelWithCase(lCase2, placement.getCase(true), false);
                    }
                }
                event.consume();
            });

            child.setOnDragExited(event -> {
                Label lCase2 = getCaseLabel(plateauDisplay, getRow(child), getCol(child), placement.getSens());
                if (event.getGestureSource() != child &&
                        event.getDragboard().hasContent(caseDominoFormat)) {
                    if(joueurActuel.getPlateau().placementValide(placement.getDomino(), getRow(child) - 1, getCol(child) - 1, placement.getCaseId(), placement.getSens()) == null &&
                            !placement.isOnPlateau()){
                        fillLabelWithCase((Label) child, null, isLockedWhenNoDomino(joueurActuel.getPlateau(), getRow(child) - 1, getCol(child) - 1));
                        if(lCase2 != null) fillLabelWithCase(lCase2, null, isLockedWhenNoDomino(joueurActuel.getPlateau(), getRow(lCase2) - 1, getCol(lCase2) - 1));
                    }
                }
                event.consume();
            });

            child.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasContent(caseDominoFormat) &&
                        joueurActuel.getPlateau().placementValide(placement.getDomino(), getRow(child) - 1, getCol(child) - 1,
                                placement.getCaseId(), placement.getSens()) == null) {
                    success = true;
                    Case c = (Case) event.getDragboard().getContent(caseDominoFormat);
                    insertDomino(placement, child, c);
                }
                event.setDropCompleted(success);
                event.consume();
            });
        }
    }

    private int getCaseDominoIndex(Node caseDom) {
        int i = 0;
        for (Node caseDisplay : partieDomino.getChildren()) {
            if (caseDisplay.equals(caseDom)) {
                return i;
            }
            i++;
        }
        return -1;
    }

    private void initMiniPlateau() {
        miniPlateaux = new ArrayList<>();
        double caseDimension = configStyle.getPetiteCaseDimension();
        int pSize = partie.getModeJeu().getTaillePlateau();
        int nbJoueurs = partie.getNbJoueur().getNbJoueurs();

        for(int cptPlateau = 0; cptPlateau < nbJoueurs; cptPlateau++){
            GridPane miniPlateau = new GridPane();
            miniPlateau.setStyle(miniPlateau.getStyle() + "-fx-border-color:black;");
            configStyle.setFixedDimensions(miniPlateau, caseDimension * pSize, caseDimension * pSize);

            for (int i = 0; i < pSize; i++) {
                for (int j = 0; j < pSize; j++) {
                    Label label = new Label();
                    configStyle.setFixedDimensions(label, caseDimension, caseDimension);
                    label.setAlignment(Pos.CENTER);
                    label.setStyle("-fx-font-size:16px;-fx-font-weight:bold;-fx-text-fill:white;-fx-border-color:black;");
                    label.setBackground(configStyle.getBackground("empty"));
                    miniPlateau.add(label, i, j);
                }
            }

            Roi r =  Roi.getRoiInt(cptPlateau);
            Label label = new Label();
            label.setStyle("-fx-font-size:16px;-fx-font-weight:bold;-fx-text-fill:" + r.getColor() + ";");
            label.setText(partie.getJoueur(r).getNomJoueur());
            if(cptPlateau % 2 == 0){
                leftContent.getChildren().addAll(label, miniPlateau);
            }else{
                rightContent.getChildren().addAll(label, miniPlateau);
            }
            miniPlateaux.add(miniPlateau);
        }
    }

    private Label getCaseLabel(GridPane plateau, int row, int col, Orientation sens) {
        try {
            int pSize = partie.getModeJeu().getTaillePlateau();
            if(plateau.equals(plateauDisplay)) pSize += 2;
            if (sens != null) {
                if(row + sens.getOffsetX() < 0 || row + sens.getOffsetX() >= pSize) return null;
                else return (Label) plateau.getChildren().get((col + placement.getColCase2Offset()) * pSize +
                        (row + placement.getRowCase2Offset()));
            } else {
                if(row < 0 || row >= pSize) return null;
                else return (Label) plateau.getChildren().get(col * pSize + row);
            }
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    private int getRow(Node node) {
        return GridPane.getRowIndex(node);
    }

    private int getCol(Node node) {
        return GridPane.getColumnIndex(node);
    }

    private boolean isLockedWhenNoDomino(Plateau p, int i, int j){
        int[] xBounds = p.getXBounds();
        int[] yBounds = p.getYBounds();
        int xSize = xBounds[1] - xBounds[0] + 1;
        int ySize = yBounds[1] - yBounds[0] + 1;
        int pSize = p.getSize();
        return i < (xBounds[0] - 1) || i > (xBounds[1] + 1) ||
                j < (yBounds[0] - 1) || j > (yBounds[1] + 1) ||
                (xSize == pSize && (i == xBounds[0] - 1 || i == xBounds[1] + 1)) ||
                (ySize == pSize && (j == yBounds[0] - 1 || j == yBounds[1] + 1));
    }

    private void preparePioche(List<IDomino> pioche, List<Roi> rois){
        piocheController.resetPioche();
        piocheController.fillPioche(pioche, rois);
    }

    private void updateOrientationDomino(PlacementDomino placement, int caseId){
        Orientation o = null;
        switch((int)partieDomino.getRotate()){
            case 0: o = Orientation.EST; break;
            case 90: o = Orientation.SUD; break;
            case 180: o = Orientation.OUEST; break;
            case 270: o = Orientation.NORD; break;
        }
        if(o != null){
            if(caseId == 1){
                o = o.getOppose();
            }
            placement.setSens(o);
        }
    }

    private void insertDomino(PlacementDomino placementDomino, Node label, Case c){
        int row = getRow(label);
        int col = getCol(label);
        fillLabelWithCase(getCaseLabel(plateauDisplay, row, col, null), null, false);
        fillLabelWithCase(getCaseLabel(plateauDisplay, row, col, placementDomino.getSens()), null, false);
        int[] translation = joueurActuel.getPlateau().calculTranslation(placementDomino.getDomino(), row - 1, col - 1, placementDomino.getCaseId(), placementDomino.getSens());
        placementDomino.setTranslation(translation);
        placementDomino.positionOnPlateau(row, col);
        if(placementDomino.needsTranslation()){
            translatePlateau();
            row += translation[0];
            col += translation[1];
        }
        recomputeLockedCases(placementDomino);
        fillLabelWithCase(getCaseLabel(plateauDisplay, row, col, null), c, false);
        System.out.println(getRow(getCaseLabel(plateauDisplay, row, col, placementDomino.getSens())));
        System.out.println(getCol(getCaseLabel(plateauDisplay, row, col, placementDomino.getSens())));
        fillLabelWithCase(getCaseLabel(plateauDisplay, row, col, placementDomino.getSens()), placementDomino.getCase(true), false);
        partieValidateButton.setDisable(false);
        partieDropDominoButton.setDisable(true);
    }

    private void fillPlateau(Plateau p, Joueur joueur) {
        int pSize = p.getSize();
        for (int i = -1; i <= pSize; i++) {
            for (int j = -1; j <= pSize; j++) {
                Label lab = getCaseLabel(plateauDisplay, i + 1, j + 1, null);
                fillLabelWithCase(lab, p.getCaseAt(i, j), isLockedWhenNoDomino(p, i, j));
            }
        }
        partiePlateauContainer.setVisible(true);
    }

    private void fillMiniPlateau(Joueur joueur) {
        Plateau p = joueur.getPlateau();
        Roi r = joueur.getCouleurRoi();
        for (int i = 0; i < p.getSize(); i++) {
            for (int j = 0; j < p.getSize(); j++) {
                Label lab = getCaseLabel(miniPlateaux.get(Roi.getRoiIndex(r)), i, j, null);
                fillLabelWithCase(lab, p.getCaseAt(i, j), false);
            }
        }
    }

    private void fillDomino(IDomino d) {
        double caseDimension = configStyle.getCaseDimension();
        if (d instanceof Domino) {
            dominoActuel = d;
            placement = new PlacementDomino(d, Orientation.EST);
            Case[] cases = d.getCases();
            for (int i = 0; i < cases.length; i++) {
                Label caseDomino = (Label) partieDomino.getChildren().get(i);
                caseDomino.setStyle(caseDomino.getStyle() + "-fx-background-color:" + cases[i].getTerrain().getColor() + ";" +
                        "-fx-text-fill:white;");
                caseDomino.setText("" + cases[i].getNbCouronne());
                configStyle.setFixedDimensions(caseDomino, caseDimension, caseDimension);
            }
            partieDomino.setRotate(0);
            for (Node n : partieDomino.getChildren()) {
                n.setRotate(0);
            }
            partieDomino.setVisible(true);
            partieValidateButton.setDisable(true);
            partieDropDominoButton.setDisable(false);
            configStyle.setFixedDimensions(partieDomino, caseDimension * 2, caseDimension);
        }
    }

    private void fillJoueurLabel(Joueur j) {
        partieTourJoueurLabel.setTextFill(Color.web(j.getCouleurRoi().getColor()));
        partieTourJoueurLabel.setText(j.getNomJoueur());
        partieTourJoueurLabel.setVisible(true);
    }

    private void fillLabelWithCase(Label l, Case c, boolean lockedOnCaseNull) {
        if(l != null){
            l.setGraphic(null);
            if (c == null) {
                l.setBackground(configStyle.getBackground(lockedOnCaseNull?"locked":"empty"));
                l.setText("");
            } else {
                if(c.getTerrain().equals(Terrain.CHATEAU)){
                    ImageView crown = new ImageView("./view/img/crown.png");
                    if(l.getWidth() < configStyle.getResizeCrownLimit()){
                        crown.setFitHeight(configStyle.getResizedCrownSize());
                        crown.setFitWidth(configStyle.getResizedCrownSize());
                    }
                    l.setGraphic(crown);
                }else{
                    l.setText("" + c.getNbCouronne());
                }
                l.setBackground(configStyle.getBackground(c.getTerrain().getLibelle()));
            }
        }
    }

    private void translatePlateau() {
        if(placement.needsTranslation()){
            int[] translation = placement.getTranslation();
            int xStart, xEnd, xInc, yStart, yEnd, yInc;
            boolean xRotation = translation[0] != 0;
            boolean lastRow = false, lastCol = false;

            if(translation[0] < 0 || !xRotation){
                xStart = 0;
                xEnd = colRowSize - 1 + translation[0];
                xInc = 1;
            }else{
                xStart = colRowSize - 1;
                xEnd = translation[0];
                xInc = -1;
            }
            if(translation[1] < 0 || xRotation){
                yStart = 0;
                yEnd = colRowSize - 1 + translation[1];
                yInc = 1;
            }else{
                yStart = colRowSize - 1;
                yEnd = translation[1];
                yInc = -1;
            }

            for(int i = xStart ; !lastRow; i += xInc){
                if(i == xEnd) lastRow = true;

                for(int j = yStart ; !lastCol; j += yInc){
                    if(j == yEnd) lastCol = true;
                    Label target = getCaseLabel(plateauDisplay, i, j, null);
                    if ((xRotation && i != xEnd) || (!xRotation && j != yEnd)){
                        Label source = getCaseLabel(plateauDisplay, i - translation[0], j - translation[1], null);
                        target.setGraphic(source.getGraphic());
                        target.setText(source.getText());
                        target.setBackground(source.getBackground());
                    }else{
                        target.setText("");
                        target.setGraphic(null);
                    }
                }
                lastCol = false;
            }
        }
    }

    private void recomputeLockedCases(PlacementDomino placementDomino) {
        int[] translation = placementDomino.getTranslation();
        int[] xBounds = placementDomino.getNewXBounds(joueurActuel.getPlateau().getXBounds());
        int[] yBounds = placementDomino.getNewYBounds(joueurActuel.getPlateau().getYBounds());

        for(int i = 0; i < colRowSize; i++){
            for(int j = 0; j < colRowSize; j++){
                if(joueurActuel.getPlateau().getCaseAt(i - 1 - translation[0], j - 1 - translation[1]) == null) {
                    if(((xBounds[1] - xBounds[0] + 1 == joueurActuel.getPlateau().getSize()) && (i == xBounds[0] - 1 || i == xBounds[1] + 1)) ||
                            ((yBounds[1] - yBounds[0] + 1 == joueurActuel.getPlateau().getSize()) && (j == yBounds[0] - 1 || j == yBounds[1] + 1))){
                        getCaseLabel(plateauDisplay, i, j, null).setBackground(configStyle.getBackground("locked"));
                    }else{
                        if(i < xBounds[0] - 1 || i > xBounds[1] + 1 || j < yBounds[0] - 1 || j > yBounds[1] + 1){
                            getCaseLabel(plateauDisplay, i, j, null).setBackground(configStyle.getBackground("locked"));
                        }else if(joueurActuel.getPlateau().getCaseAt(i - 1 + translation[0], j - 1 + translation[1]) == null){
                            getCaseLabel(plateauDisplay, i, j, null).setBackground(configStyle.getBackground("empty"));
                        }
                    }
                }
            }
        }
    }

    private void resetDomino() {
        if (placement != null && placement.isOnPlateau()) {
            placement.removeFromPlateau();
            fillPlateau(joueurActuel.getPlateau(), null);
        }
        partieDomino.setRotate(0);
        for (Node n : partieDomino.getChildren()) {
            n.setRotate(0);
        }
        placement.setSens(Orientation.EST);
        partieDomino.setVisible(true);
        partieValidateButton.setDisable(true);
        partieDropDominoButton.setDisable(false);
    }

    private void validateDominoPlacement(PlacementDomino placementDomino) throws DominoException, TuileException {
        joueurActuel.getPlateau().addDomino(placementDomino.getDomino(), placementDomino.getRow() - 1, placementDomino.getColumn() - 1, placementDomino.getCaseId(), placementDomino.getSens());
        fillMiniPlateau(joueurActuel);
        synchronized (partieLocker) { partieLocker.notifyAll(); }
    }

    public void showPiocheDialog(){
        partiePiocheParent.toFront();
        dialog.show();
    }

    public void closePiocheDialog(){
        dialog.close();
        new Thread(()->{
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Platform.runLater(()->partiePiocheParent.toBack());
        }).start();
    }

    private void simulePlacementDomino(){
        new Thread(() -> {
            try {
                Thread.sleep(status.getTotalTime());
                PlacementDomino tmpPlacement = joueurActuel.pickPossibilite(dominoActuel);
                //tmpPlacement.positionOnPlateau(tmpPlacement.getRow(), tmpPlacement.getColumn());
                Thread.sleep(1500);
                Platform.runLater(()->insertDomino(tmpPlacement, getCaseLabel(plateauDisplay, tmpPlacement.getRow()+1, tmpPlacement.getColumn()+1, null),
                        tmpPlacement.getCase(false)));
                Thread.sleep(2*500);
                Platform.runLater(()-> {
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try { validateDominoPlacement(tmpPlacement); } catch (Exception ignored) { }
                });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void displayResultsDialog(List<Joueur> joueurs){
        List<List<Joueur>> formattedScore = formatScores(joueurs);

        JFXDialog resultDialog = new JFXDialog();
        resultDialog.setOverlayClose(false);
        resultDialog.setDialogContainer(partiePiocheParent);

        GridPane resultsContent = new GridPane();
        ColumnConstraints c1 = new ColumnConstraints();
        c1.setHalignment(HPos.CENTER);
        RowConstraints r1 = new RowConstraints();
        r1.setPercentHeight(20);
        RowConstraints r2 = new RowConstraints();
        r2.setPercentHeight(60);
        RowConstraints r3 = new RowConstraints();
        r3.setPercentHeight(20);
        resultsContent.getRowConstraints().addAll(r1, r2, r3);
        resultsContent.getColumnConstraints().add(c1);

        Label fini = new Label("Partie terminée !");
        configStyle.setFixedWidth(fini, 600);
        fini.setStyle("-fx-alignment:center;-fx-font-size:24px;-fx-font-weight:bold");
        resultsContent.add(fini, 0, 0);

        GridPane scorePane = new GridPane();
        ColumnConstraints c21 = new ColumnConstraints();
        c21.setHalignment(HPos.CENTER);
        RowConstraints r21 = new RowConstraints();
        r21.setPercentHeight(15);
        RowConstraints r22 = new RowConstraints();
        r22.setPercentHeight(85);
        r22.setValignment(VPos.CENTER);
        scorePane.getColumnConstraints().add(c21);
        scorePane.getRowConstraints().addAll(r21, r22);

        VBox scoresList = new VBox();
        scoresList.setAlignment(Pos.CENTER);
        for(int i = 0; i < joueurs.size(); i++){
            if(i == 0){
                StringBuilder sb = new StringBuilder();
                for(Joueur j : formattedScore.get(i)){
                    sb.append(j.getNomJoueur()).append(", ");
                }
                if(formattedScore.get(i).size() == 1){
                    sb.append("vous êtes le vainqueur !");
                } else {

                    sb.append("vous êtes les vainqueurs !");
                }
                Label vainqueurs = new Label(sb.toString());
                configStyle.setFixedWidth(vainqueurs, 600);
                vainqueurs.setStyle("-fx-alignment:center;-fx-font-size:18px;-fx-font-weight:bold;");
                scorePane.add(vainqueurs, 0, 0);

                Label titreScores = new Label("Les scores :");
                configStyle.setFixedWidth(titreScores, 600);
                titreScores.setStyle("-fx-alignment:center;-fx-font-size:14px;-fx-font-weight:bold;");
                scoresList.getChildren().add(titreScores);
            }

            for(Joueur j : formattedScore.get(i)){
                Label scoreJoueur = new Label(formatScoreLineString(i + 1, j.getNomJoueur(), j.getScore(), configStyle.getScoreLineSize()));
                scoreJoueur.setStyle("-fx-alignment:center;-fx-font-size:14px;-fx-font-weight:bold;");
                scoresList.getChildren().add(scoreJoueur);
                configStyle.setFixedWidth(scoreJoueur, 600);
            }
        }
        scorePane.add(scoresList, 0, 1);
        resultsContent.add(scorePane, 0 , 1);

        HBox buttons = new HBox();
        buttons.setAlignment(Pos.CENTER);
        buttons.setSpacing(10);

        ImageView home = new ImageView("./view/img/home.png");
        home.setFitHeight(configStyle.getResizedCrownSize());
        home.setFitWidth(configStyle.getResizedCrownSize());
        JFXButton homeButton = new JFXButton("Menu principal", home);
        homeButton.setStyle("-fx-background-color:#424242;-fx-text-fill:white;");
        configStyle.setFixedDimensions(homeButton, 190, 40);
        homeButton.setOnAction(event -> {

        });

        ImageView param = new ImageView("./view/img/settings.png");
        param.setFitHeight(configStyle.getResizedCrownSize());
        param.setFitWidth(configStyle.getResizedCrownSize());
        JFXButton paramButton = new JFXButton("Menu des paramètres", param);
        paramButton.setStyle("-fx-background-color:#424242;-fx-text-fill:white;");
        configStyle.setFixedDimensions(paramButton, 190, 40);
        paramButton.setOnAction(event -> {

        });
        buttons.getChildren().addAll(homeButton, paramButton);

        resultsContent.add(buttons, 0 , 2);
        dialog.setContent(resultsContent);
        configStyle.setFixedDimensions(dialog.getContent(), 600, 400);
        dialog.toFront();
        dialog.show();
    }

    private List<List<Joueur>> formatScores(List<Joueur> joueurs){
        List<List<Joueur>> formattedScores = new ArrayList<>();
        for(int i = 0; i < joueurs.size(); i++){
            Joueur joueur1 = joueurs.get(i);
            formattedScores.add(i, new ArrayList<>());
            formattedScores.get(i).add(joueur1);
            if(joueur1.getEgalite()){
                for(int j = i+1; j < joueurs.size(); j++){
                    Joueur joueur2 = joueurs.get(j);
                    if(joueur2.getEgalite() && joueur1.getScoreCouronne() == joueur2.getScoreCouronne()){
                        formattedScores.get(i).add(joueur2);
                        formattedScores.add(j, new ArrayList<>());
                        i = j;
                    }else{
                        break;
                    }
                }
            }
        }
        return formattedScores;
    }

    private String formatScoreLineString(int rankIndex, String playerName, int points, int targetSize) {
        StringBuilder result = new StringBuilder(""+rankIndex+". ");
        result.append(playerName);
        String pointsString = ""+points+"pt"+(points==1?"":"s");
        System.out.println(result.length());
        System.out.println(pointsString.length());
        while (result.length() + pointsString.length() <= targetSize){
            result.append(" ");
        }
        result.append(pointsString);
        return result.toString();
    }

    private void displayTab() {
        for (Node n : plateauDisplay.getChildren()) {
            List<BackgroundFill> fills = ((Label) n).getBackground().getFills();
            System.out.println(fills.size() == 0 ? "EMPTY" : fills.get(0).getFill());
        }
        System.out.println();
    }

    public IDomino getDomino(int id){
        for(IDomino d : deckDominos){
            if(d.getIdentifiant() == id){
                return d;
            }
        }
        return null;
    }

    public void fillDeck(String path) throws IOException {
        List<String[]> dominos = CSVParser.parse(path, ",", true);

        for(String[] strs : dominos) {
            Case case1 = new Case(Integer.parseInt(strs[0]), Terrain.getTerrain(strs[1]));
            Case case2 = new Case(Integer.parseInt(strs[2]), Terrain.getTerrain(strs[3]));
            IDomino domino = new Domino(case1,case2,Integer.parseInt(strs[4]));
            deckDominos.add(domino);
        }
    }

    private void nextStep() throws DominoException, TuileException {
        String[] strs = null;
        try{
            strs = dominosPlacement.remove(0);
            if(Integer.parseInt(strs[0]) <= 0 ){
                joueurActuel.getPlateau().addDomino(new Tuile(), Integer.parseInt(strs[1]),
                        Integer.parseInt(strs[2]), 0, null);
            }else{
                //plateauActuel.possibilite(getDomino(Integer.parseInt(strs[0])));
                joueurActuel.getPlateau().addDomino(getDomino(Integer.parseInt(strs[0])),
                        Integer.parseInt(strs[1]), Integer.parseInt(strs[2]),
                        Integer.parseInt(strs[3]), Orientation.getOrientation(strs[4]));
            }
        }catch (IndexOutOfBoundsException ignored){}

        try{
            strs = dominosPlacement.get(0);
            System.out.println(joueurActuel.getPlateau().affichePlateau(true));;
            fillDomino(getDomino(Integer.parseInt(strs[0])));
            fillPlateau(joueurActuel.getPlateau(), null);
        }catch (IndexOutOfBoundsException ignored){};
    }
}
