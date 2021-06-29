package net.farsystem.mqttsngatek

import org.glassfish.grizzly.Buffer
import org.glassfish.grizzly.filterchain.BaseFilter
import org.glassfish.grizzly.filterchain.FilterChainContext
import org.glassfish.grizzly.filterchain.NextAction
import org.slf4j.LoggerFactory
import java.util.concurrent.CopyOnWriteArrayList


class CaptureFilter(val handler: MQTTSNMessageHandler) : BaseFilter() {

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val readQueue = CopyOnWriteArrayList<Buffer>()

    private val writeQueue = CopyOnWriteArrayList<Buffer>()

    override fun handleRead(ctx: FilterChainContext): NextAction {
        logger.debug("read message")
        val message = ctx.getMessage<Buffer>()
        readQueue.add(message)
        return ctx.invokeAction
    }

    override fun handleWrite(ctx: FilterChainContext): NextAction {
        logger.debug("write message")
        val message = ctx.getMessage<Buffer>()
        writeQueue.add(message)
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

    fun getReadQueue(): List<Buffer> {
        return ArrayList<Buffer>(readQueue)
    }

    fun getWriteQueue(): List<Buffer> {
        return ArrayList<Buffer>(writeQueue)
    }

    fun recycle() {
        readQueue.clear()
        writeQueue.clear()
    }
}