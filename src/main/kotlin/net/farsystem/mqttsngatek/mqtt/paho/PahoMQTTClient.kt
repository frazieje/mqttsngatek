package net.farsystem.mqttsngatek.mqtt.paho

import kotlinx.coroutines.suspendCancellableCoroutine
import net.farsystem.mqttsngatek.ManualKeepAliveMqttAsyncClient
import net.farsystem.mqttsngatek.mqtt.*
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.internal.wire.MqttConnack
import org.eclipse.paho.client.mqttv3.internal.wire.MqttPingResp
import org.eclipse.paho.client.mqttv3.internal.wire.MqttPubAck
import org.eclipse.paho.client.mqttv3.internal.wire.MqttPubRec
import org.eclipse.paho.client.mqttv3.internal.wire.MqttSuback
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence
import org.slf4j.LoggerFactory
import kotlin.coroutines.resume

class PahoMQTTClient(
    override val clientId: String,
    brokerHost: String,
    brokerPort: Int
) : MQTTClient {

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val client: ManualKeepAliveMqttAsyncClient

    init {
        val brokerUrl = "tcp://$brokerHost:$brokerPort"
        client = ManualKeepAliveMqttAsyncClient(brokerUrl, clientId, MqttDefaultFilePersistence())
    }

    override suspend fun connect(options: MQTTConnectOptions): MQTTConnack {

        val pahoConnectOptions = MqttConnectOptions()
        pahoConnectOptions.isCleanSession = options.isCleanSession
        pahoConnectOptions.keepAliveInterval = options.keepAliveInterval
        pahoConnectOptions.mqttVersion = when (options.version) {
            MQTTVersion.DEFAULT -> MqttConnectOptions.MQTT_VERSION_DEFAULT
            MQTTVersion.VERSION_3_1 -> MqttConnectOptions.MQTT_VERSION_3_1
            MQTTVersion.VERSION_3_1_1 -> MqttConnectOptions.MQTT_VERSION_3_1_1
        }

        val mqttToken = awaitCallback { client.connect(pahoConnectOptions, null, it) }!!

        if (mqttToken.exception == null) {
            val connackMsg = mqttToken.response as MqttConnack
            return MQTTConnack(MQTTReturnCode.fromCode(connackMsg.returnCode), connackMsg.sessionPresent)
        } else {
            throw mqttToken.exception
        }

    }

    override suspend fun ping(): MQTTPingResp {
        val mqttToken = awaitCallback { client.sendPing(it) }!!

        if (mqttToken.exception == null) {
            val pingRespMsg = mqttToken.response as MqttPingResp
            logger.debug("PINGRESP from mqtt broker")
            return MQTTPingResp()
        } else {
            throw mqttToken.exception
        }
    }

    override suspend fun subscribe(
        topic: String,
        qos: Int,
        dup: Boolean,
        messageId: Int,
        subscriber: (MQTTPublish) -> Unit
    ): MQTTSubAck {
        val mqttToken = awaitCallback {
            client.subscribe(topic, qos, dup, messageId, null, it) { topic, message ->
                subscriber(
                    MQTTPublish(
                        topic,
                        MQTTQoS.fromCode(message.qos),
                        message.isRetained,
                        message.isDuplicate,
                        message.id,
                        message.payload
                    )
                )
            }
        }!!

        if (mqttToken.exception == null) {
            val suback = mqttToken.response as MqttSuback
            //we can just use the first index of grantedQos[] since MQTT-SN only sends one topic at a time
            return MQTTSubAck(MQTTQoS.fromCode(suback.grantedQos[0]), suback.messageId)
        } else {
            throw mqttToken.exception
        }
    }

    override suspend fun publish(
        topic: String,
        payload: ByteArray,
        qos: Int,
        dup: Boolean,
        messageId: Int,
        retained: Boolean
    ): MQTTAck? {
        logger.debug("Sending publish to mqtt broker, topic: $topic, qos: $qos, messageId: $messageId")
        val mqttToken = awaitCallback {
            client.publish(topic, payload, qos, dup, messageId, retained, null, it)
        }!!
        return if (mqttToken.exception == null) {
            logger.debug("Publish Acknowledgement received from mqtt broker for messageId: $messageId")
            when (MQTTQoS.fromCode(qos)) {
                MQTTQoS.ZERO -> null
                MQTTQoS.ONE -> {
                    val puback = mqttToken.response as MqttPubAck
                    MQTTPubAck(puback.messageId)
                }
                MQTTQoS.TWO -> {
                    val pubrec = mqttToken.response as MqttPubRec
                    MQTTPubRec(pubrec.messageId)
                }
            }
        } else {
            throw mqttToken.exception
        }
    }

    override suspend fun disconnect() {
        awaitCallback { client.disconnect(null, it) }
    }

    override fun isConnected(): Boolean = client.isConnected

    private suspend fun awaitCallback(
        block: (IMqttActionListener) -> Unit
    ): IMqttToken? = suspendCancellableCoroutine { cont ->
        block(object : IMqttActionListener {
            override fun onSuccess(asyncActionToken: IMqttToken) {
                cont.resume(asyncActionToken)
            }
            override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable?) {
                cont.cancel(exception)
            }
        })
    }
}