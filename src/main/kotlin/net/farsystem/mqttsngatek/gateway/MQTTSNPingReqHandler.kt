package net.farsystem.mqttsngatek.gateway

import net.farsystem.mqttsngatek.*
import net.farsystem.mqttsngatek.data.repository.MQTTClientRepository
import net.farsystem.mqttsngatek.data.repository.MQTTSNClientRepository
import net.farsystem.mqttsngatek.model.NetworkContext
import org.slf4j.LoggerFactory

class MQTTSNPingReqHandler(
    private val mqttsnMessageBuilder: MQTTSNMessagBuilder,
    private val mqttsnClientRepository: MQTTSNClientRepository,
    private val mqttClientRepository: MQTTClientRepository
) : MQTTSNMessageHandler {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override suspend fun handleMessage(networkContext: NetworkContext, message: MQTTSNMessage): MQTTSNMessage? {
        val pingreq = message.body as MQTTSNPingReq
        logger.debug("PINGREQ Received with clientId ${pingreq.clientId}")

        val mqttClient = mqttsnClientRepository.getClient(networkContext)?.let {
            mqttClientRepository.getOrCreate(it)
        }

        if (mqttClient == null || !mqttClient.isConnected()) {
            logger.debug("client is null or disconnected")
            return null
        }

        mqttClient.ping()

        return mqttsnMessageBuilder.createMessage(MQTTSNMessageType.PINGRESP, MQTTSNPingResp())
    }
}