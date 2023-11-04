package net.farsystem.mqttsngatek

import java.nio.ByteBuffer

data class MQTTSNPubRel(val messageId: Int): MQTTSNBody {
    companion object {
        fun fromBuffer(buffer: ByteBuffer): MQTTSNPubRel {
            val messageId = buffer.short.toInt() and 0xFFFF
            return MQTTSNPubRel(messageId)
        }
    }

    override fun writeTo(buffer: ByteBuffer): ByteBuffer {
        buffer.putShort(messageId.toShort())
        return buffer
    }

    override fun length(): Int = 2
}
