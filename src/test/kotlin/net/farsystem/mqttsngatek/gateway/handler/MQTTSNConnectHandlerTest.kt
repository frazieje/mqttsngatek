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
import net.farsystem.mqttsngatek.mqtt.*
import net.farsystem.mqttsngatek.mqttsnclient.NativeMQTTSNClient
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.net.InetSocketAddress
import java.util.stream.Stream

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MQTTSNConnectHandlerTest {

    private val messageBuilder: MQTTSNMessagBuilder = mockk()
    private val mqttsnClientRepository: MQTTSNClientRepository = mockk()
    private val mqttClientRepository: MQTTClientRepository = mockk()
    private val outgoingProcessor: MQTTSNMessageProcessor = mockk()
    private val handler: MQTTSNMessageHandler = MQTTSNConnectHandler(
        messageBuilder,
        mqttsnClientRepository,
        mqttClientRepository,
        outgoingProcessor
    )

    @ParameterizedTest
    @MethodSource("provideConnectReturnCodeMapping")
    fun `should return connack`(
        brokerReturnCode: MQTTReturnCode,
        snReturnCode: MQTTSNReturnCode
    ) = runTest {
        val expectedClientId = "newClient"
        val expectedDuration = 60
        val expectedCleanSession = false
        val expectedWillFlag = false
        val netContext = NetworkContext(NetworkProtocol.UDP6, InetSocketAddress(10000), InetSocketAddress(10000))
        val mockMQTTClient = mockk<MQTTClient>()
        val expectedClient = MQTTSNClient(expectedClientId, netContext)
        val connectMsg = mockk<MQTTSNMessage>()
        val connackResponse = mockk<MQTTSNMessage>()
        val expectedConnectOptions = MQTTConnectOptions(expectedCleanSession, expectedDuration, MQTTVersion.VERSION_3_1_1)
        val expectedResponseBody = MQTTSNConnack(snReturnCode)
        every { connectMsg.body } returns MQTTSNConnect(expectedCleanSession, expectedWillFlag, duration = expectedDuration, clientId = expectedClientId)
        every { connackResponse.body } returns expectedResponseBody
        coEvery { mqttsnClientRepository.getClient(any<NetworkContext>()) } returns null
        coEvery { mqttsnClientRepository.addOrUpdateClient(any()) } just runs
        coEvery { mqttClientRepository.getOrCreate(expectedClient) } returns mockMQTTClient
        every { mockMQTTClient.isConnected() } returns false
        coEvery { mockMQTTClient.connect(any()) } returns MQTTConnack(brokerReturnCode, false)
        every { messageBuilder.createMessage(MQTTSNMessageType.CONNACK, expectedResponseBody) } returns connackResponse
        coEvery { outgoingProcessor.process(any(), any()) } just runs
        handler.handleMessage(netContext, connectMsg)
        coVerify {
            mqttsnClientRepository.getClient(netContext)
            mqttsnClientRepository.addOrUpdateClient(expectedClient)
            mqttClientRepository.getOrCreate(expectedClient)
            mockMQTTClient.isConnected()
            mockMQTTClient.connect(expectedConnectOptions)
        }
        verify {messageBuilder.createMessage(MQTTSNMessageType.CONNACK, expectedResponseBody) }
        coVerify { outgoingProcessor.process(any(), connackResponse) }
    }

    @Test
    fun `should return willtopicreq`() = runTest {
        val expectedClientId = "newClient"
        val expectedDuration = 60
        val expectedCleanSession = false
        val expectedWillFlag = false
        val netContext = NetworkContext(NetworkProtocol.UDP6, InetSocketAddress(10000), InetSocketAddress(10000))
        val mockMQTTClient = mockk<MQTTClient>()
        val expectedClient = MQTTSNClient(expectedClientId, netContext)
        val connectMsg = mockk<MQTTSNMessage>()
        val willTopicReqResponse = mockk<MQTTSNMessage>()
        val expectedConnectOptions = MQTTConnectOptions(expectedCleanSession, expectedDuration, MQTTVersion.VERSION_3_1_1)
        val expectedResponseBody = MQTTSNWillTopicReq()
        every { connectMsg.body } returns MQTTSNConnect(expectedCleanSession, expectedWillFlag, duration = expectedDuration, clientId = expectedClientId)
        every { willTopicReqResponse.body } returns expectedResponseBody
        coEvery { mqttsnClientRepository.getClient(any<NetworkContext>()) } returns null
        coEvery { mqttsnClientRepository.addOrUpdateClient(any()) } just runs
        coEvery { mqttClientRepository.getOrCreate(expectedClient) } returns mockMQTTClient
        every { mockMQTTClient.isConnected() } returns false
        coEvery { mockMQTTClient.connect(any()) } returns MQTTConnack(brokerReturnCode, false)
        every { messageBuilder.createMessage(MQTTSNMessageType.CONNACK, expectedResponseBody) } returns connackResponse
        coEvery { outgoingProcessor.process(any(), any()) } just runs
        handler.handleMessage(netContext, connectMsg)
        coVerify {
            mqttsnClientRepository.getClient(netContext)
            mqttsnClientRepository.addOrUpdateClient(expectedClient)
            mqttClientRepository.getOrCreate(expectedClient)
            mockMQTTClient.isConnected()
            mockMQTTClient.connect(expectedConnectOptions)
        }
        verify {messageBuilder.createMessage(MQTTSNMessageType.CONNACK, expectedResponseBody) }
        coVerify { outgoingProcessor.process(any(), connackResponse) }
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