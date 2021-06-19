package net.farsystem.mqttsngatek

import java.nio.ByteBuffer

class MQTTSNRegAck(
    val topicId: Int,
    val messageId: Int,
    val returnCode: MQTTSNReturnCode
): MQTTSNMessage {
    companion object {
        fun fromBuffer(buffer: ByteBuffer): MQTTSNRegAck {
            val topicId = buffer.short.toInt() and 0xFFFF
            val messageId = buffer.short.toInt() and 0xFFFF
            val returnCode = MQTTSNReturnCode.fromCode(buffer.get().toInt() and 0xFF)
            return MQTTSNRegAck(topicId, messageId, returnCode)
        }
    }

    override fun toBuffer(): ByteBuffer {
        TODO("Not yet implemented")
    }
}