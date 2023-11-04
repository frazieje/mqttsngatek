package net.farsystem.mqttsngatek.mqtt

interface MQTTClient {
    val clientId: String
    suspend fun connect(options: MQTTConnectOptions): MQTTConnack
    suspend fun ping(): MQTTPingResp
    suspend fun subscribe(topic: String, qos: Int, messageId: Int, subscriber: (MQTTPublish) -> Unit): MQTTSubAck
    suspend fun publish(topic: String, payload: ByteArray, qos: Int, messageId: Int, retained: Boolean): MQTTAck?
    suspend fun disconnect()
    fun isConnected(): Boolean
}