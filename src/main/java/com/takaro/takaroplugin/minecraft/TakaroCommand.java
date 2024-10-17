package com.takaro.takaroplugin.minecraft;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.takaro.takaroplugin.auth.LoginManager;
import com.takaro.takaroplugin.auth.ConnectedUser;


public class TakaroCommand implements CommandExecutor {
	private String version;

	public TakaroCommand(String version) {
		this.version = version;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		final String mcVer = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[2];
		StringBuilder msg = new StringBuilder();

		msg.append("Takaro Version: " + version + "\n");
		msg.append("Minecraft Version: " + mcVer + "\n");
		ArrayList<ConnectedUser> users = LoginManager.getInstance().getLoggedInUsers();
		
		if (users.isEmpty()) {
			msg.append("There are no logged in Takaro connections now.");
		} else {
			msg.append("Connections to Takaro from: " + "\n");
			for (int i = 0; i < users.size(); i++) {
				ConnectedUser user = users.get(i);
				msg.append(user.toString());
				if(i+1 < users.size())
					msg.append("\n");
			}
		}

		sender.sendMessage(msg.toString());
		return true;
	}
}
