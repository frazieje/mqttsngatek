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
    private val handlers: Map<MQTTSNMessageType, MQTTSNMessageHandler>,
    private val sender: NetworkMQTTSNMessageSender
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

            result?.run {

            }
            onComplete(result)
        }
    }
}