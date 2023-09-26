package net.farsystem.mqttsngatek

import java.nio.ByteBuffer

data class DefaultMQTTSNMessage(override val header: MQTTSNHeader, override val body: MQTTSNBody) : MQTTSNMessage {
    override fun writeTo(buffer: ByteBuffer) = body.writeTo(header.writeTo(buffer))
    override fun toString(): String =
        "[${header.messageType} (${header.messageLength} bytes)]"
}