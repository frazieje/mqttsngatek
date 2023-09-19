package net.farsystem.mqttsngatek.mqtt

data class MQTTConnack(
    val returnCode: MQTTReturnCode,
    val isSessionPresent: Boolean,
): MQTTMessage {
    override val type: MQTTMessageType = MQTTMessageType.CONNACK
}