package com.takaro.takaroplugin.websocket.response;

import com.google.gson.JsonObject;

public class RamUsage implements JSONOutput {

	private final String message;
	private final long free;
	private final long used;
	private final long max;
	private final String requestId;

	public RamUsage(String message, long free, long used, long max, String requestId) {
		this.message = message;
		this.free = free;
		this.used = used;
		this.max = max;
		this.requestId = requestId;
	}

	@Override
	public int getStatusCode() {
		return 1002;
	}

	@Override
	public String getMessage() {
		return message;
	}
	
	/**
	 * Free amount of RAM, in MB
	 * @return
	 */
	public long getFree() {
		return free;
	}
	
	/**
	 * Used amount of RAM, in MB
	 * @return
	 */
	public long getUsed() {
		return used;
	}
	
	/**
	 * Max amount of RAM, in MB
	 * @return
	 */
	public long getMax() {
		return max;
	}

	@Override
	public String toJSON() {
		JsonObject object = new JsonObject();
		object.addProperty("status", getStatusCode());
		object.addProperty("statusDescription", "RAM Usage");
		object.addProperty("free", getFree());
		object.addProperty("used", getUsed());
		object.addProperty("max", getMax());
		object.addProperty("message", getMessage());
		object.addProperty("requestId", requestId);
		return object.toString();
	}

}