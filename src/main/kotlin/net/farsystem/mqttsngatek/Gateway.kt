package net.farsystem.mqttsngatek

import org.glassfish.grizzly.filterchain.FilterChainBuilder
import org.glassfish.grizzly.filterchain.TransportFilter
import org.glassfish.grizzly.nio.transport.UDPNIOTransport
import org.glassfish.grizzly.nio.transport.UDPNIOTransportBuilder

class Gateway {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {

            val filterChain =

            val transport = UDPNIOTransportBuilder.newInstance().build()

            transport.processor = FilterChainBuilder.stateless()
                .add(TransportFilter())
                .build()

        }
    }

}