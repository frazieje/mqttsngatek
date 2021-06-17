package net.farsystem.mqttsngatek

import com.spoohapps.farcommon.config.ConfigFlags

interface GatewayConfig {

    @ConfigFlags("serverPort")
    fun serverPort(): Int

    @ConfigFlags("serverAddress")
    fun serverAddress(): String

}