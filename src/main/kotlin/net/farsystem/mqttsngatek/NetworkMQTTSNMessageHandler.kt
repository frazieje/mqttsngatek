package net.farsystem.mqttsngatek

import net.farsystem.mqttsngatek.model.NetworkContext

interface NetworkMQTTSNMessageHandler {
    fun receive(
        networkContext: NetworkContext,
        mqttsnMessage: MQTTSNMessage
    )
}