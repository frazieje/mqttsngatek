package net.farsystem.mqttsngatek.gateway.handler

import net.farsystem.mqttsngatek.*
import net.farsystem.mqttsngatek.data.repository.MQTTClientRepository
import net.farsystem.mqttsngatek.data.repository.MQTTSNClientRepository
import net.farsystem.mqttsngatek.data.repository.MQTTSNTopicRepository
import net.farsystem.mqttsngatek.gateway.MQTTSNMessageHandler
import net.farsystem.mqttsngatek.model.NetworkContext
import net.farsystem.mqttsngatek.model.NetworkContext.Companion.flip
import net.farsystem.mqttsngatek.mqtt.*
import org.slf4j.LoggerFactory

class MQTTSNPublishHandler(
    private val mqttsnMessageBuilder: MQTTSNMessagBuilder,
    private val mqttsnClientRepository: MQTTSNClientRepository,
    private val mqttsnTopicRepository: MQTTSNTopicRepository,
    private val mqttClientRepository: MQTTClientRepository,
    private val outgoingProcessor: MQTTSNMessageProcessor
) : MQTTSNMessageHandler {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override suspend fun handleMessage(networkContext: NetworkContext, message: MQTTSNMessage) {
        val publishMsg = message.body as MQTTSNPublish

        logger.debug("PUBLISH Received with messageId ${publishMsg.messageId}")

        val mqttsnClient = mqttsnClientRepository.getClient(networkContext)

        if (mqttsnClient == null) {
            logger.debug("MQTT-SN client not found for this connection - aborting")
            return
        }

        val mqttClient = mqttsnClient.let {
            mqttClientRepository.getOrCreate(it)
        }

        if (!mqttClient.isConnected()) {
            logger.debug("MQTT client is not connected - aborting")
            return
        }
        val response = when (publishMsg.topicType) {
            MQTTSNTopicType.NORMAL -> {
                logger.debug("MQTTSN Publish topic type NORMAL")
                mqttsnTopicRepository.getTopic(mqttsnClient, publishMsg.topicId!!)?.let {
                    logger.debug("MQTTSN Publish topic with id ${publishMsg.topicId} found, maps to \"${it.topic}\"")
                    mqttClient.publish(
                        it.topic,
                        publishMsg.payload,
                        publishMsg.qos.code,
                        publishMsg.dup,
                        publishMsg.messageId,
                        publishMsg.retained
                    )?.toSnAck(publishMsg.topicId)
                } ?: run {
                    logger.debug("MQTTSN Publish topic with id ${publishMsg.topicId} not found!")
                    mqttsnMessageBuilder.createMessage(
                        MQTTSNMessageType.PUBACK,
                        MQTTSNPubAck(
                            publishMsg.messageId,
                            publishMsg.topicId,
                            MQTTSNReturnCode.REJECTED_INVALID_TOPIC_ID
                        )
                    )
                }
            }
            MQTTSNTopicType.PREDEFINED -> {
                logger.debug("MQTTSN Publish topic type PREDEFINED")
                mqttsnTopicRepository.getPredefinedTopic(publishMsg.topicId!!)?.let {
                    logger.debug("MQTTSN Publish topic with id ${publishMsg.topicId} found, maps to \"${it.topic}\"")
                    mqttClient.publish(
                        it.topic,
                        publishMsg.payload,
                        publishMsg.qos.code,
                        publishMsg.dup,
                        publishMsg.messageId,
                        publishMsg.retained
                    )?.toSnAck(publishMsg.topicId)
                } ?: run {
                    logger.debug("MQTTSN Publish topic with id ${publishMsg.topicId} not found!")
                    mqttsnMessageBuilder.createMessage(
                        MQTTSNMessageType.PUBACK,
                        MQTTSNPubAck(
                            publishMsg.messageId,
                            publishMsg.topicId,
                            MQTTSNReturnCode.REJECTED_INVALID_TOPIC_ID
                        )
                    )
                }
            }
            MQTTSNTopicType.SHORT_NAME -> {
                logger.debug("MQTTSN Publish topic type SHORT_NAME")
                mqttClient.publish(
                    publishMsg.topic!!,
                    publishMsg.payload,
                    publishMsg.qos.code,
                    publishMsg.dup,
                    publishMsg.messageId,
                    publishMsg.retained
                )?.toSnAck()
            }
        }?.let {
            outgoingProcessor.process(networkContext.flip(), it)
        } ?: run {
            logger.debug("No outgoing message")
        }
    }

    private fun MQTTAck?.toSnAck(topicId: Int = 0) =
        when (this) {
            is MQTTPubAck -> {
                mqttsnMessageBuilder.createMessage(
                    MQTTSNMessageType.PUBACK,
                    MQTTSNPubAck(messageId, topicId, MQTTSNReturnCode.ACCEPTED)
                )
            }
            is MQTTPubRec -> mqttsnMessageBuilder.createMessage(MQTTSNMessageType.PUBREC, MQTTSNPubRec(messageId))
            is MQTTPubRel -> mqttsnMessageBuilder.createMessage(MQTTSNMessageType.PUBREL, MQTTSNPubRel(messageId))
            is MQTTPubComp -> mqttsnMessageBuilder.createMessage(MQTTSNMessageType.PUBCOMP, MQTTSNPubComp(messageId))
            else -> null
        }
}