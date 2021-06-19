package net.farsystem.mqttsngatek

import org.glassfish.grizzly.Buffer
import org.glassfish.grizzly.filterchain.BaseFilter
import org.glassfish.grizzly.filterchain.FilterChainContext
import org.glassfish.grizzly.filterchain.NextAction
import org.slf4j.LoggerFactory

class CaptureFilter : BaseFilter() {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun handleRead(ctx: FilterChainContext): NextAction {
        logger.debug("read message")
        val message = ctx.getMessage<Buffer>()
        val gwInfo = MQTTSNGwInfo.fromBuffer(message.toByteBuffer())
        return ctx.invokeAction
    }

    override fun handleWrite(ctx: FilterChainContext): NextAction {
        logger.debug("write message")
        val message = ctx.getMessage<Buffer>()
        return ctx.invokeAction
    }

    override fun handleConnect(ctx: FilterChainContext): NextAction {
        logger.debug("client connect")
        return ctx.invokeAction
    }

    override fun handleClose(ctx: FilterChainContext): NextAction {
        logger.debug("client close")
        return ctx.stopAction
    }
}