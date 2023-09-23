package net.farsystem.mqttsngatek

import net.farsystem.mqttsngatek.data.repository.MQTTClientRepository
import net.farsystem.mqttsngatek.data.repository.MQTTSNClientRepository
import net.farsystem.mqttsngatek.data.repository.MQTTSNTopicRepository
import net.farsystem.mqttsngatek.gateway.*

class MQTTSNMessageHandlerRegistry(
    messageBuilder: MQTTSNMessagBuilder,
    gatewayConfig: GatewayConfig,
    mqttsnClientRepository: MQTTSNClientRepository,
    mqttClientRepository: MQTTClientRepository,
    mqttsnTopicRepository: MQTTSNTopicRepository
) {
    private val handlers = mutableMapOf(
        MQTTSNMessageType.SEARCHGW to MQTTSNSearchGwHandler(messageBuilder, gatewayConfig),
        MQTTSNMessageType.CONNECT to MQTTSNConnectHandler(messageBuilder, mqttsnClientRepository, mqttClientRepository),
        MQTTSNMessageType.PINGREQ to MQTTSNPingReqHandler(messageBuilder, mqttsnClientRepository, mqttClientRepository),
        MQTTSNMessageType.SUBSCRIBE to MQTTSNSubscribeHandler(
            messageBuilder,
            mqttsnClientRepository,
            mqttClientRepository,
            mqttsnTopicRepository
        )
    )
    fun register(messageType: MQTTSNMessageType, handler: MQTTSNMessageHandler) = handlers.set(messageType, handler)
    fun resolve(messageType: MQTTSNMessageType): MQTTSNMessageHandler? = handlers[messageType]
}