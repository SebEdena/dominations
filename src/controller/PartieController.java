package controller;

import com.jfoenix.controls.JFXButton;
import controller.util.IndicatorFader;
import controller.util.PlacementDomino;
import exceptions.DominoException;
import exceptions.TuileException;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import jeu.Joueur;
import plateau.*;
import util.CSVParser;

import java.io.IOException;
import java.net.URL;
import java.util.*;

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


    private final double caseDimension = 50;

    private Map<String, Background> correspondanceStyle;

    private IndicatorFader status;

    private GridPane plateauDisplay;

    private int colRowSize;

    private List<IDomino> deckDominos = new ArrayList<>();

    private List<String[]> dominosPlacement = null;

    private PlacementDomino placement = null;

    private Plateau p = null;

    private final DataFormat caseDominoFormat = new DataFormat("plateauDisplay.Case");

    @FXML
    void initialize() throws DominoException, TuileException, IOException {
        assert partieIndicator != null : "fx:id=\"partieIndicator\" was not injected: check your FXML file 'partie.fxml'.";
        assert partieTitleIndicator != null : "fx:id=\"partieTitleIndicator\" was not injected: check your FXML file 'partie.fxml'.";
        assert partieTextIndicator != null : "fx:id=\"partieTextIndicator\" was not injected: check your FXML file 'partie.fxml'.";
        assert leftContent != null : "fx:id=\"leftContent\" was not injected: check your FXML file 'partie.fxml'.";
        assert toggle != null : "fx:id=\"toggle\" was not injected: check your FXML file 'partie.fxml'.";
        assert rightContent != null : "fx:id=\"rightContent\" was not injected: check your FXML file 'partie.fxml'.";
        assert partieTourJoueurLabel != null : "fx:id=\"partieTourJoueurLabel\" was not injected: check your FXML file 'partie.fxml'.";
        assert partieDomino != null : "fx:id=\"partieDomino\" was not injected: check your FXML file 'partie.fxml'.";
        assert partiePlateauContainer != null : "fx:id=\"partiePlateauContainer\" was not injected: check your FXML file 'partie.fxml'.";

        status = new IndicatorFader(partieIndicator, partieTitleIndicator, partieTextIndicator, 3000, 500);

        initCorrespondanceStyle();
        initPlateau(7);

        List<String[]> exPlateau = CSVParser.parse("./test_plateau.csv", ",", true);
        this.fillDeck("./dominos.csv");

        toggle.setOnAction(event -> {
            status.display("Hello", "What's your name ?");
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

        p = new Plateau(5);

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
    }

    private void nextStep() throws DominoException, TuileException {
        String[] strs = null;
        try{
            strs = dominosPlacement.remove(0);
            if(Integer.parseInt(strs[0]) <= 0 ){
                p.addDomino(new Tuile(), Integer.parseInt(strs[1]),
                        Integer.parseInt(strs[2]), 0, null);
            }else{
                //p.possibilite(getDomino(Integer.parseInt(strs[0])));
                p.addDomino(getDomino(Integer.parseInt(strs[0])),
                        Integer.parseInt(strs[1]), Integer.parseInt(strs[2]),
                        Integer.parseInt(strs[3]), Orientation.getOrientation(strs[4]));
            }
        }catch (IndexOutOfBoundsException ignored){}

        try{
            strs = dominosPlacement.get(0);
            System.out.println(p.affichePlateau(true));;
            fillDomino(getDomino(Integer.parseInt(strs[0])));
            fillPlateau(p, null);
        }catch (IndexOutOfBoundsException ignored){};
    }

    private void fillDomino(IDomino d) {
        if (d instanceof Domino) {
            placement = new PlacementDomino(d, Orientation.EST);
            Case[] cases = d.getCases();
            for (int i = 0; i < cases.length; i++) {
                Label caseDomino = (Label) partieDomino.getChildren().get(i);
                caseDomino.setStyle(caseDomino.getStyle() + "-fx-background-color:" + cases[i].getTerrain().getColor() + ";" +
                        "-fx-text-fill:white;");
                caseDomino.setText("" + cases[i].getNbCouronne());
                setFixedDimensions(caseDomino, caseDimension, caseDimension);
            }
            partieDomino.setRotate(0);
            for (Node n : partieDomino.getChildren()) {
                n.setRotate(0);
            }
            setFixedDimensions(partieDomino, caseDimension * 2, caseDimension);
        }
    }

    private void initPlateau(int nbColLig) {
        this.colRowSize = nbColLig;
        plateauDisplay = new GridPane();
        setFixedDimensions(plateauDisplay, caseDimension * nbColLig, caseDimension * nbColLig);

        for (int i = 0; i < nbColLig; i++) {
            for (int j = 0; j < nbColLig; j++) {
                Label label = new Label();
                setFixedDimensions(label, caseDimension, caseDimension);
                label.setAlignment(Pos.CENTER);
                label.setStyle("-fx-font-size:18px;-fx-font-weight:bold;-fx-text-fill:white;-fx-border-color:black;");
                label.setBackground(correspondanceStyle.get("empty"));
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
                    if(p.placementValide(placement.getDomino(), getRow(child) - 1, getCol(child) - 1, placement.getCaseId(), placement.getSens()) == null){
                        event.acceptTransferModes(TransferMode.MOVE);
                    }
                }
                event.consume();
            });

            child.setOnDragEntered(event -> {
                Label lCase2 = getCaseLabel(getRow(child), getCol(child), placement.getSens());
                if (event.getGestureSource() != child &&
                        event.getDragboard().hasContent(caseDominoFormat)) {
                    if(p.placementValide(placement.getDomino(), getRow(child) - 1, getCol(child) - 1, placement.getCaseId(), placement.getSens()) == null){
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
                    if(p.placementValide(placement.getDomino(), getRow(child) - 1, getCol(child) - 1, placement.getCaseId(), placement.getSens()) == null &&
                        !placement.isOnPlateau()){
                            fillLabelWithCase((Label) child, null, isLockedWhenNoDomino(p, getRow(child) - 1, getCol(child) - 1));
                            if(lCase2 != null) fillLabelWithCase(lCase2, null, isLockedWhenNoDomino(p, getRow(lCase2) - 1, getCol(lCase2) - 1));
                    }
                }
                event.consume();
            });

            child.setOnDragDropped(event -> {
                try{

                    Dragboard db = event.getDragboard();
                    boolean success = false;
                    if (db.hasContent(caseDominoFormat) &&
                            p.placementValide(placement.getDomino(), getRow(child) - 1, getCol(child) - 1,
                                    placement.getCaseId(), placement.getSens()) == null) {
                        success = true;
                        Case c = (Case) event.getDragboard().getContent(caseDominoFormat);
                        int row = getRow(child);
                        int col = getCol(child);
                        fillLabelWithCase(getCaseLabel(row, col, null), null, false);

                        int[] translation = p.calculTranslation(placement.getDomino(), row - 1, col - 1, placement.getCaseId(), placement.getSens());
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
                    }
                    event.setDropCompleted(success);
                    event.consume();
                }catch (Exception e){
                    e.printStackTrace();
                }
            });
        }
    }

    private void recomputeLockedCases() {

        int[] translation = placement.getTranslation();
        int[] xBounds = p.getXBounds();
        int[] yBounds = p.getYBounds();

        xBounds[0] = xBounds[0] - ((translation[0] != 0)?translation[0] + Integer.signum(translation[0]):1);
        xBounds[1] = xBounds[1] + ((translation[0] != 0)?translation[0] + Integer.signum(translation[0]):1);
        yBounds[0] = yBounds[0] - ((translation[1] != 0)?translation[1]  +Integer.signum(translation[1]):1);
        yBounds[1] = yBounds[1] + ((translation[1] != 0)?translation[1]  + Integer.signum(translation[1]):1);

        if(translation[0] < 0 && xBounds[0] < 0) xBounds[0] = 0;
        if(translation[0] > 0 && xBounds[1] >= colRowSize) xBounds[1] = colRowSize - 1;
        if(translation[1] < 0 && yBounds[0] < 0) yBounds[0] = 0;
        if(translation[1] > 0 && yBounds[1] >= colRowSize) yBounds[1] = colRowSize - 1;

        for(int i = 0; i < colRowSize; i++){
            for(int j = 0; j < colRowSize; j++){
                if(p.getCaseAt(i - 1 - translation[0], j - 1 - translation[1]) == null) {
                    if(((xBounds[1] - xBounds[0] + 1 == p.getSize()) && (i == xBounds[0] - 1 || i == xBounds[1] + 1)) ||
                            ((yBounds[1] - yBounds[0] + 1 == p.getSize()) && (j == yBounds[0] - 1 || j == yBounds[1] + 1))){
                        getCaseLabel(i, j, null).setBackground(correspondanceStyle.get("locked"));
                    }else{
                        if(i < xBounds[0] - 1 || i > xBounds[1] + 1 || j < yBounds[0] - 1 || j > yBounds[1] + 1){
                            getCaseLabel(i, j, null).setBackground(correspondanceStyle.get("locked"));
                        }else if(p.getCaseAt(i - 1 + translation[0], j - 1 + translation[1]) == null){
                            getCaseLabel(i, j, null).setBackground(correspondanceStyle.get("empty"));
                        }
                    }
                }
            }
        }
    }

    private void initCorrespondanceStyle() {
        correspondanceStyle = new HashMap<>();
        correspondanceStyle.put("empty", Background.EMPTY);
        correspondanceStyle.put("hover", new Background(new BackgroundFill(Color.web("#afafaf"), null, null)));
        correspondanceStyle.put("locked", new Background(new BackgroundFill(Color.web("#d50000"), null, null)));
        for (int i = 0; i < Terrain.values().length; i++) {
            Terrain t = Terrain.values()[i];
            correspondanceStyle.put(t.getLibelle(), new Background(new BackgroundFill(Color.web(t.getColor()), null, null)));
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

    private void displayTab() {
        for (Node n : plateauDisplay.getChildren()) {
            List<BackgroundFill> fills = ((Label) n).getBackground().getFills();
            System.out.println(fills.size() == 0 ? "EMPTY" : fills.get(0).getFill());
        }
        System.out.println();
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

    private void fillLabelWithCase(Label l, Case c, boolean lockedOnCaseNull) {
        if(l != null){
            l.setGraphic(null);
            if (c == null) {
                l.setBackground(correspondanceStyle.get(lockedOnCaseNull?"locked":"empty"));
                l.setText("");
            } else {
                if(c.getTerrain().equals(Terrain.CHATEAU)){
                    l.setGraphic(new ImageView("./view/img/crown.png"));
                }else{
                    l.setText("" + c.getNbCouronne());
                }
                l.setBackground(correspondanceStyle.get(c.getTerrain().getLibelle()));
            }
        }
    }

    private int getRow(Node node) {
        return GridPane.getRowIndex(node);
    }

    private int getCol(Node node) {
        return GridPane.getColumnIndex(node);
    }

    private void setFixedDimensions(Region r, double width, double height) {
        r.setPrefSize(width, height);
        r.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        r.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
    }

    private boolean isFree(Label label) {
        return p.getCaseAt(getRow(label) - 1, getCol(label) - 1) == null;
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

    private void resetDomino() {
        if (placement != null && placement.isOnPlateau()) {
            int row2 = placement.getRowCase2();
            int col2 = placement.getColCase2();
            fillLabelWithCase(getCaseLabel(placement.getRow(), placement.getColumn(), null), null, false);
            fillLabelWithCase(getCaseLabel(row2, col2, null), null, false);
            placement.removeFromPlateau();
        }
        partieDomino.setRotate(0);
        for (Node n : partieDomino.getChildren()) {
            n.setRotate(0);
        }
        placement.setSens(Orientation.EST);
        partieDomino.setVisible(true);
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

    private void translatePlateau() {
        if(placement.needsTranslation()){
            Label lCase2 = (Label)partieDomino.getChildren().get(Math.abs(placement.getCaseId() - 1));
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
                        System.out.println(i + " " + j + " : trans");
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
}
