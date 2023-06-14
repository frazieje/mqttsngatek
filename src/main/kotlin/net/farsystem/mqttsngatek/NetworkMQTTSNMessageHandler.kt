package net.farsystem.mqttsngatek

import net.farsystem.mqttsngatek.model.NetworkContext

interface NetworkMQTTSNMessageHandler {
    fun onReceive(
        networkContext: NetworkContext,
        mqttsnMessage: MQTTSNMessage,
        onComplete: (MQTTSNMessage?) -> Unit
    )
}