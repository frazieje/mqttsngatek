package net.farsystem.mqttsngatek.mqttclient;

import java.util.function.Consumer;

public interface MQTTClient {
    public void connect(Consumer<MQTTConnack> onSuccess, Consumer<MQTTConnack> onFailure);
}
