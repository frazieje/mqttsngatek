package net.farsystem.mqttsngatek.mqtt

interface MQTTClient {
    suspend fun connect(options: MQTTConnectOptions): MQTTConnack
    suspend fun disconnect()
    fun isConnected(): Boolean
}