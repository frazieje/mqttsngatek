package net.farsystem.mqttsngatek

import org.glassfish.grizzly.Buffer
import org.glassfish.grizzly.filterchain.BaseFilter
import org.glassfish.grizzly.filterchain.FilterChainContext
import org.glassfish.grizzly.filterchain.NextAction

class MQTTSNFilter : BaseFilter() {

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
            MQTTSNMessageType.SEARCHGW -> MQTTSNSearchGW.fromBuffer(byteBuffer)
            MQTTSNMessageType.CONNECT -> MQTTSNConnect.fromBuffer(byteBuffer)
            MQTTSNMessageType.SUBSCRIBE -> MQTTSNSubscribe.fromBuffer(byteBuffer)
            MQTTSNMessageType.REGISTER -> MQTTSNRegister.fromBuffer(byteBuffer)
            MQTTSNMessageType.REGACK -> MQTTSNRegAck.fromBuffer(byteBuffer)
        })

        return ctx.getInvokeAction(remainder)
    }

    override fun handleWrite(ctx: FilterChainContext): NextAction {
        return super.handleWrite(ctx)
    }

    companion object {
        private const val MQTTSN_MIN_HEADER_SIZE = 2
    }

}