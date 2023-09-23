package net.farsystem.mqttsngatek.model

import java.net.InetSocketAddress

data class NetworkContext(
    val protocol: NetworkProtocol,
    val source: InetSocketAddress,
    val destination: InetSocketAddress,
) {
    companion object {
        fun NetworkContext.flip(): NetworkContext =
            NetworkContext(protocol, destination, source)
    }
}