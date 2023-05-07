package me.gledoussal.controllers;


import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import lombok.Setter;
import me.gledoussal.Main;
import me.gledoussal.nologin.NoLogin;
import me.gledoussal.nologin.account.Account;
import me.gledoussal.nologin.auth.Microsoft;
import me.gledoussal.nologin.util.Utilities;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

public class LoginController {

    @FXML
    private ImageView back;

    @Setter
    private MainController mainController;

    private Microsoft microsoft = new Microsoft();

    private final Pane msPane = new Pane();
    private final Stage msStage = new Stage();
    @FXML
    public void initialize() {
        // Créer et démarrer la tâche de chargement des comptes
        Task<Void> loadAccountsTask = new Task<Void>() {
            @Override
            protected Void call() {
                loadAccounts();
                return null;
            }
        };
        loadAccountsTask.setOnSucceeded(event -> { mainController.onLoginTaskCompleted(); });
        new Thread(loadAccountsTask).start();
    }


    private void loadAccounts() {
        NoLogin noLogin = new NoLogin(mainController);

        mainController.setLoadingMessage("Connexion en cours");
        Main.accountList = new ArrayList<>();
        List<Account> accounts = noLogin.getAccountManager().getAccounts();
        String defaultAccount = Utilities.getDefaultAccount();
        System.out.println("Comptes trouvés : " + accounts.size());

        if (Main.account != null) {
            back.setVisible(true);
        }

        for(Account acc : accounts) {
            if(Main.account != null || noLogin.getValidator().validateAccount(acc)) {
                Main.accountList.add(acc);
                System.out.println(acc.getDisplayName() + " valide");
                if (Main.account == null && (acc.getUUID().equals(defaultAccount) || accounts.size() == 1)) {
                    Main.account = acc;
                    System.out.println(acc.getDisplayName() + " est le compte par défaut");
                }
            } else {
                System.out.println(acc.getDisplayName() + " invalide");
            }
        }
    }

    @FXML
    private void onBackClicked() {
        mainController.reopenPlay();
    }

    private static final String loginUrl = "https://login.live.com/oauth20_authorize.srf" +
            "?client_id=00000000402b5328" +
            "&response_type=code" +
            "&scope=XboxLive.signin%20offline_access" +
            "&redirect_uri=https%3A%2F%2Flogin.live.com%2Foauth20_desktop.srf";

    private static final String redirectUrlSuffix = "https://login.live.com/oauth20_desktop.srf?code=";


    public void onConnectMSClicked() {
        // Créer et démarrer la tâche de connexion à Microsoft
        Task<Void> connectToMicrosoftTask = new Task<Void>() {
            @Override
            protected Void call() {
                connectToMicrosoft();
                return null;
            }
        };

        connectToMicrosoftTask.setOnSucceeded(event -> { mainController.onAuthCompleted(); });
        new Thread(connectToMicrosoftTask).start();
    }

    private void connectToMicrosoft() {
        // On met un CountDownLatch pour attendre que l'utilisateur se connecte avant de terminer la tâche
        CountDownLatch latch = new CountDownLatch(1);

        Platform.runLater(() -> {
            Pane newMsPane = new Pane();
            Scene msScene = new Scene(newMsPane, 500, 700);
            msStage.setScene(msScene);

            newMsPane.getChildren().clear();

            WebView webView = new WebView();
            webView.getEngine().load(loginUrl);
            webView.getEngine().setJavaScriptEnabled(true);
            webView.setPrefHeight(700);
            webView.setPrefWidth(500);

            java.net.CookieHandler.setDefault(new com.sun.webkit.network.CookieManager());

            webView.getEngine().getHistory().getEntries().addListener((ListChangeListener<WebHistory.Entry>) c -> {
                if (c.next() && c.wasAdded()) {
                    for (WebHistory.Entry entry : c.getAddedSubList()) {
                        if (entry.getUrl().startsWith(redirectUrlSuffix)) {

                            msStage.hide();
                            String authCode = entry.getUrl().substring(entry.getUrl().indexOf("=") + 1, entry.getUrl().indexOf("&"));

                            microsoft.setMainController(mainController);
                            Main.account = microsoft.auth(authCode);
                            Main.accountList.add(Main.account);
                            Utilities.addAccount(Main.account);
                            Utilities.updateDefaultAccount(Main.account);

                            latch.countDown(); // Décrémenter le compteur du CountDownLatch
                        }
                    }
                }
            });

            newMsPane.getChildren().add(webView);

            msStage.show();
        });

        try {
            latch.await(); // Attendre que le CountDownLatch atteigne zéro
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
