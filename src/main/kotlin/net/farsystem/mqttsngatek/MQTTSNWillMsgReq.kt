package net.farsystem.mqttsngatek

import java.nio.ByteBuffer

class MQTTSNWillMsgReq : MQTTSNBody {
    override fun writeTo(buffer: ByteBuffer): ByteBuffer = buffer
    override fun length(): Int = 0
    companion object {
        fun fromBuffer(buffer: ByteBuffer): MQTTSNWillMsgReq {
            return MQTTSNWillMsgReq()
        }
    }
}