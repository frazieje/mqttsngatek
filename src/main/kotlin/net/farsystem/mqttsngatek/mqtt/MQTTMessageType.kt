package net.farsystem.mqttsngatek.mqtt

enum class MQTTMessageType(val code: Int) {
    CONNECT(0x01),
    CONNACK(0x02),
    PUBLISH(0x03),
    PUBACK(0x04),
    PUBREC(0x05),
    PUBREL(0x06),
    PUBCOMP(0x07),
    SUBSCRIBE(0x08),
    SUBACK(0x09),
    UNSUBSCRIBE(0x0A),
    UNSUBACK(0x0B),
    PINGREQ(0x0C),
    PINGRESP(0x0D),
    DISCONNECT(0x0E);

    companion object {
        private val VALUES = MQTTMessageType.values()
        fun fromCode(code: Int) = VALUES.first { it.code == code }
    }
}