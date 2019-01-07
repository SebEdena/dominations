package controller.util;

import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import plateau.Terrain;

import java.util.HashMap;
import java.util.Map;

public class ConfigStyle {

    private static ConfigStyle instance;

    private final double caseDimension = 50;
    private final double petiteCaseDimension = 50;
    private final double piocheJetonDimension = 35;
    private final double piocheAccepteurJetonDimension = 40;
    private final double piocheDominoInsideSpacing = 30;

    private Map<String, Background> correspondanceStyle;

    public static ConfigStyle getInstance(){
        if(instance == null){
            instance = new ConfigStyle();
        }
        return instance;
    }

    private ConfigStyle(){
        initCorrespondanceStyle();
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
        return correspondanceStyle.get(style);
    }

    public void setFixedDimensions(Region r, double width, double height) {
        r.setPrefSize(width, height);
        r.setMinSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
        r.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);
    }
}
