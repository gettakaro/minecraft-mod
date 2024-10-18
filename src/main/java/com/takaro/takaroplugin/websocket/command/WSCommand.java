package com.takaro.takaroplugin.websocket.command;

import org.java_websocket.WebSocket;

import com.takaro.takaroplugin.websocket.WSServer;

public interface WSCommand {
	void execute(WSServer wsServer, WebSocket conn, String params, String requestId);
}