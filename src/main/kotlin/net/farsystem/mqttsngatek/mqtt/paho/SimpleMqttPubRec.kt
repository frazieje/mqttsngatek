package net.farsystem.mqttsngatek.mqtt.paho

import org.eclipse.paho.client.mqttv3.internal.wire.MqttAck
import org.eclipse.paho.client.mqttv3.internal.wire.MqttWireMessage

class SimpleMqttPubRec(packetId: Int) : MqttAck(MqttWireMessage.MESSAGE_TYPE_PUBREC) {
    init {
        msgId = packetId
    }
    override fun getVariableHeader(): ByteArray = encodeMessageId()
}