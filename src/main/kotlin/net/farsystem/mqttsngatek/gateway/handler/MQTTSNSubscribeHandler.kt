package net.farsystem.mqttsngatek.gateway.handler

import net.farsystem.mqttsngatek.*
import net.farsystem.mqttsngatek.data.repository.MQTTClientRepository
import net.farsystem.mqttsngatek.data.repository.MQTTSNClientRepository
import net.farsystem.mqttsngatek.data.repository.MQTTSNTopicRepository
import net.farsystem.mqttsngatek.gateway.MQTTSNMessageHandler
import net.farsystem.mqttsngatek.model.NetworkContext
import net.farsystem.mqttsngatek.model.NetworkContext.Companion.flip
import net.farsystem.mqttsngatek.mqtt.MQTTPublishHandler
import org.slf4j.LoggerFactory

class MQTTSNSubscribeHandler(
    private val mqttsnMessagBuilder: MQTTSNMessagBuilder,
    private val mqttsnClientRepository: MQTTSNClientRepository,
    private val mqttClientRepository: MQTTClientRepository,
    private val mqttsnTopicRepository: MQTTSNTopicRepository,
    private val publishHandler: MQTTPublishHandler,
    private val outgoingProcessor: MQTTSNMessageProcessor,
) : MQTTSNMessageHandler {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override suspend fun handleMessage(networkContext: NetworkContext, message: MQTTSNMessage) {
        val subscribeMsg = message.body as MQTTSNSubscribe

        logger.debug("SUBSCRIBE Received with messageId ${subscribeMsg.messageId}")

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
        val response = when (subscribeMsg.topicType) {
            MQTTSNTopicType.NORMAL -> {
                val topic = subscribeMsg.topic!!
                val wildcard = topic.contains(Regex("[+#]"))
                val mqttSuback = mqttClient.subscribe(
                    topic,
                    subscribeMsg.qos.code,
                    subscribeMsg.dup,
                    subscribeMsg.messageId
                ) {
                    publishHandler.receive(mqttClient, it)
                }
                val mqttsnTopicId = if (!wildcard)
                    mqttsnTopicRepository.getOrCreateTopic(mqttsnClient, topic).id!!
                else 0
                MQTTSNSubAck(
                    MQTTSNQoS.fromCode(mqttSuback.grantedQos.code),
                    mqttSuback.messageId,
                    mqttsnTopicId,
                    MQTTSNReturnCode.ACCEPTED
                )
            }
            MQTTSNTopicType.SHORT_NAME -> {
                val topic = subscribeMsg.topic!!
                val mqttSuback = mqttClient.subscribe(
                    topic,
                    subscribeMsg.qos.code,
                    subscribeMsg.dup,
                    subscribeMsg.messageId
                ) {
                    publishHandler.receive(mqttClient, it)
                }
                MQTTSNSubAck(
                    MQTTSNQoS.fromCode(mqttSuback.grantedQos.code),
                    mqttSuback.messageId,
                    returnCode = MQTTSNReturnCode.ACCEPTED
                )
            }
            MQTTSNTopicType.PREDEFINED -> {
                val topicId = subscribeMsg.topicId!!
                mqttsnTopicRepository.getPredefinedTopic(topicId)?.let { predefinedTopic ->
                    val mqttSuback = mqttClient.subscribe(
                        predefinedTopic.topic,
                        subscribeMsg.qos.code,
                        subscribeMsg.dup,
                        subscribeMsg.messageId
                    ) {
                        publishHandler.receive(mqttClient, it)
                    }
                    MQTTSNSubAck(
                        MQTTSNQoS.fromCode(mqttSuback.grantedQos.code),
                        mqttSuback.messageId,
                        predefinedTopic.id!!,
                        MQTTSNReturnCode.ACCEPTED
                    )
                } ?: MQTTSNSubAck(
                    MQTTSNQoS.ZERO,
                    subscribeMsg.messageId,
                    returnCode = MQTTSNReturnCode.REJECTED_INVALID_TOPIC_ID
                )
            }
        }

        val suback = mqttsnMessagBuilder.createMessage(
            MQTTSNMessageType.SUBACK,
            response
        )

        outgoingProcessor.process(networkContext.flip(), suback)
    }
}