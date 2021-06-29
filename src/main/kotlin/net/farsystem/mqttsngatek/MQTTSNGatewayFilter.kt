package net.farsystem.mqttsngatek

import org.glassfish.grizzly.filterchain.BaseFilter
import org.glassfish.grizzly.filterchain.FilterChainContext
import org.glassfish.grizzly.filterchain.NextAction
import org.slf4j.LoggerFactory
import java.net.InetSocketAddress

class MQTTSNGatewayFilter(val mqttsnMessageHandler: MQTTSNMessageHandler, val gatewayId: Int) : BaseFilter() {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun handleRead(ctx: FilterChainContext): NextAction {

        val peerAddress = ctx.address

        val cxn = ctx.connection

        val hostAddress = (cxn.localAddress as InetSocketAddress).address.hostAddress
        val localAddress = if (hostAddress.contains('%'))
            hostAddress.substring(0, hostAddress.indexOf('%'))
        else
            hostAddress

        val message = ctx.getMessage<MQTTSNMessage>()

        when (message.header.messageType) {
            MQTTSNMessageType.SEARCHGW -> {
                val searchGw = message.body as MQTTSNSearchGw
                logger.debug("SEARCHGW Received with radius ${searchGw.radius}")
                val response =
                    mqttsnMessageHandler.createMessage(
                        MQTTSNMessageType.GWINFO,
                        MQTTSNGwInfo(gatewayId, localAddress)
                    )
                ctx.write(peerAddress, response, null)
            }
            MQTTSNMessageType.CONNECT -> {
            }
            MQTTSNMessageType.SUBSCRIBE -> {
            }
            MQTTSNMessageType.REGISTER -> {
            }
            MQTTSNMessageType.REGACK -> {
            }
        }

        return ctx.stopAction
    }

}