package net.farsystem.mqttsngatek

import org.glassfish.grizzly.Buffer
import org.glassfish.grizzly.filterchain.BaseFilter
import org.glassfish.grizzly.filterchain.FilterChainContext
import org.glassfish.grizzly.filterchain.NextAction
import org.glassfish.grizzly.memory.Buffers
import org.slf4j.LoggerFactory

class MQTTSNFilter : BaseFilter() {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun handleRead(ctx: FilterChainContext): NextAction {

        val sourceBuffer: Buffer = ctx.getMessage()

        val sourceBufferLength = sourceBuffer.remaining()

        if (sourceBufferLength < MQTTSN_MIN_HEADER_SIZE) {
            // stop the filterchain processing and store sourceBuffer to be used next time
            return ctx.getStopAction(sourceBuffer)
        }

        val messageLength = sourceBuffer.get().run {
            toInt() and 0xFF
        }.let {
            if (it == 1) {
                sourceBuffer.short.toInt()
            } else {
                it
            }
        }

        if (sourceBufferLength < messageLength) {
            return ctx.getStopAction(sourceBuffer)
        }

        val remainder = if (sourceBufferLength > messageLength) sourceBuffer.split(messageLength) else null

        val packetType = sourceBuffer.get().toInt()

        val byteBuffer = sourceBuffer.toByteBuffer()

        ctx.setMessage(when (MQTTSNMessageType.fromCode(packetType)) {
            MQTTSNMessageType.SEARCHGW -> MQTTSNSearchGw.fromBuffer(byteBuffer)
            MQTTSNMessageType.CONNECT -> MQTTSNConnect.fromBuffer(byteBuffer)
            MQTTSNMessageType.SUBSCRIBE -> MQTTSNSubscribe.fromBuffer(byteBuffer)
            MQTTSNMessageType.REGISTER -> MQTTSNRegister.fromBuffer(byteBuffer)
            MQTTSNMessageType.REGACK -> MQTTSNRegAck.fromBuffer(byteBuffer)
        })

        return ctx.getInvokeAction(remainder)
    }

    override fun handleWrite(ctx: FilterChainContext): NextAction {
        val message = ctx.getMessage<MQTTSNMessage>()
        val memoryManager = ctx.connection.transport.memoryManager
        val buffer = Buffers.wrap(memoryManager, message.toBuffer())
        ctx.setMessage(buffer.flip())
        return ctx.invokeAction
    }

    companion object {
        private const val MQTTSN_MIN_HEADER_SIZE = 2
    }

}