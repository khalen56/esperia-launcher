package me.gledoussal.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import lombok.Getter;
import lombok.Setter;

public class AccountCardController {
    @FXML
    @Getter
    @Setter
    private Pane accountPane;
    @FXML @Getter @Setter
    private ImageView accountImage;
    @FXML @Getter @Setter
    private Label accountName;
}
