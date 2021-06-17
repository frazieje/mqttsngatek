package net.farsystem.mqttsngatek

enum class MQTTSNTopicType(val code: Int) {
    NORMAL(0b00),
    PREDEFINED(0b01),
    SHORT_NAME(0b10);
    companion object {
        private val VALUES = values()
        fun fromCode(code: Int) = VALUES.first { it.code == code }
    }
}