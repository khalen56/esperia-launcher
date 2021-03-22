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
package me.gledoussal.nologin.account;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import me.gledoussal.nologin.util.Utilities;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class AccountManager 
{
	private List<Account> accounts;

	public AccountManager()
	{
		super();
		accounts = new ArrayList<Account>();
		retrieveAccounts();
	}

	private void retrieveAccounts() 
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
			JsonObject authDb = (JsonObject) profilesObj.get("authenticationDatabase");

			List<String> keys = authDb.entrySet()
					.stream()
					.map(i -> i.getKey())
					.collect(Collectors.toCollection(ArrayList::new));

			for(String accountName : keys)
			{

				JsonObject obj = (JsonObject) authDb.get(accountName);

				List<String> profileKeys = ((JsonObject) obj.get("profiles")).entrySet()
						.stream()
						.map(i -> i.getKey())
						.collect(Collectors.toCollection(ArrayList::new));

				accounts.add(new Account(profileKeys.get(0),
						obj.get("profiles").getAsJsonObject().get(profileKeys.get(0)).getAsJsonObject().get("displayName").getAsString(),
						obj.get("accessToken").getAsString(),
						accountName, obj.get("username").getAsString(), obj.get("ms").getAsBoolean(), obj.get("refreshToken").getAsString()));
			}
		}
		catch (ClassCastException e) {
			System.out.println("Malformed JSON, creating another one.");
			Utilities.initJson();
		}
		catch (FileNotFoundException e) {
			System.out.println("No launcher_profiles.json found, creating one.");
			Utilities.initJson();
		}
		catch (JsonParseException e) {
			System.out.println("Malformed JSON, creating another one.");
			Utilities.initJson();
		}
		catch (Exception e)
		{ 
			e.printStackTrace(); 
		}
	}
	
	public List<Account> getAccounts() 
	{
		return accounts;
	}

}
