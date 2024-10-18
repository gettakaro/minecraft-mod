package com.takaro.takaroplugin.websocket.command;

import com.takaro.takaroplugin.util.TpsTracker;
import com.takaro.takaroplugin.websocket.WSServer;
import com.takaro.takaroplugin.websocket.response.Tps;
import org.java_websocket.WebSocket;

import java.lang.reflect.InvocationTargetException;


public class TpsCommand implements WSCommand {

	@Override
	public void execute(WSServer wsServer, WebSocket conn, String params, String requestId) {
		try {
			double tps = getTps()[0];
			wsServer.sendToClient(conn, new Tps(tps + " ticks.", tps, requestId));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return Current server Tps
	 */
	public double[] getTps() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
		return new double[] { Math.round(TpsTracker.getTPS()) }; // rounding elsewe would get something like 19.93620414673046 / 20 tps
	}

}