package net.farsystem.mqttsngatek

import net.farsystem.mqttsngatek.model.NetworkContext
import org.glassfish.grizzly.Grizzly
import org.glassfish.grizzly.attributes.AttributeStorage

class MQTTSNContext(
    private val contextStorage: AttributeStorage,
    val message: MQTTSNMessage
) : AttributeStorage by contextStorage {

    private val messageProcessedAttributte = Grizzly.DEFAULT_ATTRIBUTE_BUILDER.createAttribute<Boolean>("messageProcessed")
    private val responseAttribute = Grizzly.DEFAULT_ATTRIBUTE_BUILDER.createAttribute<MQTTSNMessage?>("responseMessage")

    val isProcessed: Boolean
        get() = messageProcessedAttributte.get(this) ?: false

    var response: MQTTSNMessage?
        get() = responseAttribute.get(this)
        set(responseMessage) {
            responseAttribute.set(this, responseMessage)
            messageProcessedAttributte.set(this, true)
        }

    fun reset() {
        messageProcessedAttributte.remove(this)
        responseAttribute.remove(this)
    }
}