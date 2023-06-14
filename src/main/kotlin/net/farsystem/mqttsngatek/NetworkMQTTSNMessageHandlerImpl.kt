package net.farsystem.mqttsngatek

import net.farsystem.mqttsngatek.model.NetworkContext

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.farsystem.mqttsngatek.data.repository.MQTTSNClientRepository
import net.farsystem.mqttsngatek.gateway.MQTTSNConnectHandler
import net.farsystem.mqttsngatek.gateway.MQTTSNMessageHandler
import net.farsystem.mqttsngatek.gateway.MQTTSNSearchGwHandler
import org.slf4j.LoggerFactory

class NetworkMQTTSNMessageHandlerImpl(
//    private val repository: MQTTSNClientRepository,
    messageBuilder: MQTTSNMessagBuilder,
    gatewayConfig: GatewayConfig
): NetworkMQTTSNMessageHandler {

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val handlerScope = CoroutineScope(Dispatchers.IO)

    override fun onReceive(
        networkContext: NetworkContext,
        mqttsnMessage: MQTTSNMessage,
        onComplete: (MQTTSNMessage?) -> Unit
    ) {
        handlerScope.launch {

//            val client = repository.getClientByContext(networkContext)

            logger.debug(mqttsnMessage.toString())

            val handler = classMap[mqttsnMessage.header.messageType]!!

            val response = handler.handleMessage(networkContext, mqttsnMessage)

            onComplete(response)

        }
    }

    private val classMap: Map<MQTTSNMessageType, MQTTSNMessageHandler> = hashMapOf(
        MQTTSNMessageType.SEARCHGW to MQTTSNSearchGwHandler(messageBuilder, gatewayConfig),
        MQTTSNMessageType.CONNECT to MQTTSNConnectHandler(messageBuilder, gatewayConfig)
    )

}