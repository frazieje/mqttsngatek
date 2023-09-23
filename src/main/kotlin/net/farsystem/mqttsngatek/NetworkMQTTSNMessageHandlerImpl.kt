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
import net.farsystem.mqttsngatek.model.NetworkContext.Companion.flip
import org.slf4j.LoggerFactory
import java.lang.Exception

class NetworkMQTTSNMessageHandlerImpl(
    private val handlerRegistry: MQTTSNMessageHandlerRegistry,
    private val sender: NetworkMQTTSNMessageSender
): NetworkMQTTSNMessageHandler {

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val handlerScope = CoroutineScope(Dispatchers.IO)

    override fun receive(
        networkContext: NetworkContext,
        mqttsnMessage: MQTTSNMessage
    ) {
        handlerScope.launch {
            val result = try {
                logger.debug(mqttsnMessage.toString())

                val handler = handlerRegistry.resolve(mqttsnMessage.header.messageType)

                if (handler == null) {
                    logger.warn("Handler not found for MQTTSN Message $mqttsnMessage")
                }

                handler?.handleMessage(mqttsnMessage, networkContext)
            } catch (e: Exception) {
                logger.error("Error processing MQTTSN Message $mqttsnMessage", e)
                null
            }

            result?.let {
                sender.send(networkContext.flip(), it)
            }
        }
    }
}