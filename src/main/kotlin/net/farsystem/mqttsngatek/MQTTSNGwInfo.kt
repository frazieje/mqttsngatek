package net.farsystem.mqttsngatek

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

data class MQTTSNGwInfo(
    val gatewayId: Int,
    val gatewayAddress: String?
): MQTTSNMessage {
    companion object {
        fun fromBuffer(buffer: ByteBuffer): MQTTSNGwInfo {
            val gatewayId = buffer.get().toInt() and 0xFF
            val gatewayAddress = if (buffer.remaining() > 0) {
                val bytes = ByteArray(buffer.remaining())
                buffer.get(bytes, 0, bytes.size)
                String(bytes, StandardCharsets.UTF_8)
            } else null
            return MQTTSNGwInfo(gatewayId, gatewayAddress)
        }
    }

    override fun toBuffer(): ByteBuffer {
        val address = gatewayAddress?.toByteArray(StandardCharsets.UTF_8)
        val buffer = ByteBuffer.allocate(1 + (address?.size ?: 0))
        buffer.put(gatewayId.toByte())
        buffer.put(address)
        return buffer
    }
}