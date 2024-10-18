package com.takaro.takaroplugin.websocket.command;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.java_websocket.WebSocket;

import com.takaro.takaroplugin.auth.LoginManager;
import com.takaro.takaroplugin.auth.ConnectedUser;
import com.takaro.takaroplugin.config.ConfigManager;
import com.takaro.takaroplugin.config.UserData;
import com.takaro.takaroplugin.websocket.WSServer;
import com.takaro.takaroplugin.websocket.response.LoginRequired;
import com.takaro.takaroplugin.websocket.response.LoggedIn;

public class LogInCommand implements WSCommand {
	
	@Override
	public void execute(WSServer wsServer, WebSocket conn, String password, String requestId) {
		// If user is logged in, then return.
		if (LoginManager.getInstance().isSocketConnected(conn.getRemoteSocketAddress()))
			return;
		
		//Check if user exists
		for(UserData ud : ConfigManager.getInstance().getAllUsers()) {
			if(ud.getPassword().equals(password)) {
				ConnectedUser user = new ConnectedUser(conn.getRemoteSocketAddress(), ud.getUsername(), UUID.randomUUID().toString(), ud.getUserType());
				LoginManager.getInstance().logIn(user);
				
				wsServer.sendToClient(conn, new LoggedIn("Logged in", "LOGIN ********", user.getUsername(), user.getUserType(), user.getToken()));
				Bukkit.getLogger().info(user.toString() + " successfully logged in.");
				return;
			}
		}
		wsServer.sendToClient(conn, new LoginRequired("Incorrect password, try again."));
		Bukkit.getLogger().info("Password incorrect while login from " + conn.getRemoteSocketAddress());
	}

}