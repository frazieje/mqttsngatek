package net.farsystem.mqttsngatek

import net.farsystem.mqttsngatek.model.NetworkContext
import net.farsystem.mqttsngatek.model.NetworkProtocol
import org.glassfish.grizzly.filterchain.BaseFilter
import org.glassfish.grizzly.filterchain.FilterChainContext
import org.glassfish.grizzly.filterchain.NextAction
import org.slf4j.LoggerFactory
import java.net.Inet6Address
import java.net.InetSocketAddress
import java.net.NetworkInterface

class MQTTSNGatewayFilter(
    val networkMQTTSNMessageHandler: NetworkMQTTSNMessageHandler,
    val gatewayConfig: GatewayConfig
) : BaseFilter() {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun handleRead(ctx: FilterChainContext): NextAction {
        logger.debug("gateway handleRead")

        val cxn = ctx.connection

        logger.debug("gateway handleRead cxn ${System.identityHashCode(cxn)}")

        val mqttsnContext = ctx.getMessage<MQTTSNContext>()

        val networkContext = when (val protocol = NetworkProtocol.valueOf(gatewayConfig.networkProtocol())) {
            NetworkProtocol.UDP6 -> {
                val peerSocketAddress = ctx.address as InetSocketAddress
                val localSocketAddress = cxn.localAddress as InetSocketAddress
                NetworkContext(
                    protocol,
                    peerSocketAddress,
                    localSocketAddress,
                )
            }
        }

        return if (!mqttsnContext.isProcessed) {
            logger.debug("gateway handleRead message not processed, suspending ${System.identityHashCode(cxn)}")
            ctx.suspend()
            networkMQTTSNMessageHandler.onReceive(networkContext, mqttsnContext.message) {
                logger.debug("gateway handleRead message done processing, resuming ${System.identityHashCode(cxn)}")
                mqttsnContext.response = it
                ctx.resume()
            }
            ctx.suspendAction
        } else {
            logger.debug("gateway handleRead message processed, finishing ${System.identityHashCode(cxn)}")
            mqttsnContext.response?.let {
                logger.debug("gateway handleRead response found, sending ${System.identityHashCode(cxn)}")
                ctx.write(networkContext.source, it, null)
                mqttsnContext.reset()
            }
            ctx.stopAction
        }

//        when (message.header.messageType) {
//            MQTTSNMessageType.SEARCHGW -> {
//                val searchGw = message.body as MQTTSNSearchGw
//                logger.debug("SEARCHGW Received with radius ${searchGw.radius}")
//                val response =
//                    mqttsnMessagBuilder.createMessage(
//                        MQTTSNMessageType.GWINFO,
//                        MQTTSNGwInfo(gatewayConfig.gatewayId(), localAddress)
//                    )
//                ctx.write(peerAddress, response, null)
//            }
//            MQTTSNMessageType.CONNECT -> {
//                val connect = message.body as MQTTSNConnect
//                logger.debug("CONNECT Received with clientId ${connect.clientId}")
//                if (mqttClient?.isConnected != true && mqttToken?.exception == null) {
//                    val brokerUrl = "tcp://${gatewayConfig.broker()}:${gatewayConfig.brokerPort()}"
//
//                    val client =
//                        ManualKeepAliveMqttAsyncClient(brokerUrl, connect.clientId, MqttDefaultFilePersistence())
//                    clientAttribute.set(cxn, client)
//                    val options = MqttConnectOptions()
//                    options.isCleanSession = connect.cleanSession
//                    options.keepAliveInterval = connect.duration
//                    options.mqttVersion = MqttConnectOptions.MQTT_VERSION_3_1_1
//                    if (!connect.willFlag) {
//                        ctx.suspend()
//                        client.connect(options, null, object : IMqttActionListener {
//                            override fun onSuccess(asyncActionToken: IMqttToken) {
//                                tokenAttribute.set(cxn, asyncActionToken)
//                                ctx.resume()
//                            }
//
//                            override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable?) {
//                                tokenAttribute.set(cxn, asyncActionToken)
//                                ctx.resume()
//                            }
//                        })
//                        return ctx.suspendAction
//                    } else {
//                        ctx.suspend()
//                        val willtopicreq = mqttsnMessagBuilder.createMessage(
//                            MQTTSNMessageType.WILLTOPICREQ,
//                            MQTTSNWillTopicReq()
//                        )
//                        ctx.write(peerAddress, willtopicreq, null)
//                    }
//                }
//
//                if (mqttToken != null && mqttToken.response is MqttConnack && mqttToken.exception == null && mqttToken.response != null) {
//                    val connackMsg = mqttToken.response as MqttConnack
//                    val response = mqttsnMessagBuilder.createMessage(
//                        MQTTSNMessageType.CONNACK,
//                        MQTTSNConnAck(MQTTSNReturnCode.fromCode(connackMsg.returnCode))
//                    )
//                    ctx.write(peerAddress, response, null)
//                } else {
//                    logger.error("error connecting to broker", mqttToken?.exception)
//                }
//            }
//            MQTTSNMessageType.PINGREQ -> {
//                val pingreq = message.body as MQTTSNPingReq
//                logger.debug("PINGREQ Received with clientId ${pingreq.clientId}")
//                if (mqttClient == null || !mqttClient.isConnected) {
//                    logger.debug("client is null or disconnected")
//                    return ctx.stopAction
//                }
//                if (mqttToken?.response !is MqttPingResp?) {
//                    ctx.suspend()
//                    mqttClient.sendPing(object : IMqttActionListener {
//                        override fun onSuccess(asyncActionToken: IMqttToken) {
//                            tokenAttribute.set(cxn, asyncActionToken)
//                            ctx.resume()
//                        }
//
//                        override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable?) {
//                            tokenAttribute.set(cxn, asyncActionToken)
//                            ctx.resume()
//                        }
//                    })
//                    return ctx.suspendAction
//                }
//                if (mqttToken != null && mqttToken.exception == null && mqttToken.response is MqttPingResp) {
//                    val response = mqttsnMessagBuilder.createMessage(
//                        MQTTSNMessageType.PINGRESP,
//                        MQTTSNPingResp()
//                    )
//                    ctx.write(peerAddress, response, null)
//                } else {
//                    logger.error("error pinging broker", mqttToken?.exception)
//                }
//            }
//            MQTTSNMessageType.SUBSCRIBE -> {
//            }
//            MQTTSNMessageType.REGISTER -> {
//            }
//            MQTTSNMessageType.REGACK -> {
//            }
//        }
//        return ctx.stopAction
    }

}