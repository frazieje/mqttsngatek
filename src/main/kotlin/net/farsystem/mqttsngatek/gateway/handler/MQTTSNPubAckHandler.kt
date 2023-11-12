package net.farsystem.mqttsngatek.gateway.handler

import net.farsystem.mqttsngatek.*
import net.farsystem.mqttsngatek.data.repository.MQTTClientRepository
import net.farsystem.mqttsngatek.data.repository.MQTTSNClientRepository
import net.farsystem.mqttsngatek.gateway.MQTTSNMessageHandler
import net.farsystem.mqttsngatek.model.NetworkContext
import org.slf4j.LoggerFactory

class MQTTSNPubAckHandler(
    private val mqttsnClientRepository: MQTTSNClientRepository,
    private val mqttClientRepository: MQTTClientRepository
) : MQTTSNMessageHandler {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override suspend fun handleMessage(networkContext: NetworkContext, message: MQTTSNMessage) {
        val pubackMsg = message.body as MQTTSNPubAck

        logger.debug("PUBACK received with messageId ${pubackMsg.messageId}")

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

        mqttClient.pubAck(pubackMsg.messageId)

        logger.debug("MQTT puback sent to $mqttClient")
    }
}