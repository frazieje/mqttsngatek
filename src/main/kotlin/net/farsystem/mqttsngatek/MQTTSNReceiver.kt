package net.farsystem.mqttsngatek

import net.farsystem.mqttsngatek.model.NetworkContext

interface MQTTSNReceiver {
    fun receive(networkContext: NetworkContext, message: MQTTSNMessage)
}