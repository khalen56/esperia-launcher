package me.gledoussal.controllers;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import lombok.Getter;
import lombok.Setter;
import me.gledoussal.AppProperties;
import me.gledoussal.Main;

import java.io.IOException;
import java.util.Stack;

public class MainController {

    @FXML
    private Label titleLabel;

    @Getter @Setter @FXML private AnchorPane stackPane;

    @FXML
    private BorderPane loadingPane;
    @FXML
    private Label loadingMessage;

    @FXML
    private LoginController loginPaneController;
    @FXML
    private PlayController playPaneController;
    @FXML
    private UsersController usersPaneController;
    @FXML
    private OptionsController optionsPaneController;

    @Getter @Setter
    private Node loginNode;
    @Getter @Setter
    private Node playNode;
    @Getter @Setter
    private Node usersNode;
    @Getter @Setter
    private Node optionsNode;

    private boolean accountsLoaded = false;

    @FXML
    private void initialize() {
        titleLabel.setText(Main.APPLICATION_TITLE);
        FXMLLoader loginFXMLLoader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
        try {
            AppProperties.loadProperties();

            this.loginNode = loginFXMLLoader.load();
            this.loginPaneController = loginFXMLLoader.getController();
            this.loginPaneController.setMainController(this);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadPane(Node node) {
        ObservableList<Node> mainPaneChildren = this.stackPane.getChildren();
        // On place le contenu dans contentPane
        mainPaneChildren.set(0, node);
    }


    @FXML
    private void onExitClicked(MouseEvent event) {
        System.exit(0);
    }

    public void onAuthCompleted() {
        setLoadingPaneVisible(false);
        FXMLLoader loginFXMLLoader = new FXMLLoader(getClass().getResource("/views/play.fxml"));
        try {
            this.playNode = loginFXMLLoader.load();
            this.playPaneController = loginFXMLLoader.getController();
            this.playPaneController.setMainController(this);
            this.loadPane(playNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadSwitchUsers() {
        FXMLLoader loginFXMLLoader = new FXMLLoader(getClass().getResource("/views/users.fxml"));
        try {
            this.usersNode = loginFXMLLoader.load();
            this.usersPaneController = loginFXMLLoader.getController();
            this.usersPaneController.setMainController(this);
            this.loadPane(usersNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadAuth() {
        FXMLLoader loginFXMLLoader = new FXMLLoader(getClass().getResource("/views/login.fxml"));
        try {
            this.loginNode = loginFXMLLoader.load();
            this.loginPaneController = loginFXMLLoader.getController();
            this.loginPaneController.setMainController(this);
            this.loadPane(loginNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reopenPlay() {
        this.loadPane(playNode);
    }

    public void openOptions() {
        FXMLLoader loginFXMLLoader = new FXMLLoader(getClass().getResource("/views/options.fxml"));
        try {
            this.optionsNode = loginFXMLLoader.load();
            this.optionsPaneController = loginFXMLLoader.getController();
            this.optionsPaneController.setMainController(this);
            this.loadPane(optionsNode);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onLoginTaskCompleted() {
        Platform.runLater(() -> {
            setLoadingPaneVisible(false);
            if(!accountsLoaded) {
                if (Main.account == null) {
                    this.loadPane(loginNode);
                } else {
                    this.onAuthCompleted();
                }
            }
            accountsLoaded = true;
        });
    }

    public void setLoadingMessage(String message) {
        Platform.runLater(() -> {
            loadingMessage.setText(message);
        });
    }

    public void setLoadingPaneVisible(boolean visible) {
        Platform.runLater(() -> {
            loadingPane.setVisible(visible);
        });
    }
}
