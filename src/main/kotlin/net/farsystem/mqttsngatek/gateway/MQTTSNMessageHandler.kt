package net.farsystem.mqttsngatek.gateway

import net.farsystem.mqttsngatek.MQTTSNBody
import net.farsystem.mqttsngatek.MQTTSNMessage
import net.farsystem.mqttsngatek.model.NetworkContext

interface MQTTSNMessageHandler {
    suspend fun handleMessage(networkContext: NetworkContext, message: MQTTSNMessage): MQTTSNMessage?
}