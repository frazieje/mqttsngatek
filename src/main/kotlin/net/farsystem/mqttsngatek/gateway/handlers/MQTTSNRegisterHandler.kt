package net.farsystem.mqttsngatek.gateway.handlers

import net.farsystem.mqttsngatek.*
import net.farsystem.mqttsngatek.data.repository.MQTTClientRepository
import net.farsystem.mqttsngatek.data.repository.MQTTSNClientRepository
import net.farsystem.mqttsngatek.data.repository.MQTTSNTopicRepository
import net.farsystem.mqttsngatek.gateway.MQTTSNMessageHandler
import net.farsystem.mqttsngatek.model.NetworkContext
import net.farsystem.mqttsngatek.model.NetworkContext.Companion.flip
import org.slf4j.LoggerFactory

class MQTTSNRegisterHandler(
    private val mqttsnMessagBuilder: MQTTSNMessagBuilder,
    private val mqttsnClientRepository: MQTTSNClientRepository,
    private val mqttsnTopicRepository: MQTTSNTopicRepository,
    private val outgoingProcessor: MQTTSNMessageProcessor
) : MQTTSNMessageHandler {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override suspend fun handleMessage(networkContext: NetworkContext, message: MQTTSNMessage) {
        val register = message.body as MQTTSNRegister

        with (register) {
            logger.debug("REGISTER Received with messageId $messageId, topic $topic")
        }

        val mqttsnClient = mqttsnClientRepository.getClient(networkContext)

        if (mqttsnClient == null) {
            logger.debug("MQTT-SN client not found for this connection - aborting")
            return
        }

        val newTopic = mqttsnTopicRepository.getOrCreateTopic(mqttsnClient, register.topic)

        logger.debug("$mqttsnClient assigned topicId ${newTopic.id} for topic ${newTopic.topic}")

        val response = mqttsnMessagBuilder.createMessage(
            MQTTSNMessageType.REGACK,
            MQTTSNRegAck(newTopic.id!!, register.messageId, MQTTSNReturnCode.ACCEPTED)
        )

        outgoingProcessor.process(networkContext.flip(), response)
    }
}