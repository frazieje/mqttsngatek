package net.farsystem.mqttsngatek

import net.farsystem.mqttsngatek.model.NetworkContext
import net.farsystem.mqttsngatek.model.NetworkProtocol
import org.glassfish.grizzly.filterchain.BaseFilter
import org.glassfish.grizzly.filterchain.FilterChainContext
import org.glassfish.grizzly.filterchain.NextAction
import org.slf4j.LoggerFactory
import java.net.InetSocketAddress

class MQTTSNGatewayFilter(
    val protocol: NetworkProtocol,
    val receiver: (NetworkContext, MQTTSNMessage) -> Unit
) : BaseFilter() {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun handleRead(ctx: FilterChainContext): NextAction {
        logger.debug("gateway handleRead")

        val cxn = ctx.connection

        logger.debug("gateway handleRead cxn ${System.identityHashCode(cxn)}")

        val mqttsnMessage = ctx.getMessage<MQTTSNMessage>()

        val networkContext = when (protocol) {
            NetworkProtocol.UDP6 -> {
                val peerSocketAddress = ctx.address as InetSocketAddress
                val localSocketAddress = cxn.localAddress as InetSocketAddress
                NetworkContext(
                    protocol,
                    peerSocketAddress,
                    localSocketAddress,
                )
            }
        }

        logger.debug("gateway handleRead, sending to receiver. cxn: ${System.identityHashCode(cxn)}")
        receiver(networkContext, mqttsnMessage)

        return ctx.stopAction
    }

}