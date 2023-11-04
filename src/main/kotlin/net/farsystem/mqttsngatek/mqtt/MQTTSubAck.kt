package net.farsystem.mqttsngatek.mqtt

data class MQTTSubAck(val grantedQos: MQTTQoS, override val messageId: Int) : MQTTAck(messageId) {
    override val type: MQTTMessageType = MQTTMessageType.SUBACK
}
