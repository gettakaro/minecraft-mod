package com.takaro.takaroplugin;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;


public class WebSocket extends JavaPlugin {
    @Override
    public void onEnable() {
        saveDefaultConfig();
        int port = getConfig().getInt("websocket.port", 1680); // Default to port 1680 if not specified

        try {
            startWebSocketServer(port);
        } catch (IOException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    private void startWebSocketServer(int port) throws IOException, NoSuchAlgorithmException {
        ServerSocket server = new ServerSocket(port);
        try {
            System.out.println("Server has started on 127.0.0.1:" + port + ".\r\nWaiting for a connection…");
            Socket client = server.accept();
            System.out.println("A client connected.");

            InputStream in = client.getInputStream();
            OutputStream out = client.getOutputStream();

            // Read the client's request
            Scanner s = new Scanner(in, "UTF-8");
            String data = s.useDelimiter("\\r\\n\\r\\n").next();
            Matcher get = Pattern.compile("^GET").matcher(data);
            if (get.find()) {
                Matcher match = Pattern.compile("Sec-WebSocket-Key: (.*)").matcher(data);
                match.find();
                byte[] response = ("HTTP/1.1 101 Switching Protocols\r\n"
                        + "Connection: Upgrade\r\n"
                        + "Upgrade: websocket\r\n"
                        + "Sec-WebSocket-Accept: "
                        + Base64.getEncoder().encodeToString(
                        MessageDigest.getInstance("SHA-1").digest(
                                (match.group(1) + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11")
                                        .getBytes("UTF-8")))
                        + "\r\n\r\n")
                        .getBytes("UTF-8");

                out.write(response, 0, response.length);
            }

            // Now you can read/write data frames from/to the client
            // This is a simple echo server example
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        } finally {
            server.close();
        }
    }
}