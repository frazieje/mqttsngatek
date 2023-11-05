package net.farsystem.mqttsngatek.mqttsnclient;

public record MQTTSNPubAck(int messageId, int topicId, int returnCode) {}
