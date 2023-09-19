package net.farsystem.mqttsngatek.gateway

import net.farsystem.mqttsngatek.*
import net.farsystem.mqttsngatek.model.NetworkContext
import org.slf4j.LoggerFactory

class MQTTSNSearchGwHandler(
    private val messagBuilder: MQTTSNMessagBuilder,
    private val gatewayConfig: GatewayConfig,
) : MQTTSNMessageHandler {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override suspend fun handleMessage(message: MQTTSNMessage, networkContext: NetworkContext): MQTTSNMessage {

        val body = message.body as MQTTSNSearchGw

        logger.debug("SEARCHGW Received with radius ${body.radius}")

        val hostAddress = networkContext.destination.address.hostAddress

        val localAddress = if (hostAddress.contains('%'))
            hostAddress.substring(0, hostAddress.indexOf('%'))
        else
            hostAddress

        return messagBuilder.createMessage(
                MQTTSNMessageType.GWINFO,
                MQTTSNGwInfo(gatewayConfig.gatewayId(), localAddress)
            )

    }

}