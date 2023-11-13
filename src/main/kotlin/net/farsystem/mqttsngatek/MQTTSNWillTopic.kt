package net.farsystem.mqttsngatek

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

data class MQTTSNWillTopic(
    val qos: MQTTSNQoS?,
    val retained: Boolean?,
    val topic: String?
) : MQTTSNBody {
    override fun writeTo(buffer: ByteBuffer): ByteBuffer {
        if (qos != null) {
            var flags = 0x0
            flags = flags or (qos.code shl 5)
            if (retained!!)
                flags = flags or 0x10
            buffer.put(flags.toByte())
            buffer.put(topic!!.toByteArray(StandardCharsets.UTF_8))
        }
        return buffer
    }

    override fun length() =
        if (qos != null) {
            1 + topic!!.toByteArray(StandardCharsets.UTF_8).size
        } else 0

    companion object {
        fun fromBuffer(buffer: ByteBuffer): MQTTSNWillTopic {
            return if (buffer.remaining() > 0) {
                val flags = buffer.get().toInt() and 0xFF
                val qos = MQTTSNQoS.fromCode((flags and 0x60) shr 5)
                val retained = (flags and 0x10) > 0
                val bytes = ByteArray(buffer.remaining())
                buffer.get(bytes, 0, bytes.size)
                MQTTSNWillTopic(qos, retained, String(bytes, StandardCharsets.UTF_8))
            } else {
                MQTTSNWillTopic(null, null, null)
            }
        }
    }
}
