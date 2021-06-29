package net.farsystem.mqttsngatek

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

data class MQTTSNRegister(
    val topicId: Int,
    val messageId: Int,
    val topic: String
): MQTTSNBody {
    companion object {
        fun fromBuffer(buffer: ByteBuffer): MQTTSNRegister {
            val topicId = buffer.short.toInt() and 0xFFFF
            val messageId = buffer.short.toInt() and 0xFFFF
            val bytes = ByteArray(buffer.remaining())
            buffer.get(bytes, 0, bytes.size)
            val topic = String(bytes, StandardCharsets.UTF_8)
            return MQTTSNRegister(topicId, messageId, topic)
        }
    }

    override fun writeTo(buffer: ByteBuffer): ByteBuffer {
        return buffer
            .putShort(topicId.toShort())
            .putShort(messageId.toShort())
            .put(topic.toByteArray(StandardCharsets.UTF_8))
    }

    override fun length(): Int = 4 + topic.toByteArray(StandardCharsets.UTF_8).size

}
