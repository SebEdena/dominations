package controller.util;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * Classe de l'indicateur d'informations sur la partie
 * @author Mathieu Valentin, Sébastien Viguier, Laurent Yu
 * @version 1.0
 */
public class IndicatorFader {

    private Node element;
    private Label title, text;
    private FadeTransition fadeIn, fadeOut;
    private long idleTime, toggleTime;

    /**
     * Constructeur de l'indicateur
     * @param element l'élément graphique qui représente l'indicateur
     * @param title le label graphique du titre
     * @param text le label graphique du texte
     * @param idleTime le temps pendant lequel l'indicateur reste visible
     * @param toggleTime le temps pendant lequel l'animation d'apparition et de disparation se réalise
     */
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

    /**
     * Retourne le temps d'apparition de l'indicateur
     * @return le temps d'apparition de l'indicateur
     */
    public long getIdleTime() {
        return idleTime;
    }

    /**
     * Retourne le temps d'exécution de l'animation d'apparition et de disparition
     * @return le temps d'exécution de l'animation d'apparition et de disparition
     */
    public long getToggleTime() {
        return toggleTime;
    }

    /**
     * Retourne le temps total d'affichage : apparition + temps de visibilité + disparition
     * @return le temps total
     */
    public long getTotalTime() {
        return 2*toggleTime + idleTime;
    }

    /**
     * Affiche l'indicateur et le fait disparaitre
     * @param textString le texte à écrire
     * @param titleString le titre à écrire
     * @param titleColor la couleur du titre
     */
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

    /**
     * Affiche l'indicateur et le fait disparaitre
     * @param textString le texte à écrire
     * @param titleString le titre à écrire, en blanc
     */
    public void display(String textString, String titleString){
        display(textString, titleString, Color.WHITE);
    }
}
