package net.farsystem.mqttsngatek

enum class MQTTSNQoS(val code: Int) {
    ZERO(0b00),
    ONE(0b01),
    TWO(0b10),
    MINUS_ONE(0b11);
    companion object {
        private val VALUES = entries.toTypedArray()
        fun fromCode(code: Int) = VALUES.first { it.code == code }
    }
}