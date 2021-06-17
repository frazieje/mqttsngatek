package net.farsystem.mqttsngatek

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

data class MQTTSNRegister(
    val topicId: Int,
    val messageId: Int,
    val topic: String
) : MQTTSNMessage<MQTTSNRegister> {
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

    override fun fromBuffer(buffer: ByteBuffer): MQTTSNRegister {
        TODO("Not yet implemented")
    }
}
