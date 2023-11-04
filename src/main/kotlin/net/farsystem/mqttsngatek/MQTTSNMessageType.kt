package net.farsystem.mqttsngatek

enum class MQTTSNMessageType(val code: Int) {
    ADVERTISE(0x00),
    SEARCHGW(0x01),
    GWINFO(0x02),
    CONNECT(0x04),
    CONNACK(0x05),
    WILLTOPICREQ(0x06),
    WILLTOPIC(0x07),
    WILLMSGREQ(0x08),
    WILLMSG(0x09),
    REGISTER(0x0A),
    REGACK(0x0B),
    PUBLISH(0x0C),
    PUBACK(0x0D),
    PUBCOMP(0x0E),
    PUBREC(0x0F),
    PUBREL(0x10),
    SUBSCRIBE(0x12),
    SUBACK(0x13),
    UNSUBSCRIBE(0x14),
    UNSUBACK(0x15),
    PINGREQ(0x16),
    PINGRESP(0x17),
    DISCONNECT(0x18);

    companion object {
        private val VALUES = values()
        fun fromCode(code: Int) = VALUES.first { it.code == code }
    }
}