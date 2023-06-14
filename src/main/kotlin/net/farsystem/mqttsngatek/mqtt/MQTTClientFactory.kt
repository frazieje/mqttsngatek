package net.farsystem.mqttsngatek.mqtt

interface MQTTClientFactory {
    suspend fun getClient()
}