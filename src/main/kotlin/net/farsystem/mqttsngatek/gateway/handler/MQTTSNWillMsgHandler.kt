package net.farsystem.mqttsngatek.gateway.handler

import net.farsystem.mqttsngatek.*
import net.farsystem.mqttsngatek.data.repository.MQTTClientRepository
import net.farsystem.mqttsngatek.data.repository.MQTTSNClientRepository
import net.farsystem.mqttsngatek.data.repository.MQTTSNWillRepository
import net.farsystem.mqttsngatek.gateway.MQTTSNMessageHandler
import net.farsystem.mqttsngatek.model.NetworkContext
import net.farsystem.mqttsngatek.model.NetworkContext.Companion.flip
import net.farsystem.mqttsngatek.mqtt.MQTTConnectOptions
import net.farsystem.mqttsngatek.mqtt.MQTTQoS
import net.farsystem.mqttsngatek.mqtt.MQTTReturnCode
import net.farsystem.mqttsngatek.mqtt.MQTTVersion
import org.slf4j.LoggerFactory

class MQTTSNWillMsgHandler(
    private val messageBuilder: MQTTSNMessagBuilder,
    private val mqttsnClientRepository: MQTTSNClientRepository,
    private val mqttsnWillRepository: MQTTSNWillRepository,
    private val mqttClientRepository: MQTTClientRepository,
    private val outgoingProcessor: MQTTSNMessageProcessor,
) : MQTTSNMessageHandler {

    private val logger = LoggerFactory.getLogger(this::class.simpleName)

    override suspend fun handleMessage(networkContext: NetworkContext, message: MQTTSNMessage) {

        val willMsg = message.body as MQTTSNWillMsg

        logger.debug("WILLMSG message received")

        val mqttsnClient = mqttsnClientRepository.getClient(networkContext)

        if (mqttsnClient == null) {
            logger.debug("MQTT-SN client not found for this connection - aborting")
            return
        }

        val mqttClient = mqttsnClient.let {
            mqttClientRepository.getOrCreate(it)
        }

        if (mqttClient.isConnected()) {
            logger.warn("client already connected, disconnecting")
            mqttClient.disconnect()
            logger.debug("client disconnected")
        }

        val pendingTopic = mqttsnWillRepository.getPendingWillTopic(mqttsnClient)!!

        val connect = mqttsnWillRepository.getPendingConnect(mqttsnClient)!!

        val options = MQTTConnectOptions(
            connect.cleanSession,
            connect.duration,
            MQTTVersion.VERSION_3_1_1,
            pendingTopic.topic!!,
            willMsg.message,
            pendingTopic.retained!!,
            MQTTQoS.fromCode(pendingTopic.qos!!.code)
        )

        logger.debug("connecting to broker")
        val mqttConnack = mqttClient.connect(options)
        logger.debug("connection to broker done")

        val rc = when(mqttConnack.returnCode) {
            MQTTReturnCode.ACCEPTED -> MQTTSNReturnCode.ACCEPTED
            MQTTReturnCode.REJECTED_BAD_CREDENTIALS -> MQTTSNReturnCode.REJECTED_NOT_SUPPORTED
            MQTTReturnCode.REJECTED_ID_REJECTED -> MQTTSNReturnCode.REJECTED_INVALID_TOPIC_ID
            MQTTReturnCode.REJECTED_NOT_AUTHORIZED -> MQTTSNReturnCode.REJECTED_NOT_SUPPORTED
            MQTTReturnCode.REJECTED_SERVER_UNAVAILABLE -> MQTTSNReturnCode.REJECTED_CONGESTION
            MQTTReturnCode.REJECTED_UNACCEPTABLE_PROTOCOL -> MQTTSNReturnCode.REJECTED_NOT_SUPPORTED
        }

        outgoingProcessor.process(
            networkContext.flip(),
            messageBuilder.createMessage(MQTTSNMessageType.CONNACK, MQTTSNConnack(rc))
        )

    }
}