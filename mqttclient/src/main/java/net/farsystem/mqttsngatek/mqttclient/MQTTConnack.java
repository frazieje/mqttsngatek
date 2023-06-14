package net.farsystem.mqttsngatek.mqttclient;

public class MQTTConnack {

    private int returnCode;
    private boolean isSessionPresent;

    public int getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }

    public boolean isSessionPresent() {
        return isSessionPresent;
    }

    public void setSessionPresent(boolean sessionPresent) {
        isSessionPresent = sessionPresent;
    }

}
