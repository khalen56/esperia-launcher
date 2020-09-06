package me.gledoussal.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.FlowPane;
import javafx.scene.text.Font;
import lombok.Setter;
import me.gledoussal.Main;
import me.gledoussal.nologin.util.Utilities;
import me.gledoussal.nologin.account.Account;

import java.io.IOException;

public class UsersController {

    @FXML
    private FlowPane accountsContainer;
    @Setter
    private MainController mainController;

    @FXML
    public void initialize() {
        for (Account acc : Main.accountList) {
            FXMLLoader accountFXMLLoader = new FXMLLoader(getClass().getResource("/views/userCard.fxml"));
            try {
                Node accountNode = accountFXMLLoader.load();
                AccountCardController accountCardController = accountFXMLLoader.getController();
                accountCardController.getAccountImage().setImage(new Image("https://www.esperia-rp.net/skins/avatar/minecraft/" + Utilities.formatUuid(acc.getUUID() + "/64")));
                accountCardController.getAccountName().setText(acc.getDisplayName());
                accountCardController.getAccountName().setFont(Font.font("Brown Regular"));
                accountsContainer.getChildren().add(accountCardController.getAccountPane());
                accountCardController.getAccountPane().setOnMouseReleased(event -> {
                    if (acc.getUUID() != Main.account.getUUID()) {
                        Main.account = acc;
                        mainController.onAuthCompleted();
                        Utilities.updateDefaultAccount(acc);
                    } else {
                        mainController.reopenPlay();
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        FXMLLoader accountFXMLLoader = new FXMLLoader(getClass().getResource("/views/userCard.fxml"));
        try {
            Node accountNode = accountFXMLLoader.load();
            AccountCardController accountCardController = accountFXMLLoader.getController();
            accountCardController.getAccountImage().setOpacity(.5);
            accountCardController.getAccountName().setText("Ajouter un compte");
            accountCardController.getAccountName().setFont(Font.font("Brown Regular"));
            accountsContainer.getChildren().add(accountCardController.getAccountPane());
            accountCardController.getAccountPane().setOnMouseReleased(event -> {
                mainController.loadAuth();
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
