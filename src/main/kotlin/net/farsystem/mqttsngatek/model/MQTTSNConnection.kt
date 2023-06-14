package net.farsystem.mqttsngatek.model

data class MQTTSNConnection(
    val networkContext: NetworkContext,
    val mqttsnClient: MQTTSNClient
)