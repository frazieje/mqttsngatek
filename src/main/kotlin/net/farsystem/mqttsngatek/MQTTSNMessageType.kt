package net.farsystem.mqttsngatek

enum class MQTTSNMessageType(val code: Int) {
    ADVERTISE(0x00),
    SEARCHGW(0x01),
    GWINFO(0x02),
    CONNECT(0x04),
    REGISTER(0x0A),
    REGACK(0x0B),
    SUBSCRIBE(0x12);

    companion object {
        private val VALUES = values()
        fun fromCode(code: Int) = VALUES.first { it.code == code }
    }
}