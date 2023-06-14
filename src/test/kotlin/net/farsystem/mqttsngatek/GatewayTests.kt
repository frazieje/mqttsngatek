package net.farsystem.mqttsngatek

import net.farsystem.mqttsngatek.mqttsnclient.NativeMQTTSNClient
import org.glassfish.grizzly.filterchain.FilterChainBuilder
import org.glassfish.grizzly.filterchain.TransportFilter
import org.glassfish.grizzly.memory.Buffers
import org.glassfish.grizzly.nio.transport.UDPNIOConnection
import org.glassfish.grizzly.nio.transport.UDPNIOTransport
import org.glassfish.grizzly.nio.transport.UDPNIOTransportBuilder
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.net.InetSocketAddress
import java.util.concurrent.TimeUnit


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GatewayTests {

    lateinit var cxn: UDPNIOConnection
    lateinit var clientTransport: UDPNIOTransport

    private val mqttsnMessageBuilder = MQTTSNMessagBuilderImpl()

    private val gatewayConfig = DefaultGatewayConfig()

    private val networkMQTTSNMessageHandler = NetworkMQTTSNMessageHandlerImpl(mqttsnMessageBuilder, gatewayConfig)

    private val captureFilter: CaptureFilter = CaptureFilter()

    private val expectedGatewayId = 123

    private val serverAddress = "::1"
    private val mqttsnport = 10000

    @BeforeAll
    fun setup() {

        val serverFilterChainBuilder = FilterChainBuilder.stateless()
            .add(TransportFilter())
            .add(MQTTSNFilter(mqttsnMessageBuilder))
            .add(MQTTSNGatewayFilter(networkMQTTSNMessageHandler, gatewayConfig))

        val serverTransport = UDPNIOTransportBuilder.newInstance()
            .setProcessor(serverFilterChainBuilder.build()).build()
        serverTransport.bind("::", 10000)
        serverTransport.start()

        val clientFilterChain = FilterChainBuilder.stateless()
            .add(TransportFilter())
            .add(captureFilter)
            .build()

        clientTransport = UDPNIOTransportBuilder.newInstance()
            .setProcessor(clientFilterChain)
            .build()

        clientTransport.start()

        cxn = clientTransport.connect(serverAddress, 10000).get(1, TimeUnit.SECONDS) as UDPNIOConnection

    }

    @Test
    fun `MQTTSN Connect is processed`() {
        val client = NativeMQTTSNClient()
        val bytes = client.serializeConnect("mqttClient", 10, true, false)
        val buf = Buffers.wrap(cxn.transport.memoryManager, bytes)
        cxn.write(buf)
        val response = captureFilter.getReadDeque().pollFirst(1000, TimeUnit.MILLISECONDS)
        assertNotNull(response)
        val respBuffer = response.toByteBuffer()
        val connack = client.deserializeConnAck(respBuffer)
        assertEquals(0, connack.returnCode)
    }

    @Test
    fun `MQTTSN Subscribe with normal topic is processed`() {
        val client = NativeMQTTSNClient()
        val bytes = client.serializeSubscribeNormal(false, 1, 1234, "someTopic")
        val buf = Buffers.wrap(cxn.transport.memoryManager, bytes)
        cxn.write(buf)
        Thread.sleep(60000)
    }

    @Test
    fun `MQTTSN Subscribe with short topic is processed`() {
        val client = NativeMQTTSNClient()
        val bytes = client.serializeSubscribeShortName(false, 0, 3456, "st")
        val buf = Buffers.wrap(cxn.transport.memoryManager, bytes)
        cxn.write(buf)
        Thread.sleep(60000)
    }

    @Test
    fun `MQTTSN Subscribe with predefined topic is processed`() {
        val client = NativeMQTTSNClient()
        val bytes = client.serializeSubscribePredefined(false, 2, 34562, 7648)
        val buf = Buffers.wrap(cxn.transport.memoryManager, bytes)
        cxn.write(buf)
        Thread.sleep(60000)
    }

    @Test
    fun `MQTTSN Register is processed`() {
        val client = NativeMQTTSNClient()
        val bytes = client.serializeRegister(4832, 4440, "someNewTopic")
        val buf = Buffers.wrap(cxn.transport.memoryManager, bytes)
        cxn.write(buf)
        Thread.sleep(60000)
    }

    @Test
    fun `MQTTSN SearchGW is processed`() {
        val client = NativeMQTTSNClient()
        val bytes = client.serializeSearchGW(5)
        val buf = Buffers.wrap(cxn.transport.memoryManager, bytes)
        cxn.write(buf)
        val response = captureFilter.getReadDeque().pollFirst(100, TimeUnit.MILLISECONDS)
        assertNotNull(response)
        val respBuffer = response!!.toByteBuffer()
        val gwinfo = client.deserializeGwInfo(respBuffer)
        assertEquals(expectedGatewayId, gwinfo.id)
        val serverAddress = InetSocketAddress(serverAddress, mqttsnport)
        assertEquals(serverAddress, InetSocketAddress(gwinfo.address, mqttsnport))
    }

    @Test
    fun `MQTTSN RegAck is processed`() {
        val client = NativeMQTTSNClient()
        val bytes = client.serializeRegAck(3234, 876, 2)
        val buf = Buffers.wrap(cxn.transport.memoryManager, bytes)
        cxn.write(buf)
        Thread.sleep(60000)
    }

    @Test
    fun `MQTTSN PingReq is processed`() {
        val client = NativeMQTTSNClient()
        var bytes = client.serializeConnect("mqttClient", 10, true, false)
        var buf = Buffers.wrap(cxn.transport.memoryManager, bytes)
        cxn.write(buf)
        var response = captureFilter.getReadDeque().pollFirst(500, TimeUnit.MILLISECONDS)
        assertNotNull(response)
        var respBuffer = response!!.toByteBuffer()
        val connack = client.deserializeConnAck(respBuffer)
        assertEquals(0, connack.returnCode)
        bytes = client.serializePingReq(null)
        buf = Buffers.wrap(cxn.transport.memoryManager, bytes)
        cxn.write(buf)
        response = captureFilter.getReadDeque().pollFirst(500, TimeUnit.MILLISECONDS)
        assertNotNull(response)
        respBuffer = response!!.toByteBuffer()
        val pingResp = client.deserializePingResp(respBuffer)
        assertNotNull(pingResp)
//        captureFilter.getReadDeque().take()
    }

}