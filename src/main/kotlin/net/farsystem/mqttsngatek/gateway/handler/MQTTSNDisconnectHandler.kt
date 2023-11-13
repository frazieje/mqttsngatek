package net.farsystem.mqttsngatek.gateway.handler

import net.farsystem.mqttsngatek.MQTTSNMessage
import net.farsystem.mqttsngatek.gateway.MQTTSNMessageHandler
import net.farsystem.mqttsngatek.model.NetworkContext

class MQTTSNDisconnectHandler : MQTTSNMessageHandler {
    override suspend fun handleMessage(networkContext: NetworkContext, message: MQTTSNMessage) {
        TODO("Not yet implemented")
    }
}