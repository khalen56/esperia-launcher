package me.gledoussal;

import java.io.*;
import java.util.Properties;

public class AppProperties {
    public static Properties properties;

    public static void loadProperties() {
        AppProperties.properties = new Properties();

        File fileName = new File(Main.DIR, "launcher.properties");
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(fileName);
            properties.load(fis);
        } catch (FileNotFoundException e) {
            System.out.println("Pas de fichier de configuration trouvé.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveProperties() {
        File fileName = new File(Main.DIR, "launcher.properties");
        try {
            OutputStream output = new FileOutputStream(fileName);
            AppProperties.properties.store(output, null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
