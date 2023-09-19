package net.farsystem.mqttsngatek

import kotlinx.coroutines.test.runTest
import net.farsystem.mqttsngatek.data.repository.InMemoryMQTTClientRepository
import net.farsystem.mqttsngatek.data.repository.InMemoryMQTTSNClientRepository
import net.farsystem.mqttsngatek.gateway.GrizzlyMQTTSNGateway
import net.farsystem.mqttsngatek.gateway.MQTTSNGateway
import net.farsystem.mqttsngatek.mqtt.MQTTConnack
import net.farsystem.mqttsngatek.mqtt.MQTTPingResp
import net.farsystem.mqttsngatek.mqtt.MQTTReturnCode
import net.farsystem.mqttsngatek.mqtt.paho.PahoMQTTClient
import net.farsystem.mqttsngatek.mqttsnclient.NativeMQTTSNClient
import org.glassfish.grizzly.filterchain.FilterChainBuilder
import org.glassfish.grizzly.filterchain.TransportFilter
import org.glassfish.grizzly.memory.Buffers
import org.glassfish.grizzly.nio.transport.UDPNIOConnection
import org.glassfish.grizzly.nio.transport.UDPNIOTransport
import org.glassfish.grizzly.nio.transport.UDPNIOTransportBuilder
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.net.InetSocketAddress
import java.util.concurrent.TimeUnit
import java.util.stream.Stream


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GatewayTests {

    lateinit var cxn: UDPNIOConnection

    private val mqttsnMessageBuilder = MQTTSNMessagBuilderImpl()

    private val gatewayConfig = object : GatewayConfig {
        override fun port(): Int = 10000

        override fun networkInterface(): String = "lo0"

        override fun networkProtocol(): String = "UDP6"

        override fun gatewayId(): Int = 210

        override fun broker(): String = "127.0.0.1"

        override fun brokerPort(): Int = 1883
    }

    private val fakeMQTTClient = FakeMQTTClient()

    private val nativeClient = NativeMQTTSNClient()

    private val mqttsnClientRepository = InMemoryMQTTSNClientRepository()

    private val mqttClientRepository = InMemoryMQTTClientRepository(gatewayConfig) { _, _, _ -> fakeMQTTClient }

//    private val mqttClientRepository = InMemoryMQTTClientRepository(
//        gatewayConfig,
//    ) { clientId, host, port ->
//        PahoMQTTClient(clientId, host, port)
//    }

    private val networkMQTTSNMessageHandler =
        NetworkMQTTSNMessageHandlerImpl(
            mqttsnMessageBuilder,
            gatewayConfig,
            mqttsnClientRepository,
            mqttClientRepository
        )

    private val captureFilter: CaptureFilter = CaptureFilter("client")

    private val serverAddress = "::1"

    private lateinit var mqttsnGateway: MQTTSNGateway

    private lateinit var clientTransport: UDPNIOTransport

    @BeforeAll
    fun setup() {

        mqttsnGateway = GrizzlyMQTTSNGateway(
            gatewayConfig,
            mqttsnMessageBuilder,
            networkMQTTSNMessageHandler
        )

        mqttsnGateway.start()

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

    @AfterAll
    fun teardown() {
        mqttsnGateway.shutdown()
        clientTransport.shutdownNow()
    }

    @ParameterizedTest
    @MethodSource("provideConnectReturnCodeMapping")
    fun `MQTTSN Connect is processed`(
        brokerReturnCode: MQTTReturnCode,
        snReturnCode: MQTTSNReturnCode
    ) = runTest {
        val bytes = nativeClient.serializeConnect("mqttsnClient", 10, true, false)
        val buf = Buffers.wrap(cxn.transport.memoryManager, bytes)
        val expectedResponse = MQTTConnack(brokerReturnCode, false)
        fakeMQTTClient.queueResponse(expectedResponse)
        val serverSocketAddress = InetSocketAddress(serverAddress, gatewayConfig.port())
        cxn.write(serverSocketAddress, buf, null)
        val response = captureFilter.getLastRead()
        val connack = nativeClient.deserializeConnAck(response)
        assertEquals(snReturnCode.code, connack.returnCode())
    }

    @Test
    fun `MQTTSN Subscribe with normal topic is processed`() {
        val client = NativeMQTTSNClient()
        val bytes = client.serializeSubscribeNormal(false, 1, 1234, "someTopic")
        val buf = Buffers.wrap(cxn.transport.memoryManager, bytes)
        cxn.write(buf)
        Thread.sleep(120000)
    }
//
//    @Test
//    fun `MQTTSN Subscribe with short topic is processed`() {
//        val client = NativeMQTTSNClient()
//        val bytes = client.serializeSubscribeShortName(false, 0, 3456, "st")
//        val buf = Buffers.wrap(cxn.transport.memoryManager, bytes)
//        cxn.write(buf)
//        Thread.sleep(60000)
//    }
//
//    @Test
//    fun `MQTTSN Subscribe with predefined topic is processed`() {
//        val client = NativeMQTTSNClient()
//        val bytes = client.serializeSubscribePredefined(false, 2, 34562, 7648)
//        val buf = Buffers.wrap(cxn.transport.memoryManager, bytes)
//        cxn.write(buf)
//        Thread.sleep(60000)
//    }
//
//    @Test
//    fun `MQTTSN Register is processed`() {
//        val client = NativeMQTTSNClient()
//        val bytes = client.serializeRegister(4832, 4440, "someNewTopic")
//        val buf = Buffers.wrap(cxn.transport.memoryManager, bytes)
//        cxn.write(buf)
//        Thread.sleep(60000)
//    }

    @Test
    fun `MQTTSN SearchGW is processed`() = runTest {
        val client = NativeMQTTSNClient()
        val bytes = client.serializeSearchGW(5)
        val buf = Buffers.wrap(cxn.transport.memoryManager, bytes)
        cxn.write(buf)
        val response = captureFilter.getLastRead()
        val gwinfo = client.deserializeGwInfo(response)
        assertEquals(gatewayConfig.gatewayId(), gwinfo.id())
        val serverAddress = InetSocketAddress(serverAddress, gatewayConfig.port())
        assertEquals(serverAddress, InetSocketAddress(gwinfo.address(), gatewayConfig.port()))
    }

//    @Test
//    fun `MQTTSN RegAck is processed`() {
//        val client = NativeMQTTSNClient()
//        val bytes = client.serializeRegAck(3234, 876, 2)
//        val buf = Buffers.wrap(cxn.transport.memoryManager, bytes)
//        cxn.write(buf)
//        Thread.sleep(60000)
//    }

    @Test
    fun `MQTTSN PingReq is processed`() = runTest {
        val client = NativeMQTTSNClient()
        var bytes = client.serializeConnect("mqttsnClient", 10, true, false)
        var buf = Buffers.wrap(cxn.transport.memoryManager, bytes)
        fakeMQTTClient.queueResponse(MQTTConnack(MQTTReturnCode.ACCEPTED, false))
        cxn.write(buf)
        var response = captureFilter.getLastRead()
        assertNotNull(response)
        val connack = client.deserializeConnAck(response)
        assertEquals(MQTTSNReturnCode.ACCEPTED.code, connack.returnCode)
        bytes = client.serializePingReq(null)
        buf = Buffers.wrap(cxn.transport.memoryManager, bytes)
        fakeMQTTClient.queueResponse(MQTTPingResp())
        cxn.write(buf)
        response = captureFilter.getLastRead()
        assertNotNull(response)
        val pingResp = client.deserializePingResp(response)
        assertNotNull(pingResp)
    }

    companion object {
        @JvmStatic
        private fun provideConnectReturnCodeMapping() =
            Stream.of(
                Arguments.of(MQTTReturnCode.ACCEPTED, MQTTSNReturnCode.ACCEPTED),
                Arguments.of(MQTTReturnCode.REJECTED_BAD_CREDENTIALS, MQTTSNReturnCode.REJECTED_NOT_SUPPORTED),
                Arguments.of(MQTTReturnCode.REJECTED_ID_REJECTED, MQTTSNReturnCode.REJECTED_INVALID_TOPIC_ID),
                Arguments.of(MQTTReturnCode.REJECTED_NOT_AUTHORIZED, MQTTSNReturnCode.REJECTED_NOT_SUPPORTED),
                Arguments.of(MQTTReturnCode.REJECTED_SERVER_UNAVAILABLE, MQTTSNReturnCode.REJECTED_CONGESTION),
                Arguments.of(MQTTReturnCode.REJECTED_UNACCEPTABLE_PROTOCOL, MQTTSNReturnCode.REJECTED_NOT_SUPPORTED),
            )
    }

}