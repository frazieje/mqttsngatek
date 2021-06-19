package net.farsystem.mqttsngatek

import java.nio.ByteBuffer

data class MQTTSNSearchGw(
    val radius: Int,
): MQTTSNMessage {
    companion object {
        fun fromBuffer(buffer: ByteBuffer): MQTTSNSearchGw {
            return MQTTSNSearchGw(buffer.get().toInt() and 0xFF)
        }
    }

    override fun toBuffer(): ByteBuffer {
        TODO("Not yet implemented")
    }
}