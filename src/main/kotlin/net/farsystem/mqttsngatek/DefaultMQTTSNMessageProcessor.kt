package net.farsystem.mqttsngatek

import net.farsystem.mqttsngatek.model.NetworkContext

import org.slf4j.LoggerFactory
import java.lang.Exception

class DefaultMQTTSNMessageProcessor(
    private val handlerRegistry: MQTTSNMessageHandlerRegistry,
): MQTTSNMessageProcessor {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override suspend fun process(networkContext: NetworkContext, mqttsnMessage: MQTTSNMessage): MQTTSNMessage? =
        try {
            logger.debug(mqttsnMessage.toString())

            val handler = handlerRegistry.resolve(mqttsnMessage.header.messageType)

            if (handler == null) {
                logger.warn("Handler not found for MQTTSN Message $mqttsnMessage")
            }

            handler?.handleMessage(networkContext, mqttsnMessage)
        } catch (e: Exception) {
            logger.error("Error processing MQTTSN Message $mqttsnMessage", e)
            null
        }
}