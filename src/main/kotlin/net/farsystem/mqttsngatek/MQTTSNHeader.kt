package net.farsystem.mqttsngatek

import java.nio.ByteBuffer

interface MQTTSNHeader {
    val messageLength: Int
    val messagteType: MQTTSNMessageType
    fun toBuffer(): ByteBuffer
}