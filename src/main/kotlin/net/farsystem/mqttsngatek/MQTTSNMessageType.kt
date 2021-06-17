package net.farsystem.mqttsngatek

enum class MQTTSNMessageType(val code: Int) {
    SEARCHGW(0x01),
    CONNECT(0x04),
    REGISTER(0x0A),
    REGACK(0x0B),
    SUBSCRIBE(0x12);
    companion object {
        private val VALUES = values()
        fun fromCode(code: Int) = VALUES.first { it.code == code }
    }
}