package net.farsystem.mqttsngatek

import net.farsystem.mqttsngatek.model.NetworkContext

interface NetworkMQTTSNMessageSender {
    suspend fun send(networkContext: NetworkContext, mqttsnMessage: MQTTSNMessage)
}