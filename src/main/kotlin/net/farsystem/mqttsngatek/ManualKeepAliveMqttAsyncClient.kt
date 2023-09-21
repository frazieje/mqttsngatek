package net.farsystem.mqttsngatek

import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.internal.wire.MqttPingReq
import org.eclipse.paho.client.mqttv3.internal.wire.MqttSubscribe
import org.slf4j.LoggerFactory

class ManualKeepAliveMqttAsyncClient(
    serverURI: String?,
    clientId: String?,
    persistence: MqttClientPersistence?
) : MqttAsyncClient(serverURI, clientId, persistence, NoOpPingSender()) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun sendPing(callback: IMqttActionListener) {
        val token = MqttToken(clientId)
        token.actionCallback = callback
        comms.sendNoWait(MqttPingReq(), token)
    }

    fun subscribe(
        topic: String,
        qos: Int,
        messageId: Int,
        userContext: Any?,
        callback: IMqttActionListener,
        messageListener: IMqttMessageListener
    ): IMqttToken {
        MqttTopic.validate(topic, true)
        comms.setMessageListener(topic, messageListener)
        return try {
            logger.debug("$clientId subscribe to topic=$topic")

            val topics = arrayOf(topic)
            val qoss = intArrayOf(qos)

            val token = MqttToken(clientId)
            token.actionCallback = callback
            token.userContext = userContext
            token.internalTok.topics = topics

            val register = MqttSubscribe(topics, qoss)
            register.messageId = messageId

            comms.sendNoWait(register, token)

            token
        } catch (e: Exception) {
            comms.removeMessageListener(topic)
            throw e
        }
    }
}