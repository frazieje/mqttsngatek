package net.farsystem.mqttsngatek

import net.farsystem.mqttsngatek.data.repository.*
import net.farsystem.mqttsngatek.gateway.*
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

            val mqttsnTopicRepository: MQTTSNTopicRepository = InMemoryMQTTSNTopicRepository(
                emptyMap()
            )

            val classMap: Map<MQTTSNMessageType, MQTTSNMessageHandler> = hashMapOf(
                MQTTSNMessageType.SEARCHGW to MQTTSNSearchGwHandler(messageBuilder, config),
                MQTTSNMessageType.CONNECT to MQTTSNConnectHandler(messageBuilder, mqttsnClientRepository, mqttClientRepository),
                MQTTSNMessageType.PINGREQ to MQTTSNPingReqHandler(messageBuilder, mqttsnClientRepository, mqttClientRepository),
                MQTTSNMessageType.SUBSCRIBE to MQTTSNSubscribeHandler(
                    messageBuilder,
                    mqttsnClientRepository,
                    mqttClientRepository,
                    mqttsnTopicRepository
                )
            )

            val handler: NetworkMQTTSNMessageHandler = NetworkMQTTSNMessageHandlerImpl(
                classMap,
                NetworkMQTTSNMessageSenderImpl(messageBuilder)
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