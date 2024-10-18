package com.takaro.takaroplugin.websocket.command;

import java.util.concurrent.ExecutionException;

import com.takaro.takaroplugin.config.ConfigManager;
import com.takaro.takaroplugin.config.UserData;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.java_websocket.WebSocket;

import com.takaro.takaroplugin.TakaroPlugin;
import com.takaro.takaroplugin.auth.LoginManager;
import com.takaro.takaroplugin.auth.ConnectedUser;
import com.takaro.takaroplugin.config.UserType;
import com.takaro.takaroplugin.websocket.WSServer;

public class ExecCommand implements WSCommand {
	LoginManager loginManager = LoginManager.getInstance();

	@Override
	public void execute(WSServer wsServer, WebSocket conn, String command, String requestId) {
		ConnectedUser u = LoginManager.getInstance().getUser(conn.getRemoteSocketAddress());
		if(u == null || u.getUserType() != UserType.ADMIN) {
			if(u != null)
				Bukkit.getLogger().warning(u + " tried to run " + command + " without permission.");
			return;
		}

		boolean allowCommand = checkWhitelist(conn, command);
		if (!allowCommand) {
			Bukkit.getLogger().warning(u + " tried to run " + command + " without permission.");
			return;
		}
		
		Bukkit.getLogger().info(conn.getRemoteSocketAddress()+ " executed: " + command);
		ConsoleCommandSender sender = Bukkit.getServer().getConsoleSender();
		TakaroPlugin plugin = (TakaroPlugin) Bukkit.getPluginManager().getPlugin("TakaroPlugin");
		try {
			@SuppressWarnings("unused")
			boolean success = Bukkit.getScheduler()
					.callSyncMethod(plugin, () -> Bukkit.dispatchCommand(sender, command)).get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

	}
	
	private boolean checkWhitelist(WebSocket conn, String command) {
		for(UserData ud : ConfigManager.getInstance().getAllUsers()) {
			if (ud.getUsername().equals(loginManager.getUser(conn.getRemoteSocketAddress()).getUsername())) {

				if (!ud.isWhitelistEnabled()) { //Skip whitelist check.
					return true;
				}

				String[] splitCommand = command.split(" ");

				for (String whitelistedCommand : ud.getWhitelistedCommands()) {
					String[] splitWhitelistedCommand = whitelistedCommand.split(" ");

					if(equalsArray(splitCommand, splitWhitelistedCommand)) {
						//Command matches the whitelist
						if(ud.isWhitelistActsAsBlacklist())
							return false; //If acts as blacklist, do not allow command
						else
							return true; //If acts as Whitelist, allow command
					}
				}
				
				//If execution reached this point, then the command is not in the blacklist.
				if(ud.isWhitelistActsAsBlacklist())
					return true; //If acts as blacklist, allow command
				else
					return false; //If acts as Whitelist, do not allow command
			}
		}
		throw new RuntimeException("No user matched the whitelist check.");
	}
	
	/**
	 * Check if the user command matches the whitelisted command
	 * 
	 * @param splitCommand Command sent by user
	 * @param splitWhitelistedCommand Command in the whitelist
	 * @return true if the user command matches the whitelist command
	 */
	private boolean equalsArray(String[] splitCommand, String[] splitWhitelistedCommand) {
		for (int i = 0; i < splitWhitelistedCommand.length; i++)
			if (!splitCommand[i].equalsIgnoreCase(splitWhitelistedCommand[i])) 
				return false; //Does not match so far
		return true; //Matches the command
	}

}