package net.farsystem.mqttsngatek

import net.farsystem.mqttsngatek.mqtt.paho.SimpleMqttPubComp
import net.farsystem.mqttsngatek.mqtt.paho.SimpleMqttPubRec
import net.farsystem.mqttsngatek.mqtt.paho.SimpleMqttPubRel
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.internal.wire.*
import org.slf4j.LoggerFactory

/**
 * Make modifications to the default Paho async MQTT client to support the needs of the MQTT-SN Gateway.
 *
 * Modifications/Overrides:
 * 1. Use a No-op ping sender implementation to prevent the MQTT client from automatically sending pings to the broker.
 *     We want to ensure that any sent pings come from MQTT-SN clients and are forwarded along.
 * 2. Add a sendPing function to manually send keep-alive pings
 * 3. Add a custom subscribe function to allow the message/packet ID and duplicate flag to be set by the connected
 *     MQTT-SN clients and forwarded unchanged to the broker.
 * 4. Add a custom publish function to allow the message/packet ID and duplicate flag to be set by the connected
 *     MQTT-SN clients and forwarded unchanged to the broker.
 * 5. Use manual ACK messages so that they can be forwarded from MQTT-SN clients. By default the Paho MQTT client
 *     automatically sends acknowledgements.
 */
class ManualMqttAsyncClient(
    serverURI: String?,
    clientId: String?,
    persistence: MqttClientPersistence?
) : MqttAsyncClient(serverURI, clientId, persistence, NoOpPingSender()) {

    init {
        comms.setManualAcks(true)
    }

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

    fun unsubscribe(
        topic: String,
        messageId: Int,
        userContext: Any?,
        callback: IMqttActionListener
    ): IMqttToken {
        MqttTopic.validate(topic, true)

        logger.debug("$clientId unsubscribe from topic=$topic")

        val topics = arrayOf(topic)

        val token = MqttToken(clientId)
        token.actionCallback = callback
        token.userContext = userContext
        token.internalTok.topics = topics

        val unsubscribe = MqttUnsubscribe(topics)
        unsubscribe.messageId = messageId
        comms.sendNoWait(unsubscribe, token)

        return token
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

    fun pubAck(messageId: Int, callback: IMqttActionListener) {
        val token = MqttToken(clientId)
        token.actionCallback = callback
        comms.sendNoWait(MqttPubAck(messageId), token)
    }

    fun pubRec(messageId: Int, callback: IMqttActionListener) {
        val token = MqttToken(clientId)
        token.actionCallback = callback
        comms.sendNoWait(SimpleMqttPubRec(messageId), token)
    }

    fun pubRel(messageId: Int, callback: IMqttActionListener) {
        val token = MqttToken(clientId)
        token.actionCallback = callback
        comms.sendNoWait(SimpleMqttPubRel(messageId), token)
    }

    fun pubComp(messageId: Int, callback: IMqttActionListener) {
        val token = MqttToken(clientId)
        token.actionCallback = callback
        comms.sendNoWait(SimpleMqttPubComp(messageId), token)
    }
}