package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import controller.util.ConfigStyle;
import controller.util.IndicatorFader;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.PointLight;
import javafx.stage.Screen;
import javafx.stage.StageStyle;
import javafx.stage.Window;
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

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
    private JFXButton toggle;

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
    private JFXButton trynextbutton;

    @FXML
    private JFXButton toggleDialog;

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

    private PlacementDomino placement = null;

    private Plateau plateauActuel = null;

    private Partie partie;

    private double windowWidth, windowHeight, offsetDialog = 100;

    private final DataFormat caseDominoFormat = new DataFormat("plateau.Case");

    @FXML
    void initialize() throws DominoException, TuileException, IOException {
        assert partieIndicator != null : "fx:id=\"partieG2Indicator\" was not injected: check your FXML file 'partie.fxml'.";
        assert partieTitleIndicator != null : "fx:id=\"partieTitleIndicator\" was not injected: check your FXML file 'partie.fxml'.";
        assert partieTextIndicator != null : "fx:id=\"partiematicoTextIndicator\" was not injected: check your FXML file 'partie.fxml'.";
        assert leftContent != null : "fx:id=\"leftContent\" was not injected: check your FXML file 'partie.fxml'.";
        assert toggle != null : "fx:id=\"toggle\" was not injected: check your FXML file 'partie.fxml'.";
        assert rightContent != null : "fx:id=\"rightContent\" was not injected: check your FXML file 'partie.fxml'.";
        assert partieTourJoueurLabel != null : "fx:id=\"partieUYurJoueurLabel\" was not injected: check your FXML file 'partie.fxml'.";
        assert partieDomino != null : "fx:id=\"partieDomino\" was not injected: check your FXML file 'partie.fxml'.";
        assert partiePlateauContainer != null : "fx:id=\"partiePlateauContainer\" was not injected: check your FXML file 'partie.fxml'.";

        status = new IndicatorFader(partieIndicator, partieTitleIndicator, partieTextIndicator, 3000, 500);
        configStyle = ConfigStyle.getInstance();

        initDialog();
        initPlateau(7);
        partieValidateButton.setDisable(true);
        partieDropDominoButton.setDisable(false);

        List<String[]> exPlateau = CSVParser.parse("./test_plateau.csv", ",", true);
        this.fillDeck("./dominos.csv");

        toggle.setOnAction(event -> {
            status.display("Hello", "What's your name ?");
        });

        toggleDialog.setOnAction(event -> {
            showPiocheDialog();
        });


        try {
            dominosPlacement = CSVParser.parse("./test_plateau.csv", ",", true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        partieDomino.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.SECONDARY)) {
                partieDomino.setRotate(Math.floor(((int)partieDomino.getRotate() + 90) % 360));
                for (Node child : partieDomino.getChildren()) {
                    child.setRotate(-(int)partieDomino.getRotate());
                }
            }
        });

        plateauActuel = new Plateau(5);

        partieRevertDominoButton.setOnAction(event -> {
            resetDomino();
        });

        trynextbutton.setOnAction(event -> {
            try {
                nextStep();
            } catch (DominoException e) {
                e.printStackTrace();
            } catch (TuileException e) {
                e.printStackTrace();
            }
        });



        initDominoSourceDrag();
        initDominoTargetDrag();

        nextStep();
        try {
            jouerPartie();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void jouerPartie() throws Exception {
        List<IDomino> deck = Jeu.chargementDominos("./dominos.csv");
        ModeJeu modeJeu = ModeJeu.STANDARD;
        NbJoueur nbJoueur = NbJoueur.jeuA4;
        List<Joueur> joueurs = new ArrayList<>();
        joueurs.add(new Joueur("Seb", Roi.getRoiInt(0), nbJoueur, modeJeu, 0));
        joueurs.add(new Joueur("Laurent", Roi.getRoiInt(1), nbJoueur, modeJeu, 0));
        joueurs.add(new Joueur("Mathieu", Roi.getRoiInt(2), nbJoueur, modeJeu, 0));
        joueurs.add(new Joueur("Kabir", Roi.getRoiInt(3), nbJoueur, modeJeu, 0));
        partie = new Partie(joueurs, deck, nbJoueur, modeJeu);

        new Thread(() -> {
            Object partieLocker = new Object();
            piocheController.initContent(partie, partieLocker);
            preparePioche(partie.pioche(), partie.melangerRois());
            showPiocheDialog();
            try { synchronized(partieLocker) { partieLocker.wait(); }
            } catch (InterruptedException ignored) { }
            closePiocheDialog();
        }).start();
       /* do {
            List<IDomino> pioche = partie.pioche();
            preparePioche(pioche);
            showPiocheDialog();
            afficherPioche(pioche);
            closePiocheDialog();
            if(!partie.partieFinie()){
                List<Roi> rois = partie.melangerRois();
                tirageJoueur(rois);
                placementJoueur();
            }
        } while(!partie.partieFinie());
        afficheScore();
        System.out.println("Fin du jeu !");*/

    }

    private void initDialog(){
        dialog = new JFXDialog();
        dialog.toBack();
        dialog.setContent(pioche);
        dialog.setDialogContainer(partiePiocheParent);
        configStyle.setFixedDimensions(dialog.getContent(), 1200, 900);
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
                    //partieDomino.setVisible(false);
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
                    if(plateauActuel.placementValide(placement.getDomino(), getRow(child) - 1, getCol(child) - 1, placement.getCaseId(), placement.getSens()) == null){
                        event.acceptTransferModes(TransferMode.MOVE);
                    }
                }
                event.consume();
            });

            child.setOnDragEntered(event -> {
                Label lCase2 = getCaseLabel(getRow(child), getCol(child), placement.getSens());
                if (event.getGestureSource() != child &&
                        event.getDragboard().hasContent(caseDominoFormat)) {
                    if(plateauActuel.placementValide(placement.getDomino(), getRow(child) - 1, getCol(child) - 1, placement.getCaseId(), placement.getSens()) == null){
                        Case c = (Case) event.getDragboard().getContent(caseDominoFormat);
                        fillLabelWithCase((Label) child, c, false);
                        fillLabelWithCase(lCase2, placement.getCase(true), false);
                    }
                }
                event.consume();
            });

            child.setOnDragExited(event -> {
                Label lCase2 = getCaseLabel(getRow(child), getCol(child), placement.getSens());
                if (event.getGestureSource() != child &&
                        event.getDragboard().hasContent(caseDominoFormat)) {
                    if(plateauActuel.placementValide(placement.getDomino(), getRow(child) - 1, getCol(child) - 1, placement.getCaseId(), placement.getSens()) == null &&
                        !placement.isOnPlateau()){
                            fillLabelWithCase((Label) child, null, isLockedWhenNoDomino(plateauActuel, getRow(child) - 1, getCol(child) - 1));
                            if(lCase2 != null) fillLabelWithCase(lCase2, null, isLockedWhenNoDomino(plateauActuel, getRow(lCase2) - 1, getCol(lCase2) - 1));
                    }
                }
                event.consume();
            });

            child.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasContent(caseDominoFormat) &&
                        plateauActuel.placementValide(placement.getDomino(), getRow(child) - 1, getCol(child) - 1,
                                placement.getCaseId(), placement.getSens()) == null) {
                    success = true;
                    Case c = (Case) event.getDragboard().getContent(caseDominoFormat);
                    int row = getRow(child);
                    int col = getCol(child);
                    fillLabelWithCase(getCaseLabel(row, col, null), null, false);

                    int[] translation = plateauActuel.calculTranslation(placement.getDomino(), row - 1, col - 1, placement.getCaseId(), placement.getSens());
                    placement.setTranslation(translation);
                    placement.positionOnPlateau(row, col);
                    if(placement.needsTranslation()){
                        translatePlateau();
                        row += translation[0];
                        col += translation[1];
                    }
                    placement.positionOnPlateau(row, col);
                    recomputeLockedCases();
                    fillLabelWithCase(getCaseLabel(row, col, null), c, false);
                    fillLabelWithCase(getCaseLabel(row, col, placement.getSens()), placement.getCase(true), false);
                    partieValidateButton.setDisable(false);
                    partieDropDominoButton.setDisable(true);
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

    private Label getCaseLabel(int row, int col, Orientation sens) {
        try {
            if (sens != null) {
                if(row + sens.getOffsetX() < 0 || row + sens.getOffsetX() >= colRowSize) return null;
                else return (Label) plateauDisplay.getChildren().get((col + placement.getColCase2Offset()) * colRowSize +
                        (row + placement.getRowCase2Offset()));
            } else {
                if(row < 0 || row >= colRowSize) return null;
                else return (Label) plateauDisplay.getChildren().get(col * colRowSize + row);
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

    private void fillPlateau(Plateau p, Joueur joueur) {
        if (p.tuilePresente()) {
            int pSize = p.getSize();
            for (int i = -1; i <= pSize; i++) {
                for (int j = -1; j <= pSize; j++) {
                    Label lab = getCaseLabel(i + 1, j + 1, null);
                    fillLabelWithCase(lab, p.getCaseAt(i, j), isLockedWhenNoDomino(p, i, j));
                }
            }
        } else {

        }
    }

    private void fillDomino(IDomino d) {
        double caseDimension = configStyle.getCaseDimension();
        if (d instanceof Domino) {
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
            configStyle.setFixedDimensions(partieDomino, caseDimension * 2, caseDimension);
        }
    }

    private void fillLabelWithCase(Label l, Case c, boolean lockedOnCaseNull) {
        if(l != null){
            l.setGraphic(null);
            if (c == null) {
                l.setBackground(configStyle.getBackground(lockedOnCaseNull?"locked":"empty"));
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
                    Label target = getCaseLabel(i, j, null);
                    if ((xRotation && i != xEnd) || (!xRotation && j != yEnd)){
                        Label source = getCaseLabel(i - translation[0], j - translation[1], null);
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

    private void recomputeLockedCases() {
        int[] translation = placement.getTranslation();
        int[] xBounds = placement.getNewXBounds(plateauActuel.getXBounds());
        int[] yBounds = placement.getNewYBounds(plateauActuel.getYBounds());

        for(int i = 0; i < colRowSize; i++){
            for(int j = 0; j < colRowSize; j++){
                if(plateauActuel.getCaseAt(i - 1 - translation[0], j - 1 - translation[1]) == null) {
                    if(((xBounds[1] - xBounds[0] + 1 == plateauActuel.getSize()) && (i == xBounds[0] - 1 || i == xBounds[1] + 1)) ||
                            ((yBounds[1] - yBounds[0] + 1 == plateauActuel.getSize()) && (j == yBounds[0] - 1 || j == yBounds[1] + 1))){
                        getCaseLabel(i, j, null).setBackground(configStyle.getBackground("locked"));
                    }else{
                        if(i < xBounds[0] - 1 || i > xBounds[1] + 1 || j < yBounds[0] - 1 || j > yBounds[1] + 1){
                            getCaseLabel(i, j, null).setBackground(configStyle.getBackground("locked"));
                        }else if(plateauActuel.getCaseAt(i - 1 + translation[0], j - 1 + translation[1]) == null){
                            getCaseLabel(i, j, null).setBackground(configStyle.getBackground("empty"));
                        }
                    }
                }
            }
        }
    }

    private void resetDomino() {
        if (placement != null && placement.isOnPlateau()) {
            placement.removeFromPlateau();
            fillPlateau(plateauActuel, null);
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
                plateauActuel.addDomino(new Tuile(), Integer.parseInt(strs[1]),
                        Integer.parseInt(strs[2]), 0, null);
            }else{
                //plateauActuel.possibilite(getDomino(Integer.parseInt(strs[0])));
                plateauActuel.addDomino(getDomino(Integer.parseInt(strs[0])),
                        Integer.parseInt(strs[1]), Integer.parseInt(strs[2]),
                        Integer.parseInt(strs[3]), Orientation.getOrientation(strs[4]));
            }
        }catch (IndexOutOfBoundsException ignored){}

        try{
            strs = dominosPlacement.get(0);
            System.out.println(plateauActuel.affichePlateau(true));;
            fillDomino(getDomino(Integer.parseInt(strs[0])));
            fillPlateau(plateauActuel, null);
        }catch (IndexOutOfBoundsException ignored){};
    }
}
