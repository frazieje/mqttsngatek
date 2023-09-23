package net.farsystem.mqttsngatek.gateway

import net.farsystem.mqttsngatek.*
import net.farsystem.mqttsngatek.data.repository.MQTTClientRepository
import net.farsystem.mqttsngatek.data.repository.MQTTSNClientRepository
import net.farsystem.mqttsngatek.data.repository.MQTTSNTopicRepository
import net.farsystem.mqttsngatek.model.NetworkContext
import org.slf4j.LoggerFactory

class MQTTSNSubscribeHandler(
    private val mqttsnMessagBuilder: MQTTSNMessagBuilder,
    private val mqttsnClientRepository: MQTTSNClientRepository,
    private val mqttClientRepository: MQTTClientRepository,
    private val mqttsnTopicRepository: MQTTSNTopicRepository
) : MQTTSNMessageHandler {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override suspend fun handleMessage(message: MQTTSNMessage, networkContext: NetworkContext): MQTTSNMessage? {
        val subscribeMsg = message.body as MQTTSNSubscribe

        logger.debug("SUBSCRIBE Received with messageId ${subscribeMsg.messageId}")

        val mqttsnClient = mqttsnClientRepository.getClient(networkContext)

        if (mqttsnClient == null) {
            logger.debug("MQTT-SN client not found for this connection - aborting")
            return null
        }

        val mqttClient = mqttsnClient.let {
            mqttClientRepository.getOrCreate(it)
        }

        if (!mqttClient.isConnected()) {
            logger.debug("MQTT client is not connected - aborting")
            return null
        }

        val response = when (subscribeMsg.topicType) {
            MQTTSNTopicType.NORMAL -> {
                val topic = subscribeMsg.topic!!
                val mqttSuback = mqttClient.subscribe(topic, subscribeMsg.qos.code, subscribeMsg.messageId)
                val mqttsnTopic = mqttsnTopicRepository.getOrCreateTopic(mqttsnClient, topic)
                MQTTSNSuback(
                    MQTTSNQoS.fromCode(mqttSuback.grantedQos.code),
                    subscribeMsg.messageId,
                    mqttsnTopic.id,
                    MQTTSNReturnCode.ACCEPTED
                )
            }
            MQTTSNTopicType.SHORT_NAME -> {
                val topic = subscribeMsg.topic!!
                val mqttSuback = mqttClient.subscribe(topic, subscribeMsg.qos.code, subscribeMsg.messageId)
                MQTTSNSuback(
                    MQTTSNQoS.fromCode(mqttSuback.grantedQos.code),
                    subscribeMsg.messageId,
                    returnCode = MQTTSNReturnCode.ACCEPTED
                )
            }
            MQTTSNTopicType.PREDEFINED -> {
                val topicId = subscribeMsg.topicId!!
                mqttsnTopicRepository.getPredefinedTopic(topicId)?.let {
                    val mqttSuback = mqttClient.subscribe(it.topic, subscribeMsg.qos.code, subscribeMsg.messageId)
                    MQTTSNSuback(
                        MQTTSNQoS.fromCode(mqttSuback.grantedQos.code),
                        subscribeMsg.messageId,
                        it.id,
                        MQTTSNReturnCode.ACCEPTED
                    )
                } ?: MQTTSNSuback(
                    MQTTSNQoS.ZERO,
                    subscribeMsg.messageId,
                    returnCode = MQTTSNReturnCode.REJECTED_INVALID_TOPIC_ID
                )
            }
        }

        return mqttsnMessagBuilder.createMessage(
            MQTTSNMessageType.SUBACK,
            response
        )
    }
}