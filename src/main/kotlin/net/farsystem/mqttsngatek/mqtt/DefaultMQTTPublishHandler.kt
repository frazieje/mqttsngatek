package net.farsystem.mqttsngatek.mqtt

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import net.farsystem.mqttsngatek.*
import net.farsystem.mqttsngatek.data.repository.MQTTSNClientRepository
import net.farsystem.mqttsngatek.data.repository.MQTTSNPublishRepository
import net.farsystem.mqttsngatek.data.repository.MQTTSNTopicRepository
import net.farsystem.mqttsngatek.model.MQTTSNTopic
import net.farsystem.mqttsngatek.model.NetworkContext.Companion.flip
import org.slf4j.LoggerFactory

class DefaultMQTTPublishHandler(
    private val mqttsnMessageBuilder: MQTTSNMessagBuilder,
    private val mqttsnClientRepository: MQTTSNClientRepository,
    private val mqttsnTopicRepository: MQTTSNTopicRepository,
    private val mqttsnPublishRepository: MQTTSNPublishRepository,
    private val outgoingProcessor: MQTTSNMessageProcessor,
) : MQTTPublishHandler {

    private val logger = LoggerFactory.getLogger(this::class.simpleName)

    private val handler = CoroutineScope(Dispatchers.IO)

    private var lastRegistrationId: UShort = 0u

    override fun receive(client: MQTTClient, message: MQTTPublish) {
        logger.debug("Received MQTT Publish $message for client $client.")
        handler.launch {
            mqttsnClientRepository.getClient(client.clientId)?.run {
                logger.debug("MQTTSN client $this found for MQTT client $client.")
                val publishBody = mqttsnTopicRepository.getTopic(this, message.topic)?.let {
                    logger.debug("MQTTSN Normal Topic found $it")
                    message.toSnPublish(it)
                } ?: mqttsnTopicRepository.getPredefinedTopic(message.topic)?.let {
                    logger.debug("MQTTSN Predefined topic found $it")
                    message.toSnPublish(it)
                }
                val response = publishBody?.let {
                    mqttsnMessageBuilder.createMessage(MQTTSNMessageType.PUBLISH, it)
                } ?: run {
                    logger.debug("No MQTTSN Topic found, Registration needed")
                    val topic = mqttsnTopicRepository.getOrCreateTopic(this, message.topic)
                    val registerBody = MQTTSNRegister(
                        topic.id!!,
                        getNextRegistrationId().toInt(),
                        topic.topic
                    )
                    mqttsnPublishRepository.put(this, registerBody.messageId, message.toSnPublish(topic))
                    mqttsnMessageBuilder.createMessage(MQTTSNMessageType.REGISTER, registerBody)
                }
                outgoingProcessor.process(networkContext.flip(), response)
            } ?: run {
                logger.debug("MQTTSN client not found for MQTT client $client.")
                //TODO: disconnect/dispose MQTT client?
            }
        }
    }

    private fun getNextRegistrationId(): UShort {
        if (lastRegistrationId == UShort.MAX_VALUE) {
            lastRegistrationId = 0u
        }
        return ++lastRegistrationId
    }

    private fun MQTTPublish.toSnPublish(topic: MQTTSNTopic) =
        MQTTSNPublish(
            dup,
            retained,
            MQTTSNQoS.fromCode(qos.code),
            messageId,
            topic.type,
            null,
            topic.id,
            payload
        )
}