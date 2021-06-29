package net.farsystem.mqttsngatek

import java.nio.ByteBuffer

class MQTTSNHeaderImpl(override val messageType: MQTTSNMessageType, override val messageLength: Int) : MQTTSNHeader {
    override fun writeTo(buffer: ByteBuffer): ByteBuffer {
        return if (messageLength <= 255) {
            buffer.put(messageLength.toByte())
        } else {
            buffer
                .put(0x01)
                .putShort(messageLength.toShort())
        }.put(messageType.code.toByte())
    }
}