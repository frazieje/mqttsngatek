package net.farsystem.mqttsngatek.mqtt

enum class MQTTReturnCode(val code: Int) {
    ACCEPTED(0x00),
    REJECTED_UNACCEPTABLE_PROTOCOL(0x01),
    REJECTED_ID_REJECTED(0x02),
    REJECTED_SERVER_UNAVAILABLE(0x03),
    REJECTED_BAD_CREDENTIALS(0x04),
    REJECTED_NOT_AUTHORIZED(0x05);
    companion object {
        private val VALUES = values()
        fun fromCode(code: Int) = VALUES.first { it.code == code }
    }
}