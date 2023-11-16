package net.farsystem.mqttsngatek

import net.farsystem.mqttsngatek.data.repository.*
import net.farsystem.mqttsngatek.gateway.*
import net.farsystem.mqttsngatek.mqtt.DefaultMQTTPublishHandler
import net.farsystem.mqttsngatek.mqtt.MQTTPublishHandler
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

            val messageBuilder: MQTTSNMessagBuilder = DefaultMQTTSNMessageBuilder()

            val transport: MQTTSNTransport = GrizzlyMQTTSNTransport(config, messageBuilder)

            val mqttsnClientRepository: MQTTSNClientRepository = InMemoryMQTTSNClientRepository()

            val mqttClientRepository: MQTTClientRepository = InMemoryMQTTClientRepository(
                config,
                ::PahoMQTTClient
            )

            val mqttsnTopicRepository: MQTTSNTopicRepository = InMemoryMQTTSNTopicRepository(
                emptyMap()
            )

            val mqttsnPublishRepository: MQTTSNPublishRepository = InMemoryMQTTSNPublishRepository()

            val mqttsnWillRepository: MQTTSNWillRepository = InMemoryMQTTSNWillRepository()

            val outgoingProcessor: MQTTSNMessageProcessor = OutgoingMQTTSNMessageProcessor(
                transport
            )

            val mqttPublishHandler: MQTTPublishHandler = DefaultMQTTPublishHandler(
                messageBuilder,
                mqttsnClientRepository,
                mqttsnTopicRepository,
                mqttsnPublishRepository,
                outgoingProcessor
            )

            val handlerRegistry = MQTTSNMessageHandlerRegistry(
                messageBuilder,
                config,
                mqttsnClientRepository,
                mqttClientRepository,
                mqttsnTopicRepository,
                mqttsnWillRepository,
                mqttsnPublishRepository,
                mqttPublishHandler,
                outgoingProcessor
            )

            val incomingProcessor: MQTTSNMessageProcessor = IncomingMQTTSNMessageProcessor(
                handlerRegistry
            )

            val gateway: MQTTSNGateway = DefaultMQTTSNGateway(
                transport,
                incomingProcessor
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