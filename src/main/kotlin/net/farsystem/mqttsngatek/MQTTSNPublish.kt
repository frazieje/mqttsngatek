package net.farsystem.mqttsngatek

import java.lang.IllegalArgumentException
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

data class MQTTSNPublish(
    val dup: Boolean,
    val retained: Boolean,
    val qos: MQTTSNQoS,
    val messageId: Int,
    val topicType: MQTTSNTopicType,
    val topic: String?,
    val topicId: Int?,
    val payload: ByteArray
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
        topic?.let {
            if (it.length > 2) {
                throw IllegalArgumentException("Short topic must fit within a two octets")
            }
        }
    }

    override fun writeTo(buffer: ByteBuffer): ByteBuffer {
        var flags = 0x0
        if(dup)
            flags = flags or 0x80
        flags = flags or (qos.code shl 5)
        if (retained)
            flags = flags or 0x10
        flags = flags or topicType.code
        buffer.put(flags.toByte())
        when (topicType) {
            MQTTSNTopicType.SHORT_NAME -> {
                buffer.put(topic!!.toByteArray(StandardCharsets.UTF_8))
            }
            MQTTSNTopicType.NORMAL, MQTTSNTopicType.PREDEFINED -> {
                buffer.putShort(topicId!!.toShort())
            }
        }
        buffer.putShort(messageId.toShort())
        buffer.put(payload)
        return buffer
    }

    override fun length(): Int = 5 + payload.size

    companion object {
        fun fromBuffer(buffer: ByteBuffer): MQTTSNPublish {
            val flags = buffer.get().toInt() and 0xFF
            val dup = (flags and 0x80) > 0
            val qos = MQTTSNQoS.fromCode((flags and 0x60) shr 5)
            val retained = (flags and 0x10) > 0
            val topicType = MQTTSNTopicType.fromCode(flags and 0x3)
            return when (topicType) {
                MQTTSNTopicType.NORMAL, MQTTSNTopicType.PREDEFINED -> {
                    val topicId = buffer.short.toInt() and 0xFFFF
                    val (messageId, payloadBytes) = readMessageIdAndPayload(buffer)
                    MQTTSNPublish(dup, retained, qos, messageId, topicType, null, topicId, payloadBytes)
                }
                MQTTSNTopicType.SHORT_NAME -> {
                    val bytes = ByteArray(2)
                    buffer.get(bytes, 0, bytes.size)
                    val (messageId, payloadBytes) = readMessageIdAndPayload(buffer)
                    MQTTSNPublish(
                        dup,
                        retained,
                        qos,
                        messageId,
                        topicType,
                        String(bytes, StandardCharsets.UTF_8),
                        null,
                        payloadBytes
                    )
                }
            }
        }

        private fun readMessageIdAndPayload(buffer: ByteBuffer): Pair<Int, ByteArray> {
            val messageId = buffer.short.toInt() and 0xFFFF
            val payloadBytes = ByteArray(buffer.remaining())
            buffer.get(payloadBytes, 0, payloadBytes.size)
            return Pair(messageId, payloadBytes)
        }
    }
}