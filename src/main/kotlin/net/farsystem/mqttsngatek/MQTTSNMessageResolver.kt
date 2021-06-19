package net.farsystem.mqttsngatek

import java.nio.ByteBuffer
import kotlin.jvm.Throws

interface MQTTSNMessageResolver {
    @Throws(ByteBufferTooShortException::class)
    fun resolve(buffer: ByteBuffer): MQTTSNPacket
}