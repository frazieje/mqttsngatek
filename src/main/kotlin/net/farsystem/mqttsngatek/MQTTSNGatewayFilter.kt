package net.farsystem.mqttsngatek

import org.glassfish.grizzly.filterchain.BaseFilter
import org.glassfish.grizzly.filterchain.FilterChainContext
import org.glassfish.grizzly.filterchain.NextAction
import org.slf4j.LoggerFactory

class MQTTSNGatewayFilter : BaseFilter() {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun handleRead(ctx: FilterChainContext): NextAction {

        val peerAddress = ctx.address

        when (val message = ctx.getMessage<Any>()) {
            is MQTTSNSearchGw -> {
                logger.debug("SEARCHGW Received with radius ${message.radius}")
                val gwInfo = MQTTSNGwInfo(124, ctx.connection.localAddress.toString())
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