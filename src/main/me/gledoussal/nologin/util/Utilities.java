/*
 * Copyright 2015 Lifok
 *
 * This file is part of NoLogin.

 * NoLogin is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * NoLogin is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with NoLogin.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.gledoussal.nologin.util;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.litarvan.openauth.model.response.AuthResponse;
import me.gledoussal.nologin.account.Account;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;

public class Utilities {

    public static File getMinecraftDirectory() {
        String os = System.getProperty("os.name").toLowerCase();
        String homeDirectory = System.getProperty("user.home", ".");
        File localFile;
        if(os.contains("win")) {
            String roaming = System.getenv("APPDATA");
            if(roaming != null) {
                localFile = new File(roaming, ".openlauncherlib/");
            } else {
                localFile = new File(homeDirectory, ".openlauncherlib/");
            }
        } else if (os.contains("mac")) {
            localFile = new File(homeDirectory, "Library/Application Support/openlauncherlib");
        } else {
            localFile = new File(homeDirectory, ".openlauncherlib/");
        }
        if ((!localFile.exists()) && (!localFile.mkdirs())) {
            return null;
        }
        return localFile;
    }

    public static void initJson() {
        try
        {
            File profiles = new File(getMinecraftDirectory(), "launcher_profiles.json");
            JsonObject profilesObj = new JsonObject();
            profilesObj.addProperty("clientToken", "");

            JsonObject selectedUserObj = new JsonObject();
            selectedUserObj.addProperty("account", "");
            selectedUserObj.addProperty("profile", "");

            profilesObj.add("selectedUser", selectedUserObj);

            JsonObject authDbObj = new JsonObject();
            profilesObj.add("authenticationDatabase", authDbObj);

            FileWriter writer = new FileWriter(profiles);
            writer.write(profilesObj.toString());
            writer.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }


    public static void addAccount(Account acc, AuthResponse response) {
        File profiles = new File(getMinecraftDirectory(), "launcher_profiles.json");

        try
        {
            FileInputStream fis = new FileInputStream(profiles);
            byte[] data = new byte[(int) fis.available()];
            fis.read(data);
            fis.close();
            String jsonProfiles = new String(data, "UTF-8");
            JsonObject profilesObj = (JsonObject) (new JsonParser()).parse(jsonProfiles);

            if (response != null) {
                profilesObj.remove("clientToken");
                profilesObj.addProperty("clientToken", response.getClientToken());
            }

            JsonObject authDbObj = profilesObj.getAsJsonObject("authenticationDatabase");
            authDbObj.remove(acc.getUserId());

            JsonObject userObj = new JsonObject();
            userObj.addProperty("accessToken", acc.getAccessToken());
            userObj.addProperty("username", acc.getUsername());
            userObj.addProperty("ms", acc.isMicrosoft());
            userObj.addProperty("refreshToken", acc.getRefreshToken());

            JsonObject userProfilesObj = new JsonObject();
            JsonObject userProfileObj = new JsonObject();
            userProfileObj.addProperty("displayName", acc.getDisplayName());

            userObj.add("profiles", userProfilesObj);
            userProfilesObj.add(acc.getUUID(), userProfileObj);

            JsonObject selectedUserObj = new JsonObject();
            selectedUserObj.addProperty("account", acc.getUserId());
            selectedUserObj.addProperty("profile", acc.getUUID());
            profilesObj.add("selectedUser", selectedUserObj);

            authDbObj.add(acc.getUserId(), userObj);

            FileWriter writer = new FileWriter(profiles);
            writer.write(profilesObj.toString());
            writer.close();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void updateDefaultAccount(Account acc) {
        File profiles = new File(getMinecraftDirectory(), "launcher_profiles.json");

        try
        {
            FileInputStream fis = new FileInputStream(profiles);
            byte[] data = new byte[fis.available()];
            fis.read(data);
            fis.close();
            String jsonProfiles = new String(data, "UTF-8");
            JsonObject profilesObj = (JsonObject) (new JsonParser()).parse(jsonProfiles);

            profilesObj.remove("selectedUser");
            JsonObject selectedUserObj = new JsonObject();
            selectedUserObj.addProperty("account", acc.getUserId());
            selectedUserObj.addProperty("profile", acc.getUUID());
            profilesObj.add("selectedUser", selectedUserObj);

            FileWriter writer = new FileWriter(profiles);
            writer.write(profilesObj.toString());
            writer.close();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static String getDefaultAccount() {
        File profiles = new File(getMinecraftDirectory(), "launcher_profiles.json");
        try
        {
            FileInputStream fis = new FileInputStream(profiles);
            byte[] data = new byte[fis.available()];
            fis.read(data);
            fis.close();
            String jsonProfiles = new String(data, "UTF-8");
            JsonObject profilesObj = (JsonObject) (new JsonParser()).parse(jsonProfiles);

            return profilesObj.getAsJsonObject("selectedUser").get("account").getAsString();

        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static String getClientToken() {
        File profiles = new File(getMinecraftDirectory(), "launcher_profiles.json");
        try
        {
            FileInputStream fis = new FileInputStream(profiles);
            byte[] data = new byte[fis.available()];
            fis.read(data);
            fis.close();
            String jsonProfiles = new String(data, "UTF-8");
            JsonObject profilesObj = (JsonObject) (new JsonParser()).parse(jsonProfiles);

            return profilesObj.get("clientToken").getAsString();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static String formatUuid(String uuid) {
        StringBuilder sb = new StringBuilder(uuid);
        sb.insert(8, "-");
        sb = new StringBuilder(sb.toString());
        sb.insert(13, "-");
        sb = new StringBuilder(sb.toString());
        sb.insert(18, "-");
        sb = new StringBuilder(sb.toString());
        sb.insert(23, "-");

        return sb.toString();
    }
}
