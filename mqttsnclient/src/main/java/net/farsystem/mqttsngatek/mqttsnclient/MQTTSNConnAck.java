package net.farsystem.mqttsngatek.mqttsnclient;

public class MQTTSNConnAck {
    private final int returnCode;
    public MQTTSNConnAck(int returnCode) {
        this.returnCode = returnCode;
    }
    public int getReturnCode() {
        return returnCode;
    }
}
