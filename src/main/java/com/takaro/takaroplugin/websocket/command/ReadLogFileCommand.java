package com.takaro.takaroplugin.websocket.command;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.bukkit.Bukkit;
import org.java_websocket.WebSocket;

import com.takaro.takaroplugin.websocket.WSServer;
import com.takaro.takaroplugin.websocket.response.ConsoleOutput;

public class ReadLogFileCommand implements WSCommand{

	@Override
	public void execute(WSServer wsServer, WebSocket conn, String params, String requestId) {
		List<String> lines = null;
		try {
			 lines = Files.readAllLines(Paths.get("logs/latest.log"), StandardCharsets.UTF_8);
		} catch (IOException e) {
			try {
				lines = Files.readAllLines(Paths.get("logs/latest.log"), StandardCharsets.ISO_8859_1);
			}catch(IOException ex) {
				ex.printStackTrace();
			}
			
		}
		
		if(lines == null) {
			Bukkit.getLogger().info("Error trying to read latest.log file.");
			return;
		}
		
		for(String line : lines)
			wsServer.sendToClient(conn, new ConsoleOutput(line, null, requestId));
	}
}
