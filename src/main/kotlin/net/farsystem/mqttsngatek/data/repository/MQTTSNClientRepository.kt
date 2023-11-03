package net.farsystem.mqttsngatek.data.repository

import net.farsystem.mqttsngatek.model.MQTTSNClient
import net.farsystem.mqttsngatek.model.NetworkContext

interface MQTTSNClientRepository {
    suspend fun removeClient(client: MQTTSNClient)
    suspend fun getClient(clientId: String): MQTTSNClient?
    suspend fun getClient(networkContext: NetworkContext): MQTTSNClient?
    suspend fun addOrUpdateClient(client: MQTTSNClient)
}