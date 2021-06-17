package net.farsystem.mqttsngatek

class DefaultGatewayConfig : GatewayConfig {

    override fun serverPort(): Int {
        return 10000
    }

    override fun serverAddress(): String {
        return "::"
    }
}