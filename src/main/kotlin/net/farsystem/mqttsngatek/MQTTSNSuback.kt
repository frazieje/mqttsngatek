package net.farsystem.mqttsngatek

import java.nio.ByteBuffer

data class MQTTSNSubAck(
    val qos: MQTTSNQoS,
    val messageId: Int,
    val topicId: Int = 0,
    val returnCode: MQTTSNReturnCode
): MQTTSNBody {
    companion object {
        fun fromBuffer(buffer: ByteBuffer): MQTTSNSubAck {
            val flags = buffer.get().toInt() and 0xFF
            val qos = MQTTSNQoS.fromCode((flags and 0x60) shr 5)
            val topicIdRaw = buffer.short.toInt() and 0xFFFF
            val messageId = buffer.short.toInt() and 0xFFFF
            val rc = MQTTSNReturnCode.fromCode(buffer.get().toInt() and 0xFF)
            val topicId = if (rc == MQTTSNReturnCode.ACCEPTED) {
                topicIdRaw
            } else {
                0
            }
            return MQTTSNSubAck(qos, messageId, topicId, rc)
        }
    }

    override fun writeTo(buffer: ByteBuffer): ByteBuffer {
        var flags = 0x0
        flags = flags or (qos.code shl 5)
        buffer.put(flags.toByte())
            .putShort(topicId.toShort())
            .putShort(messageId.toShort())
            .put(returnCode.code.toByte())
        return buffer
    }

    override fun length(): Int = 6
}