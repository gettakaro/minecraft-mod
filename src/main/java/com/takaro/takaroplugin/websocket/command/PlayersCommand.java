package com.takaro.takaroplugin.websocket.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.java_websocket.WebSocket;

import com.takaro.takaroplugin.websocket.WSServer;
import com.takaro.takaroplugin.websocket.response.Players;

public class PlayersCommand implements WSCommand{

	@Override
	public void execute(WSServer wsServer, WebSocket conn, String params, String requestId) {
		List<Map<String, String>> connectedPlayersList = new ArrayList<>();
		

		for(Player player : Bukkit.getOnlinePlayers()) {
			Map<String, String> playerInfo = new HashMap<>();
			
			playerInfo.put("uuid", player.getUniqueId().toString());
			playerInfo.put("ip", player.getAddress().getAddress().getHostAddress());
			playerInfo.put("ping", String.valueOf(player.getPing()));
			playerInfo.put("name", player.getName());

			connectedPlayersList.add(playerInfo);
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