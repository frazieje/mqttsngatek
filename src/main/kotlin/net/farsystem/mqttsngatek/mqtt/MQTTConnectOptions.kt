package net.farsystem.mqttsngatek.mqtt

data class MQTTConnectOptions(val isCleanSession: Boolean, val keepAliveInterval: Int, val version: MQTTVersion)