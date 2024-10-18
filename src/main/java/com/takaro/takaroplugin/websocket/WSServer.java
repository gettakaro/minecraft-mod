package com.takaro.takaroplugin.websocket;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.java_websocket.WebSocket;
import org.java_websocket.exceptions.WebsocketNotConnectedException;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import com.takaro.takaroplugin.auth.LoginManager;
import com.takaro.takaroplugin.util.DateTimeUtils;
import com.takaro.takaroplugin.util.JsonUtils;

import com.takaro.takaroplugin.websocket.command.WSCommandFactory;
import com.takaro.takaroplugin.websocket.command.WSCommand;
import com.takaro.takaroplugin.websocket.response.ConsoleOutput;
import com.takaro.takaroplugin.websocket.response.JSONOutput;
import com.takaro.takaroplugin.websocket.response.LoginRequired;
import com.takaro.takaroplugin.websocket.response.LoggedIn;
import com.takaro.takaroplugin.websocket.response.UnknownCommand;

public class WSServer extends WebSocketServer {

    private final HashMap<String, WSCommand> commands = WSCommandFactory.getCommandsHashMap();

    public WSServer(InetSocketAddress address) {
        super(address);
        setReuseAddr(true);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        Bukkit.getLogger().info("New connection from " + conn.getRemoteSocketAddress());
        if (LoginManager.getInstance().isSocketConnected(conn.getRemoteSocketAddress())) {
            sendToClient(conn, new LoggedIn("Connected. Already logged in, welcome back!"));
            Bukkit.getLogger().info("Connected and resumed session from " + conn.getRemoteSocketAddress());
        } else {
            sendToClient(conn, new LoginRequired("Connected. Already logged in, welcome back!"));
            Bukkit.getLogger().info("Connected and waiting login from " + conn.getRemoteSocketAddress());
        }
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        if(!JsonUtils.containsStringProperty(message, "command") //Contains a command
                || ( !JsonUtils.containsStringProperty(message, "token") && !JsonUtils.getStringProperty(message, JsonUtils.COMMAND_PROPERTY).equals("LOGIN"))
        )
            return;

        // Get command and params
        String wsCommand = JsonUtils.getStringProperty(message, JsonUtils.COMMAND_PROPERTY);
        String wsToken = JsonUtils.getStringProperty(message, JsonUtils.TOKEN_PROPERTY);
        String wsCommandParams = JsonUtils.getStringProperty(message, JsonUtils.PARAMS_PROPERTY);
        String wsRequestId = JsonUtils.getStringProperty(message, JsonUtils.REQUEST_ID);

        // Run command
        WSCommand cmd = commands.get(wsCommand);

        if (cmd == null) {
            // Command does not exist
            sendToClient(conn, new UnknownCommand("Unknown command", message, wsRequestId));
            Bukkit.getLogger().info("Unknown Command: " + message);
        } else if (!wsCommand.equals("LOGIN")
                && !LoginManager.getInstance().isLoggedIn(conn.getRemoteSocketAddress(), wsToken)) {
            // User is not authorised. DO NOTHING, IMPORTANT!
            sendToClient(conn, new LoginRequired("Forbidden"));
            Bukkit.getLogger().warning(conn.getRemoteSocketAddress() + " tried to run " + message + " while not logged in!");
        } else {
            cmd.execute(this, conn, wsCommandParams, wsRequestId);
        }
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        LoginManager.getInstance().logOut(conn.getRemoteSocketAddress());
        Bukkit.getLogger().info("Closed connection and logged out from " + conn.getRemoteSocketAddress());
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        Bukkit.getLogger().warning("Error occurred on connection " + conn.getRemoteSocketAddress() + ": " + ex);
    }

    @Override
    public void onStart() {
        Bukkit.getLogger().info("WebSocket Server started successfully.");
    }

    /**
     * Sends the message to all connected AND logged-in users
     */
    public void onNewConsoleLinePrinted(String line) {
        Collection<WebSocket> connections = getConnections();
        for (WebSocket connection : connections) {
            if (LoginManager.getInstance().isSocketConnected(connection.getRemoteSocketAddress()))
                sendToClient(connection, new ConsoleOutput(line, DateTimeUtils.getTimeAsString(), null));
        }
    }

    /**
     * Sends this JSONOutput to client
     * @param conn    Connection to client
     * @param content JSONOutput object
     */
    public void sendToClient(WebSocket conn, JSONOutput content) {
        try {
            conn.send(content.toJSON());
        }catch(WebsocketNotConnectedException e) {
            LoginManager.getInstance().logOut(conn.getRemoteSocketAddress());
            Bukkit.getLogger().warning("Attempted to send a message to a disconnected WebSocket client.");
        }
    }
}
