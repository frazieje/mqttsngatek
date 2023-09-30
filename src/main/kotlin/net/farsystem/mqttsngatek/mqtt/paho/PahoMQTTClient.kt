package net.farsystem.mqttsngatek.mqtt.paho

import kotlinx.coroutines.suspendCancellableCoroutine
import net.farsystem.mqttsngatek.MQTTSNSuback
import net.farsystem.mqttsngatek.ManualKeepAliveMqttAsyncClient
import net.farsystem.mqttsngatek.mqtt.*
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.internal.wire.MqttConnack
import org.eclipse.paho.client.mqttv3.internal.wire.MqttPingResp
import org.eclipse.paho.client.mqttv3.internal.wire.MqttSuback
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence
import org.slf4j.LoggerFactory
import kotlin.coroutines.resume

class PahoMQTTClient(
    clientId: String,
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
        messageId: Int,
        subscriber: (MQTTPublish) -> Unit
    ): MQTTSuback {
        val mqttToken = awaitCallback {
            client.subscribe(topic, qos, messageId, null, it, buildSubscriber(subscriber))
        }!!

        if (mqttToken.exception == null) {
            val suback = mqttToken.response as MqttSuback
            //we can just use the first index of grantedQos[] since MQTT-SN only sends one topic at a time
            return MQTTSuback(MQTTQoS.fromCode(suback.grantedQos[0]))
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

    private fun buildSubscriber(subscriber: (MQTTPublish) -> Unit): IMqttMessageListener {
        return IMqttMessageListener { topic, message ->
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
    }

}