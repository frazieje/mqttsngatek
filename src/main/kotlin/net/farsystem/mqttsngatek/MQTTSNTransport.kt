package net.farsystem.mqttsngatek

import net.farsystem.mqttsngatek.model.NetworkContext

interface MQTTSNTransport {
    fun send(networkContext: NetworkContext, message: MQTTSNMessage)
}