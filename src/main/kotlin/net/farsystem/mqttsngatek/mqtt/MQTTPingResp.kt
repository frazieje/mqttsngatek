package net.farsystem.mqttsngatek.mqtt

class MQTTPingResp : MQTTMessage {
    override val type: MQTTMessageType = MQTTMessageType.PINGRESP
}