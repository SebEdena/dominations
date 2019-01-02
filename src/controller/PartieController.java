package controller;

import com.jfoenix.controls.JFXButton;
import controller.util.IndicatorFader;
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
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
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


    private final double caseDimension = 50;

    private Map<String, Background> correspondanceStyle;

    private IndicatorFader status;

    private GridPane plateauDisplay;

    private int colRowSize;

    private List<IDomino> deckDominos = new ArrayList<>();

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

        toggle.setOnAction(event -> {
            status.display("Hello", "What's your name ?");
        });

        List<String[]> dominos = null;
        try {
            dominos = CSVParser.parse("./dominos.csv", ",", true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (String[] strs : dominos) {
            Case case1 = new Case(Integer.parseInt(strs[0]), Terrain.getTerrain(strs[1]));
            Case case2 = new Case(Integer.parseInt(strs[2]), Terrain.getTerrain(strs[3]));
            IDomino domino = new Domino(case1, case2, Integer.parseInt(strs[4]));
            deckDominos.add(domino);
        }

        fillDomino(deckDominos.get(35));
        partieDomino.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.SECONDARY)) {
                partieDomino.setRotate(Math.floor(partieDomino.getRotate() + 90));
                for (Node child : partieDomino.getChildren()) {
                    child.setRotate(Math.floor(child.getRotate() - 90));
                }
                placement.setSens(placement.getSens().getRotationHoraire());
            }
        });

        partieRevertDominoButton.setOnAction(event -> {
            resetDomino();
        });

        initDominoSourceDrag();
        initDominoTargetDrag();


        List<String[]> exPlateau = CSVParser.parse("./test_plateau.csv", ",", true);
        this.fillDeck("./dominos.csv");

        p = new Plateau(5);

        for(String[] strs : exPlateau){
            if(Integer.parseInt(strs[0]) <= 0 ){
                p.addDomino(new Tuile(), Integer.parseInt(strs[1]),
                        Integer.parseInt(strs[2]), 0, null);
            }else{
                //p.possibilite(getDomino(Integer.parseInt(strs[0])));
                p.addDomino(getDomino(Integer.parseInt(strs[0])),
                        Integer.parseInt(strs[1]), Integer.parseInt(strs[2]),
                        Integer.parseInt(strs[3]), Orientation.getOrientation(strs[4]));
            }
            System.out.println(p.affichePlateau(true));
        }
        System.out.println(p.affichePlateau(true));
        System.out.println(p.calculPoint());

        fillPlateau(p, null);
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
                    content.put(caseDominoFormat, placement.getDomino().getCases()[pickedCase]);
                    placement.setCaseId(pickedCase);
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
                System.out.println("____OVER___");
                Label lCase2 = getCaseLabel(getRow(child), getCol(child), placement.getSens());
                if (event.getGestureSource() != child &&
                        event.getDragboard().hasContent(caseDominoFormat) &&
                        isFree((Label) child) &&
                        (lCase2 == null || isFree(lCase2))) {
                    if(p.placementValide(placement.getDomino(), getRow(child) - 1, getCol(child) - 1, placement.getCaseId(), placement.getSens()) == null){
                        event.acceptTransferModes(TransferMode.MOVE);
                    }
                }
                event.consume();
            });

            child.setOnDragEntered(event -> {
                System.out.println("____ENTER___");
                Label lCase2 = getCaseLabel(getRow(child), getCol(child), placement.getSens());
                if (event.getGestureSource() != child &&
                        event.getDragboard().hasContent(caseDominoFormat) &&
                        isFree((Label) child) &&
                        (isFree(lCase2))) {
                    if(p.placementValide(placement.getDomino(), getRow(child) - 1, getCol(child) - 1, placement.getCaseId(), placement.getSens()) == null){
                        ((Control) child).setBackground(correspondanceStyle.get("hover"));
                        lCase2.setBackground(correspondanceStyle.get("hover"));
                    }
                }
                event.consume();
            });

            child.setOnDragExited(event -> {
                Label lCase2 = getCaseLabel(getRow(child), getCol(child), placement.getSens());
                System.out.println("____EXIT___");
                if (event.getGestureSource() != child &&
                        event.getDragboard().hasContent(caseDominoFormat) &&
                        isFree((Label) child) &&
                        (lCase2 == null || isFree(lCase2))) {
                    if(p.placementValide(placement.getDomino(), getRow(child) - 1, getCol(child) - 1, placement.getCaseId(), placement.getSens()) == null){
                        ((Control) child).setBackground(correspondanceStyle.get("empty"));
                        lCase2.setBackground(correspondanceStyle.get("empty"));
                    }
                }
                event.consume();
            });

            child.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasContent(caseDominoFormat) &&
                        isFree((Label) child)) {
                    success = true;
                    Case c = (Case) event.getDragboard().getContent(caseDominoFormat);
                    int caseIndex = placement.getDomino().getCaseIndex(c, false);
                    int row = getRow(child);
                    int col = getCol(child);

                    fillLabelWithCase((Label) child, (Case) event.getDragboard().getContent(caseDominoFormat), false);
                    fillLabelWithCase(getCaseLabel(row, col, placement.getSens()),
                            placement.getDomino().getCases()[Math.abs(caseIndex - 1)], false);
                    placement.positionOnPlateau(row, col);
                }
                event.setDropCompleted(success);
                event.consume();
            });
        }
    }

    private void initCorrespondanceStyle() {
        correspondanceStyle = new HashMap<>();
        correspondanceStyle.put("empty", Background.EMPTY);
        correspondanceStyle.put("hover", new Background(new BackgroundFill(Color.GRAY, null, null)));
        correspondanceStyle.put("locked", new Background(new BackgroundFill(Color.web("d50000"), null, null)));
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

    private Label getCaseLabel(int row, int col, Orientation sens) {
        try {
            if (sens != null) {
                if ((row == 0 && placement.getRowCase2Offset() < 0) ||
                        (row == (colRowSize - 1) && placement.getRowCase2Offset() > 0)) {
                    return null;
                } else {
                    return (Label) plateauDisplay.getChildren().get((col + placement.getColCase2Offset()) * colRowSize +
                            (row + placement.getRowCase2Offset()));
                }
            } else {
                return (Label) plateauDisplay.getChildren().get(col * colRowSize + row);
            }
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    private void fillLabelWithCase(Label l, Case c, boolean lockedOnCaseNull) {
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

    private void resetDomino() {
        if (placement != null && placement.isOnPlateau()) {
            int row2 = placement.getRowCase2();
            int col2 = placement.getColCase2();
            fillLabelWithCase(getCaseLabel(placement.getRow(), placement.getColumn(), null), null, false);
            fillLabelWithCase(getCaseLabel(row2, col2, null), null, false);
            partieDomino.setRotate(0);
            placement.removeFromPlateau();
            placement.setSens(Orientation.EST);
            partieDomino.setVisible(true);
            for (Node n : partieDomino.getChildren()) {
                n.setRotate(0);
            }
        }
    }

    private void fillPlateau(Plateau p, Joueur joueur) {
        if (p.tuilePresente()) {
            int[] xBounds = p.getXBounds();
            int[] yBounds = p.getYBounds();
            int pSize = p.getSize();
            for (int i = -1; i <= pSize; i++) {
                for (int j = -1; j <= pSize; j++) {
                    Label lab = getCaseLabel(i + 1, j + 1, null);
                    if (i == -1 || i == pSize || j == -1 || j == pSize) {
                        fillLabelWithCase(lab, null, true);
                    } else {
                        Case c = p.getCaseAt(i, j);
                        if ((i >= (xBounds[0]) && i <= (xBounds[1])) && (j >= (yBounds[0]) && j <= (yBounds[1]))) {
                            fillLabelWithCase(lab, c, false);
                        }else{
                            fillLabelWithCase(lab, null, false);
                        }
                    }
                }

            }
        } else {

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
