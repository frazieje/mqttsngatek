package net.farsystem.mqttsngatek

import org.glassfish.grizzly.Buffer
import org.glassfish.grizzly.filterchain.BaseFilter
import org.glassfish.grizzly.filterchain.FilterChainContext
import org.glassfish.grizzly.filterchain.NextAction
import org.slf4j.LoggerFactory
import java.util.concurrent.BlockingDeque
import java.util.concurrent.LinkedBlockingDeque


class CaptureFilter : BaseFilter() {

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val readQueue = LinkedBlockingDeque<Buffer>()

    private val writeQueue = LinkedBlockingDeque<Buffer>()

    override fun handleRead(ctx: FilterChainContext): NextAction {
        logger.debug("read message")
        val message = ctx.getMessage<Buffer>()
        readQueue.putLast(message)
        return ctx.invokeAction
    }

    override fun handleWrite(ctx: FilterChainContext): NextAction {
        logger.debug("write message")
        val message = ctx.getMessage<Buffer>()
        writeQueue.putLast(message)
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

    fun getReadDeque(): BlockingDeque<Buffer> {
        return readQueue
    }

    fun getWriteDeque(): BlockingDeque<Buffer> {
        return writeQueue
    }

    fun recycle() {
        readQueue.clear()
        writeQueue.clear()
    }
}