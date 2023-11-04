package net.farsystem.mqttsngatek

import java.nio.ByteBuffer

data class MQTTSNPubAck(
    val messageId: Int,
    val topicId: Int = 0,
    val returnCode: MQTTSNReturnCode
) : MQTTSNBody {
        companion object {
            fun fromBuffer(buffer: ByteBuffer): MQTTSNPubAck {
                val topicIdRaw = buffer.short.toInt() and 0xFFFF
                val messageId = buffer.short.toInt() and 0xFFFF
                val rc = MQTTSNReturnCode.fromCode(buffer.get().toInt() and 0xFF)
                val topicId = if (rc == MQTTSNReturnCode.ACCEPTED) {
                    topicIdRaw
                } else {
                    0
                }
                return MQTTSNPubAck(messageId, topicId, rc)
            }
        }

        override fun writeTo(buffer: ByteBuffer): ByteBuffer {
            buffer.putShort(topicId.toShort())
                .putShort(messageId.toShort())
                .put(returnCode.code.toByte())
            return buffer
        }

        override fun length(): Int = 5
    }