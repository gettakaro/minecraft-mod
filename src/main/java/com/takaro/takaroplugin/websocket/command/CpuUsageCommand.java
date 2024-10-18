package com.takaro.takaroplugin.websocket.command;

import java.lang.management.ManagementFactory;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.java_websocket.WebSocket;

import com.takaro.takaroplugin.websocket.WSServer;
import com.takaro.takaroplugin.websocket.response.CpuUsage;

public class CpuUsageCommand implements WSCommand {

	@Override
	public void execute(WSServer wsServer, WebSocket conn, String params, String requestId) {
		try {
			double usage = getProcessCpuLoad();
			wsServer.sendToClient(conn, new CpuUsage("Usage is " + usage, usage, requestId));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Check out usage for the whole system
	 * Got from https://stackoverflow.com/questions/18489273/how-to-get-percentage-of-cpu-usage-of-os-from-java
	 * @return CPU Usage for the whole system
	 * @throws Exception Something went wrong
	 */
	public double getProcessCpuLoad() throws Exception {
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		ObjectName name = ObjectName.getInstance("java.lang:type=OperatingSystem");
		AttributeList list = mbs.getAttributes(name, new String[] {"SystemCpuLoad"});

		if (list.isEmpty())
			return Double.NaN;

		Attribute att = (Attribute) list.get(0);
		Double value = (Double) att.getValue();

		// usually takes a couple of seconds before we get real values
		if (value == -1.0)
			return Double.NaN;
		// returns a percentage value with 1 decimal point precision
		return ((int) (value * 1000) / 10.0);
	}

}