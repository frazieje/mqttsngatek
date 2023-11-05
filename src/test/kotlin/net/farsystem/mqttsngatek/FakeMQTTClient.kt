package net.farsystem.mqttsngatek

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.farsystem.mqttsngatek.mqtt.*
import java.nio.ByteBuffer
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

class FakeMQTTClient(override val clientId: String) : MQTTClient {

    private val responseQueue = LinkedBlockingDeque<MQTTMessage?>()

    private val requestQueue = LinkedBlockingDeque<MQTTMessage>()

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
        dup: Boolean,
        messageId: Int,
        subscriber: (MQTTPublish) -> Unit
    ): MQTTSubAck {
        return responseQueue.removeFirst() as MQTTSubAck
    }

    override suspend fun publish(
        topic: String,
        payload: ByteArray,
        qos: Int,
        dup: Boolean,
        messageId: Int,
        retained: Boolean
    ): MQTTAck? {
        requestQueue.add(MQTTPublish(
            topic,
            MQTTQoS.fromCode(qos),
            retained,
            dup,
            messageId,
            payload
        ))
        return responseQueue.removeFirst() as? MQTTAck
    }

    override suspend fun disconnect() {
        isConnected = false
    }

    suspend fun getLastRequest(timeoutMillis: Long = 1000): MQTTMessage = withContext(Dispatchers.IO) {
        requestQueue.pollLast(timeoutMillis, TimeUnit.MILLISECONDS)
            ?: throw TimeoutException()
    }

    override fun isConnected(): Boolean = isConnected

}