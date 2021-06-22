package net.farsystem.mqttsngatek

import java.nio.ByteBuffer

interface MQTTSNPacket {
    val header: MQTTSNHeader
    val message: MQTTSNMessage
    fun toBuffer(): ByteBuffer
    fun length() = header.messageLength
}