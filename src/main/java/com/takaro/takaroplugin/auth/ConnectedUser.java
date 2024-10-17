package com.takaro.takaroplugin.auth;

import java.net.InetSocketAddress;

import com.takaro.takaroplugin.config.UserType;

public class ConnectedUser {
	private String username;
	private InetSocketAddress socketAddress;
	private String token;
	private UserType userType;
	
	public ConnectedUser(InetSocketAddress socketAddress, String username, String token, UserType userType) {
		this.socketAddress = socketAddress;
		this.username = username;
		this.token = token;
		this.userType = userType;
	}
	
	public String getUsername() {
		return username;
	}
	
	public InetSocketAddress getSocketAddress() {
		return socketAddress;
	}
	
	public String getToken() {
		return token;
	}
	
	public UserType getUserType() {
		return userType;
	}
	
	public String toString() {
		return "User " + username + " from " + socketAddress + " as " + userType;
	}
}