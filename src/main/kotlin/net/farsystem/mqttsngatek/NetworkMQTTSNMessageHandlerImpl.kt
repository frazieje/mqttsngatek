package net.farsystem.mqttsngatek

import net.farsystem.mqttsngatek.model.NetworkContext

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.farsystem.mqttsngatek.data.repository.MQTTClientRepository
import net.farsystem.mqttsngatek.data.repository.MQTTSNClientRepository
import net.farsystem.mqttsngatek.data.repository.MQTTSNTopicRepository
import net.farsystem.mqttsngatek.gateway.*
import net.farsystem.mqttsngatek.model.MQTTSNClient
import org.slf4j.LoggerFactory
import java.lang.Exception

class NetworkMQTTSNMessageHandlerImpl(
    messageBuilder: MQTTSNMessagBuilder,
    gatewayConfig: GatewayConfig,
    mqttsnClientRepository: MQTTSNClientRepository,
    mqttClientRepository: MQTTClientRepository,
    mqttsnTopicRepository: MQTTSNTopicRepository
): NetworkMQTTSNMessageHandler {

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val handlerScope = CoroutineScope(Dispatchers.IO)

    override fun onReceive(
        networkContext: NetworkContext,
        mqttsnMessage: MQTTSNMessage,
        onComplete: (MQTTSNMessage?) -> Unit
    ) {
        handlerScope.launch {
            val result = try {
                logger.debug(mqttsnMessage.toString())

                val handler = classMap[mqttsnMessage.header.messageType]!!

                handler.handleMessage(mqttsnMessage, networkContext)
            } catch (e: Exception) {
                logger.error("Error processing MQTTSN Message $mqttsnMessage", e)
                null
            }
            onComplete(result)
        }
    }

    private val classMap: Map<MQTTSNMessageType, MQTTSNMessageHandler> = hashMapOf(
        MQTTSNMessageType.SEARCHGW to MQTTSNSearchGwHandler(messageBuilder, gatewayConfig),
        MQTTSNMessageType.CONNECT to MQTTSNConnectHandler(messageBuilder, mqttsnClientRepository, mqttClientRepository),
        MQTTSNMessageType.PINGREQ to MQTTSNPingReqHandler(messageBuilder, mqttsnClientRepository, mqttClientRepository),
        MQTTSNMessageType.SUBSCRIBE to MQTTSNSubscribeHandler(
            messageBuilder,
            mqttsnClientRepository,
            mqttClientRepository,
            mqttsnTopicRepository
        )
    )
}