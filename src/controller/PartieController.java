package controller;

import com.jfoenix.controls.JFXButton;
import controller.util.IndicatorFader;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
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



    private final double caseDimension = 50;

    private IndicatorFader status;

    private GridPane plateau;

    private int colRowSize;

    private List<IDomino> deckDominos = new ArrayList<>();

    private IDomino selectedDomino = null;

    private Orientation selectedDominoOrientation;

    private final DataFormat caseDominoFormat = new DataFormat("plateau.Case");

    private Background onDragOverBackground = new Background(new BackgroundFill(Color.GRAY, null, null));

    @FXML
    void initialize() {
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

        for(String[] strs : dominos) {
            Case case1 = new Case(Integer.parseInt(strs[0]), Terrain.getTerrain(strs[1]));
            Case case2 = new Case(Integer.parseInt(strs[2]), Terrain.getTerrain(strs[3]));
            IDomino domino = new Domino(case1,case2,Integer.parseInt(strs[4]));
            deckDominos.add(domino);
        }

        fillDomino(deckDominos.get(15));
        partieDomino.setOnMouseClicked(event -> {
            if(event.getButton().equals(MouseButton.SECONDARY)){
                partieDomino.setRotate(Math.floor(partieDomino.getRotate() + 90));
                for(Node child: partieDomino.getChildren()){
                    child.setRotate(Math.floor(child.getRotate() - 90));
                }
                selectedDominoOrientation = selectedDominoOrientation.getRotationHoraire();
            }
        });


        initDominoSourceDrag();
        initDominoTargetDrag();

    }

    private void fillDomino(IDomino d){
        if(d instanceof Domino){
            Case[] cases = d.getCases();
            for (int i = 0; i < cases.length; i++){
                Label caseDomino = (Label) partieDomino.getChildren().get(i);
                caseDomino.setStyle(caseDomino.getStyle()+"-fx-background-color:"+cases[i].getTerrain().getColor()+";" +
                        "-fx-text-fill:white;");
                caseDomino.setText(""+cases[i].getNbCouronne());
            }
            selectedDomino = d;
            selectedDominoOrientation = Orientation.EST;
        }
    }

    private void initPlateau(int nbColLig){
        this.colRowSize = nbColLig;
        plateau = new GridPane();

        plateau.setPrefWidth(caseDimension*nbColLig);
        plateau.setMinWidth(Control.USE_PREF_SIZE);
        plateau.setMaxWidth(Control.USE_PREF_SIZE);
        plateau.setPrefHeight(caseDimension*nbColLig);
        plateau.setMinHeight(Control.USE_PREF_SIZE);
        plateau.setMaxHeight(Control.USE_PREF_SIZE);

        for(int i=0;i<nbColLig;i++){
            for(int j=0;j<nbColLig;j++){
                Label label = new Label();
                label.setPrefWidth(caseDimension);
                label.setMinWidth(Control.USE_PREF_SIZE);
                label.setMaxWidth(Control.USE_PREF_SIZE);
                label.setPrefHeight(caseDimension);
                label.setMinHeight(Control.USE_PREF_SIZE);
                label.setMaxHeight(Control.USE_PREF_SIZE);
                label.setAlignment(Pos.CENTER);
                label.setStyle("-fx-font-size:18px;-fx-font-weight:bold;-fx-text-fill:white;-fx-border-color:black;");
                label.setBackground(Background.EMPTY);
                plateau.add(label, i, j);
            }
        }
        partiePlateauContainer.getChildren().add(plateau);
    }

    private void initDominoSourceDrag(){
        for(Node caseDom : partieDomino.getChildren()){
            caseDom.setOnDragDetected(event -> {
                if(selectedDomino != null){
                    Dragboard db = caseDom.startDragAndDrop(TransferMode.MOVE);
                    ClipboardContent content = new ClipboardContent();
                    content.put(caseDominoFormat, selectedDomino.getCases()[getCaseDominoIndex(caseDom)]);
                    db.setContent(content);
                }
                event.consume();
            });
        }
    }

    private void initDominoTargetDrag(){
        for(Node child : plateau.getChildren()){
            child.setOnDragOver(event -> {
                if (event.getGestureSource() != child &&
                        event.getDragboard().hasContent(caseDominoFormat) &&
                        ((Label) child).getText().equals("")) {
                    event.acceptTransferModes(TransferMode.MOVE);
                }
                event.consume();
            });

            child.setOnDragEntered(event -> {
                if (event.getGestureSource() != child &&
                        event.getDragboard().hasContent(caseDominoFormat) &&
                        ((Label) child).getText().equals("")) {
                    ((Control)child).setBackground(onDragOverBackground);
                }
                event.consume();
            });

            child.setOnDragExited(event -> {
                if (event.getGestureSource() != child &&
                        event.getDragboard().hasContent(caseDominoFormat) &&
                        ((Label) child).getText().equals("")) {
                    ((Control)child).setBackground(Background.EMPTY);
                }
                event.consume();
            });

            child.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                boolean success = false;
                if (db.hasContent(caseDominoFormat) &&
                        ((Label) child).getText().equals("")) {
                    success = true;
                    int otherCaseIndex =  selectedDomino.getCaseIndex(
                            (Case) event.getDragboard().getContent(caseDominoFormat), true);
                    fillLabelWithCase((Label)child, (Case) event.getDragboard().getContent(caseDominoFormat));
                    fillLabelWithCase(getCaseLabel(
                            getRow(child)+((otherCaseIndex==0)?-1:1)*selectedDominoOrientation.getOffsetX(),
                            getCol(child)+((otherCaseIndex==0)?-1:1)*selectedDominoOrientation.getOffsetY()),
                            selectedDomino.getCases()[otherCaseIndex]);
                }
                event.setDropCompleted(success);
                event.consume();
                displayTab();
            });
        }
    }

    private int getCaseDominoIndex(Node caseDom){
        int i = 0;
        for (Node caseDisplay : partieDomino.getChildren()){
            if(caseDisplay.equals(caseDom)) {
                System.out.println("hello");
                return i;
            }
            i++;
        }
        return -1;
    }

    private void displayTab(){
        for(Node n : plateau.getChildren()){
            List<BackgroundFill> fills = ((Label) n).getBackground().getFills();
            System.out.println(fills.size() == 0?"EMPTY":fills.get(0).getFill());
        }
        System.out.println();
    }

    private Label getCaseLabel(int row, int col){
        return (Label) plateau.getChildren().get(col*colRowSize + row);
    }

    private void fillLabelWithCase(Label l, Case c){
        l.setStyle(l.getStyle()+"-fx-background-color:"+c.getTerrain().getColor()+";");
        l.setText(""+c.getNbCouronne());
    }

    private int getRow(Node n){
        return GridPane.getRowIndex(n);
    }

    private int getCol(Node n){
        return GridPane.getColumnIndex(n);
    }
}
