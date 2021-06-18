package net.farsystem.mqttsngatek

import java.nio.ByteBuffer

data class MQTTSNSearchGw(
    val radius: Int
) {
    companion object {
        fun fromBuffer(buffer: ByteBuffer): MQTTSNSearchGw {
            return MQTTSNSearchGw(buffer.get().toInt() and 0xFF)
        }
    }
}