package net.farsystem.mqttsngatek.mqtt

data class MQTTConnectOptions(
    val isCleanSession: Boolean,
    val keepAliveInterval: Int,
    val version: MQTTVersion,
    val willTopic: String? = null,
    val willPayload: ByteArray = byteArrayOf(),
    val willRetained: Boolean = false,
    val willQos: MQTTQoS = MQTTQoS.ZERO,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MQTTConnectOptions

        if (isCleanSession != other.isCleanSession) return false
        if (keepAliveInterval != other.keepAliveInterval) return false
        if (version != other.version) return false
        if (willTopic != other.willTopic) return false
        if (!willPayload.contentEquals(other.willPayload)) return false
        if (willRetained != other.willRetained) return false
        if (willQos != other.willQos) return false

        return true
    }

    override fun hashCode(): Int {
        var result = isCleanSession.hashCode()
        result = 31 * result + keepAliveInterval
        result = 31 * result + version.hashCode()
        result = 31 * result + (willTopic?.hashCode() ?: 0)
        result = 31 * result + willPayload.contentHashCode()
        result = 31 * result + willRetained.hashCode()
        result = 31 * result + willQos.hashCode()
        return result
    }

}