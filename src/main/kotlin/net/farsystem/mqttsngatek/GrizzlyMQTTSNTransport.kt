package net.farsystem.mqttsngatek

import kotlinx.coroutines.suspendCancellableCoroutine
import net.farsystem.mqttsngatek.model.NetworkContext
import net.farsystem.mqttsngatek.model.NetworkProtocol
import org.glassfish.grizzly.CompletionHandler
import org.glassfish.grizzly.Connection
import org.glassfish.grizzly.filterchain.FilterChainBuilder
import org.glassfish.grizzly.filterchain.TransportFilter
import org.glassfish.grizzly.nio.transport.UDPNIOTransport
import org.glassfish.grizzly.nio.transport.UDPNIOTransportBuilder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.net.InetSocketAddress
import java.net.NetworkInterface
import kotlin.coroutines.resume

class GrizzlyMQTTSNTransport(
    private val config: GatewayConfig,
    private val messageBuilder: MQTTSNMessagBuilder,
) : MQTTSNTransport {

    private val logger: Logger = LoggerFactory.getLogger(Gateway::class.java)

    private val transport: UDPNIOTransport = UDPNIOTransportBuilder.newInstance().build()

    private val receivers: MutableList<(NetworkContext, MQTTSNMessage) -> Unit> = mutableListOf()

    override suspend fun send(networkContext: NetworkContext, message: MQTTSNMessage) {
        val connection: Connection<Any> = awaitCallback {
            transport.connect(networkContext.destination, networkContext.source, it)
        }

        awaitCallback { connection.write(message, it) }

        connection.awaitClosure()
    }
    override fun receive(receiver: (NetworkContext, MQTTSNMessage) -> Unit) {
        receivers.add(receiver)
    }

    override fun start() {
        logger.debug("Starting Grizzly MQTTSN transport")
        transport.processor = FilterChainBuilder.stateless()
            .add(TransportFilter())
            .add(MQTTSNFilter(messageBuilder))
            .add(MQTTSNGatewayFilter(NetworkProtocol.valueOf(config.networkProtocol()), ::receive))
            .build()

        if (config.networkInterface().isNotBlank()) {
            NetworkInterface.networkInterfaces().forEach { iface ->
                if (iface.isLoopback || iface.name == config.networkInterface()) {
                    iface.interfaceAddresses.forEach {
                        transport.bind(InetSocketAddress(it.address, config.port()))
                        logger.info("Binding to address ${it.address.hostAddress}")
                    }
                }
            }
            logger.info("Starting transport on ${config.networkInterface()}...")

        } else {
            logger.info("Starting transport on :: ...")
            transport.bind("::", config.port())
        }

        transport.start()
    }

    override fun stop() {
        transport.shutdownNow()
    }

    private fun receive(networkContext: NetworkContext, message: MQTTSNMessage) {
        receivers.forEach { it(networkContext, message) }
    }

    private suspend fun <T> awaitCallback(
        block: (CompletionHandler<T>) -> Unit
    ): T = suspendCancellableCoroutine { cont ->
        block(object : CompletionHandler<T> {
            override fun cancelled() {
                cont.cancel(null)
            }

            override fun failed(throwable: Throwable?) {
                cont.cancel(throwable)
            }

            override fun completed(result: T?) {
                cont.resume(result!!)
            }

            override fun updated(result: T?) {
                throw NotImplementedError()
            }
        })
    }

    private suspend fun Connection<Any>.awaitClosure() = suspendCancellableCoroutine { cont ->
        addCloseListener { _, _ -> cont.resume(Unit) }
        close()
    }
}