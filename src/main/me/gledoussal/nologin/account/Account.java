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

public class Account
{
    private String uuid, displayName, accessToken, userId, username, refreshToken;
    private Boolean isMicrosoft;

    public Account(String uuid, String displayName, String accessToken, String userId, String username) {
        this.uuid = uuid;
        this.displayName = displayName;
        this.accessToken = accessToken;
        this.userId = userId;
        this.username = username;
        this.isMicrosoft = false;
        this.refreshToken = "";
    }
    
    public Account(String uuid, String displayName, String accessToken, String userId, String username, Boolean isMicrosoft)
    {
    	this.uuid = uuid;
    	this.displayName = displayName;
    	this.accessToken = accessToken;
    	this.userId = userId;
    	this.username = username;
    	this.isMicrosoft = isMicrosoft;
        this.refreshToken = "";
    }

    public Account(String uuid, String displayName, String accessToken, String userId, String username, Boolean isMicrosoft, String refreshToken)
    {
        this.uuid = uuid;
        this.displayName = displayName;
        this.accessToken = accessToken;
        this.userId = userId;
        this.username = username;
        this.isMicrosoft = isMicrosoft;
        this.refreshToken = refreshToken;
    }
    
    public String getUUID()
    {
    	return uuid;
    }
    
    public String getDisplayName() 
    {
    	return displayName;
    }
    
    public String getAccessToken() 
    {
    	return accessToken;
    }
    
    public String getUserId()
    {
    	return userId;
    }
    
    public String getUsername()
    {
    	return username;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

	public Boolean isMicrosoft() {
        return isMicrosoft;
    }

    public void setIsMicrosoft(boolean isMicrosoft) {
        this.isMicrosoft = isMicrosoft;
    }
}