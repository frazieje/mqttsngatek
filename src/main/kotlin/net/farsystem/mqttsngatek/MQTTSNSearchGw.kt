package net.farsystem.mqttsngatek

import java.nio.ByteBuffer

data class MQTTSNSearchGw(
    val radius: Int,
): MQTTSNBody {
    companion object {
        fun fromBuffer(buffer: ByteBuffer): MQTTSNSearchGw {
            return MQTTSNSearchGw(buffer.get().toInt() and 0xFF)
        }
    }

    override fun writeTo(buffer: ByteBuffer): ByteBuffer {
        return buffer.put(radius.toByte())
    }

    override fun length(): Int = 1

}