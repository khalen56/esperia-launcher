package me.gledoussal.controllers;


import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
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

public class LoginController {

    @FXML
    private ImageView back;

    @Setter
    private MainController mainController;

    private final Pane msPane = new Pane();
    private final Stage msStage = new Stage();

    private LoginTaskDelegate loginTaskDelegate;

    @FXML
    public void initialize() {
        // Créer et démarrer la tâche de chargement des comptes
        Task<Void> loadAccountsTask = createLoadAccountsTask();
        loadAccountsTask.setOnSucceeded(event -> {

            loginTaskDelegate.onLoginTaskCompleted();

            msStage.initModality(Modality.APPLICATION_MODAL);
            msStage.initOwner(Main.primaryStage);
        });
        loadAccountsTask.setOnFailed(event -> {
            // Gérer les erreurs ici, si nécessaire
        });
        new Thread(loadAccountsTask).start();
    }


    private Task<Void> createLoadAccountsTask() {
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() {
                loadAccounts();
                System.out.println("Comptes chargés");
                return null;
            }
        };
        return task;
    }


    private void loadAccounts() {
        NoLogin noLogin = new NoLogin();
        String token = Utilities.getClientToken();
        if (token != null) {
        }
        loginTaskDelegate.updateLoadingMessage("Connexion en cours");
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
        Pane newMsPane = new Pane(); // Créer un nouveau Pane à chaque appel
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
                        // once we got the auth code, we can turn it into a oauth token

                        Main.account = Microsoft.auth(authCode);
                        Main.accountList.add(Main.account);
                        Utilities.addAccount(Main.account);
                        Utilities.updateDefaultAccount(Main.account);
                        mainController.onAuthCompleted();

                    }
                }
            }
        });

        newMsPane.getChildren().add(webView);

        msStage.show();
    }

    public interface LoginTaskDelegate {
        void onLoginTaskCompleted();
        void updateLoadingMessage(String message);
    }

    public void setLoginTaskDelegate(LoginTaskDelegate loginTaskDelegate) {
        this.loginTaskDelegate = loginTaskDelegate;
    }

}
