package net.farsystem.mqttsngatek

import org.glassfish.grizzly.CompletionHandler
import org.glassfish.grizzly.filterchain.BaseFilter
import org.glassfish.grizzly.filterchain.FilterChainContext
import org.glassfish.grizzly.filterchain.NextAction
import org.slf4j.LoggerFactory
import java.net.Inet6Address
import java.net.InetAddress
import java.net.InetSocketAddress

class MQTTSNGatewayFilter() : BaseFilter() {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun handleRead(ctx: FilterChainContext): NextAction {

        val peerAddress = ctx.address

        val cxn = ctx.connection

        val localAddress = (cxn.localAddress as InetSocketAddress).address.hostAddress

        when (val message = ctx.getMessage<Any>()) {
            is MQTTSNSearchGw -> {
                logger.debug("SEARCHGW Received with radius ${message.radius}")
                val gwInfo = MQTTSNGwInfo(124, localAddress)
                ctx.write(peerAddress, gwInfo, null)
            }
            is MQTTSNRegister -> {
            }
            is MQTTSNSubscribe -> {
            }
            is MQTTSNConnect -> {
            }
            is MQTTSNRegAck -> {
            }
        }

        return ctx.stopAction
    }

}