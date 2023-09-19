package net.farsystem.mqttsngatek

import net.farsystem.mqttsngatek.data.repository.InMemoryMQTTClientRepository
import net.farsystem.mqttsngatek.data.repository.InMemoryMQTTSNClientRepository
import net.farsystem.mqttsngatek.data.repository.MQTTClientRepository
import net.farsystem.mqttsngatek.data.repository.MQTTSNClientRepository
import net.farsystem.mqttsngatek.gateway.GrizzlyMQTTSNGateway
import net.farsystem.mqttsngatek.gateway.MQTTSNGateway
import net.farsystem.mqttsngatek.mqtt.MQTTClient
import net.farsystem.mqttsngatek.mqtt.MQTTClientFactory
import net.farsystem.mqttsngatek.mqtt.paho.PahoMQTTClient
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

            val mqttsnClientRepository: MQTTSNClientRepository = InMemoryMQTTSNClientRepository()

            val mqttClientRepository: MQTTClientRepository = InMemoryMQTTClientRepository(
                config,
                ::PahoMQTTClient
            )

            val handler: NetworkMQTTSNMessageHandler = NetworkMQTTSNMessageHandlerImpl(
                messageBuilder,
                config,
                mqttsnClientRepository,
                mqttClientRepository
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