package me.gledoussal.controllers;

import fr.theshark34.openlauncherlib.LaunchException;
import fr.theshark34.openlauncherlib.external.ExternalLaunchProfile;
import fr.theshark34.openlauncherlib.external.ExternalLauncher;
import fr.theshark34.openlauncherlib.minecraft.AuthInfos;
import fr.theshark34.openlauncherlib.minecraft.GameFolder;
import fr.theshark34.openlauncherlib.minecraft.MinecraftLauncher;
import fr.theshark34.supdate.BarAPI;
import fr.theshark34.supdate.application.integrated.FileDeleter;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import lombok.Setter;
import me.gledoussal.AppProperties;
import me.gledoussal.Main;
import me.gledoussal.nologin.util.Utilities;
import me.gledoussal.status.Esperia;
import me.gledoussal.status.Mojang;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Scanner;

import fr.theshark34.supdate.SUpdate;

public class PlayController {

    @FXML
    private Label versionLabel;
    @FXML
    private Label updateLabel;
    @FXML
    private Label playerNameLabel;
    @FXML
    private ImageView playerImage;
    @FXML
    private Circle mojangStatusCircle;
    @FXML
    private Label playersCountLabel;

    @Setter
    private MainController mainController;

    private SUpdate su = null;

    @FXML
    public void initialize() {

        new Thread(() -> {
            Esperia server = new Esperia();
            if (server.isOnline()) {
                Platform.runLater(() -> {
                    playersCountLabel.setText(server.getPlayerCount() + "/200");
                });
            } else {
                Platform.runLater(() -> {
                    playersCountLabel.setText("Hors ligne");
                    playersCountLabel.setTextFill(Color.RED);
                });
            }
        }).start();

        new Thread(() -> {
            Mojang mojang = new Mojang();
            Platform.runLater(() -> {
                mojangStatusCircle.setFill(mojang.getStatusColor());
            });
        }).start();

        playerImage.setImage(new Image("https://www.esperia-rp.net/skins/avatar/minecraft/" + Utilities.formatUuid(Main.account.getUUID() + "/64")));
        playerNameLabel.setText(Main.account.getDisplayName());

        versionLabel.setText(Main.LAUNCHER_VERSION);
        checkLauncherUpdate();
    }

    @FXML
    private void onPlayerImageClicked() {
        this.mainController.loadSwitchUsers();
    }

    @FXML
    private void onExternalLinkClicked(MouseEvent event) {
        String id = ((ImageView) event.getSource()).getId();

        try {
            URI uri = null;
            switch (id) {
                case "w":
                    uri = new URI("https://esperia-rp.net");
                    break;
                case "ds":
                    uri = new URI("https://discord.gg/GGxR2gK");
                    break;
                case "tw":
                    uri = new URI("https://twitter.com/EsperiaRP");
                    break;
                case "fb":
                    uri = new URI("http://www.facebook.com/EsperiaRP");
                    break;
                case "yt":
                    uri = new URI("http://www.youtube.com/user/EsperiaRP");
                    break;
            }

            if (uri != null) {
                Desktop.getDesktop().browse(uri);
            }
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void onOptionsClicked(MouseEvent event) {
        mainController.openOptions();
    }

    @FXML
    private void onPlayClicked() {
        new Thread(this::update).start();
    }

    private void update() {

        Platform.runLater(() -> {
            updateLabel.setText("Vérification de l'intégrité des fichiers");
            updateLabel.setVisible(true);
        });

        if (!AppProperties.properties.getProperty("beta", "false").equals("true")) {
            su = new SUpdate(Main.UPDATE_URL, Main.DIR);
        } else {
            su = new SUpdate(Main.BETA_UPDATE_URL, Main.BETA_DIR);
        }
        su.addApplication(new FileDeleter());

        Thread t = new Thread(new Runnable() {

            private Label l;

            public Runnable init(Label label) {
                l = label;
                return this;
            }

            @Override
            public void run() {
                boolean flag = true;
                while (flag) {
                    int val = (int) (BarAPI.getNumberOfTotalDownloadedBytes() / 1000);
                    int max = (int) (BarAPI.getNumberOfTotalBytesToDownload() / 1000);

                    if (max != 0) {
                        Platform.runLater(() -> {
                            l.setText("Téléchargement en cours : " + BarAPI.getNumberOfDownloadedFiles() + "/" + BarAPI.getNumberOfFileToDownload()
                                    + " (" + val * 100 / max + " %).");
                        });
                    }

                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        flag = false;
                    }
                }
            }
        }.init(updateLabel));

        t.start();

        try {
            su.start();
            t.interrupt();
            Platform.runLater(() -> {
                updateLabel.setVisible(false);
            });
            launch();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void launch() {
        try {
            ExternalLaunchProfile profile;
            ExternalLauncher mcLauncher;
            AuthInfos authInfos = new AuthInfos(Main.account.getDisplayName(), Main.account.getAccessToken(),
                    Main.account.getUUID());

            if (!AppProperties.properties.getProperty("beta", "false").equals("true"))
                profile = MinecraftLauncher.createExternalProfile(Main.INFOS, GameFolder.BASIC, authInfos);
            else
                profile = MinecraftLauncher.createExternalProfile(Main.BETA_INFOS, GameFolder.BASIC, authInfos);

            String ram = AppProperties.properties.getProperty("ram", "2");
            profile.getVmArgs().addAll(Arrays.asList("-Xms" + ram + "G", "-Xmx" + ram + "G"));
            mcLauncher = new ExternalLauncher(profile);
            mcLauncher.launch();

            System.exit(0);
        } catch (LaunchException exc) {
            exc.printStackTrace();
        }
    }

    private void checkLauncherUpdate() {
        new Thread(() -> {
            try {
                String out = null;
                out = new Scanner(new URL(Main.LAUNCHER_CHECK_URL).openStream(), "UTF-8").useDelimiter("\\A").next();
                if (!out.equals(Main.LAUNCHER_VERSION)) {
                    Platform.runLater(() -> {
                        versionLabel.setText(versionLabel.getText() + " (une mise à jour est disponible !)");
                        versionLabel.setStyle("-fx-underline: true;-fx-cursor: hand;");
                        versionLabel.setOnMousePressed(event -> {
                            try {
                                URI uri;
                                String os = System.getProperty("os.name").toLowerCase();
                                if (os.contains("win")) {
                                    uri = new URI(Main.LAUNCHER_DOWNLOAD_EXE_URL);
                                } else {
                                    uri = new URI(Main.LAUNCHER_DOWNLOAD_JAR_URL);
                                }
                                Desktop.getDesktop().browse(uri);
                            } catch (IOException | URISyntaxException e) {
                                e.printStackTrace();
                            }
                        });
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
