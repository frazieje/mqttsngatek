package net.farsystem.mqttsngatek

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.glassfish.grizzly.Buffer
import org.glassfish.grizzly.filterchain.BaseFilter
import org.glassfish.grizzly.filterchain.FilterChainContext
import org.glassfish.grizzly.filterchain.NextAction
import org.slf4j.LoggerFactory
import java.nio.ByteBuffer
import java.util.concurrent.BlockingDeque
import java.util.concurrent.LinkedBlockingDeque
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException


class CaptureFilter(private val tag: String = "") : BaseFilter() {

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val readQueue = LinkedBlockingDeque<Buffer>()

    private val writeQueue = LinkedBlockingDeque<Buffer>()

    override fun handleRead(ctx: FilterChainContext): NextAction {
        logDebug("read message")
        val message = ctx.getMessage<Buffer>()
        readQueue.putLast(message)
        return ctx.invokeAction
    }

    override fun handleWrite(ctx: FilterChainContext): NextAction {
        logDebug("write message")
        val message = ctx.getMessage<Buffer>()
        writeQueue.putLast(message)
        return ctx.invokeAction
    }

    override fun handleConnect(ctx: FilterChainContext): NextAction {
        logDebug("connect")
        return ctx.invokeAction
    }

    override fun handleClose(ctx: FilterChainContext): NextAction {
        logDebug("close")
        return ctx.stopAction
    }

    suspend fun getLastWrite(timeoutMillis: Long = 1000): ByteBuffer = withContext(Dispatchers.IO) {
        writeQueue.pollFirst(timeoutMillis, TimeUnit.MILLISECONDS)?.toByteBuffer()
            ?: throw TimeoutException()
    }

    suspend fun getLastRead(timeoutMillis: Long = 1000): ByteBuffer = withContext(Dispatchers.IO) {
        readQueue.pollLast(timeoutMillis, TimeUnit.MILLISECONDS)?.toByteBuffer()
            ?: throw TimeoutException()
    }

    private fun logDebug(msg: String) {
        logger.debug("${if (tag.isNotEmpty()) "$tag: " else ""}$msg")
    }

    fun recycle() {
        readQueue.clear()
        writeQueue.clear()
    }
}