package net.farsystem.mqttsngatek.gateway.handler

import net.farsystem.mqttsngatek.*
import net.farsystem.mqttsngatek.data.repository.MQTTSNClientRepository
import net.farsystem.mqttsngatek.data.repository.MQTTSNPublishRepository
import net.farsystem.mqttsngatek.gateway.MQTTSNMessageHandler
import net.farsystem.mqttsngatek.model.NetworkContext
import net.farsystem.mqttsngatek.model.NetworkContext.Companion.flip
import org.slf4j.LoggerFactory

class MQTTSNRegAckHandler(
    private val mqttsnMessagBuilder: MQTTSNMessagBuilder,
    private val mqttsnClientRepository: MQTTSNClientRepository,
    private val mqttsnPublishRepository: MQTTSNPublishRepository,
    private val outgoingProcessor: MQTTSNMessageProcessor
): MQTTSNMessageHandler {

    private val logger = LoggerFactory.getLogger(this::class.simpleName)

    override suspend fun handleMessage(networkContext: NetworkContext, message: MQTTSNMessage) {
        val regAckMsg = message.body as MQTTSNRegAck

        logger.debug("REGACK received with messageId ${regAckMsg.messageId}")

        val mqttsnClient = mqttsnClientRepository.getClient(networkContext)

        if (mqttsnClient == null) {
            logger.debug("MQTT-SN client not found for this connection - aborting")
            return
        }

        when (regAckMsg.returnCode) {
            MQTTSNReturnCode.ACCEPTED -> {
                mqttsnPublishRepository.getPendingPublish(mqttsnClient, regAckMsg.messageId)?.let {
                    val response = mqttsnMessagBuilder.createMessage(
                        MQTTSNMessageType.PUBLISH,
                        it
                    )
                    outgoingProcessor.process(networkContext.flip(), response)
                }
            }
            else -> {
                logger.debug("MQTT-SN rejected registration with: ${regAckMsg.returnCode}")
            }
        }
    }

}