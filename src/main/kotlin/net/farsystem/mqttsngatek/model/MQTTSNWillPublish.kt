package net.farsystem.mqttsngatek.model

import net.farsystem.mqttsngatek.MQTTSNQoS

data class MQTTSNWillPublish(
    val qos: MQTTSNQoS,
    val retained: Boolean,
    val topic: String,
    val payload: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MQTTSNWillPublish

        if (qos != other.qos) return false
        if (retained != other.retained) return false
        if (topic != other.topic) return false
        if (!payload.contentEquals(other.payload)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = qos.hashCode()
        result = 31 * result + retained.hashCode()
        result = 31 * result + topic.hashCode()
        result = 31 * result + payload.contentHashCode()
        return result
    }
}
