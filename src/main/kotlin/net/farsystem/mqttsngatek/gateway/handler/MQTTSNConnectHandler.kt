package net.farsystem.mqttsngatek.gateway.handler

import net.farsystem.mqttsngatek.*
import net.farsystem.mqttsngatek.data.repository.MQTTClientRepository
import net.farsystem.mqttsngatek.data.repository.MQTTSNClientRepository
import net.farsystem.mqttsngatek.data.repository.MQTTSNWillRepository
import net.farsystem.mqttsngatek.gateway.MQTTSNMessageHandler
import net.farsystem.mqttsngatek.model.MQTTSNClient
import net.farsystem.mqttsngatek.model.NetworkContext
import net.farsystem.mqttsngatek.model.NetworkContext.Companion.flip
import net.farsystem.mqttsngatek.mqtt.*
import org.slf4j.LoggerFactory

class MQTTSNConnectHandler(
    private val mqttsnMessageBuilder: MQTTSNMessagBuilder,
    private val mqttsnClientRepository: MQTTSNClientRepository,
    private val mqttClientRepository: MQTTClientRepository,
    private val mqttsnWillRepository: MQTTSNWillRepository,
    private val outgoingProcessor: MQTTSNMessageProcessor,
) : MQTTSNMessageHandler {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override suspend fun handleMessage(networkContext: NetworkContext, message: MQTTSNMessage) {
        val body = message.body as MQTTSNConnect
        logger.debug("CONNECT message received at handler with ClientID: ${body.clientId}")

        val snClient = mqttsnClientRepository.getClient(networkContext)?.apply {
            if (clientId != body.clientId) {
                mqttsnClientRepository.addOrUpdateClient(this)
            }
        } ?: MQTTSNClient(body.clientId, networkContext).also {
            mqttsnClientRepository.addOrUpdateClient(it)
        }

        val client = mqttClientRepository.getOrCreate(snClient)

        if (client.isConnected()) {
            logger.warn("client already connected, disconnecting")
            client.disconnect()
            logger.debug("client disconnected")
        }

        val options = MQTTConnectOptions(
            body.cleanSession,
            body.duration,
            MQTTVersion.VERSION_3_1_1
        )

        //TODO: Handle cleanSession

        val response = if (!body.willFlag) {
            logger.debug("will flag not set, connecting to broker")
            val mqttConnack = client.connect(options)
            logger.debug("connection to broker done")

            val rc = when(mqttConnack.returnCode) {
                MQTTReturnCode.ACCEPTED -> MQTTSNReturnCode.ACCEPTED
                MQTTReturnCode.REJECTED_BAD_CREDENTIALS -> MQTTSNReturnCode.REJECTED_NOT_SUPPORTED
                MQTTReturnCode.REJECTED_ID_REJECTED -> MQTTSNReturnCode.REJECTED_INVALID_TOPIC_ID
                MQTTReturnCode.REJECTED_NOT_AUTHORIZED -> MQTTSNReturnCode.REJECTED_NOT_SUPPORTED
                MQTTReturnCode.REJECTED_SERVER_UNAVAILABLE -> MQTTSNReturnCode.REJECTED_CONGESTION
                MQTTReturnCode.REJECTED_UNACCEPTABLE_PROTOCOL -> MQTTSNReturnCode.REJECTED_NOT_SUPPORTED
            }

            mqttsnMessageBuilder.createMessage(
                MQTTSNMessageType.CONNACK,
                MQTTSNConnack(rc)
            )
        } else {
            logger.debug("will flag set, requesting will topic")
            mqttsnWillRepository.putPendingConnect(snClient, body)
            mqttsnMessageBuilder.createMessage(
                MQTTSNMessageType.WILLTOPICREQ,
                MQTTSNWillTopicReq()
            )
        }
        outgoingProcessor.process(networkContext.flip(), response)
    }

}