package me.gledoussal;

import fr.theshark34.openlauncherlib.minecraft.GameInfos;
import fr.theshark34.openlauncherlib.minecraft.GameTweak;
import fr.theshark34.openlauncherlib.minecraft.GameType;
import fr.theshark34.openlauncherlib.minecraft.GameVersion;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import me.gledoussal.nologin.account.Account;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class Main extends Application {

    public static final String APPLICATION_TITLE = "Esperia Launcher";
    public static final String WEBSITE_URL = "https://www.esperia-rp.net/";
    public static final String SERVER_IP = "esperia-rp.net";
    public static final int SERVER_PORT = 25565;
    private static final GameVersion VERSION = new GameVersion("1.12.2", GameType.V1_8_HIGHER);

    public static final GameInfos INFOS = new GameInfos("esperia_beta", VERSION, new GameTweak[] { GameTweak.FORGE });
    public static final File DIR = INFOS.getGameDir();
    public static final String UPDATE_URL = "https://launcher.esperia-rp.net/";

    private static final GameVersion BETA_VERSION = new GameVersion("1.12.2", GameType.V1_8_HIGHER);
    public static final GameInfos BETA_INFOS = new GameInfos("esperia_beta_testing", BETA_VERSION, new GameTweak[] { GameTweak.FORGE });
    public static final File BETA_DIR = BETA_INFOS.getGameDir();
    public static final String BETA_UPDATE_URL = "https://testing.launcher.esperia-rp.net/";

    public static final String LAUNCHER_VERSION = "1.2.2";
    public static final String LAUNCHER_CHECK_URL = "https://launcher.esperia-rp.net/resources/launcher.version";
    public static final String LAUNCHER_DOWNLOAD_EXE_URL = "https://launcher.esperia-rp.net/resources/Esperia.exe";
    public static final String LAUNCHER_DOWNLOAD_JAR_URL = "https://launcher.esperia-rp.net/resources/Esperia.jar";

    public static Account account;
    public static List<Account> accountList = new ArrayList<Account>();

    private double  xOffset = 0.0;
    private double  yOffset = 0.0;

    @Override
    public void start(Stage primaryStage) throws Exception{

        if (System.getProperty("os.name").contains("Windows")) {
            if(System.getenv("ProgramFiles(x86)") != null) {
                if (System.getProperty("sun.arch.data.model").equals("32")) {
                    JOptionPane.showMessageDialog(null, "Java 32bit est installé sur un windows" +
                            "64bit. Le jeu ne pourra pas se lancer ! Le navigateur va s'ouvrir sur le site de " +
                            "téléchargement de Java. Il faut télécharger la version \"Windows Hors ligne (64 bits)\"");
                    Desktop.getDesktop().browse(new URI("https://www.java.com/fr/download/manual.jsp"));
                    System.exit(1);
                }
            }
        }

        DIR.mkdir();

        Parent root = FXMLLoader.load(getClass().getResource("/views/main.fxml"));
        root.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        root.setOnMouseDragged(event -> {

            if (yOffset < 36) {
                primaryStage.setX(event.getScreenX() - xOffset);
                primaryStage.setY(event.getScreenY() - yOffset);
            }

        });

        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setTitle(Main.APPLICATION_TITLE);
        primaryStage.getIcons().add(new Image(Main.class.getResourceAsStream("/images/icon.png")));
        primaryStage.setResizable(false);
        primaryStage.setScene(new Scene(root, 1200, 737));
        primaryStage.show();
        Platform.runLater(root::requestFocus);
    }




    public static void main(String[] args) {
        launch(args);
    }
}
