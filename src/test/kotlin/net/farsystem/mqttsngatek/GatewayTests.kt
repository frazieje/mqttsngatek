package net.farsystem.mqttsngatek

import net.farsystem.mqttsngatek.mqttsnclient.NativeMQTTSNClient
import org.glassfish.grizzly.Connection
import org.glassfish.grizzly.filterchain.FilterChainBuilder
import org.glassfish.grizzly.filterchain.TransportFilter
import org.glassfish.grizzly.memory.Buffers
import org.glassfish.grizzly.nio.transport.UDPNIOTransportBuilder
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.util.concurrent.TimeUnit


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GatewayTests {

    lateinit var cxn: Connection<*>

    @BeforeAll
    fun setup() {
        val serverFilterChainBuilder = FilterChainBuilder.stateless()
            .add(TransportFilter())
            .add(MQTTSNFilter())
            .add(MQTTSNGatewayFilter())

        val serverTransport = UDPNIOTransportBuilder.newInstance()
            .setProcessor(serverFilterChainBuilder.build()).build()

        serverTransport.bind(10000)
        serverTransport.start()

        val clientFilterChain = FilterChainBuilder.stateless()
            .add(TransportFilter())
            .build()

        val clientTransport = UDPNIOTransportBuilder.newInstance()
            .setProcessor(clientFilterChain)
            .build()

        clientTransport.start()

        cxn = clientTransport.connect("::1", 10000).get(1, TimeUnit.SECONDS)
    }

    @Test
    fun `MQTTSN Connect is processed`() {
        val client = NativeMQTTSNClient()
        val bytes = client.serializeConnect("mqttClient", 10, true, false)
        val buf = Buffers.wrap(cxn.transport.memoryManager, bytes)
        cxn.write(buf)
        Thread.sleep(60000)
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
        Thread.sleep(60000)
    }

    @Test
    fun `MQTTSN RegAck is processed`() {
        val client = NativeMQTTSNClient()
        val bytes = client.serializeRegAck(3234, 876, 2)
        val buf = Buffers.wrap(cxn.transport.memoryManager, bytes)
        cxn.write(buf)
        Thread.sleep(60000)
    }

}