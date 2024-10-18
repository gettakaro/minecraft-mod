package com.takaro.takaroplugin.websocket.response;

import com.google.gson.JsonObject;

public class Tps implements JSONOutput {

    private final String message;
    private final double tps;
    private final String requestId;

    public Tps(String message, double tps, String requestId) {
        this.message = message;
        this.tps = tps;
        this.requestId = requestId;
    }

    @Override
    public int getStatusCode() {
        return 1003;
    }

    @Override
    public String getMessage() {
        return message;
    }

    /**
     * Gets current server TPS
     * @return Global Server TPS
     */
    public double getTps() {
        return tps;
    }

    @Override
    public String toJSON() {
        JsonObject object = new JsonObject();
        object.addProperty("status", getStatusCode());
        object.addProperty("statusDescription", "TPS Usage");
        object.addProperty("tps", getTps());
        object.addProperty("message", getMessage());
        object.addProperty("requestId", requestId);
        return object.toString();
    }

}