package net.farsystem.mqttsngatek

import net.farsystem.mqttsngatek.model.NetworkContext
import org.slf4j.LoggerFactory

class OutgoingMQTTSNMessageProcessor(
    private val mqttsnTransport: MQTTSNTransport
) : MQTTSNMessageProcessor {

    private val logger = LoggerFactory.getLogger(this::class.simpleName)

    override suspend fun process(networkContext: NetworkContext, mqttsnMessage: MQTTSNMessage) {
        //TODO: Implement retransmission / queueing
        //TODO: Implement transactions??
        logger.debug("process outgoing message $mqttsnMessage to $networkContext")
        mqttsnTransport.send(networkContext, mqttsnMessage)
    }
}