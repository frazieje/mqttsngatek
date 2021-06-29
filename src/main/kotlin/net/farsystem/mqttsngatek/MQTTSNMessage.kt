package net.farsystem.mqttsngatek

import java.nio.ByteBuffer

interface MQTTSNMessage {
    val header: MQTTSNHeader
    val body: MQTTSNBody
    fun writeTo(buffer: ByteBuffer): ByteBuffer
    fun length() = header.messageLength
}