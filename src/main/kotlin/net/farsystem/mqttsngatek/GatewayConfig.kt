package net.farsystem.mqttsngatek

import com.spoohapps.farcommon.config.ConfigFlags

interface GatewayConfig {

    @ConfigFlags("port")
    fun port(): Int

    @ConfigFlags("networkInterface")
    fun networkInterface(): String

    @ConfigFlags("gatewayId")
    fun gatewayId(): Int

}