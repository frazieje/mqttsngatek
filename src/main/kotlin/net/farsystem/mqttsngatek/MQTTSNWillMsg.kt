package net.farsystem.mqttsngatek

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

data class MQTTSNWillMsg(
    val message: String
) : MQTTSNBody {
    override fun writeTo(buffer: ByteBuffer): ByteBuffer {
        buffer.put(message.toByteArray(StandardCharsets.UTF_8))
        return buffer
    }

    override fun length() = message.toByteArray(StandardCharsets.UTF_8).size

    companion object {
        fun fromBuffer(buffer: ByteBuffer): MQTTSNWillMsg {
            val bytes = ByteArray(buffer.remaining())
            buffer.get(bytes, 0, bytes.size)
            return MQTTSNWillMsg(String(bytes, StandardCharsets.UTF_8))
        }
    }
}