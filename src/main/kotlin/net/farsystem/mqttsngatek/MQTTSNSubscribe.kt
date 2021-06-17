package net.farsystem.mqttsngatek

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

data class MQTTSNSubscribe(
    val dup: Boolean,
    val qos: MQTTSNQoS,
    val messageId: Int,
    val topicType: MQTTSNTopicType,
    val topic: String?,
    val topicId: Int?
) {
    companion object {
        fun fromBuffer(buffer: ByteBuffer): MQTTSNSubscribe {
            val flags = buffer.get().toInt() and 0xFF
            val dup = (flags and 0x80) > 0
            val qos = MQTTSNQoS.fromCode((flags and 0x60) shr 5)
            val topicType = MQTTSNTopicType.fromCode(flags and 0x3)
            val messageId = buffer.short.toInt() and 0xFFFF
            return when (topicType) {
                MQTTSNTopicType.NORMAL, MQTTSNTopicType.SHORT_NAME -> {
                    val bytes = ByteArray(buffer.remaining())
                    buffer.get(bytes, 0, bytes.size)
                    MQTTSNSubscribe(dup, qos, messageId, topicType, String(bytes, StandardCharsets.UTF_8), null)
                }
                MQTTSNTopicType.PREDEFINED -> {
                    val topicId = buffer.short.toInt() and 0xFFFF
                    MQTTSNSubscribe(dup, qos, messageId, topicType, null, topicId)
                }
            }
        }
    }
}