package net.farsystem.mqttsngatek

import net.farsystem.mqttsngatek.model.NetworkContext
import net.farsystem.mqttsngatek.model.NetworkProtocol
import org.glassfish.grizzly.Grizzly
import org.glassfish.grizzly.filterchain.BaseFilter
import org.glassfish.grizzly.filterchain.FilterChainContext
import org.glassfish.grizzly.filterchain.NextAction
import org.slf4j.LoggerFactory
import java.net.InetSocketAddress

class MQTTSNGatewayFilter(
    val networkMQTTSNMessageHandler: NetworkMQTTSNMessageHandler,
    val config: GatewayConfig
) : BaseFilter() {

    private val messageProcessedAttributte = Grizzly.DEFAULT_ATTRIBUTE_BUILDER.createAttribute<Boolean>("messageProcessed")
    private val responseAttribute = Grizzly.DEFAULT_ATTRIBUTE_BUILDER.createAttribute<MQTTSNMessage?>("responseMessage")

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun handleRead(ctx: FilterChainContext): NextAction {
        logger.debug("gateway handleRead")

        val cxn = ctx.connection

        val peerAddress = cxn.peerAddress as? InetSocketAddress

        val gateWayAddress = cxn.localAddress as InetSocketAddress

        logger.debug("gateway handleRead cxn ${System.identityHashCode(cxn)}")

        val message = ctx.getMessage<MQTTSNMessage>()

        val networkContext = NetworkContext(
            NetworkProtocol.valueOf(config.networkProtocol()),
            peerAddress?.address ?: gateWayAddress.address,
            peerAddress?.port ?: gateWayAddress.port,
            gateWayAddress.address,
            gateWayAddress.port
        )

        val messageProcessed = messageProcessedAttributte[ctx.connection]
        val responseMessage = responseAttribute[ctx.connection]

        return if (messageProcessed != true) {
            logger.debug("gateway handleRead message not processed, suspending ${System.identityHashCode(cxn)}")
            ctx.suspend()
            networkMQTTSNMessageHandler.onReceive(networkContext, message) {
                logger.debug("gateway handleRead message done processing, resuming ${System.identityHashCode(cxn)}")
                messageProcessedAttributte[ctx.connection] = true
                responseAttribute[ctx.connection] = it
                ctx.resume()
            }
            ctx.suspendAction
        } else {
            logger.debug("gateway handleRead message processed, finishing ${System.identityHashCode(cxn)}")
            if (responseMessage != null) {
                logger.debug("gateway handleRead response found, sending ${System.identityHashCode(cxn)}")
                ctx.write(peerAddress, responseMessage, null)
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