package net.farsystem.mqttsngatek.gateway.handler

import net.farsystem.mqttsngatek.MQTTSNMessage
import net.farsystem.mqttsngatek.MQTTSNPingResp
import net.farsystem.mqttsngatek.MQTTSNPubAck
import net.farsystem.mqttsngatek.data.repository.MQTTClientRepository
import net.farsystem.mqttsngatek.data.repository.MQTTSNClientRepository
import net.farsystem.mqttsngatek.gateway.MQTTSNMessageHandler
import net.farsystem.mqttsngatek.model.NetworkContext
import org.slf4j.LoggerFactory

class MQTTSNPingRespHandler(
    private val mqttsnClientRepository: MQTTSNClientRepository,
    private val mqttClientRepository: MQTTClientRepository
) : MQTTSNMessageHandler {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override suspend fun handleMessage(networkContext: NetworkContext, message: MQTTSNMessage) {
        val pingRespMsg = message.body as MQTTSNPingResp

        logger.debug("PINGRESP received")

        val mqttsnClient = mqttsnClientRepository.getClient(networkContext)

        if (mqttsnClient == null) {
            logger.debug("MQTT-SN client not found for this connection - aborting")
            return
        }

        // TODO: Manage keepalive
    }

}