package me.gledoussal.status;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.gledoussal.Main;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class Server {
    private static JsonObject serversState;

    private int playerCount;
    private boolean online;

    public Server() {
        initServersState();
        playerCount = serversState.getAsJsonArray("players").size();
        online = serversState.get("online").getAsBoolean();
    }

    /**
     * @return le nombre de joueurs présents sur le serveur.
     */
    public String getPlayerCount() {
        return this.playerCount + "";
    }

    /**
     * @return {@code true} si le serveur est en ligne, {@code false} sinon.
     */
    public boolean isOnline() {
        return this.online;
    }

    private static void initServersState() {
        URL url = null;
        try {
            url = new URL(Main.WEBSITE_URL + "utils/status");
            URLConnection request = url.openConnection();
            request.connect();

            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent()));
            serversState = root.getAsJsonObject();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
