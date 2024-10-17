package com.takaro.takaroplugin.websocket.response;

import com.google.gson.JsonObject;

public class Tps implements JSONOutput {

    private final String message;
    private final double tps;

    public Tps(String message, double tps) {
        this.message = message;
        this.tps = tps;
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
        return object.toString();
    }

}