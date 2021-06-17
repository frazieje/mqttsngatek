package net.farsystem.mqttsngatek

import org.glassfish.grizzly.filterchain.BaseFilter
import org.glassfish.grizzly.filterchain.FilterChainContext
import org.glassfish.grizzly.filterchain.NextAction

class MQTTSNGatewayFilter : BaseFilter() {

    override fun handleRead(ctx: FilterChainContext): NextAction {

        val message = ctx.getMessage<Any>()

        when (message) {
            is MQTTSNSearchGW -> {
                val radius = message.radius
                val test = ""
            }
            is MQTTSNRegister -> {
                val test = message.topic
                val test2 = ""
            }
            is MQTTSNSubscribe -> {
                val test = ""
            }
            is MQTTSNConnect -> {
                val test = ""
            }
            is MQTTSNRegAck -> {
                val test = ""
            }
        }

        return ctx.stopAction
    }

}