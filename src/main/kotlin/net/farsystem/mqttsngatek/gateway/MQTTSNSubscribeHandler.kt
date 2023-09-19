package net.farsystem.mqttsngatek.gateway

import net.farsystem.mqttsngatek.MQTTSNMessage
import net.farsystem.mqttsngatek.model.NetworkContext

class MQTTSNSubscribeHandler : MQTTSNMessageHandler {
    override suspend fun handleMessage(message: MQTTSNMessage, networkContext: NetworkContext): MQTTSNMessage? {
        return null
    }
}