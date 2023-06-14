package net.farsystem.mqttsngatek

import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.internal.wire.MqttPingReq

class ManualKeepAliveMqttAsyncClient(
    serverURI: String?,
    clientId: String?,
    persistence: MqttClientPersistence?
) : MqttAsyncClient(serverURI, clientId, persistence, NoOpPingSender()) {
    fun sendPing(callback: IMqttActionListener) {
        val token = MqttToken(clientId)
        token.actionCallback = callback
        comms.sendNoWait(MqttPingReq(), token)
    }
}