package net.farsystem.mqttsngatek.mqtt.paho

import kotlinx.coroutines.suspendCancellableCoroutine
import net.farsystem.mqttsngatek.ManualKeepAliveMqttAsyncClient
import net.farsystem.mqttsngatek.mqtt.*
import org.eclipse.paho.client.mqttv3.IMqttActionListener
import org.eclipse.paho.client.mqttv3.IMqttAsyncClient
import org.eclipse.paho.client.mqttv3.IMqttToken
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.internal.wire.MqttConnack
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence
import org.slf4j.LoggerFactory
import kotlin.coroutines.resume

class PahoMQTTClient(private val brokerUrl: String, private val clientId: String) : MQTTClient {

    private val logger = LoggerFactory.getLogger(this::class.java)

    val client: IMqttAsyncClient

    init {
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