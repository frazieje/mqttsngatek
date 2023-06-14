package net.farsystem.mqttsngatek.gateway

import net.farsystem.mqttsngatek.*
import org.glassfish.grizzly.filterchain.FilterChainBuilder
import org.glassfish.grizzly.filterchain.TransportFilter
import org.glassfish.grizzly.nio.transport.UDPNIOTransport
import org.glassfish.grizzly.nio.transport.UDPNIOTransportBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.InetSocketAddress
import java.net.NetworkInterface

class GrizzlyMQTTSNGateway(
    private val config: GatewayConfig,
    private val mqttsnMessageBuilder: MQTTSNMessagBuilder,
    private val networkMQTTSNMessageHandler: NetworkMQTTSNMessageHandler
): MQTTSNGateway {

    private lateinit var transport: UDPNIOTransport

    private val logger: Logger = LoggerFactory.getLogger(Gateway::class.java)

    override fun start() {

        transport = UDPNIOTransportBuilder.newInstance().build()

        transport.processor = FilterChainBuilder.stateless()
            .add(TransportFilter())
            .add(MQTTSNFilter(mqttsnMessageBuilder))
            .add(MQTTSNGatewayFilter(networkMQTTSNMessageHandler, config))
            .build()

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

    }

    override fun shutdown() {
        transport.shutdownNow()
    }
}