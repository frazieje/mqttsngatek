package net.farsystem.mqttsngatek

import net.farsystem.mqttsngatek.mqttsnclient.NativeMQTTSNClient
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import java.nio.ByteBuffer

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MQTTSNMessageHandlerTests {

    private val handler: MQTTSNMessageHandler = MQTTSNMessageHandlerImpl()
    private val client = NativeMQTTSNClient()

    private lateinit var mqttSnSearchGwMessage: MQTTSNMessage
    private lateinit var mqttSnSearchGwBytes: ByteBuffer

    private lateinit var mqttSnConnectMessage: MQTTSNMessage
    private lateinit var mqttSnConnectBytes: ByteBuffer

    private val expectedRadius = 5

    private val expectedClientId = "mqttClient"
    private val expectedDuration = 10
    private val expectedCleanSession = true
    private val expectedWillFlag = true

    @BeforeAll
    fun setup() {
        mqttSnConnectBytes = client.serializeConnect(expectedClientId, expectedDuration, expectedCleanSession, expectedWillFlag)
        mqttSnConnectMessage = handler.decode(mqttSnConnectBytes)

        mqttSnSearchGwBytes = client.serializeSearchGW(expectedRadius)
        mqttSnSearchGwMessage = handler.decode(mqttSnSearchGwBytes)
    }

    /* MQTTSN SearchGW */

    @Test
    fun `MQTTSN SearchGW Type is resolved`() {
        assertEquals(MQTTSNMessageType.SEARCHGW, mqttSnSearchGwMessage.header.messageType)
    }

    @Test
    fun `MQTTSN SearchGW Header length is resolved`() {
        assertEquals(mqttSnSearchGwBytes.capacity(), mqttSnSearchGwMessage.header.messageLength)
    }

    @Test
    fun `MQTTSN SearchGW Packet is resolved`() {
        assertEquals(mqttSnSearchGwBytes.capacity(), mqttSnSearchGwMessage.length())
    }

    @Test
    fun `MQTTSN SearchGW Message is resolved`() {
        assertTrue(mqttSnSearchGwMessage.body is MQTTSNSearchGw)
    }

    @Test
    fun `MQTTSN SearchGW radius is deserialized correctly`() {
        assertEquals(expectedRadius, (mqttSnSearchGwMessage.body as MQTTSNSearchGw).radius)
    }

    /* MQTTSN CONNECT */

    @Test
    fun `MQTTSN Connect Type is resolved`() {
        assertEquals(MQTTSNMessageType.CONNECT, mqttSnConnectMessage.header.messageType)
    }

    @Test
    fun `MQTTSN Connect Header length is resolved`() {
        assertEquals(mqttSnConnectBytes.capacity(), mqttSnConnectMessage.header.messageLength)
    }

    @Test
    fun `MQTTSN Connect Packet is resolved`() {
        assertEquals(mqttSnConnectBytes.capacity(), mqttSnConnectMessage.length())
    }

    @Test
    fun `MQTTSN Connect Message is resolved`() {
        assertTrue(mqttSnConnectMessage.body is MQTTSNConnect)
    }

    @Test
    fun `MQTTSN Connect clientId is deserialized correctly`() {
        assertEquals(expectedClientId, (mqttSnConnectMessage.body as MQTTSNConnect).clientId)
    }

    @Test
    fun `MQTTSN Connect duration is deserialized correctly`() {
        assertEquals(expectedDuration, (mqttSnConnectMessage.body as MQTTSNConnect).duration)
    }

    @Test
    fun `MQTTSN Connect clean session flag is deserialized correctly`() {
        assertEquals(expectedCleanSession, (mqttSnConnectMessage.body as MQTTSNConnect).cleanSession)
    }

    @Test
    fun `MQTTSN Connect will flag is deserialized correctly`() {
        assertEquals(expectedWillFlag, (mqttSnConnectMessage.body as MQTTSNConnect).willFlag)
    }
}