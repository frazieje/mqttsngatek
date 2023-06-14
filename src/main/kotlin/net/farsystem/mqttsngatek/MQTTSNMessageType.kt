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
    SUBSCRIBE(0x12),
    PINGREQ(0x16),
    PINGRESP(0x17);

    companion object {
        private val VALUES = values()
        fun fromCode(code: Int) = VALUES.first { it.code == code }
    }
}