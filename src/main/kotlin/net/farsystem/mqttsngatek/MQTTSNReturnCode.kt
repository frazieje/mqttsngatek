package net.farsystem.mqttsngatek

enum class MQTTSNReturnCode(val code: Int) {
    ACCEPTED(0x00),
    REJECTED_CONGESTION(0x01),
    REJECTED_INVALID_TOPIC_ID(0x02),
    REJECTED_NOT_SUPPORTED(0x03);
    companion object {
        private val VALUES = MQTTSNReturnCode.values()
        fun fromCode(code: Int) = VALUES.first { it.code == code }
    }
}