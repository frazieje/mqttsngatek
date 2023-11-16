package net.farsystem.mqttsngatek

import net.farsystem.mqttsngatek.data.repository.*
import net.farsystem.mqttsngatek.gateway.*
import net.farsystem.mqttsngatek.gateway.handler.*
import net.farsystem.mqttsngatek.mqtt.MQTTPublishHandler

class MQTTSNMessageHandlerRegistry(
    messageBuilder: MQTTSNMessagBuilder,
    gatewayConfig: GatewayConfig,
    mqttsnClientRepository: MQTTSNClientRepository,
    mqttClientRepository: MQTTClientRepository,
    mqttsnTopicRepository: MQTTSNTopicRepository,
    mqttsnWillRepository: MQTTSNWillRepository,
    mqttsnPublishRepository: MQTTSNPublishRepository,
    publishHandler: MQTTPublishHandler,
    outgoingProcessor: MQTTSNMessageProcessor,
) {
    private val handlers = mutableMapOf(
        MQTTSNMessageType.SEARCHGW to MQTTSNSearchGwHandler(messageBuilder, gatewayConfig, outgoingProcessor),
        MQTTSNMessageType.CONNECT to MQTTSNConnectHandler(
            messageBuilder,
            mqttsnClientRepository,
            mqttClientRepository,
            mqttsnWillRepository,
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
        ),
        MQTTSNMessageType.REGISTER to MQTTSNRegisterHandler(
            messageBuilder,
            mqttsnClientRepository,
            mqttsnTopicRepository,
            outgoingProcessor
        ),
        MQTTSNMessageType.PUBLISH to MQTTSNPublishHandler(
            messageBuilder,
            mqttsnClientRepository,
            mqttsnTopicRepository,
            mqttClientRepository,
            outgoingProcessor
        ),
        MQTTSNMessageType.PUBACK to MQTTSNPubAckHandler(
            mqttsnClientRepository, mqttClientRepository
        ),
        MQTTSNMessageType.UNSUBSCRIBE to MQTTSNUnsubscribeHandler(
            messageBuilder,
            mqttsnClientRepository,
            mqttClientRepository,
            mqttsnTopicRepository,
            outgoingProcessor
        ),
        MQTTSNMessageType.PUBREL to MQTTSNPubRelHandler(
            messageBuilder,
            mqttsnClientRepository,
            mqttClientRepository,
            outgoingProcessor
        ),
        MQTTSNMessageType.PUBREC to MQTTSNPubRecHandler(
            messageBuilder,
            mqttsnClientRepository,
            mqttClientRepository,
            outgoingProcessor
        ),
        MQTTSNMessageType.PUBCOMP to MQTTSNPubAckHandler(
            mqttsnClientRepository, mqttClientRepository
        ),
        MQTTSNMessageType.PINGRESP to MQTTSNPingRespHandler(
            mqttsnClientRepository,
            mqttClientRepository
        ),
        MQTTSNMessageType.REGACK to MQTTSNRegAckHandler(
            messageBuilder,
            mqttsnClientRepository,
            mqttsnPublishRepository,
            outgoingProcessor
        ),
        MQTTSNMessageType.PINGREQ to MQTTSNPingReqHandler(
            messageBuilder,
            mqttsnClientRepository,
            mqttClientRepository,
            outgoingProcessor
        ),
        MQTTSNMessageType.PINGRESP to MQTTSNPingRespHandler(
            mqttsnClientRepository,
            mqttClientRepository
        ),
        MQTTSNMessageType.WILLMSG to MQTTSNWillMsgHandler(
            messageBuilder,
            mqttsnClientRepository,
            mqttsnWillRepository,
            mqttClientRepository,
            outgoingProcessor
        ),
        MQTTSNMessageType.WILLTOPIC to MQTTSNWillTopicHandler(
            messageBuilder,
            mqttsnClientRepository,
            mqttsnWillRepository,
            outgoingProcessor
        )
    )
    fun register(messageType: MQTTSNMessageType, handler: MQTTSNMessageHandler) = handlers.set(messageType, handler)
    fun resolve(messageType: MQTTSNMessageType): MQTTSNMessageHandler? = handlers[messageType]
}