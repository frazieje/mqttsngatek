package net.farsystem.mqttsngatek

import java.nio.ByteBuffer

class MQTTSNWillTopicReq : MQTTSNBody {
    override fun writeTo(buffer: ByteBuffer): ByteBuffer = buffer
    override fun length(): Int = 0
    companion object {
        fun fromBuffer(buffer: ByteBuffer): MQTTSNWillTopicReq {
            return MQTTSNWillTopicReq()
        }
    }
}