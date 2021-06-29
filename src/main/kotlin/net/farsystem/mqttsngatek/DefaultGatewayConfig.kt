package net.farsystem.mqttsngatek

class DefaultGatewayConfig : GatewayConfig {

    override fun port(): Int {
        return 10000
    }

    override fun networkInterface(): String {
        return "enp0s25"
    }

    override fun gatewayId(): Int = 0x6d

}