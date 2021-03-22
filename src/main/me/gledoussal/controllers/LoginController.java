package me.gledoussal.controllers;

import fr.litarvan.openauth.AuthPoints;
import fr.litarvan.openauth.AuthenticationException;
import fr.litarvan.openauth.Authenticator;
import fr.litarvan.openauth.model.AuthAgent;
import fr.litarvan.openauth.model.response.AuthResponse;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
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

    @FXML
    private TextField usernameField;

    @FXML
    private TextField passwordField;

    @FXML
    private CheckBox rememberCheck;

    @FXML
    private Label loginStatusLabel;

    @Setter
    private MainController mainController;

    private String clientToken = "";

    private final Pane msPane = new Pane();
    private final Stage msStage = new Stage();



    @FXML
    public void initialize() {
        NoLogin noLogin = new NoLogin();
        String token = Utilities.getClientToken();
        if (token != null) {
            clientToken = token;
        }

        Main.accountList = new ArrayList<>();
        List<Account> accounts = noLogin.getAccountManager().getAccounts();
        String defaultAccount = Utilities.getDefaultAccount();
        System.out.println("Comptes trouvés : " + accounts.size());
        for(Account acc : accounts) {
            if(noLogin.getValidator().validateAccount(acc)) {
                Main.accountList.add(acc);
                System.out.println(acc.getDisplayName() + " valide");
                if (acc.getUUID().equals(defaultAccount) || accounts.size() == 1) {
                    Main.account = acc;
                    System.out.println(acc.getDisplayName() + " est le compte par défaut");
                    back.setVisible(true);
                }
            } else {
                System.out.println(acc.getDisplayName() + " invalide");
            }
        }

        msStage.initModality(Modality.APPLICATION_MODAL);
        msStage.initOwner(Main.primaryStage);

        Scene msScene = new Scene(msPane, 500, 700);
        msStage.setScene(msScene);
    }

    public void onConnectClicked() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        Boolean remember = rememberCheck.isSelected();

        if (!username.isEmpty() && !password.isEmpty()) {
            auth(username, password, remember);
        } else {
            loginStatusLabel.setText("Merci de remplir le nom d'utilisateur et le mot de passe.");
            loginStatusLabel.setTextFill(Color.RED);
        }
    }

    private void auth(String username, String password, Boolean remember) {
        Authenticator authenticator = new Authenticator(Authenticator.MOJANG_AUTH_URL, AuthPoints.NORMAL_AUTH_POINTS);
        AuthResponse response;

        try {
            response = authenticator.authenticate(AuthAgent.MINECRAFT, username, password, clientToken);
            Account account = new Account(response.getSelectedProfile().getId(),
                    response.getSelectedProfile().getName(), response.getAccessToken(),
                    response.getSelectedProfile().getId(), username);

            Main.account = account;

            if (remember) {
                Main.accountList.add(account);
                System.out.println("Nombre de comptes : " + Main.accountList.size());
                Utilities.addAccount(account, response);
                Utilities.updateDefaultAccount(account);
            }

            mainController.onAuthCompleted();
        } catch (AuthenticationException e) {
            e.printStackTrace();
            loginStatusLabel.setText(e.getMessage());
            loginStatusLabel.setTextFill(Color.RED);
        }
    }

    @FXML
    private void onBackClicked() {
        mainController.reopenPlay();
    }

    private Scene msScene;

    private static final String loginUrl = "https://login.live.com/oauth20_authorize.srf" +
            "?client_id=00000000402b5328" +
            "&response_type=code" +
            "&scope=XboxLive.signin%20offline_access" +
            "&redirect_uri=https%3A%2F%2Flogin.live.com%2Foauth20_desktop.srf";

    private static final String redirectUrlSuffix = "https://login.live.com/oauth20_desktop.srf?code=";


    public void onConnectMSClicked() {
        msPane.getChildren().clear();

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
                        Utilities.addAccount(Main.account, null);
                        Utilities.updateDefaultAccount(Main.account);
                        mainController.onAuthCompleted();

                    }
                }
            }
        });

        msPane.getChildren().add(webView);

        msStage.show();
    }
}
