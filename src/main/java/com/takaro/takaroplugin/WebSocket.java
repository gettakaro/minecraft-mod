package com.takaro.takaroplugin;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;


public class WebSocket extends JavaPlugin {
    public static void startWebSocketServer(String host, int port, Logger logger) throws IOException, NoSuchAlgorithmException {
        ServerSocket server = new ServerSocket(port, 50, InetAddress.getByName(host));

        logger.info("Server has started on " + host + ":" + port + ".\r\nWaiting for a connection…");
        Socket client = server.accept();
        logger.info("A client connected.");

        InputStream in = client.getInputStream();
        OutputStream out = client.getOutputStream();
        Scanner scanner = new Scanner(in, "UTF-8");
        while (scanner.hasNextLine()) {
            String command = scanner.nextLine();
            switch (command) {
            case "ping":
                String pongResponse = "pong";
                out.write(pongResponse.getBytes("UTF-8"));
                break;
            case "hello":
                String helloResponse = "Hello, client!";
                out.write(helloResponse.getBytes("UTF-8"));
                break;
            case "time":
                String timeResponse = "Current time: " + System.currentTimeMillis();
                out.write(timeResponse.getBytes("UTF-8"));
                break;
            default:
                String unknownResponse = "Unknown command: " + command;
                out.write(unknownResponse.getBytes("UTF-8"));
                break;
            }
        }
    }
}