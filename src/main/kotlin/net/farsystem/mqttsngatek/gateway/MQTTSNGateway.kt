package net.farsystem.mqttsngatek.gateway

import net.farsystem.mqttsngatek.GatewayConfig
import net.farsystem.mqttsngatek.NetworkMQTTSNMessageHandler

interface MQTTSNGateway {
    fun start()
    fun shutdown()
}