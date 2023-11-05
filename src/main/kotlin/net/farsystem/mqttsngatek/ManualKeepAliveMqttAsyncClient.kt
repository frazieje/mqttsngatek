package net.farsystem.mqttsngatek

import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.internal.wire.MqttPingReq
import org.eclipse.paho.client.mqttv3.internal.wire.MqttPublish
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
        dup: Boolean,
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

            val subscribe = MqttSubscribe(topics, qoss)
            subscribe.messageId = messageId
            subscribe.setDuplicate(dup)
            comms.sendNoWait(subscribe, token)

            token
        } catch (e: Exception) {
            comms.removeMessageListener(topic)
            throw e
        }
    }

    fun publish(
        topic: String,
        payload: ByteArray,
        qos: Int,
        dup: Boolean,
        messageId: Int,
        retained: Boolean,
        userContext: Any?,
        messageListener: IMqttActionListener
    ): IMqttDeliveryToken {
        logger.debug("$clientId publish to topic=$topic with messageId=$messageId")
        MqttTopic.validate(topic, false)
        val token = MqttDeliveryToken(clientId)
        token.actionCallback = messageListener
        token.userContext = userContext
        val message = MqttMessage(payload)
        message.qos = qos
        message.isRetained = retained
        token.internalTok.message = message
        token.internalTok.topics = arrayOf(topic)
        val pubMsg = MqttPublish(topic, message)
        pubMsg.messageId = messageId
        pubMsg.setDuplicate(dup)
        comms.sendNoWait(pubMsg, token)
        return token
    }
}