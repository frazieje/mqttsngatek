package net.farsystem.mqttsngatek.mqtt

import net.farsystem.mqttsngatek.MQTTSNQoS

enum class MQTTQoS(val code: Int) {
    ZERO(0),
    ONE(1),
    TWO(2);
    companion object {
        private val VALUES = entries.toTypedArray()
        fun fromCode(code: Int) = VALUES.first { it.code == code }
    }
}