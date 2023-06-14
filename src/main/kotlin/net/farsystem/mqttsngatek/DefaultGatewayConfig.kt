package net.farsystem.mqttsngatek

import net.farsystem.mqttsngatek.model.NetworkProtocol

class DefaultGatewayConfig : GatewayConfig {

    override fun port(): Int {
        return 10000
    }

    override fun networkInterface(): String {
        return "en0"
    }

    override fun networkProtocol(): String {
        return "UDP6"
    }

    override fun gatewayId(): Int = 0x6d

    override fun broker(): String {
        return "127.0.0.1"
    }

    override fun brokerPort(): Int {
        return 1883
    }

}