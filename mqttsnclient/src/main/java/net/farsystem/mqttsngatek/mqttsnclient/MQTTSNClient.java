package net.farsystem.mqttsngatek.mqttsnclient;

import java.nio.ByteBuffer;

public interface MQTTSNClient {
    ByteBuffer serializeConnect(String clientId, int duration, boolean cleanSession, boolean willFlag);
    ByteBuffer serializeSubscribeNormal(boolean dup, int qos, int messageId, String topic);
    ByteBuffer serializeSubscribeShortName(boolean dup, int qos, int messageId, String topicShortName);
    ByteBuffer serializeSubscribePredefined(boolean dup, int qos, int messageId, int predefinedTopicId);
    ByteBuffer serializeRegister(int topicId, int messageId, String topic);
    ByteBuffer serializeSearchGW(int radius);
    ByteBuffer serializeRegAck(int topicId, int messageId, int returnCode);
    MQTTSNGwInfo deserializeMQTTSNGwInfo(ByteBuffer buffer);
}
