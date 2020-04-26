package me.gledoussal.status;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.scene.paint.Color;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Mojang {
    public Color getStatusColor() {
        String out = null;
        final Color[] ret = {Color.GREEN};
        try {
            out = new Scanner(new URL("https://status.mojang.com/check").openStream(), "UTF-8").useDelimiter("\\A").next();
            JsonArray json = new JsonParser().parse(out).getAsJsonArray();
            json.forEach(o -> {
                JsonObject object = o.getAsJsonObject();
                String key = object.entrySet().iterator().next().getKey();
                String color = object.get(key).getAsString();

                List<String> essentials = Arrays.asList("sessionserver.mojang.com", "authserver.mojang.com");
                if (essentials.contains(key)) {
                    if (!color.equals("green")) {
                        if (color.equals("yellow")) {
                            if (!ret[0].equals(Color.RED)) {
                                ret[0] = Color.YELLOW;
                            }
                        } else {
                            ret[0] = Color.RED;
                        }
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ret[0];
    }
}
