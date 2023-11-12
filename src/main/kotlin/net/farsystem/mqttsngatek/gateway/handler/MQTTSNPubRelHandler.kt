package net.farsystem.mqttsngatek.gateway.handler

import net.farsystem.mqttsngatek.*
import net.farsystem.mqttsngatek.data.repository.MQTTClientRepository
import net.farsystem.mqttsngatek.data.repository.MQTTSNClientRepository
import net.farsystem.mqttsngatek.gateway.MQTTSNMessageHandler
import net.farsystem.mqttsngatek.model.NetworkContext
import net.farsystem.mqttsngatek.model.NetworkContext.Companion.flip
import org.slf4j.LoggerFactory

class MQTTSNPubRelHandler(
    private val mqttsnMessageBuilder: MQTTSNMessagBuilder,
    private val mqttsnClientRepository: MQTTSNClientRepository,
    private val mqttClientRepository: MQTTClientRepository,
    private val outgoingProcessor: MQTTSNMessageProcessor
) : MQTTSNMessageHandler {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override suspend fun handleMessage(networkContext: NetworkContext, message: MQTTSNMessage) {
        val pubackMsg = message.body as MQTTSNPubAck

        logger.debug("PUBREL received with messageId ${pubackMsg.messageId}")

        val mqttsnClient = mqttsnClientRepository.getClient(networkContext)

        if (mqttsnClient == null) {
            logger.debug("MQTT-SN client not found for this connection - aborting")
            return
        }

        val mqttClient = mqttsnClient.let {
            mqttClientRepository.get(it)
        }

        if (mqttClient?.isConnected() != true) {
            logger.debug("MQTT client is not connected - aborting")
            return
        }

        val pubComp = mqttClient.pubRel(pubackMsg.messageId)

        val response = mqttsnMessageBuilder.createMessage(
            MQTTSNMessageType.PUBCOMP,
            MQTTSNPubComp(pubComp.messageId)
        )

        logger.debug("MQTT pubComp received, sending to $mqttsnClient")
        outgoingProcessor.process(networkContext.flip(), response)
    }

}