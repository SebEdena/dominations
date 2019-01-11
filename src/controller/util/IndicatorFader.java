package controller.util;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class IndicatorFader {

    private Node element;
    private Label title, text;
    private FadeTransition fadeIn, fadeOut;
    private long idleTime, toggleTime;

    public IndicatorFader(Node element, Label title, Label text, long idleTime, long toggleTime){
        this.element = element;
        this.idleTime = idleTime;
        this.toggleTime = toggleTime;
        this.title = title;
        this.text = text;

        fadeIn = new FadeTransition(Duration.millis((double)toggleTime), element);
        fadeIn.setFromValue(0d);
        fadeIn.setToValue(1d);
        fadeIn.setCycleCount(1);

        fadeOut = new FadeTransition(Duration.millis((double)toggleTime), element);
        fadeOut.setFromValue(1d);
        fadeOut.setToValue(0d);
        fadeOut.setCycleCount(1);
    }

    public long getIdleTime() {
        return idleTime;
    }

    public long getToggleTime() {
        return toggleTime;
    }

    public long getTotalTime() {
        return 2*toggleTime + idleTime;
    }

    public void display(String textString, String titleString, Color titleColor){
        new Thread(() -> {
            Platform.runLater(() -> {
                title.setTextFill(titleColor);
                title.setText(titleString);
                text.setText(textString);
                element.toFront();
                fadeIn.play();
            });
            try { Thread.sleep(idleTime); } catch (InterruptedException ignored) { }
            Platform.runLater(() -> {
                fadeOut.play();
            });
            try { Thread.sleep(toggleTime); } catch (InterruptedException ignored) { }
            Platform.runLater(() -> {
                element.toBack();
            });
        }).start();
    }

    public void display(String textString, String titleString){
        display(textString, titleString, Color.WHITE);
    }
}
