package com.takaro.takaroplugin;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import com.takaro.takaroplugin.WebSocket;


public class TakaroPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
        getConfig().options().setHeader(List.of("Config file for TakaroPlugin"));
        getConfig().options().pathSeparator('/');
        getConfig().options().configuration().set("config-location", "./plugins/TakaroPlugin/config.yml");

        getLogger().info("TakaroPlugin has been enabled!");
        int port = getConfig().getInt("websocket.port", 1680); // Default to port 1680 if not specified
        String hostname = getConfig().getString("websocket.hostname", "0.0.0.0"); // Default to localhost if not specified
        
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            try {
                WebSocket.startWebSocketServer(hostname, port, getLogger());
            } catch (IOException | NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        });
    }

    public Location getPlayerLocation(String playerName) {
        Player player = Bukkit.getPlayer(playerName);
        if (player == null) {
            return null;
        }

        Location location = player.getLocation();
        return new Location(location.getWorld(), location.getX(), location.getY(), location.getZ());
    }
    
    public void sendMessage(String message, boolean isDirectMessage) {
        String fullMessage = "[🗨️ Chat] Server: " + (isDirectMessage ? "[DM] " : "") + message;

        // Emit event (assuming you have a method to handle this)
        emitChatMessageEvent(message, isDirectMessage);

        // Log the message
        getLogger().info(fullMessage);
    }

    private void emitChatMessageEvent(String message, boolean isDirectMessage) {
        // Broadcast the message to all players if it's not a direct message
        if (!isDirectMessage) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage("[🗨️ Chat] Server: " + message);
            }
        } else {
            Player targetPlayer = Bukkit.getPlayerExact(message.split(" ")[0]);
            if (targetPlayer != null) {
                String directMessage = message.substring(message.indexOf(" ") + 1);
                targetPlayer.sendMessage("[🗨️ Chat] Server: [DM] " + directMessage);
            } else {
                getLogger().warning("Player not found for direct message: " + message);
            }
        }
    }

    public void teleportPlayer(String playerName, double x, double y, double z) {
        Player player = Bukkit.getPlayer(playerName);
        if (player == null) {
            getLogger().warning("Player not found: " + playerName);
            return;
        }

        Location newLocation = new Location(player.getWorld(), x, y, z);
        player.teleport(newLocation);

        getLogger().info("Teleported " + playerName + " to " + x + ", " + y + ", " + z);
    }

    public CommandOutput executeConsoleCommand(String rawCommand) {
        String encodedCommand = java.net.URLEncoder.encode(rawCommand, java.nio.charset.StandardCharsets.UTF_8);
        getLogger().info("Executing command: \"" + rawCommand + "\"");

        CommandOutput commandOutput = new CommandOutput("Command execution failed", false);
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            boolean success = Bukkit.dispatchCommand(Bukkit.getConsoleSender(), encodedCommand);
            commandOutput.rawResult = success ? "Command executed successfully" : "Command execution failed";
            commandOutput.success = success;
            getLogger().info("Command output: " + commandOutput.getRawResult());
        });
        return commandOutput;
    }

    public class CommandOutput {
        private String rawResult;
        private boolean success;

        public CommandOutput(String rawResult, boolean success) {
            this.rawResult = rawResult;
            this.success = success;
        }

        public String getRawResult() {
            return rawResult;
        }

        public boolean isSuccess() {
            return success;
        }
    }

    public void kickPlayer(String playerName, String reason) {
        String command = "kick " + playerName + " " + reason;
        executeConsoleCommand(command);
    }

    public void banPlayer(String playerName, String reason, String expiresAt) {
        if (expiresAt == null || expiresAt.isEmpty()) {
            expiresAt = "2521-01-01 00:00:00";
        }

        LocalDateTime expiresAtDate = LocalDateTime.parse(expiresAt);
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(now, expiresAtDate);

        String unit = "minute";
        long durationValue = duration.toMinutes();

        if (durationValue >= 60) {
            unit = "hour";
            durationValue = duration.toHours();
        }

        if (durationValue >= 24) {
            unit = "day";
            durationValue = duration.toDays();
        }

        if (durationValue >= 7) {
            unit = "week";
            durationValue = duration.toDays() / 7;
        }

        if (durationValue >= 30) {
            unit = "month";
            durationValue = duration.toDays() / 30;
        }

        if (durationValue >= 365) {
            unit = "year";
            durationValue = duration.toDays() / 365;
        }

        String command = "ban add " + playerName + " " + durationValue + " " + unit + " " + reason;
        executeConsoleCommand(command);
    }

    public void unbanPlayer(String playerName) {
        String command = "ban remove " + playerName;
        executeConsoleCommand(command);
    }

    
}
