package net.farsystem.mqttsngatek.gateway.handler


import net.farsystem.mqttsngatek.*
import net.farsystem.mqttsngatek.data.repository.MQTTClientRepository
import net.farsystem.mqttsngatek.data.repository.MQTTSNClientRepository
import net.farsystem.mqttsngatek.data.repository.MQTTSNTopicRepository
import net.farsystem.mqttsngatek.gateway.MQTTSNMessageHandler
import net.farsystem.mqttsngatek.model.NetworkContext
import net.farsystem.mqttsngatek.model.NetworkContext.Companion.flip
import org.slf4j.LoggerFactory

class MQTTSNUnsubscribeHandler(
    private val mqttsnMessagBuilder: MQTTSNMessagBuilder,
    private val mqttsnClientRepository: MQTTSNClientRepository,
    private val mqttClientRepository: MQTTClientRepository,
    private val mqttsnTopicRepository: MQTTSNTopicRepository,
    private val outgoingProcessor: MQTTSNMessageProcessor,
) : MQTTSNMessageHandler {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override suspend fun handleMessage(networkContext: NetworkContext, message: MQTTSNMessage) {

        val body = message.body as MQTTSNUnsubscribe

        logger.debug("UNSUBSCRIBE Received with messageId ${body.messageId}")

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

        val response = when (body.topicType) {
            MQTTSNTopicType.NORMAL,
            MQTTSNTopicType.SHORT_NAME -> body.topic
            MQTTSNTopicType.PREDEFINED -> body.topicId?.let {
                mqttsnTopicRepository.getPredefinedTopic(it)?.topic
            }
        }?.let {
            val unsuback = mqttClient.unsubscribe(it, body.messageId)
            mqttsnMessagBuilder.createMessage(
                MQTTSNMessageType.UNSUBACK,
                MQTTSNUnsubAck(unsuback.messageId)
            )
        }

        if (response == null) {
            logger.warn("Could not find matching topic for $body")
        } else {
            outgoingProcessor.process(networkContext.flip(), response)
        }
    }
}