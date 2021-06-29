package net.farsystem.mqttsngatek

import java.nio.ByteBuffer

class MQTTSNRegAck(
    val topicId: Int,
    val messageId: Int,
    val returnCode: MQTTSNReturnCode
): MQTTSNBody {
    companion object {
        fun fromBuffer(buffer: ByteBuffer): MQTTSNRegAck {
            val topicId = buffer.short.toInt() and 0xFFFF
            val messageId = buffer.short.toInt() and 0xFFFF
            val returnCode = MQTTSNReturnCode.fromCode(buffer.get().toInt() and 0xFF)
            return MQTTSNRegAck(topicId, messageId, returnCode)
        }
    }

    override fun writeTo(buffer: ByteBuffer): ByteBuffer {
        return buffer
            .putShort(topicId.toShort())
            .putShort(messageId.toShort())
            .put(returnCode.code.toByte())
    }

    override fun length(): Int = 5

}