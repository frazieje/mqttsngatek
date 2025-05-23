package net.farsystem.mqttsngatek

import java.nio.ByteBuffer
import kotlin.jvm.Throws

interface MQTTSNMessagBuilder {
    @Throws(ByteBufferTooShortException::class)
    fun decode(buffer: ByteBuffer): MQTTSNMessage
    fun createMessage(type: MQTTSNMessageType, body: MQTTSNBody): MQTTSNMessage
}