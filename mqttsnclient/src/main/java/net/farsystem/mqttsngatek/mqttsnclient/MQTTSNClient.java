package net.farsystem.mqttsngatek.mqttsnclient;

import java.nio.ByteBuffer;

public interface MQTTSNClient {
    ByteBuffer serializeConnect(String clientId, int duration, boolean cleanSession, boolean willFlag);
    ByteBuffer serializePublishNormal(boolean dup, int qos, boolean retained, int messageId, int topicId, byte[] payload);
//    ByteBuffer serializePublishPredefined(boolean dup, int qos, boolean retained, int messageId, int topicId, byte[] payload);
//    ByteBuffer serializePublishShortName(boolean dup, int qos, boolean retained, int messageId, String topic, byte[] payload);
    ByteBuffer serializeSubscribeNormal(boolean dup, int qos, int messageId, String topic);
    ByteBuffer serializeSubscribeShortName(boolean dup, int qos, int messageId, String topicShortName);
    ByteBuffer serializeSubscribePredefined(boolean dup, int qos, int messageId, int predefinedTopicId);
    ByteBuffer serializeRegister(int messageId, String topic);
    ByteBuffer serializeSearchGW(int radius);
    ByteBuffer serializeRegAck(int topicId, int messageId, int returnCode);
    ByteBuffer serializePingReq(String clientId);
    MQTTSNPingResp deserializePingResp(ByteBuffer buffer);
    MQTTSNGwInfo deserializeGwInfo(ByteBuffer buffer);
    MQTTSNConnAck deserializeConnAck(ByteBuffer buffer);
    MQTTSNPubAck deserializePubAck(ByteBuffer buffer);
    MQTTSNRegAck deserializeRegAck(ByteBuffer buffer);
    MQTTSNSubAck deserializeSubAck(ByteBuffer buffer);
    MQTTSNPublish deserializePublish(ByteBuffer buffer);
}
