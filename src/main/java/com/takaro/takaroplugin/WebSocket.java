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
        new Thread(() -> {
            while (true) {
                try {
                    logger.info("Server has started on " + host + ":" + port + ".\r\nWaiting for a connection…");
                    Socket client = server.accept();
                    logger.info("A client connected.");
                    logger.info("Client address: " + client.getInetAddress().getHostAddress());
                    logger.info("Client port: " + client.getPort());

                    InputStream in = client.getInputStream();
                    OutputStream out = client.getOutputStream();
                    logger.info("Reading client handshake...");
                    Scanner scanner = new Scanner(in, "UTF-8");
                    String data = scanner.useDelimiter("\\r\\n\\r\\n").next();
                    logger.info("Client handshake data: " + data);

                    Matcher get = Pattern.compile("^GET").matcher(data);
                    if (get.find()) {
                        Matcher match = Pattern.compile("Sec-WebSocket-Key: (.*)").matcher(data);
                        match.find();
                        String key = match.group(1).trim();
                        logger.info("WebSocket key: " + key);

                        MessageDigest sha1 = MessageDigest.getInstance("SHA-1");
                        String responseKey = Base64.getEncoder().encodeToString(sha1.digest((key + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes("UTF-8")));
                        logger.info("Response key: " + responseKey);

                        String response = "HTTP/1.1 101 Switching Protocols\r\n"
                                + "Connection: Upgrade\r\n"
                                + "Upgrade: websocket\r\n"
                                + "Sec-WebSocket-Accept: " + responseKey + "\r\n\r\n";
                        out.write(response.getBytes("UTF-8"));
                        logger.info("Handshake response sent.");
                    }
                    
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
                } catch (IOException e) {
                    logger.severe("Server error: " + e.getMessage());
                } catch (NoSuchAlgorithmException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();
    }
}