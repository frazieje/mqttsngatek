package net.farsystem.mqttsngatek

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

data class MQTTSNConnect(
    val cleanSession: Boolean = true,
    val willFlag: Boolean,
    val protocolId: Int = 1,
    val duration: Int,
    val clientId: String
) {
    companion object {
        fun fromBuffer(buffer: ByteBuffer): MQTTSNConnect {
            val flags = buffer.get().toInt() and 0xFF
            val cleanSession = (flags and 0x04) > 0
            val willFlag = (flags and 0x08) > 0
            val protocolId = buffer.get().toInt() and 0xFF
            val duration = buffer.short.toInt() and 0xFFFF
            val bytes = ByteArray(buffer.remaining())
            buffer.get(bytes, 0, bytes.size)
            val clientId = String(bytes, StandardCharsets.UTF_8)
            return MQTTSNConnect(cleanSession, willFlag, protocolId, duration, clientId)
        }
    }
}