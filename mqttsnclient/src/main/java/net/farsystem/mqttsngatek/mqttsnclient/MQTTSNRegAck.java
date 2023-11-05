package net.farsystem.mqttsngatek.mqttsnclient;

public record MQTTSNRegAck(int messageId, int topicId, int returnCode) {}
