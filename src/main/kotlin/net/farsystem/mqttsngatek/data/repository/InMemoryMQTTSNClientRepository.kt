package net.farsystem.mqttsngatek.data.repository

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.farsystem.mqttsngatek.model.MQTTSNClient
import net.farsystem.mqttsngatek.model.NetworkContext
import org.slf4j.LoggerFactory

class InMemoryMQTTSNClientRepository : MQTTSNClientRepository {

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val mutex = Mutex()

    private val clientsById = mutableMapOf<String, MQTTSNClient>()

    private val idsByContext = mutableMapOf<NetworkContext, String>()

    override suspend fun removeClient(client: MQTTSNClient) = mutex.withLock {
        logger.debug("removing client \"${client.clientId}\"")
        clientsById.remove(client.clientId)
        idsByContext.entries.removeIf { it.value == client.clientId }
        Unit
    }

    override suspend fun getClient(clientId: String): MQTTSNClient? = mutex.withLock {
        clientsById[clientId]
    }

    override suspend fun getClient(networkContext: NetworkContext): MQTTSNClient? = mutex.withLock {
        clientsById[idsByContext[networkContext]]
    }

    override suspend fun addOrUpdateClient(
        client: MQTTSNClient,
        networkContext: NetworkContext
    ) = mutex.withLock {
        clientsById[client.clientId] = client
        idsByContext[networkContext] = client.clientId
    }
}