package net.farsystem.mqttsngatek

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

data class MQTTSNPingReq(val clientId: String?): MQTTSNBody {
    companion object {
        fun fromBuffer(buffer: ByteBuffer): MQTTSNPingReq {
            val clientId = if (buffer.hasRemaining()) {
                val bytes = ByteArray(buffer.remaining())
                buffer.get(bytes, 0, bytes.size)
                String(bytes, StandardCharsets.UTF_8)
            } else null
            return MQTTSNPingReq(clientId)
        }
    }

    override fun writeTo(buffer: ByteBuffer): ByteBuffer {
        return clientId?.run {
            return buffer.put(toByteArray(StandardCharsets.UTF_8))
        } ?: buffer
    }

    override fun length(): Int {
        return clientId?.run {
            toByteArray(StandardCharsets.UTF_8).size
        } ?: 0
    }
}