package net.farsystem.mqttsngatek.gateway.handler

import net.farsystem.mqttsngatek.*
import net.farsystem.mqttsngatek.data.repository.MQTTClientRepository
import net.farsystem.mqttsngatek.data.repository.MQTTSNClientRepository
import net.farsystem.mqttsngatek.gateway.MQTTSNMessageHandler
import net.farsystem.mqttsngatek.model.NetworkContext
import net.farsystem.mqttsngatek.model.NetworkContext.Companion.flip
import org.slf4j.LoggerFactory

class MQTTSNPubRecHandler(
    private val mqttsnMessageBuilder: MQTTSNMessagBuilder,
    private val mqttsnClientRepository: MQTTSNClientRepository,
    private val mqttClientRepository: MQTTClientRepository,
    private val outgoingProcessor: MQTTSNMessageProcessor
) : MQTTSNMessageHandler {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override suspend fun handleMessage(networkContext: NetworkContext, message: MQTTSNMessage) {
        val pubRecMsg = message.body as MQTTSNPubRec

        logger.debug("PUBREC received with messageId ${pubRecMsg.messageId}")

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

        val pubRel = mqttClient.pubRec(pubRecMsg.messageId)

        val response = mqttsnMessageBuilder.createMessage(
            MQTTSNMessageType.PUBREL,
            MQTTSNPubComp(pubRel.messageId)
        )

        logger.debug("MQTT pubRel received, sending to $mqttsnClient")
        outgoingProcessor.process(networkContext.flip(), response)
    }
}