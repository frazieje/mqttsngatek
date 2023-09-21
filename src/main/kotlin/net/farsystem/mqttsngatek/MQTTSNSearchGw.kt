package net.farsystem.mqttsngatek

import java.lang.IllegalArgumentException
import java.nio.ByteBuffer

data class MQTTSNSearchGw(
    val radius: Int,
): MQTTSNBody {

    init {
        val shortVal = radius.toShort()
        if ((shortVal.toInt() and 0xFF) != radius) {
            throw IllegalArgumentException("Radius must fit within a two octets (short)")
        }
    }

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