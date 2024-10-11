package com.takaro.takaroplugin;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;


public class TakaroPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("TakaroPlugin has been enabled!");
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
}
