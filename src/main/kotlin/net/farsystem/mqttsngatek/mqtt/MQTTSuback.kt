package net.farsystem.mqttsngatek.mqtt

data class MQTTSuback(val grantedQos: MQTTQoS) : MQTTMessage {
    override val type: MQTTMessageType = MQTTMessageType.SUBACK
}
