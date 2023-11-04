package net.farsystem.mqttsngatek

import net.farsystem.mqttsngatek.mqtt.*
import java.util.concurrent.LinkedBlockingDeque

class FakeMQTTClient(override val clientId: String) : MQTTClient {

    private val responseQueue = LinkedBlockingDeque<MQTTMessage>()

    fun queueResponse(message: MQTTMessage) {
        responseQueue.add(message)
    }

    private var isConnected = false

    override suspend fun connect(options: MQTTConnectOptions): MQTTConnack {
        isConnected = true
        return responseQueue.removeFirst() as MQTTConnack
    }

    override suspend fun ping(): MQTTPingResp {
        return responseQueue.removeFirst() as MQTTPingResp
    }

    override suspend fun subscribe(
        topic: String,
        qos: Int,
        messageId: Int,
        subscriber: (MQTTPublish) -> Unit
    ): MQTTSubAck {
        return responseQueue.removeFirst() as MQTTSubAck
    }

    override suspend fun disconnect() {
        isConnected = false
    }

    override fun isConnected(): Boolean = isConnected

}