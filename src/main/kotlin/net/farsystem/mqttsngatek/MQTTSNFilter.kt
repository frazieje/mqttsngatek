package net.farsystem.mqttsngatek

import org.glassfish.grizzly.Buffer
import org.glassfish.grizzly.filterchain.BaseFilter
import org.glassfish.grizzly.filterchain.FilterChainContext
import org.glassfish.grizzly.filterchain.NextAction
import org.glassfish.grizzly.memory.Buffers
import org.slf4j.LoggerFactory

class MQTTSNFilter(val mqttsnMessageResolver: MQTTSNMessageResolver) : BaseFilter() {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun handleRead(ctx: FilterChainContext): NextAction {

        logger.debug("server received message")

        val sourceBuffer: Buffer = ctx.getMessage()

        val sourceBufferLength = sourceBuffer.remaining()

        val sourceByteBuffer = sourceBuffer.toByteBuffer()

        val packet = try {
            mqttsnMessageResolver.resolve(sourceByteBuffer)
        } catch (e: ByteBufferTooShortException) {
            return ctx.getStopAction(sourceBuffer)
        }

        ctx.setMessage(packet)

        val packetLength = packet.length()

        val remainder = if (sourceBufferLength > packetLength) sourceBuffer.split(packetLength) else null

        sourceBuffer.tryDispose()

        return ctx.getInvokeAction(remainder)
    }

    override fun handleWrite(ctx: FilterChainContext): NextAction {
        logger.debug("server write")
        val message = ctx.getMessage<MQTTSNMessage>()
        val memoryManager = ctx.connection.transport.memoryManager
        val buffer = Buffers.wrap(memoryManager, message.toBuffer().flip())
        buffer.allowBufferDispose(true)
        ctx.setMessage(buffer)
        return ctx.invokeAction
    }

    override fun handleConnect(ctx: FilterChainContext): NextAction {
        logger.debug("server connect")
        return ctx.invokeAction
    }

    override fun handleClose(ctx: FilterChainContext): NextAction {
        logger.debug("server close")
        return ctx.stopAction
    }


}