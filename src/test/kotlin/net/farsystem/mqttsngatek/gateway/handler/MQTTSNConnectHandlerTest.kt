package net.farsystem.mqttsngatek.gateway.handler

import io.mockk.*
import kotlinx.coroutines.test.runTest
import net.farsystem.mqttsngatek.*
import net.farsystem.mqttsngatek.data.repository.MQTTClientRepository
import net.farsystem.mqttsngatek.data.repository.MQTTSNClientRepository
import net.farsystem.mqttsngatek.gateway.MQTTSNMessageHandler
import net.farsystem.mqttsngatek.model.MQTTSNClient
import net.farsystem.mqttsngatek.model.NetworkContext
import net.farsystem.mqttsngatek.model.NetworkProtocol
import net.farsystem.mqttsngatek.mqtt.MQTTClient
import net.farsystem.mqttsngatek.mqtt.MQTTConnack
import net.farsystem.mqttsngatek.mqtt.MQTTReturnCode
import net.farsystem.mqttsngatek.mqttsnclient.NativeMQTTSNClient
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.net.InetSocketAddress

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MQTTSNConnectHandlerTest {

    private val messageBuilder: MQTTSNMessagBuilder = DefaultMQTTSNMessageBuilder()
    private val mqttsnClientRepository: MQTTSNClientRepository = mockk()
    private val mqttClientRepository: MQTTClientRepository = mockk()
    private val outgoingProcessor: MQTTSNMessageProcessor = mockk()
    private val handler: MQTTSNMessageHandler = MQTTSNConnectHandler(
        messageBuilder,
        mqttsnClientRepository,
        mqttClientRepository,
        outgoingProcessor
    )

    private val nativeClient = NativeMQTTSNClient()

    @Test
    fun `should create mqttsn client`() = runTest {
        val expectedClientId = "newClient"
        val expectedDuration = 60
        val expectedCleanSession = false
        val expectedWillFlag = false
        val netContext = NetworkContext(NetworkProtocol.UDP6, InetSocketAddress(10000), InetSocketAddress(10000))
        val mockMQTTClient = mockk<MQTTClient>()
        val expectedClient = MQTTSNClient(expectedClientId, netContext)
        val connectMsg = mockk<MQTTSNMessage>()
        every { connectMsg.body } returns MQTTSNConnect(expectedCleanSession, expectedWillFlag, duration = expectedDuration, clientId = expectedClientId)
        coEvery { mqttsnClientRepository.getClient(any<NetworkContext>()) } returns null
        coEvery { mqttsnClientRepository.addOrUpdateClient(any()) } just runs
        coEvery { mqttClientRepository.getOrCreate(expectedClient) } returns mockMQTTClient
        every { mockMQTTClient.isConnected() } returns false
        coEvery { mockMQTTClient.connect(any()) } returns MQTTConnack(MQTTReturnCode.ACCEPTED, false)
        coEvery { outgoingProcessor.process(any(), any()) } just runs
        handler.handleMessage(netContext, connectMsg)
        coVerify { mqttsnClientRepository.addOrUpdateClient(expectedClient) }
        val expectedResponse = messageBuilder.createMessage(MQTTSNMessageType.CONNACK, MQTTSNConnack(MQTTSNReturnCode.ACCEPTED))
        coVerify { outgoingProcessor.process(any(), expectedResponse) }
    }
}