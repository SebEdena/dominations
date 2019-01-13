package controller.util;

import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import jeu.Roi;
import plateau.Terrain;

import java.util.HashMap;
import java.util.Map;

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
    private final long standardWaitTime = 500;
    private final long technicalWaitTime = 100;

    private Map<String, Background> correspondanceStyleBackground;
    private Map<String, Border> correspondanceStyleBorder;

    public static ConfigStyle getInstance(){
        if(instance == null){
            instance = new ConfigStyle();
        }
        return instance;
    }

    private ConfigStyle(){
        initCorrespondanceStyles();
    }

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

    public double getCaseDimension() {
        return caseDimension;
    }

    public double getPetiteCaseDimension() {
        return petiteCaseDimension;
    }

    public double getPiocheJetonDimension() {
        return piocheJetonDimension;
    }

    public double getPiocheAccepteurJetonDimension() {
        return piocheAccepteurJetonDimension;
    }

    public double getPiocheDominoInsideSpacing() {
        return piocheDominoInsideSpacing;
    }

    public Background getBackground(String style){
        return correspondanceStyleBackground.get(style);
    }

    public Border getBorder(String style){
        return correspondanceStyleBorder.get(style);
    }

    public double getResizeCrownLimit() {
        return resizeCrownLimit;
    }

    public double getResizedCrownSize() {
        return resizedCrownSize;
    }

    public double getOffsetMaximizedDialog() {
        return offsetMaximizedDialog;
    }

    public long getStandardWaitTime() {
        return standardWaitTime;
    }

    public long getTechnicalWaitTime() {
        return technicalWaitTime;
    }

    public void setFixedDimensions(Region r, double width, double height) {
        r.setPrefSize(width, height);
        r.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        r.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
    }
}
