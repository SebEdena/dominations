package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import controller.util.ConfigStyle;
import controller.util.IndicatorFader;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import model.jeu.*;
import model.joueur.Joueur;
import model.plateau.PlacementDomino;
import model.exceptions.DominoException;
import model.exceptions.TuileException;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import model.plateau.*;

import java.net.URL;
import java.util.*;

/**
 * Classe du controlleur de la scène de partie
 * @author Mathieu Valentin, Sébastien Viguier, Laurent Yu
 * @version 1.0
 */
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
    private StackPane partieDialogParent;

    @FXML
    private StackPane pioche;

    @FXML
    private StackPane scores;

    @FXML
    private PiocheController piocheController;

    @FXML
    private ScoresController scoresController;


    private JFXDialog piocheDialog, scoresDialog;

    private ConfigStyle configStyle;

    private IndicatorFader status;

    private GridPane plateauDisplay;

    private int colRowSize;

    private List<GridPane> miniPlateaux = null;

    private PlacementDomino placement = null;

    private Joueur joueurActuel = null;

    private IDomino dominoActuel = null;

    private Partie partie;

    private final Object partieLocker = new Object();

    private double windowWidth, windowHeight;

    private final DataFormat caseDominoFormat = new DataFormat("model.plateau.Case");

    /**
     * Fonction appelée automatiquement à l'instantiation de la classe
     */
    @FXML
    void initialize() {
        assert partieRootNode != null : "fx:id=\"partieRootNode\" was not injected: check your FXML file 'partie.fxml'.";
        assert partieDialogParent != null : "fx:id=\"partiePiocheParent\" was not injected: check your FXML file 'partie.fxml'.";
        assert partieIndicator != null : "fx:id=\"partieIndicator\" was not injected: check your FXML file 'partie.fxml'.";
        assert partieTitleIndicator != null : "fx:id=\"partieTitleIndicator\" was not injected: check your FXML file 'partie.fxml'.";
        assert partieTextIndicator != null : "fx:id=\"partieTextIndicator\" was not injected: check your FXML file 'partie.fxml'.";
        assert leftContent != null : "fx:id=\"leftContent\" was not injected: check your FXML file 'partie.fxml'.";
        assert rightContent != null : "fx:id=\"rightContent\" was not injected: check your FXML file 'partie.fxml'.";
        assert partieTourJoueurLabel != null : "fx:id=\"partieTourJoueurLabel\" was not injected: check your FXML file 'partie.fxml'.";
        assert partieDomino != null : "fx:id=\"partieDomino\" was not injected: check your FXML file 'partie.fxml'.";
        assert partiePlateauContainer != null : "fx:id=\"partiePlateauContainer\" was not injected: check your FXML file 'partie.fxml'.";
        assert partieRevertDominoButton != null : "fx:id=\"partieRevertDominoButton\" was not injected: check your FXML file 'partie.fxml'.";
        assert partieDropDominoButton != null : "fx:id=\"partieDropDominoButton\" was not injected: check your FXML file 'partie.fxml'.";
        assert partieValidateButton != null : "fx:id=\"partieValidateButton\" was not injected: check your FXML file 'partie.fxml'.";

        configStyle = ConfigStyle.getInstance();
        status = new IndicatorFader(partieIndicator, partieTitleIndicator, partieTextIndicator, 6*configStyle.getStandardWaitTime(), configStyle.getStandardWaitTime());
        leftContent.setSpacing(configStyle.getPiocheDominoInsideSpacing());
        rightContent.setSpacing(configStyle.getPiocheDominoInsideSpacing());
        partieValidateButton.setDisable(true);
        partieDropDominoButton.setDisable(false);
    }

    /**
     * Intialise la partie et les éléments graphiques de l'application
     * @param joueurs la liste des joueurs
     * @param deck la liste des dominos
     * @param nbJoueur l'énumération du nombre de joueurs
     * @param modeJeu l'énumération du mode de jeu
     * @param windowWidth la longueur de la fenêtre
     * @param windowHeight la hauteur de la fenêtre
     */
    public void init(List<Joueur> joueurs, List<IDomino> deck, NbJoueur nbJoueur, ModeJeu modeJeu, double windowWidth, double windowHeight) {
        partie = new Partie(joueurs, deck, nbJoueur, modeJeu);
        this.windowHeight = windowHeight;
        this.windowWidth = windowWidth;

        initDialogs();
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

    /**
     * Lance officiellement la partie en lançant un thread attitré
     */
    public void jouerPartie() {
        new Thread(() -> {
            try{
                piocheController.initContent(partie, status, partieLocker);
                Thread.sleep(configStyle.getStandardWaitTime());
                Platform.runLater(() -> status.display("Débutons la partie, que le meilleur gagne !", "C'est parti !"));
                Thread.sleep(status.getTotalTime());
                while (true) {
                    List<IDomino> tirage = partie.pioche();
                    if(partie.partieFinie()) break;

                    preparePioche(tirage, partie.melangerRois());
                    Platform.runLater(this::showPiocheDialog);
                    Platform.runLater(piocheController::startPioche);
                    synchronized(partieLocker) { partieLocker.wait(); }

                    closePiocheDialog();
                    Thread.sleep(configStyle.getStandardWaitTime());

                    List<Pair<IDomino, Joueur>> choix = piocheController.getChoix();
                    for(Pair<IDomino, Joueur> combi : choix){
                        IDomino d = combi.getKey();
                        Joueur j = combi.getValue();
                        Platform.runLater(()->{
                            joueurActuel = j;
                            fillDomino(d);
                            fillPlateau(j.getPlateau(), j);
                            fillJoueurLabel(j);
                        });
                        if(j.isIA()){
                            Platform.runLater(() ->
                                    status.display(j.getNomJoueur() + " place son domino...", j.getNomJoueur(), Color.web(j.getCouleurRoi().getColor())));
                            simulePlacementDomino();
                        }else{
                            Platform.runLater(() ->
                                    status.display("Choisissez quoi faire de votre domino", j.getNomJoueur(), Color.web(j.getCouleurRoi().getColor())));
                        }
                        synchronized(partieLocker) { partieLocker.wait(); }
                    }
                    Platform.runLater(() -> {
                        partieTourJoueurLabel.setVisible(false);
                        partieDropDominoButton.setDisable(true);
                        partieValidateButton.setDisable(true);
                        partiePlateauContainer.setVisible(false);
                        partieDomino.setVisible(false);
                    });
                    Thread.sleep(configStyle.getStandardWaitTime());
                    if(partie.hasNextTurn()) {
                        status.display("Préparez-vous à piocher.", "Fin du tour");
                        Thread.sleep(status.getTotalTime() + configStyle.getTechnicalWaitTime());
                    }
                }
                List<Joueur> results = partie.calculScores();
                Platform.runLater(() -> displayResultsDialog(results));
            } catch (Exception ignored) {
                ignored.printStackTrace();
            }
        }).start();
    }

    /**
     * Initialise les fenêtres modales (JFXDialog) de la scène
     */
    private void initDialogs(){
        piocheDialog = new JFXDialog();
        piocheDialog.toBack();
        piocheDialog.setContent(pioche);
        piocheDialog.setDialogContainer(partieDialogParent);
        configStyle.setFixedDimensions(piocheDialog.getContent(), windowWidth - configStyle.getOffsetMaximizedDialog(), windowHeight - configStyle.getOffsetMaximizedDialog());
        piocheDialog.setOverlayClose(false);

        scoresDialog = new JFXDialog();
        scoresDialog.toBack();
        scoresDialog.setDialogContainer(partieDialogParent);
        scoresDialog.setOverlayClose(false);
    }

    /**
     * Initialise le plateau central
     * @param nbColLig le nombre de ligne et colonnes
     */
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
        partiePlateauContainer.setVisible(false);
        partiePlateauContainer.getChildren().add(plateauDisplay);
    }

    /**
     * Initialise l'évènement de drag and drop pour le domino
     */
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

    /**
     * Initialise l'évènement de drag and drop pour les cases du plateau
     */
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

    /**
     * Calcul l'indice de la case sur le domino (0 ou 1)
     * @param caseDom la case
     * @return son indice sur le domino
     */
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

    /**
     * Initialise les plateaux latéraux
     */
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

    /**
     * Récupère le label correspondant à la case du plateau
     * @param plateau le plateau de jeu sur lequel chercher
     * @param row la ligne de la ligne
     * @param col la colonne de la ligne
     * @param sens s'il faut chercher autour de la case demandée
     * @return le label de la case
     */
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

    /**
     * Retourne la ligne d'un élément dans un plateau
     * @param node l'élément
     * @return la ligne de l'élément
     */
    private int getRow(Node node) {
        return GridPane.getRowIndex(node);
    }

    /**
     * Retourne la  colonne d'un élément dans un plateau
     * @param node l'élément
     * @return la colonne de l'élément
     */
    private int getCol(Node node) {
        return GridPane.getColumnIndex(node);
    }

    /**
     * Cherche si la case concernée devrait avoir l'affichage hors-limites (rouge) ou vide (gris clair)
     * @param p le plateau sur lequel chercher
     * @param i la ligne de la case
     * @param j la colonne de la case
     * @return true si la case doit être hors-limites, faux sinon
     */
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

    /**
     * Prépare l'affichage de la pioche
     * @param pioche la liste des dominos piochés
     * @param rois la liste des rois dans l'ordre de tirage
     */
    private void preparePioche(List<IDomino> pioche, List<Roi> rois){
        piocheController.resetPioche();
        piocheController.fillPioche(pioche, rois);
    }

    /**
     * Met à jour l'orientation du domino
     * @param placement le placement de domino
     * @param caseId la case qui a été prise
     */
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

    /**
     * Insère le domino pris sur le plateau
     * @param placementDomino le placement du domino
     * @param label la case sur laquelle insérer la case prise
     * @param c la case prise
     */
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
        fillLabelWithCase(getCaseLabel(plateauDisplay, row, col, placementDomino.getSens()), placementDomino.getCase(true), false);
        partieValidateButton.setDisable(false);
        partieDropDominoButton.setDisable(true);
    }

    /**
     * Remplit le plateau choisi
     * @param p le plateau choisi
     * @param joueur le joueur qui possède le plateau
     */
    private void fillPlateau(Plateau p, Joueur joueur) {
        int pSize = p.getSize();
        for (int i = -1; i <= pSize; i++) {
            for (int j = -1; j <= pSize; j++) {
                Label lab = getCaseLabel(plateauDisplay, i + 1, j + 1, null);
                fillLabelWithCase(lab, p.getCaseAt(i, j), isLockedWhenNoDomino(p, i, j));
            }
        }
        partiePlateauContainer.setVisible(!joueur.isIA());
    }

    /**
     * Remplit le plateau latéral choisi
     * @param p le plateau latéral choisi
     * @param joueur le joueur qui possède le plateau latéral
     */
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

    /**
     * Remplir le domino
     * @param d l'objet domino associé
     */
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

    /**
     * Remplit le label en haut de la scène
     * @param j le joueur à utiliser pour le remplissage
     */
    private void fillJoueurLabel(Joueur j) {
        partieTourJoueurLabel.setTextFill(Color.web(j.getCouleurRoi().getColor()));
        partieTourJoueurLabel.setText(j.getNomJoueur());
        partieTourJoueurLabel.setVisible(true);
    }

    /**
     * Remplit un label du plateau en fonction de la case choisie
     * @param l le label du plateau
     * @param c la case à utiliser
     * @param lockedOnCaseNull si la case est null, vrai si la case doit être représentée en hors-limites, faux sinon
     */
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

    /**
     * Réalise une translation du plateau en fonction du placement actuel
     */
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

    /**
     * Recalcule les cases hors-limites
     * @param placementDomino le placement considéré
     */
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

    /**
     * Réinitialise le domino
     */
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

    /**
     * Réalise la validation du placement de domino
     * @param placementDomino le placement considéré
     * @throws DominoException si le placement de domino est incorrect
     * @throws TuileException si la tuile n'est pas encore placée
     */
    private void validateDominoPlacement(PlacementDomino placementDomino) throws DominoException, TuileException {
        joueurActuel.getPlateau().addDomino(placementDomino.getDomino(), placementDomino.getRow() - 1, placementDomino.getColumn() - 1, placementDomino.getCaseId(), placementDomino.getSens());
        fillMiniPlateau(joueurActuel);
        synchronized (partieLocker) { partieLocker.notifyAll(); }
    }

    /**
     * Affiche la fenêtre modale
     */
    public void showPiocheDialog(){
        partieDialogParent.toFront();
        piocheDialog.show();
    }

    /**
     * Ferme la fenêtre modale
     */
    public void closePiocheDialog(){
        new Thread(()->{
            try { Thread.sleep(configStyle.getStandardWaitTime()); } catch (InterruptedException ignored) { }
            Platform.runLater(piocheDialog::close);
            try { Thread.sleep(configStyle.getTechnicalWaitTime()); } catch (InterruptedException ignored) { }
            Platform.runLater(partieDialogParent::toBack);
        }).start();
    }

    /**
     * Simule le placement d'un domino par une IA
     */
    private void simulePlacementDomino(){
        new Thread(() -> {
            try {
                Thread.sleep(status.getTotalTime());
                try {
                    PlacementDomino tmpPlacement = joueurActuel.pickPossibilite(dominoActuel);
                    Thread.sleep(configStyle.getStandardWaitTime());
                    Platform.runLater(()->insertDomino(tmpPlacement, getCaseLabel(plateauDisplay, tmpPlacement.getRow()+1, tmpPlacement.getColumn()+1, null),
                            tmpPlacement.getCase(false)));
                    Thread.sleep(configStyle.getStandardWaitTime());
                    Platform.runLater(()-> {
                        try { validateDominoPlacement(tmpPlacement); } catch (Exception ignored) { }
                    });
                } catch (Exception e) {
                    try {
                        Thread.sleep(configStyle.getStandardWaitTime());
                        partieDomino.setVisible(false);
                        synchronized (partieLocker) { partieLocker.notifyAll(); }
                    } catch (Exception ignored) { }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Affiche la fenêtre modale d'affichage des résultats
     * @param joueurs la liste ordonnée des joueurs pas score
     */
    private void displayResultsDialog(List<Joueur> joueurs) {
        scoresController.prepareDisplay(joueurs, partie.getNbJoueur());
        scoresDialog.setContent(scores);
        configStyle.setFixedDimensions(scoresDialog.getContent(), 600, 400);
        partieDialogParent.toFront();
        scoresDialog.show();
    }

    /**
     * Fonction de debug pour afficher le plateau en console
     */
    private void displayTab() {
        for (Node n : plateauDisplay.getChildren()) {
            List<BackgroundFill> fills = ((Label) n).getBackground().getFills();
            System.out.println(fills.size() == 0 ? "EMPTY" : fills.get(0).getFill());
        }
        System.out.println();
    }
}
