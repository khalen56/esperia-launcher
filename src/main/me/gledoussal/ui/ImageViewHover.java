package me.gledoussal.ui;

import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

public class ImageViewHover extends ImageView {

    private ScaleTransition scaleTransition;
    private SimpleDoubleProperty expandToMaxProperty;

    public ImageViewHover() {

        scaleTransition = new ScaleTransition(Duration.millis(200), this);
        scaleTransition.setCycleCount(1);
        scaleTransition.setInterpolator(Interpolator.EASE_BOTH);

        expandToMaxProperty = new SimpleDoubleProperty(1.1);

        setOnMouseEntered((MouseEvent t) -> {
            scaleTransition.setFromX(getScaleX());
            scaleTransition.setFromY(getScaleY());
            scaleTransition.setToX(expandToMaxProperty.get());
            scaleTransition.setToY(expandToMaxProperty.get());
            scaleTransition.playFromStart();
        });

        setOnMouseExited((MouseEvent t) -> {
            scaleTransition.setFromX(getScaleX());
            scaleTransition.setFromY(getScaleY());
            scaleTransition.setToX(1);
            scaleTransition.setToY(1);
            scaleTransition.playFromStart();
        });
    }
}
