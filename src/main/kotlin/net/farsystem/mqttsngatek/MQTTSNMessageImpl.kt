package net.farsystem.mqttsngatek

import java.nio.ByteBuffer

class MQTTSNMessageImpl(override val header: MQTTSNHeader, override val body: MQTTSNBody) : MQTTSNMessage {
    override fun writeTo(buffer: ByteBuffer) = body.writeTo(header.writeTo(buffer))
}