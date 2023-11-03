package net.farsystem.mqttsngatek.mqtt

fun interface MQTTPublishHandler {
    fun receive(client: MQTTClient, message: MQTTPublish)
}