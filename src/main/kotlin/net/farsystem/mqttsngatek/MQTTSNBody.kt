package net.farsystem.mqttsngatek

import java.nio.ByteBuffer

interface MQTTSNBody {
    fun writeTo(buffer: ByteBuffer): ByteBuffer
    fun length(): Int
}