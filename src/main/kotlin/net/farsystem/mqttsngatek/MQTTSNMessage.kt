package net.farsystem.mqttsngatek

import java.nio.ByteBuffer

interface MQTTSNMessage {
    fun toBuffer(): ByteBuffer
}