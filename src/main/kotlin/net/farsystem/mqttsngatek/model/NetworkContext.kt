package net.farsystem.mqttsngatek.model

import java.net.InetAddress

data class NetworkContext(
    val protocol: NetworkProtocol,
    val sourceAddress: InetAddress,
    val sourcePort: Int,
    val destinationAddress: InetAddress,
    val destinationPort: Int
)