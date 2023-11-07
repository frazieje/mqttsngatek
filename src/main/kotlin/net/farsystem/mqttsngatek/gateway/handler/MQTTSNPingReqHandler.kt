package net.farsystem.mqttsngatek.gateway.handler

import net.farsystem.mqttsngatek.*
import net.farsystem.mqttsngatek.data.repository.MQTTClientRepository
import net.farsystem.mqttsngatek.data.repository.MQTTSNClientRepository
import net.farsystem.mqttsngatek.gateway.MQTTSNMessageHandler
import net.farsystem.mqttsngatek.model.NetworkContext
import net.farsystem.mqttsngatek.model.NetworkContext.Companion.flip
import org.slf4j.LoggerFactory

class MQTTSNPingReqHandler(
    private val mqttsnMessageBuilder: MQTTSNMessagBuilder,
    private val mqttsnClientRepository: MQTTSNClientRepository,
    private val mqttClientRepository: MQTTClientRepository,
    private val outgoingProcessor: MQTTSNMessageProcessor,
) : MQTTSNMessageHandler {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override suspend fun handleMessage(networkContext: NetworkContext, message: MQTTSNMessage) {
        val pingreq = message.body as MQTTSNPingReq
        logger.debug("PINGREQ Received with clientId ${pingreq.clientId}")

        val mqttClient = mqttsnClientRepository.getClient(networkContext)?.let {
            mqttClientRepository.getOrCreate(it)
        }

        if (mqttClient == null || !mqttClient.isConnected()) {
            logger.debug("client is null or disconnected")
            return
        }

        mqttClient.ping()

        val response = mqttsnMessageBuilder.createMessage(MQTTSNMessageType.PINGRESP, MQTTSNPingResp())
        outgoingProcessor.process(networkContext.flip(), response)
    }
}