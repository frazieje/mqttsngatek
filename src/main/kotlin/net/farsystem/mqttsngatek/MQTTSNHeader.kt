package net.farsystem.mqttsngatek

import java.nio.ByteBuffer

interface MQTTSNHeader {
    val messageLength: Int
    val messageType: MQTTSNMessageType
    fun writeTo(buffer: ByteBuffer): ByteBuffer
}