package net.farsystem.mqttsngatek.mqtt.paho

import org.eclipse.paho.client.mqttv3.internal.wire.MqttAck
import org.eclipse.paho.client.mqttv3.internal.wire.MqttWireMessage

class SimpleMqttPubRel(packetId: Int) : MqttAck(MqttWireMessage.MESSAGE_TYPE_PUBREL) {
    init {
        msgId = packetId
    }
    override fun getVariableHeader(): ByteArray = encodeMessageId()
}