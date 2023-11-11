package net.farsystem.mqttsngatek.mqtt

data class MQTTUnsubAck(override val messageId: Int): MQTTAck(messageId) {
    override val type: MQTTMessageType = MQTTMessageType.UNSUBACK
}