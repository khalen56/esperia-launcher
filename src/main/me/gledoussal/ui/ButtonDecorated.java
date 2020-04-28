package me.gledoussal.ui;

import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.beans.NamedArg;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class ButtonDecorated extends HBox {
    private Label label;
    private Region rect;
    private TranslateTransition translateTransition;
    private SimpleDoubleProperty expandToMaxProperty;

    public ButtonDecorated(@NamedArg("text") String text, @NamedArg("textSize") double textSize) {

        setAlignment(Pos.CENTER);

        label = new Label();
        label.setFont(Font.font("System", FontWeight.BOLD, textSize));
        label.setText(text);
        label.setTextFill(Color.WHITE);

        getChildren().add(label);

        rect = new Region();
        rect.setMaxHeight(textSize * .9);
        rect.setMaxWidth(textSize * .9);
        rect.setMinHeight(textSize * .9);
        rect.setMinWidth(textSize * .9);
        rect.setStyle("-fx-border-style: solid solid none none;-fx-border-color:white;-fx-border-width: 2");
        rect.setRotate(45);

        getChildren().add(rect);

        translateTransition = new TranslateTransition(Duration.millis(200), rect);
        translateTransition.setCycleCount(1);
        translateTransition.setInterpolator(Interpolator.EASE_BOTH);

        expandToMaxProperty = new SimpleDoubleProperty(5);

        setOnMouseEntered((MouseEvent t) -> {
            translateTransition.setFromX(getTranslateX());
            translateTransition.setToX(expandToMaxProperty.get());
            translateTransition.playFromStart();
        });

        setOnMouseExited((MouseEvent t) -> {
            translateTransition.setFromX((expandToMaxProperty.get() * translateTransition.getCurrentTime().toMillis() / translateTransition.getDuration().toMillis()));
            translateTransition.setToX(0);
            translateTransition.playFromStart();
        });

    }
}
