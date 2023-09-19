package net.farsystem.mqttsngatek.mqtt

fun interface MQTTClientFactory {
    suspend fun getClient(clientId: String, brokerHost: String, brokerPort: Int): MQTTClient
}