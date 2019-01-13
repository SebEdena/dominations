package controller;

import com.jfoenix.controls.JFXButton;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import controller.util.ConfigStyle;
import controller.util.IndicatorFader;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import model.joueur.Joueur;
import model.jeu.Partie;
import model.jeu.Roi;
import model.plateau.*;

/**
 * Classe du controlleur de la sous-scène de pioche
 * @author Mathieu Valentin, Sébastien Viguier, Laurent Yu
 * @version 1.0
 */
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

    private final DataFormat roiFormat = new DataFormat("model.jeu.roi");
    private IndicatorFader status;

    /**
     * Fonction appelée automatiquement à l'instantiation de la classe
     */
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

    /**
     * Initialise le contenu de la pioche
     * @param p la partie en cours
     * @param status l'indicateur de statut de partie
     * @param partieLocker l'objet à débloquer par notifyAll() quand la phase de pioche est terminée
     */
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

    /**
     * Initialise le drag and drop pour un jeton
     * @param node le jeton
     */
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

    /**
     * Initialise le drag and drop pour un accepteur de jeton
     * @param node
     */
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
                Roi roi = (Roi) db.getContent(roiFormat);
                Joueur j = partie.getJoueur(roi);
                insertJeton(node, j);
            }
            event.setDropCompleted(success);
            event.consume();
        });
    }

    /**
     * Retourne la liste des choix réaliser : Domino > Joueur
     * @return la liste des choix réaliser : Domino > Joueur
     */
    public List<Pair<IDomino, Joueur>> getChoix(){
        return choix;
    }

    /**
     * Réinitialise la pioche
     */
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

    /**
     * Remplit la pioche avec les jetons demandés
     * @param pioche la liste des dominos de la pioche
     * @param rois la liste des rois piochés
     */
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

    /**
     * Renvoie la position du domino dans la liste de dominos
     * @param dominoContainer le domino cherché
     * @return la position du domino si c'est un domino, -1 sinon;
     */
    private int positionAmongDominos(Node dominoContainer){
        for(int i = 0; i < piocheDominosContainer.getChildren().size(); i++){
            if(piocheDominosContainer.getChildren().get(i).equals(dominoContainer)) return i;
        }
        return -1;
    }

    /**
     * Renvoie le roi associé au jeton
     * @param jeton le jetoon
     * @return le roi
     */
    private Roi getRoiFromLabel(Label jeton){
        return Roi.getRoiInt(Integer.parseInt(jeton.getText()) - 1);
    }

    /**
     * Insère le jeton dans un accepteur
     * @param accepteur l'accepteur de jeton
     * @param joueur le joueur associé au jeton
     */
    private void insertJeton(VBox accepteur, Joueur joueur){
        Node jeton = piocheJetonsContainer.getChildren().remove(0);
        accepteur.getChildren().add(jeton);
        accepteur.getParent().setDisable(true);
        piocheValidateJetonButton.setDisable(false);
        lastMovedJeton = jeton;
        int position = positionAmongDominos(accepteur.getParent());
        Pair<IDomino, Joueur> choisi = choix.get(position);
        choixEnCours = new Pair<>(choisi.getKey(), joueur);
    }

    /**
     * Remplit un domino
     * @param d le domino
     * @param dContainer le conteneur de domino
     */
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

    /**
     * Remplit un jeton
     * @param jeton le jeton cible
     * @param roi le roi associé
     */
    private void fillJeton(Label jeton, Roi roi){
        jeton.setBackground(configStyle.getBackground(roi.getLibelle()));
        jeton.setBorder(configStyle.getBorder(roi.getLibelle()));
        jeton.setText(""+(Roi.getRoiIndex(roi)+1));
    }

    /**
     * Remplit le plateau
     * @param p le plateau en modèle
     */
    private void fillPlateau(Plateau p){
        int pSize = p.getSize();
        for (int i = 0; i < pSize; i++) {
            for (int j = 0; j < pSize; j++) {
                fillLabelWithCase(getCaseLabel(i, j), p.getCaseAt(i, j));
            }
        }
    }

    /**
     * Récupère le label dans la case du plateau cherchée
     * @param row la ligne de la case
     * @param col la colonne de la case
     * @return le label trouvé
     */
    private Label getCaseLabel(int row, int col) {
        try {
            GridPane p = (GridPane) piochePlateauContainer.getChildren().get(indexDomino);
            return (Label) p.getChildren().get(col * partie.getModeJeu().getTaillePlateau() + row);
        } catch (IndexOutOfBoundsException e) {
            return null;
        }
    }

    /**
     * Remplit un label du plateau avec une case
     * @param l le label
     * @param c la case
     */
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

    /**
     * Valide l'affectation du domino au joueur
     */
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
        piocheValidateJetonButton.setDisable(true);
    }

    /**
     * Démarre la phase de pioche
     */
    public void startPioche() {
        new Thread(()->{
            try {
                Thread.sleep(configStyle.getStandardWaitTime());
                status.display("Glissez votre jeton sous le domino voulu", "Début de la pioche");
                Thread.sleep(status.getTotalTime() + configStyle.getTechnicalWaitTime());
                nextPioche();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * Passe au prochain joueur
     */
    private void nextPioche(){
        try {
            Thread.sleep(configStyle.getStandardWaitTime());
            Label jeton = (Label) piocheJetonsContainer.getChildren().get(0);
            Roi roi = Roi.getRoiInt(Integer.parseInt(jeton.getText()) - 1);
            status.display("Glissez votre jeton sous le domino voulu", partie.getJoueur(roi).getNomJoueur(), Color.web(roi.getColor()));
            Platform.runLater(()->{
                fillPlateau(partie.getJoueurs().get(Integer.parseInt(jeton.getText()) - 1).getPlateau());
                jeton.setDisable(false);
            });
            if(partie.getJoueurs().get(Integer.parseInt(jeton.getText()) - 1).isIA()){
                List<IDomino> liste = new ArrayList<>();
                for(Pair<IDomino, Joueur> paire : choix){
                    if(paire.getValue() == null) liste.add(paire.getKey());
                }
                IDomino d = liste.get(partie.getJoueurs().get(Integer.parseInt(jeton.getText()) - 1).pickInPioche(liste, partie.getJoueurs()));
                simulePickDomino(d);
            }
        } catch (Exception ignored) { }
    }

    /**
     * Simule le choix d'un domino par une IA
     * @param domino le domino à choisir
     */
    private void simulePickDomino(IDomino domino) {
        new Thread(() -> {
            try {
                Thread.sleep(status.getTotalTime());
                for(Node dominoContainer : piocheDominosContainer.getChildren()){
                    if(((Label)((VBox) dominoContainer).getChildren().get(indexIdDomino)).getText().equals(""+domino.getIdentifiant())){
                        Label jeton = (Label) piocheJetonsContainer.getChildren().get(0);
                        VBox accepteur = (VBox) ((VBox) dominoContainer).getChildren().get(indexAccepteurJeton);
                        Thread.sleep(configStyle.getStandardWaitTime());
                        Platform.runLater(()->insertJeton(accepteur, partie.getJoueur(Roi.getRoiInt(Integer.parseInt(jeton.getText()) - 1))));
                        Thread.sleep(2*configStyle.getStandardWaitTime());
                        validerAffectationDominoJoueur();
                        break;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
