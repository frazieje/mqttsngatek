package net.farsystem.mqttsngatek.mqttsnclient;

import java.nio.ByteBuffer;

public class NativeMQTTSNClient implements MQTTSNClient {

    static {
        System.loadLibrary("mqttsnclient");
    }

    @Override
    public native ByteBuffer serializeConnect(String clientId, int duration, boolean cleanSession, boolean willFlag);

    @Override
    public native ByteBuffer serializeSubscribeNormal(boolean dup, int qos, int messageId, String topic);

    @Override
    public native ByteBuffer serializeSubscribeShortName(boolean dup, int qos, int messageId, String topicShortName);

    @Override
    public native ByteBuffer serializeSubscribePredefined(boolean dup, int qos, int messageId, int predefinedTopicId);

    @Override
    public native ByteBuffer serializeRegister(int topicId, int messageId, String topic);

    @Override
    public native ByteBuffer serializeSearchGW(int radius);

    @Override
    public native ByteBuffer serializeRegAck(int topicId, int messageId, int returnCode);

    @Override
    public native MQTTSNGwInfo deserializeMQTTSNGwInfo(ByteBuffer buffer);

}
