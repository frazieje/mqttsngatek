package net.farsystem.mqttsngatek.mqttclient;

import java.util.function.Consumer;

public class NativeMQTTClient implements MQTTClient {
    @Override
    public native void connect(Consumer<MQTTConnack> onSuccess, Consumer<MQTTConnack> onFailure);
}
