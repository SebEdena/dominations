package controller.util;

import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import model.jeu.Roi;
import model.plateau.Terrain;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe permettant de décrire un model.plateau de model.jeu
 * @author Mathieu Valentin, Sébastien Viguier, Laurent Yu
 * @version 1.0
 */
public class ConfigStyle {

    private static ConfigStyle instance;

    private final double caseDimension = 50;
    private final double petiteCaseDimension = 40;
    private final double piocheJetonDimension = 35;
    private final double piocheAccepteurJetonDimension = 40;
    private final double piocheDominoInsideSpacing = 30;
    private final double resizeCrownLimit = 50;
    private final double resizedCrownSize = 40;
    private final double offsetMaximizedDialog = 100;
    private final long standardWaitTime = 50;
    private final long technicalWaitTime = 100;

    private Map<String, Background> correspondanceStyleBackground;
    private Map<String, Border> correspondanceStyleBorder;

    /**
     * Retourne l'instance du singleton
     * @return l'instance
     */
    public static ConfigStyle getInstance(){
        if(instance == null){
            instance = new ConfigStyle();
        }
        return instance;
    }

    /**
     * Constructeur de la configuration
     */
    private ConfigStyle(){
        initCorrespondanceStyles();
    }

    /**
     * Initialise les bordures et fonds liés aux type de jeton ou de case à remplir
     */
    private void initCorrespondanceStyles() {
        correspondanceStyleBackground = new HashMap<>();
        correspondanceStyleBorder = new HashMap<>();
        correspondanceStyleBackground.put("empty", Background.EMPTY);
        correspondanceStyleBackground.put("hover", new Background(new BackgroundFill(Color.web("#afafaf"), null, null)));
        correspondanceStyleBackground.put("locked", new Background(new BackgroundFill(Color.web("#d50000"), null, null)));
        for (int i = 0; i < Terrain.values().length; i++) {
            Terrain t = Terrain.values()[i];
            correspondanceStyleBackground.put(t.getLibelle(), new Background(new BackgroundFill(Color.web(t.getColor()), null, null)));
        }
        for (int i = 0; i < Roi.values().length; i++) {
            Roi roi = Roi.values()[i];
            correspondanceStyleBackground.put(roi.getLibelle(), new Background(new BackgroundFill(Color.web(roi.getColor()), new CornerRadii(100, true), null)));
            correspondanceStyleBorder.put(roi.getLibelle(), new Border(new BorderStroke(Color.web(roi.getColor()).interpolate(Color.BLACK, 0.2), BorderStrokeStyle.SOLID, new CornerRadii(100, true), new BorderWidths(4))));
        }
    }

    /**
     * Retourne la dimension de la case sur un plateau standard
     * @return la dimension de la case sur un plateau standard
     */
    public double getCaseDimension() {
        return caseDimension;
    }

    /**
     * Retourne la dimension de la case sur les petits plateaux
     * @return la dimension de la case sur les petits plateaux
     */
    public double getPetiteCaseDimension() {
        return petiteCaseDimension;
    }

    /**
     * Retourne la dimension d'un jeton de pioche
     * @return la dimension d'un jeton de pioche
     */
    public double getPiocheJetonDimension() {
        return piocheJetonDimension;
    }

    /**
     * Retourne la taille de l'accepteur de jeton
     * @return la taille de l'accepteur de jeton
     */
    public double getPiocheAccepteurJetonDimension() {
        return piocheAccepteurJetonDimension;
    }

    /**
     * Retourne l'espacement entre les dominos de la pioche
     * @return l'espacement entre les dominos de la pioche
     */
    public double getPiocheDominoInsideSpacing() {
        return piocheDominoInsideSpacing;
    }

    /**
     * Retourne le fond associé à la chaine demandée
     * @param style la chaine du fond recherché
     * @return le fond recherché, ou null si style ne correspond à aucun fond
     */
    public Background getBackground(String style){
        return correspondanceStyleBackground.get(style);
    }

    /**
     * Retourne la bordure associée à la chaine demandée
     * @param style la chaine de la bordure recherchée
     * @return la bordure recherché, ou null si style ne correspond à aucune bordure
     */
    public Border getBorder(String style){
        return correspondanceStyleBorder.get(style);
    }

    /**
     * Retourne la limite en dessous de laquelle une icone doit être réduite
     * @return la limite en dessous de laquelle une icone doit être réduite
     */
    public double getResizeCrownLimit() {
        return resizeCrownLimit;
    }

    /**
     * Retourne la taille d'une icone réduite
     * @return la taille d'une icone réduite
     */
    public double getResizedCrownSize() {
        return resizedCrownSize;
    }

    /**
     * Retourne l'écart entre la fenêtre et le dialogue plein écran
     * @return l'écart entre la fenêtre et le dialogue plein écran
     */
    public double getOffsetMaximizedDialog() {
        return offsetMaximizedDialog;
    }

    /**
     * Retourne un temps d'attente standard dans l'application
     * @return le temps d'attente standard dans l'application
     */
    public long getStandardWaitTime() {
        return standardWaitTime;
    }

    /**
     * Retourne le temps d'attente pour raisons techniques
     * @return le temps d'attente pour raisons techniques
     */
    public long getTechnicalWaitTime() {
        return technicalWaitTime;
    }

    /**
     * Fixe les dimensions d'un élément graphique
     * @param r l'élément à modifier
     * @param width la longueur fixe
     * @param height la hauteur fixe
     */
    public void setFixedDimensions(Region r, double width, double height) {
        r.setPrefSize(width, height);
        r.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        r.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
    }
}
