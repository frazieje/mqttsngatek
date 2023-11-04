package net.farsystem.mqttsngatek.mqtt

data class MQTTPubAck(override val messageId: Int) : MQTTAck(messageId) {
    override val type: MQTTMessageType = MQTTMessageType.PUBACK
}

data class MQTTPubRec(override val messageId: Int) : MQTTAck(messageId) {
    override val type: MQTTMessageType = MQTTMessageType.PUBREC
}

data class MQTTPubRel(override val messageId: Int) : MQTTAck(messageId) {
    override val type: MQTTMessageType = MQTTMessageType.PUBREL
}

data class MQTTPubComp(override val messageId: Int) : MQTTAck(messageId) {
    override val type: MQTTMessageType = MQTTMessageType.PUBCOMP
}

