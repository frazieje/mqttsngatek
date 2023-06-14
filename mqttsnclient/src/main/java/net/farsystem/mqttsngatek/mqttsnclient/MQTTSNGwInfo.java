package net.farsystem.mqttsngatek.mqttsnclient;

public class MQTTSNGwInfo {
    private final int id;
    private final String address;
    public MQTTSNGwInfo(int id, String address) {
        this.id = id;
        this.address = address;
    }
    public int getId() {
        return id;
    }
    public String getAddress() {
        return address;
    }
}
