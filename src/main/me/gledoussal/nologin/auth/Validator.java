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
package me.gledoussal.nologin.auth;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import me.gledoussal.controllers.MainController;
import me.gledoussal.nologin.account.Account;
import me.gledoussal.nologin.util.Utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class Validator {

	private String clientToken;

	private MainController mainController;

	private Microsoft microsoft = new Microsoft();

	public Validator() 
	{
		super();
	}

	/**
	 * Call this method to validate the AccessToken of an account, it will automatically try to refresh if validation fail.
	 * @param acc
	 * @return true is the validation or the refresh success. 
	 */
	public boolean validateAccount(Account acc) 
	{
		return refreshToken(acc);
	}

	private boolean refreshToken(Account acc)
	{
		updateMcFile(microsoft.refreshToken(acc));
		return true;
	}

	/**
	 * Used to rewrite the launcher_profiles file
	 * @return
	 */
	private void updateMcFile(Account acc)
	{
		File profiles = new File(Utilities.getMinecraftDirectory(), "launcher_profiles.json");
		try 
		{
			FileInputStream fis = new FileInputStream(profiles);
			byte[] data = new byte[(int) fis.available()];
			fis.read(data);
			fis.close();
			String jsonProfiles = new String(data, "UTF-8");
			JsonObject profilesObj = (JsonObject) (new JsonParser()).parse(jsonProfiles);

			for (Map.Entry<String, JsonElement> entry : profilesObj.getAsJsonObject("authenticationDatabase").entrySet()) {
				if (entry.getValue().getAsJsonObject().getAsJsonObject("profiles").getAsJsonObject(acc.getUUID()) != null) {
					JsonObject profileObj = profilesObj.getAsJsonObject("authenticationDatabase").getAsJsonObject(entry.getKey());

					profileObj.remove("accessToken");
					profileObj.addProperty("accessToken", acc.getAccessToken());

					if (acc.getLastTokenRefresh() != 0) {
						profileObj.remove("lastTokenRefresh");
						profileObj.addProperty("lastTokenRefresh", acc.getLastTokenRefresh());
					}

					profileObj.remove("refreshToken");
					profileObj.addProperty("refreshToken", acc.getRefreshToken());
				}
			}

			FileWriter writer = new FileWriter(profiles);
			writer.write(profilesObj.toString());
			writer.close();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}

	public void setMainController(MainController mainController) {
		microsoft.setMainController(mainController);
	}
}
