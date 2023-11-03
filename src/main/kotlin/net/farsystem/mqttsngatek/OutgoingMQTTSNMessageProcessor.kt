package net.farsystem.mqttsngatek

import net.farsystem.mqttsngatek.model.NetworkContext

class OutgoingMQTTSNMessageProcessor(
    private val mqttsnTransport: MQTTSNTransport
) : MQTTSNMessageProcessor {
    override suspend fun process(networkContext: NetworkContext, mqttsnMessage: MQTTSNMessage) {
        //TODO: Implement retransmission / queueing
        //TODO: Implement transactions??
        mqttsnTransport.send(networkContext, mqttsnMessage)
    }
}