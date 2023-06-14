package net.farsystem.mqttsngatek.data.repository

import net.farsystem.mqttsngatek.model.MQTTSNClient
import net.farsystem.mqttsngatek.model.NetworkContext

interface MQTTSNClientRepository {
    fun getClientById(clientId: String): MQTTSNClient?
    fun getClientByContext(networkContext: NetworkContext): MQTTSNClient?
}