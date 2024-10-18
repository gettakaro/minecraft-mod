package com.takaro.takaroplugin.websocket.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.java_websocket.WebSocket;

import com.takaro.takaroplugin.websocket.WSServer;
import com.takaro.takaroplugin.websocket.response.Players;

public class PlayersCommand implements WSCommand{

	@Override
	public void execute(WSServer wsServer, WebSocket conn, String params, String requestId) {
		List<String> connectedPlayersList = new ArrayList<String>();
		for(Player player : Bukkit.getOnlinePlayers()) {
			connectedPlayersList.add(player.getName());
		}
		
		int connectedPlayers = connectedPlayersList.size();
		int maxPlayers = Bukkit.getMaxPlayers();
		
		wsServer.sendToClient(conn, 
			new Players(
                "Connected " + connectedPlayers + " players for a maximum of " + maxPlayers,
				connectedPlayers,
				maxPlayers,
				connectedPlayersList,
				requestId
			));
	}
	
}