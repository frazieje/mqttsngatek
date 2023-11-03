package net.farsystem.mqttsngatek

import net.farsystem.mqttsngatek.data.repository.MQTTClientRepository
import net.farsystem.mqttsngatek.data.repository.MQTTSNClientRepository
import net.farsystem.mqttsngatek.data.repository.MQTTSNTopicRepository
import net.farsystem.mqttsngatek.gateway.*
import net.farsystem.mqttsngatek.gateway.handlers.MQTTSNConnectHandler
import net.farsystem.mqttsngatek.gateway.handlers.MQTTSNPingReqHandler
import net.farsystem.mqttsngatek.gateway.handlers.MQTTSNSearchGwHandler
import net.farsystem.mqttsngatek.gateway.handlers.MQTTSNSubscribeHandler
import net.farsystem.mqttsngatek.mqtt.MQTTPublishHandler

class MQTTSNMessageHandlerRegistry(
    messageBuilder: MQTTSNMessagBuilder,
    gatewayConfig: GatewayConfig,
    mqttsnClientRepository: MQTTSNClientRepository,
    mqttClientRepository: MQTTClientRepository,
    mqttsnTopicRepository: MQTTSNTopicRepository,
    publishHandler: MQTTPublishHandler,
    outgoingProcessor: MQTTSNMessageProcessor,
) {
    private val handlers = mutableMapOf(
        MQTTSNMessageType.SEARCHGW to MQTTSNSearchGwHandler(messageBuilder, gatewayConfig, outgoingProcessor),
        MQTTSNMessageType.CONNECT to MQTTSNConnectHandler(
            messageBuilder,
            mqttsnClientRepository,
            mqttClientRepository,
            outgoingProcessor
        ),
        MQTTSNMessageType.PINGREQ to MQTTSNPingReqHandler(
            messageBuilder,
            mqttsnClientRepository,
            mqttClientRepository,
            outgoingProcessor
        ),
        MQTTSNMessageType.SUBSCRIBE to MQTTSNSubscribeHandler(
            messageBuilder,
            mqttsnClientRepository,
            mqttClientRepository,
            mqttsnTopicRepository,
            publishHandler,
            outgoingProcessor
        )
    )
    fun register(messageType: MQTTSNMessageType, handler: MQTTSNMessageHandler) = handlers.set(messageType, handler)
    fun resolve(messageType: MQTTSNMessageType): MQTTSNMessageHandler? = handlers[messageType]
}