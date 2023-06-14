package net.farsystem.mqttsngatek

import java.nio.ByteBuffer

class MQTTSNPingResp : MQTTSNBody {
    override fun writeTo(buffer: ByteBuffer): ByteBuffer = buffer
    override fun length(): Int = 0
    companion object {
        fun fromBuffer(buffer: ByteBuffer): MQTTSNPingResp {
            return MQTTSNPingResp()
        }
    }
}