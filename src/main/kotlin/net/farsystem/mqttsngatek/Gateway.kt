package net.farsystem.mqttsngatek

import net.farsystem.mqttsngatek.gateway.GrizzlyMQTTSNGateway
import net.farsystem.mqttsngatek.gateway.MQTTSNGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory


class Gateway {

    companion object {

        @JvmStatic
        private val logger: Logger = LoggerFactory.getLogger(Gateway::class.java)

        @JvmStatic
        fun main(args: Array<String>) {

            val config = DefaultGatewayConfig()

            val messageBuilder: MQTTSNMessagBuilder = MQTTSNMessagBuilderImpl()

            val handler: NetworkMQTTSNMessageHandler = NetworkMQTTSNMessageHandlerImpl(
                messageBuilder,
                config
            )

            val gateway: MQTTSNGateway = GrizzlyMQTTSNGateway(
                config,
                messageBuilder,
                handler
            )

            try {
                gateway.start()
            } finally {
                gateway.shutdown()
                logger.info("Stopped gateway...")
            }

        }
    }

}