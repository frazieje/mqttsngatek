package net.farsystem.mqttsngatek

import java.nio.ByteBuffer

data class MQTTSNSearchGW(
    val radius: Int
) {
    companion object {
        fun fromBuffer(buffer: ByteBuffer): MQTTSNSearchGW {
            return MQTTSNSearchGW(buffer.get().toInt() and 0xFF)
        }
    }
}