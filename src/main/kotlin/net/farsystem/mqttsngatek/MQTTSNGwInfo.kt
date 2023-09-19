package net.farsystem.mqttsngatek

import java.lang.IllegalArgumentException
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

data class MQTTSNGwInfo(
    val gatewayId: Int,
    val gatewayAddress: String?
): MQTTSNBody {

    init {
        val byteVal = gatewayId.toByte()
        if ((byteVal.toInt() and 0xFF) != gatewayId) {
            throw IllegalArgumentException("GatewayId must fit within a single octet")
        }
    }

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

    override fun writeTo(buffer: ByteBuffer): ByteBuffer {
        val address = gatewayAddress?.toByteArray(StandardCharsets.UTF_8)
        buffer.put(gatewayId.toByte())
        address?.run {
            buffer.put(address)
        }
        return buffer
    }

    override fun length(): Int = 1 + (gatewayAddress?.toByteArray(StandardCharsets.UTF_8)?.size ?: 0)
}