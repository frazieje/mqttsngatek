package net.farsystem.mqttsngatek

interface GatewayConfig {

    fun port(): Int

    fun networkInterface(): String

    fun networkProtocol(): String

    fun gatewayId(): Int

    fun broker(): String

    fun brokerPort(): Int
}