package controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXToggleButton;
import com.jfoenix.validation.RequiredFieldValidator;
import controller.util.SceneSwitcher;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import jeu.*;
import plateau.Case;
import plateau.Domino;
import plateau.IDomino;
import plateau.Terrain;
import util.CSVParser;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

public class MenuController {

    private static final int GP_JOUEUR_LABEL_INDEX = 0;
    private static final int GP_NOM_JOUEUR_INDEX = 1;
    private static final int GP_IA_TOGGLE_INDEX = 2;
    private static final int GP_IA_DIFFICULTE_INDEX = 3;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private JFXComboBox<Label> menuModeJeuCBox;

    @FXML
    private JFXComboBox<Label> menuNbJoueursCBox;

    @FXML
    private VBox menuPlayerPanesContainer;

    @FXML
    private GridPane player1;

    @FXML
    private GridPane player2;

    @FXML
    private GridPane player3;

    @FXML
    private GridPane player4;

    @FXML
    private JFXButton menuResetButton;

    @FXML
    private JFXButton menuStartButton;

    private List<GridPane> players;

    @FXML
    void initialize() {
        assert menuModeJeuCBox != null : "fx:id=\"menuModeJeuCBox\" was not injected: check your FXML file 'menu.fxml'.";
        assert menuNbJoueursCBox != null : "fx:id=\"menuNbJoueursCBox\" was not injected: check your FXML file 'menu.fxml'.";
        assert menuPlayerPanesContainer != null : "fx:id=\"menuPlayerPanesContainer\" was not injected: check your FXML file 'menu.fxml'.";
        assert player1 != null : "fx:id=\"player1\" was not injected: check your FXML file 'menu.fxml'.";
        assert player2 != null : "fx:id=\"player2\" was not injected: check your FXML file 'menu.fxml'.";
        assert player3 != null : "fx:id=\"player3\" was not injected: check your FXML file 'menu.fxml'.";
        assert player4 != null : "fx:id=\"player4\" was not injected: check your FXML file 'menu.fxml'.";
        assert menuResetButton != null : "fx:id=\"menuResetButton\" was not injected: check your FXML file 'menu.fxml'.";
        assert menuStartButton != null : "fx:id=\"menuStartButton\" was not injected: check your FXML file 'menu.fxml'.";

        players = Arrays.asList(player1, player2, player3, player4);

        initData();
    }

    private void initData(){
        initGameMode();
        initPlayerPanes();

        menuResetButton.setOnAction(event -> {
            resetForm();
        });
        menuStartButton.setOnAction(event -> {
            validateForm();
        });
        menuModeJeuCBox.valueProperty().addListener((observable, oldValue, newValue) -> updateNbJoueursCBox(oldValue, newValue));
    }

    private void initGameMode(){
        for(ModeJeu modeJeu : ModeJeu.values()){
            Label label = new Label(modeJeu.getLibelle());
            menuModeJeuCBox.getItems().add(label);
        }

        fillNbJoueursCBox(Arrays.asList(NbJoueur.values()), null);

        menuNbJoueursCBox.setOnAction(event -> {
            Label data = menuNbJoueursCBox.getSelectionModel().getSelectedItem();
            int nbJoueurs = (data == null)?players.size():(int)data.getUserData();
            for(int i = 0; i < players.size(); i ++){
                togglePlayerPane(players.get(i), (i < nbJoueurs));
            }
        });

        menuModeJeuCBox.getValidators().add(new RequiredFieldValidator("Le champ est obligatoire"));
        menuNbJoueursCBox.getValidators().add(new RequiredFieldValidator("Le champ est obligatoire"));
    }

    private void fillNbJoueursCBox(List<NbJoueur> listeModesJeuAcceptes, NbJoueur selectedItem) {
        Label selectedOption = null;
        menuNbJoueursCBox.getItems().clear();
        for(NbJoueur nbJoueur : listeModesJeuAcceptes) {
            Label label = new Label(nbJoueur.getNbJoueurs() + " Joueurs");
            label.setUserData(nbJoueur.getNbJoueurs());
            if(selectedItem != null && nbJoueur.equals(selectedItem)) selectedOption = label;
            menuNbJoueursCBox.getItems().add(label);
        }
        if(selectedOption != null) menuNbJoueursCBox.getSelectionModel().select(selectedOption);
    }

    private void initPlayerPanes() {
        int colorIndex = 0;
        for(GridPane player : players){
            Label joueurLabel = (Label) player.getChildren().get(GP_JOUEUR_LABEL_INDEX);
            JFXTextField nomJoueur = (JFXTextField) player.getChildren().get(GP_NOM_JOUEUR_INDEX);
            JFXToggleButton iaToggle = (JFXToggleButton) player.getChildren().get(GP_IA_TOGGLE_INDEX);
            JFXComboBox<String> iaDifficulte = (JFXComboBox<String>) player.getChildren().get(GP_IA_DIFFICULTE_INDEX);

            String hexColor = Roi.values()[colorIndex].getColor();
            Color color = Color.web(hexColor);

            joueurLabel.setTextFill(color);

            nomJoueur.setStyle(nomJoueur.getStyle() + "-fx-font-weight:bold;-fx-text-fill:"+hexColor+";-fx-prompt-text-fill:"+hexColor+";");
            nomJoueur.setUnFocusColor(color);
            nomJoueur.setFocusColor(color);
            nomJoueur.getValidators().add(new RequiredFieldValidator("Le champ est obligatoire"));

            iaToggle.setTextFill(color);
            iaToggle.setToggleLineColor(color.interpolate(Color.WHITE, 0.3));
            iaToggle.setToggleColor(color);
            iaToggle.setOnAction(event -> {
                updateIADifficulteStatut((Toggle) event.getSource(), player);
            });

            for(ModeIA modeIA : ModeIA.values()) {
                iaDifficulte.getItems().add(modeIA.getLibelle());
            }

            iaDifficulte.setStyle("-fx-font-weight:bold;-fx-text-fill:"+hexColor+";-fx-prompt-text-fill:"+hexColor+";");
            iaDifficulte.setUnFocusColor(color);
            iaDifficulte.setFocusColor(color);
            iaDifficulte.getValidators().add(new RequiredFieldValidator("Le champ est obligatoire"));

            updateIADifficulteStatut(iaToggle, player);
            togglePlayerPane(player, false);

            colorIndex++;
        }
    }

    private void togglePlayerPane(GridPane player, boolean active){
        if(player.getParent().equals(menuPlayerPanesContainer)){
            for (Node child : player.getChildren()){
                if(child.equals(player.getChildren().get(GP_IA_DIFFICULTE_INDEX))){
                    child.setDisable(!active || !((Toggle) player.getChildren().get(GP_IA_TOGGLE_INDEX)).isSelected());
                } else{
                    child.setDisable(!active);
                }
            }
        }
    }

    private void updateIADifficulteStatut(Toggle sourceButton, GridPane player){
        Node targetCombo = player.getChildren().get(GP_IA_DIFFICULTE_INDEX);
        if(sourceButton.isSelected()){
            targetCombo.setDisable(false);
        }else{
            targetCombo.setDisable(true);
        }
    }

    private void updateNbJoueursCBox(Label oldValue, Label newValue){
        if(newValue != null){
            NbJoueur selectedItem = null;
            ModeJeu nouveauMode = ModeJeu.getModeJeu(newValue.getText());
            if(oldValue != null && menuNbJoueursCBox.getSelectionModel().getSelectedItem() != null){
                NbJoueur nbJ = NbJoueur.getParamsJeu((int)menuNbJoueursCBox.getSelectionModel().getSelectedItem().getUserData());
                if(nouveauMode.getListeModesJeuAcceptes().contains(nbJ)) selectedItem = nbJ;
            }
            fillNbJoueursCBox(nouveauMode.getListeModesJeuAcceptes(), selectedItem);
            for(int i = (selectedItem == null)?0:selectedItem.getNbJoueurs() + 1; i < players.size(); i++){
                togglePlayerPane(players.get(i), false);
            }
        } else {
            fillNbJoueursCBox(Arrays.asList(NbJoueur.values()), null);
        }
    }

    private void resetForm(){
        menuModeJeuCBox.getSelectionModel().clearSelection();
        menuNbJoueursCBox.getSelectionModel().clearSelection();
        for(GridPane p : players){
            resetPlayerPane(p);
        }
    }

    private void resetPlayerPane(GridPane player){
        if(player.getParent().equals(menuPlayerPanesContainer)){
            List<Node> child = player.getChildren();
            ((TextField) child.get(GP_NOM_JOUEUR_INDEX)).setText("");
            ((Toggle) child.get(GP_IA_TOGGLE_INDEX)).setSelected(false);
            ((ComboBox) child.get(GP_IA_DIFFICULTE_INDEX)).getSelectionModel().clearSelection();
            updateIADifficulteStatut((Toggle) child.get(GP_IA_TOGGLE_INDEX), player);
            togglePlayerPane(player, false);
        }
    }

    private void validateForm() {
        boolean validated = true;
        validated = validated & menuModeJeuCBox.validate();
        validated = validated & menuNbJoueursCBox.validate();
        for (GridPane p : players){
            if(!p.getChildren().get(GP_NOM_JOUEUR_INDEX).isDisabled()){
                validated = validated & ((JFXTextField)p.getChildren().get(GP_NOM_JOUEUR_INDEX)).validate();
            }
            if(!p.getChildren().get(GP_IA_DIFFICULTE_INDEX).isDisabled()){
                validated = validated & ((JFXComboBox)p.getChildren().get(GP_IA_DIFFICULTE_INDEX)).validate();
            }
        }
        if(validated){
            lancerPartie();
        }
    }

    private void lancerPartie(){
        List<IDomino> deck = null;
        try {
            deck = chargementDominos("./dominos.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
        ModeJeu modeJeu = ModeJeu.getModeJeu(menuModeJeuCBox.getSelectionModel().getSelectedItem().getText());
        NbJoueur nbJoueurs = NbJoueur.getParamsJeu((int)menuNbJoueursCBox.getSelectionModel().getSelectedItem().getUserData());
        List<Joueur> joueurs = new ArrayList<>();

        for(int i = 0; i < nbJoueurs.getNbJoueurs(); i++){
            GridPane player = players.get(i);
            Joueur j = null;
            String nomJoueur = ((TextField) player.getChildren().get(GP_NOM_JOUEUR_INDEX)).getText();
            Roi roi = Roi.getRoiInt(i);
            if(((Toggle) player.getChildren().get(GP_IA_TOGGLE_INDEX)).isSelected()){
                ModeIA modeIA = ModeIA.getModeIA(((ComboBox) player.getChildren().get(GP_IA_DIFFICULTE_INDEX)).getSelectionModel().getSelectedItem().toString());
                try {
                    j = ModeIA.getIAClasse(modeIA, nomJoueur, roi, nbJoueurs, modeJeu, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    j = new Joueur(nomJoueur, roi, nbJoueurs, modeJeu, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            joueurs.add(j);
        }

        try {
            SceneSwitcher.getInstance().startPartie(joueurs, deck, nbJoueurs, modeJeu);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Methode de chargement des dominos à partir d'un fichier csv et les transformes en objet IDomino
     * @param path Chemin d'accès à la liste des dominos
     * @return La liste des dominos chargés
     * @throws IOException
     * @see CSVParser#parse
     */
    private List<IDomino> chargementDominos(String path) throws IOException {
        // Récupération des dominos parsés
        List<String[]> dominos = CSVParser.parse(path, ",", true);
        List<IDomino> dominosCharges = new ArrayList<>();
        // Création des dominos et de leurs cases
        for(String[] strs : dominos) {
            Case case1 = new Case(Integer.parseInt(strs[0]), Terrain.getTerrain(strs[1]));
            Case case2 = new Case(Integer.parseInt(strs[2]), Terrain.getTerrain(strs[3]));
            IDomino domino = new Domino(case1,case2,Integer.parseInt(strs[4]));
            dominosCharges.add(domino);
        }
        return dominosCharges;
    }
}