package net.farsystem.mqttsngatek.gateway.handler

import net.farsystem.mqttsngatek.*
import net.farsystem.mqttsngatek.data.repository.MQTTSNClientRepository
import net.farsystem.mqttsngatek.data.repository.MQTTSNWillRepository
import net.farsystem.mqttsngatek.gateway.MQTTSNMessageHandler
import net.farsystem.mqttsngatek.model.NetworkContext
import net.farsystem.mqttsngatek.model.NetworkContext.Companion.flip
import org.slf4j.LoggerFactory

class MQTTSNWillTopicHandler(
    private val messageBuilder: MQTTSNMessagBuilder,
    private val mqttsnClientRepository: MQTTSNClientRepository,
    private val mqttsnWillRepository: MQTTSNWillRepository,
    private val outgoingProcessor: MQTTSNMessageProcessor,
) : MQTTSNMessageHandler {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override suspend fun handleMessage(networkContext: NetworkContext, message: MQTTSNMessage) {
        val willTopicMsg = message.body as MQTTSNWillTopic

        logger.debug("WILLTOPIC Received")

        val mqttsnClient = mqttsnClientRepository.getClient(networkContext)

        if (mqttsnClient == null) {
            logger.debug("MQTT-SN client not found for this connection - aborting")
            return
        }

        mqttsnWillRepository.putPendingWillTopic(mqttsnClient, willTopicMsg)

        outgoingProcessor.process(
            networkContext.flip(),
            messageBuilder.createMessage(MQTTSNMessageType.WILLMSGREQ, MQTTSNWillMsgReq())
        )
    }
}