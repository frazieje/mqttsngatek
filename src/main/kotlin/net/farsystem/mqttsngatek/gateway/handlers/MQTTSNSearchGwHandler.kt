package net.farsystem.mqttsngatek.gateway.handlers

import net.farsystem.mqttsngatek.*
import net.farsystem.mqttsngatek.gateway.MQTTSNMessageHandler
import net.farsystem.mqttsngatek.model.NetworkContext
import net.farsystem.mqttsngatek.model.NetworkContext.Companion.flip
import org.slf4j.LoggerFactory

class MQTTSNSearchGwHandler(
    private val messagBuilder: MQTTSNMessagBuilder,
    private val gatewayConfig: GatewayConfig,
    private val outgoingProcessor: MQTTSNMessageProcessor,
) : MQTTSNMessageHandler {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override suspend fun handleMessage(networkContext: NetworkContext, message: MQTTSNMessage) {

        val body = message.body as MQTTSNSearchGw

        logger.debug("SEARCHGW Received with radius ${body.radius}")

        val hostAddress = networkContext.destination.address.hostAddress

        val localAddress = if (hostAddress.contains('%'))
            hostAddress.substring(0, hostAddress.indexOf('%'))
        else
            hostAddress

        val response = messagBuilder.createMessage(
            MQTTSNMessageType.GWINFO,
            MQTTSNGwInfo(gatewayConfig.gatewayId(), localAddress)
        )
        outgoingProcessor.process(networkContext.flip(), response)
    }

}