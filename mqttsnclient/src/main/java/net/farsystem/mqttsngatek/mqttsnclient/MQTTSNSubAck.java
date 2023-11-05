package net.farsystem.mqttsngatek.mqttsnclient;

public record MQTTSNSubAck(int qos, int messageId, int topicId, int returnCode) {}
