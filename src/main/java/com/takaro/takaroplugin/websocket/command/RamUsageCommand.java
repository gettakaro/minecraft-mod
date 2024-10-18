package com.takaro.takaroplugin.websocket.command;

import org.java_websocket.WebSocket;

import com.takaro.takaroplugin.websocket.WSServer;
import com.takaro.takaroplugin.websocket.response.RamUsage;

public class RamUsageCommand implements WSCommand {

	@Override
	public void execute(WSServer wsServer, WebSocket conn, String params, String requestId) {
		Runtime r = Runtime.getRuntime();
		
		long free = r.freeMemory() / 1024 / 1024;
		long max = r.maxMemory() / 1024 / 1024;
		long used = r.totalMemory() / 1024 / 1024 - free;
		
		wsServer.sendToClient(conn,
			new RamUsage(
                free + " free, " + used + " used, " + max + " maximum memory.",
				free,
				used,
				max,
				requestId
			));
	}
}
