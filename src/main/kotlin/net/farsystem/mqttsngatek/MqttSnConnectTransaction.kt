package net.farsystem.mqttsngatek

class MqttSnConnectTransaction {
    enum class State {
        INIT,
        WILL_TOPIC_REQUESTED,
        WILL_TOPIC_RECEIVED,
        WILL_MESSAGE_REQUESTED,
        WILL_MESSAGE_RECEIVED,
    }
}