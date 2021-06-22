package net.farsystem.mqttsngatek

import com.spoohapps.farcommon.Config
import org.glassfish.grizzly.filterchain.FilterChainBuilder
import org.glassfish.grizzly.filterchain.TransportFilter
import org.glassfish.grizzly.nio.transport.UDPNIOTransportBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory


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

            val resolver: MQTTSNMessageResolver = MQTTSNMessageResolverImpl()

            transport.processor = FilterChainBuilder.stateless()
                .add(TransportFilter())
                .add(MQTTSNFilter(resolver))
                .build()

            try {
                logger.info("Starting transport...")
                transport.bind(config.serverAddress(), config.serverPort())
                transport.start()

                Thread.currentThread().join()
            } finally {
                transport.shutdownNow()
                logger.info("Stopped transport...")
            }

        }
    }

}