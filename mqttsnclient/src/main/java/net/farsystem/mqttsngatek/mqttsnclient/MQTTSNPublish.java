package net.farsystem.mqttsngatek.mqttsnclient;

public record MQTTSNPublish(boolean dup, int qos, boolean retained, int messageId, String topic, int topicId, byte[] payload) {}
