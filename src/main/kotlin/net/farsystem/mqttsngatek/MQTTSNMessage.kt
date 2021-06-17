package net.farsystem.mqttsngatek

import java.nio.ByteBuffer

interface MQTTSNMessage<T> {
    fun fromBuffer(buffer: ByteBuffer): T
}