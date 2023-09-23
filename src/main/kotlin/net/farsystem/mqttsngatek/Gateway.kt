package net.farsystem.mqttsngatek

import net.farsystem.mqttsngatek.data.repository.*
import net.farsystem.mqttsngatek.gateway.*
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

            val mqttsnTopicRepository: MQTTSNTopicRepository = InMemoryMQTTSNTopicRepository(
                emptyMap()
            )

            val handlerRegistry = MQTTSNMessageHandlerRegistry(
                messageBuilder,
                config,
                mqttsnClientRepository,
                mqttClientRepository,
                mqttsnTopicRepository
            )

            val handler: NetworkMQTTSNMessageHandler = NetworkMQTTSNMessageHandlerImpl(
                handlerRegistry,
                GrizzlyMQTTSNMessageSender(messageBuilder)
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