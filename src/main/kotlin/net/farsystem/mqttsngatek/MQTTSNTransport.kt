package net.farsystem.mqttsngatek

import kotlinx.coroutines.flow.Flow
import net.farsystem.mqttsngatek.model.NetworkContext

interface MQTTSNTransport {
    suspend fun send(networkContext: NetworkContext, message: MQTTSNMessage)
    fun receive(receiver: (NetworkContext, MQTTSNMessage) -> Unit)
    fun start()
    fun stop()
}