package net.farsystem.mqttsngatek

import net.farsystem.mqttsngatek.model.NetworkContext

interface MQTTSNMessageProcessor {
    suspend fun process(networkContext: NetworkContext, mqttsnMessage: MQTTSNMessage)
}