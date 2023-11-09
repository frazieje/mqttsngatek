package net.farsystem.mqttsngatek.mqtt

interface MQTTClient {
    val clientId: String
    suspend fun connect(options: MQTTConnectOptions): MQTTConnack
    suspend fun ping(): MQTTPingResp
    suspend fun subscribe(
        topic: String,
        qos: Int,
        dup: Boolean,
        messageId: Int,
        subscriber: (MQTTPublish) -> Unit
    ): MQTTSubAck
    suspend fun publish(
        topic: String,
        payload: ByteArray,
        qos: Int,
        dup: Boolean,
        messageId: Int,
        retained: Boolean
    ): MQTTAck?

    suspend fun pubAck(
        messageId: Int
    )

    suspend fun disconnect()
    fun isConnected(): Boolean
}