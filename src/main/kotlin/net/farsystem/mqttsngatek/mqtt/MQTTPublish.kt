package net.farsystem.mqttsngatek.mqtt

data class MQTTPublish(
    val topic: String,
    val qos: MQTTQoS,
    val retained: Boolean = false,
    val dup: Boolean = false,
    val messageId: Int,
    val payload: ByteArray
) : MQTTMessage {
    override val type: MQTTMessageType = MQTTMessageType.PUBLISH
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MQTTPublish

        if (topic != other.topic) return false
        if (qos != other.qos) return false
        if (retained != other.retained) return false
        if (dup != other.dup) return false
        if (messageId != other.messageId) return false
        if (!payload.contentEquals(other.payload)) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = topic.hashCode()
        result = 31 * result + qos.hashCode()
        result = 31 * result + retained.hashCode()
        result = 31 * result + dup.hashCode()
        result = 31 * result + messageId
        result = 31 * result + payload.contentHashCode()
        result = 31 * result + type.hashCode()
        return result
    }
}
