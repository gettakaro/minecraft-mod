package com.takaro.takaroplugin;

import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import com.takaro.takaroplugin.util.TpsTracker;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Filter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.java_websocket.server.DefaultSSLWebSocketServerFactory;

import com.takaro.takaroplugin.config.ConfigManager;
import com.takaro.takaroplugin.minecraft.TakaroCommand;
import com.takaro.takaroplugin.util.LogFilter;
import com.takaro.takaroplugin.websocket.WSServer;

public class TakaroPlugin extends JavaPlugin {
    private WSServer server;
    private Thread wsThread;

    @Override
    public void onEnable() {
        Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new TpsTracker(), 100L, 1L);

        try {
			startWS();
		} catch (Exception e) {
			Bukkit.getLogger().warning("Error occured while starting WebSocket Server.");
			e.printStackTrace();
		}

        //This filter is used to read the whole console.
		Filter f = new LogFilter(getWSServer());
		((org.apache.logging.log4j.core.Logger) LogManager.getRootLogger()).addFilter(f);
		
		getCommand("Takaro").setExecutor(new TakaroCommand(this.getDescription().getVersion()));
    }

	@Override
	public void onDisable() {
		try {
			server.stop();
			wsThread = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Start WebSocket server
	 */
	private void startWS() throws Exception {
		// Create WebSocket server
		server = new WSServer(ConfigManager.getInstance().getSocketAdress());
		
		if(ConfigManager.getInstance().isSslEnabled()) {
			// Configure SSL
			String STORETYPE = ConfigManager.getInstance().getStoreType();
			String KEYSTORE = ConfigManager.getInstance().getKeyStore();
			String STOREPASSWORD = ConfigManager.getInstance().getStorePassword();
			String KEYPASSWORD = ConfigManager.getInstance().getKeyPassword();
			
			KeyStore ks = KeyStore.getInstance(STORETYPE);
			File kf = new File(KEYSTORE);
			ks.load(new FileInputStream(kf), STOREPASSWORD.toCharArray());
			
			KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			kmf.init(ks, KEYPASSWORD.toCharArray());
			TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
			tmf.init(ks);
			
			SSLContext sslContext = null;
			sslContext = SSLContext.getInstance("TLS");
			sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
			
			server.setWebSocketFactory(new DefaultSSLWebSocketServerFactory(sslContext));
		}

		// Start Server
		wsThread = new Thread(new Runnable() {
			@Override
			public void run() {
				server.run();
			}
		});
		wsThread.start();
	}

	public WSServer getWSServer() {
		return server;
	}
}
