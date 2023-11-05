package net.farsystem.mqttsngatek.mqttsnclient;

import java.nio.ByteBuffer;

public class NativeMQTTSNClient implements MQTTSNClient {

    static {
        System.loadLibrary("mqttsnclient");
    }

    @Override
    public native ByteBuffer serializeConnect(String clientId, int duration, boolean cleanSession, boolean willFlag);

    @Override
    public native ByteBuffer serializePublishNormal(boolean dup, int qos, boolean retained, int messageId, int topicId, byte[] payload);

//    @Override
//    public native ByteBuffer serializePublishPredefined(boolean dup, int qos, boolean retained, int messageId, int topicId, byte[] payload);
//
//    @Override
//    public native ByteBuffer serializePublishShortName(boolean dup, int qos, boolean retained, int messageId, String topic, byte[] payload);

    @Override
    public native ByteBuffer serializeSubscribeNormal(boolean dup, int qos, int messageId, String topic);

    @Override
    public native ByteBuffer serializeSubscribeShortName(boolean dup, int qos, int messageId, String topicShortName);

    @Override
    public native ByteBuffer serializeSubscribePredefined(boolean dup, int qos, int messageId, int predefinedTopicId);

    @Override
    public native ByteBuffer serializeRegister(int messageId, String topic);

    @Override
    public native ByteBuffer serializeSearchGW(int radius);

    @Override
    public native ByteBuffer serializeRegAck(int topicId, int messageId, int returnCode);

    @Override
    public native ByteBuffer serializePingReq(String clientId);

    @Override
    public native MQTTSNPingResp deserializePingResp(ByteBuffer buffer);

    @Override
    public native MQTTSNGwInfo deserializeGwInfo(ByteBuffer buffer);

    @Override
    public native MQTTSNConnAck deserializeConnAck(ByteBuffer buffer);

    @Override
    public native MQTTSNPubAck deserializePubAck(ByteBuffer buffer);

    @Override
    public native MQTTSNRegAck deserializeRegAck(ByteBuffer buffer);

    @Override
    public native MQTTSNSubAck deserializeSubAck(ByteBuffer buffer);

    @Override
    public native MQTTSNPublish deserializePublish(ByteBuffer buffer);
}
