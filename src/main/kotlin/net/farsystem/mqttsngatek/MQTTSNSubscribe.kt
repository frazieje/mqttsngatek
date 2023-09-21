package net.farsystem.mqttsngatek

import java.lang.IllegalArgumentException
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

data class MQTTSNSubscribe(
    val dup: Boolean,
    val qos: MQTTSNQoS,
    val messageId: Int,
    val topicType: MQTTSNTopicType,
    val topic: String?,
    val topicId: Int?
): MQTTSNBody {

    init {
        val shortVal = messageId.toShort()
        if ((shortVal.toInt() and 0xFF) != messageId) {
            throw IllegalArgumentException("MessageId must fit within a two octets")
        }
        topicId?.let {
            val topicShort = it.toShort()
            if ((topicShort.toInt() and 0xFF) != it) {
                throw IllegalArgumentException("TopicId must fit within a two octets")
            }
        }
    }

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

    override fun writeTo(buffer: ByteBuffer): ByteBuffer {
        var flags = 0x0
        if(dup)
            flags = flags or 0x80
        flags = flags or (qos.code shl 5)
        flags = flags or topicType.code
        buffer
            .put(flags.toByte())
            .putShort(messageId.toShort())
        when (topicType) {
            MQTTSNTopicType.NORMAL, MQTTSNTopicType.SHORT_NAME -> {
                buffer.put(topic!!.toByteArray(StandardCharsets.UTF_8))
            }
            MQTTSNTopicType.PREDEFINED -> {
                buffer.putShort(topicId!!.toShort())
            }
        }
        return buffer
    }

    override fun length(): Int = 3 + when (topicType) {
        MQTTSNTopicType.NORMAL, MQTTSNTopicType.SHORT_NAME -> {
            topic!!.toByteArray(StandardCharsets.UTF_8).size
        }
        MQTTSNTopicType.PREDEFINED -> {
            2
        }
    }
}