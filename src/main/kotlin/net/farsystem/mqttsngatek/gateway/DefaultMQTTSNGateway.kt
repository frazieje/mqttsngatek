package net.farsystem.mqttsngatek.gateway

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import net.farsystem.mqttsngatek.*
import net.farsystem.mqttsngatek.model.NetworkContext.Companion.flip
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class DefaultMQTTSNGateway(
    private val mqttsnTransport: MQTTSNTransport,
    private val processor: MQTTSNMessageProcessor
): MQTTSNGateway {

    private val logger: Logger = LoggerFactory.getLogger(Gateway::class.java)

    private val gatewayScope = CoroutineScope(Dispatchers.IO)

    override fun start() {
        logger.debug("Starting MQTTSN Gateway...")
        mqttsnTransport.start()
        logger.debug("Subscribing to gateway messages")
        mqttsnTransport.receive { networkContext, mqttsnMessage ->
            gatewayScope.launch {
                processor.process(networkContext, mqttsnMessage)?.let {
                    mqttsnTransport.send(networkContext.flip(), it)
                }
            }
        }
        //TODO: Handle advertisement
//        gatewayScope.launch {
//            while(true) {
//                //gateway advertisement
//                //delay()
//            }
//        }
    }

    override fun shutdown() {
        mqttsnTransport.stop()
        gatewayScope.cancel()
    }
}