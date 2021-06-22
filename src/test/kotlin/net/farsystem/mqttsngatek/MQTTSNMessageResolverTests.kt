package net.farsystem.mqttsngatek

import net.farsystem.mqttsngatek.mqttsnclient.NativeMQTTSNClient
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import java.nio.ByteBuffer

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MQTTSNMessageResolverTests {

    private val resolver: MQTTSNMessageResolver = MQTTSNMessageResolverImpl()
    private val client = NativeMQTTSNClient()

    private lateinit var mqttSnSearchGwPacket: MQTTSNPacket
    private lateinit var mqttSnSearchGwBytes: ByteBuffer

    private lateinit var mqttSnConnectPacket: MQTTSNPacket
    private lateinit var mqttSnConnectBytes: ByteBuffer

    private val expectedRadius = 5

    private val expectedClientId = "mqttClient"
    private val expectedDuration = 10
    private val expectedCleanSession = true
    private val expectedWillFlag = true

    @BeforeAll
    fun setup() {
        mqttSnConnectBytes = client.serializeConnect(expectedClientId, expectedDuration, expectedCleanSession, expectedWillFlag)
        mqttSnConnectPacket = resolver.resolve(mqttSnConnectBytes)

        mqttSnSearchGwBytes = client.serializeSearchGW(expectedRadius)
        mqttSnSearchGwPacket = resolver.resolve(mqttSnSearchGwBytes)
    }

    /* MQTTSN SearchGW */

    @Test
    fun `MQTTSN SearchGW Type is resolved`() {
        assertEquals(MQTTSNMessageType.SEARCHGW, mqttSnSearchGwPacket.header.messageType)
    }

    @Test
    fun `MQTTSN SearchGW Header length is resolved`() {
        assertEquals(mqttSnSearchGwBytes.capacity(), mqttSnSearchGwPacket.header.messageLength)
    }

    @Test
    fun `MQTTSN SearchGW Packet is resolved`() {
        assertEquals(mqttSnSearchGwBytes.capacity(), mqttSnSearchGwPacket.length())
    }

    @Test
    fun `MQTTSN SearchGW Message is resolved`() {
        assertTrue(mqttSnSearchGwPacket.message is MQTTSNSearchGw)
    }

    @Test
    fun `MQTTSN SearchGW radius is deserialized correctly`() {
        assertEquals(expectedRadius, (mqttSnSearchGwPacket.message as MQTTSNSearchGw).radius)
    }

    /* MQTTSN CONNECT */

    @Test
    fun `MQTTSN Connect Type is resolved`() {
        assertEquals(MQTTSNMessageType.CONNECT, mqttSnConnectPacket.header.messageType)
    }

    @Test
    fun `MQTTSN Connect Header length is resolved`() {
        assertEquals(mqttSnConnectBytes.capacity(), mqttSnConnectPacket.header.messageLength)
    }

    @Test
    fun `MQTTSN Connect Packet is resolved`() {
        assertEquals(mqttSnConnectBytes.capacity(), mqttSnConnectPacket.length())
    }

    @Test
    fun `MQTTSN Connect Message is resolved`() {
        assertTrue(mqttSnConnectPacket.message is MQTTSNConnect)
    }

    @Test
    fun `MQTTSN Connect clientId is deserialized correctly`() {
        assertEquals(expectedClientId, (mqttSnConnectPacket.message as MQTTSNConnect).clientId)
    }

    @Test
    fun `MQTTSN Connect duration is deserialized correctly`() {
        assertEquals(expectedDuration, (mqttSnConnectPacket.message as MQTTSNConnect).duration)
    }

    @Test
    fun `MQTTSN Connect clean session flag is deserialized correctly`() {
        assertEquals(expectedCleanSession, (mqttSnConnectPacket.message as MQTTSNConnect).cleanSession)
    }

    @Test
    fun `MQTTSN Connect will flag is deserialized correctly`() {
        assertEquals(expectedWillFlag, (mqttSnConnectPacket.message as MQTTSNConnect).willFlag)
    }
}