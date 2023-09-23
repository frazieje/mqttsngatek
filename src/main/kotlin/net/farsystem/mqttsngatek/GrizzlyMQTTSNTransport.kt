package net.farsystem.mqttsngatek

import net.farsystem.mqttsngatek.model.NetworkContext
import net.farsystem.mqttsngatek.model.NetworkProtocol
import org.glassfish.grizzly.filterchain.FilterChainBuilder
import org.glassfish.grizzly.filterchain.TransportFilter
import org.glassfish.grizzly.nio.transport.UDPNIOTransport
import org.glassfish.grizzly.nio.transport.UDPNIOTransportBuilder

class GrizzlyMQTTSNTransport(
    protocol: NetworkProtocol,
    messageBuilder: MQTTSNMessagBuilder,
) : MQTTSNTransport {

    private val transport: UDPNIOTransport = UDPNIOTransportBuilder.newInstance().build()

    init {

        transport.processor = FilterChainBuilder.stateless()
            .add(TransportFilter())
            .add(MQTTSNFilter(messageBuilder))
            .add(MQTTSNGatewayFilter(protocol, this))
            .build()

        transport.start()

    }

    override fun send(networkContext: NetworkContext, message: MQTTSNMessage) {
        TODO("Not yet implemented")
    }

    override fun receive(networkContext: NetworkContext, message: MQTTSNMessage) {

    }
}