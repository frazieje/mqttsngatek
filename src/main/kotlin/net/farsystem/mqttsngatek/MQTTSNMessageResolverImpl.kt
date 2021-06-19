package net.farsystem.mqttsngatek

import java.nio.ByteBuffer

class MQTTSNMessageResolverImpl: MQTTSNMessageResolver {
    override fun resolve(buffer: ByteBuffer): MQTTSNPacket {
        val bufferLength = buffer.remaining()

        if (bufferLength < MQTTSN_MIN_HEADER_SIZE) {
            throw ByteBufferTooShortException()
        }

        val messageLength = buffer.get().run {
            toInt() and 0xFF
        }.let {
            if (it == 1) {
                buffer.short.toInt() and 0xFFFF
            } else {
                it
            }
        }

        if (bufferLength < messageLength) {
            throw ByteBufferTooShortException()
        }

        val packetType = buffer.get().toInt()

        val type = MQTTSNMessageType.fromCode(packetType)

        val header = object : MQTTSNHeader {
            override val messageLength: Int
                get() = messageLength
            override val messagteType: MQTTSNMessageType
                get() = type
            override fun toBuffer(): ByteBuffer {
                return if (messageLength <= 255) {
                    ByteBuffer.allocate(2)
                        .put(messageLength.toByte())
                } else {
                    ByteBuffer.allocate(4)
                        .put(0x01)
                        .putShort(messageLength.toShort())
                }.put(type.code.toByte())
            }
        }

        val message = classMap[type]!!(buffer)

        return object : MQTTSNPacket {
            override val header: MQTTSNHeader
                get() = header
            override val message: MQTTSNMessage
                get() = message

            override fun toBuffer(): ByteBuffer {
                val headerBuffer = header.toBuffer()
                val messageBuffer = message.toBuffer()
                return ByteBuffer.allocate(headerBuffer.capacity() + messageBuffer.capacity())
                    .put(headerBuffer)
                    .put(messageBuffer)
            }
        }

    }
    companion object {
        private const val MQTTSN_MIN_HEADER_SIZE = 2
        val classMap: Map<MQTTSNMessageType, (ByteBuffer) -> MQTTSNMessage> = hashMapOf(
            MQTTSNMessageType.SEARCHGW to (MQTTSNSearchGw)::fromBuffer,
            MQTTSNMessageType.CONNECT to (MQTTSNConnect)::fromBuffer,
            MQTTSNMessageType.SUBSCRIBE to (MQTTSNSubscribe)::fromBuffer,
            MQTTSNMessageType.REGISTER to (MQTTSNRegister)::fromBuffer,
            MQTTSNMessageType.REGACK to (MQTTSNRegAck)::fromBuffer,
        )
    }
}