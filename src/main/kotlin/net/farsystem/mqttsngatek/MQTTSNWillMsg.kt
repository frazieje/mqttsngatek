package net.farsystem.mqttsngatek

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

data class MQTTSNWillMsg(
    val message: ByteArray
) : MQTTSNBody {
    override fun writeTo(buffer: ByteBuffer): ByteBuffer {
        buffer.put(message)
        return buffer
    }

    override fun length() = message.size

    companion object {
        fun fromBuffer(buffer: ByteBuffer): MQTTSNWillMsg {
            val bytes = ByteArray(buffer.remaining())
            buffer.get(bytes, 0, bytes.size)
            return MQTTSNWillMsg(bytes)
        }
    }
}