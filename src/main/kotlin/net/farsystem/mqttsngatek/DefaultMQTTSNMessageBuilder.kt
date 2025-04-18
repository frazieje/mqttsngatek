package net.farsystem.mqttsngatek

import java.nio.ByteBuffer

class DefaultMQTTSNMessageBuilder: MQTTSNMessagBuilder {

    override fun decode(buffer: ByteBuffer): MQTTSNMessage {
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

        val type = MQTTSNMessageType.fromCode(buffer.get().toInt())

        val header = DefaultMQTTSNHeader(type, messageLength)

        return DefaultMQTTSNMessage(header, classMap[type]!!(buffer))
    }

    override fun createMessage(type: MQTTSNMessageType, body: MQTTSNBody): MQTTSNMessage {
        val bodyLength = body.length()
        val shortHeaderLength = 2
        val longHeaderLength = 4
        val headerLength = if ((bodyLength + shortHeaderLength) < UByte.MAX_VALUE.toInt()) {
            shortHeaderLength
        } else {
            longHeaderLength
        }
        val header = DefaultMQTTSNHeader(type, headerLength + bodyLength)
        return DefaultMQTTSNMessage(header, body)
    }

    companion object {
        private const val MQTTSN_MIN_HEADER_SIZE = 2
        val classMap: Map<MQTTSNMessageType, (ByteBuffer) -> MQTTSNBody> = hashMapOf(
            MQTTSNMessageType.SEARCHGW to MQTTSNSearchGw::fromBuffer,
            MQTTSNMessageType.CONNECT to MQTTSNConnect::fromBuffer,
            MQTTSNMessageType.SUBSCRIBE to MQTTSNSubscribe::fromBuffer,
            MQTTSNMessageType.REGISTER to MQTTSNRegister::fromBuffer,
            MQTTSNMessageType.REGACK to MQTTSNRegAck::fromBuffer,
            MQTTSNMessageType.GWINFO to MQTTSNGwInfo::fromBuffer,
            MQTTSNMessageType.PINGREQ to MQTTSNPingReq::fromBuffer,
            MQTTSNMessageType.PINGRESP to MQTTSNPingResp::fromBuffer,
            MQTTSNMessageType.PUBLISH to MQTTSNPublish::fromBuffer,
            MQTTSNMessageType.PUBACK to MQTTSNPubAck::fromBuffer,
            MQTTSNMessageType.PUBCOMP to MQTTSNPubComp::fromBuffer,
            MQTTSNMessageType.PUBREC to MQTTSNPubRec::fromBuffer,
            MQTTSNMessageType.PUBREL to MQTTSNPubRel::fromBuffer,
            MQTTSNMessageType.UNSUBSCRIBE to MQTTSNUnsubscribe::fromBuffer,
            MQTTSNMessageType.WILLTOPIC to MQTTSNWillTopic::fromBuffer,
            MQTTSNMessageType.WILLMSG to MQTTSNWillMsg::fromBuffer
        )
    }
}