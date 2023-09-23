package net.farsystem.mqttsngatek

import kotlinx.coroutines.suspendCancellableCoroutine
import net.farsystem.mqttsngatek.model.NetworkContext
import org.glassfish.grizzly.CompletionHandler
import org.glassfish.grizzly.Connection
import org.glassfish.grizzly.filterchain.FilterChainBuilder
import org.glassfish.grizzly.filterchain.TransportFilter
import org.glassfish.grizzly.nio.transport.UDPNIOTransport
import org.glassfish.grizzly.nio.transport.UDPNIOTransportBuilder
import java.net.SocketAddress
import kotlin.coroutines.resume

class GrizzlyMQTTSNMessageSender(
    messageBuilder: MQTTSNMessagBuilder
) : NetworkMQTTSNMessageSender {

    private val transport: UDPNIOTransport = UDPNIOTransportBuilder.newInstance().build()

    init {

        transport.processor = FilterChainBuilder.stateless()
            .add(TransportFilter())
            .add(MQTTSNFilter(messageBuilder))
            .build()

        transport.start()

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

    override suspend fun send(networkContext: NetworkContext, mqttsnMessage: MQTTSNMessage) {

        val connection: Connection<Any> = awaitCallback {
            transport.connect(networkContext.destination, networkContext.source, it)
        }

        awaitCallback { connection.write(mqttsnMessage, it) }

        connection.awaitClosure()

    }

}