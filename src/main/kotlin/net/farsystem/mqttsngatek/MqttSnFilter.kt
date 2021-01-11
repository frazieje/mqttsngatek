package net.farsystem.mqttsngatek

import org.glassfish.grizzly.filterchain.BaseFilter
import org.glassfish.grizzly.filterchain.FilterChainContext
import org.glassfish.grizzly.filterchain.NextAction

class MqttSnFilter : BaseFilter() {

    override fun handleRead(ctx: FilterChainContext?): NextAction {

        val address = ctx?.address

        val message: String? = ctx?.getMessage()



        return super.handleRead(ctx)
    }

}