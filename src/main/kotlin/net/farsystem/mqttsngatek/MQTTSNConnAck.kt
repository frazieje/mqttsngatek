package net.farsystem.mqttsngatek

import java.nio.ByteBuffer

data class MQTTSNConnAck(val returnCode: MQTTSNReturnCode): MQTTSNBody {
    override fun writeTo(buffer: ByteBuffer): ByteBuffer = buffer.put(returnCode.code.toByte())
    override fun length(): Int = 1
}