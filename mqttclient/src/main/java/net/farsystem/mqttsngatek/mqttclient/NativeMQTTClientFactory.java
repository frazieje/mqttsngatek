package net.farsystem.mqttsngatek.mqttclient;

public class NativeMQTTClientFactory implements MQTTClientFactory {
    @Override
    public native MQTTClient getClient();
}
