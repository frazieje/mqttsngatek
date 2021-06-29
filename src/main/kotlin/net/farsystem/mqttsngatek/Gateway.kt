package net.farsystem.mqttsngatek

import com.spoohapps.farcommon.Config
import org.glassfish.grizzly.filterchain.FilterChainBuilder
import org.glassfish.grizzly.filterchain.TransportFilter
import org.glassfish.grizzly.nio.transport.UDPNIOTransportBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.NetworkInterface


class Gateway {

    companion object {

        @JvmStatic
        private val logger: Logger = LoggerFactory.getLogger(Gateway::class.java)

        @JvmStatic
        fun main(args: Array<String>) {

            val config = Config.from(GatewayConfig::class.java)
                .apply(DefaultGatewayConfig())
                .build()

            val transport = UDPNIOTransportBuilder.newInstance().build()

            val handler: MQTTSNMessageHandler = MQTTSNMessageHandlerImpl()

            transport.processor = FilterChainBuilder.stateless()
                .add(TransportFilter())
                .add(MQTTSNFilter(handler))
                .add(MQTTSNGatewayFilter(handler, config.gatewayId()))
                .build()

            try {
                if (config.networkInterface().isNotBlank()) {
                    NetworkInterface.networkInterfaces().forEach { iface ->
                        if (iface.isLoopback || iface.name == config.networkInterface()) {
                            iface.interfaceAddresses.forEach {
                                transport.bind(InetSocketAddress(it.address, config.port()))
                                logger.info("Binding to address ${it.address.hostAddress}")
                            }
                        }
                    }
                    logger.info("Starting transport on ${config.networkInterface()}...")

                } else {
                    logger.info("Starting transport on :: ...")
                    transport.bind("::", config.port())
                }

                transport.start()

                Thread.currentThread().join()
            } finally {
                transport.shutdownNow()
                logger.info("Stopped transport...")
            }

        }
    }

}