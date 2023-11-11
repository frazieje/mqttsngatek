package net.farsystem.mqttsngatek.data.repository

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.farsystem.mqttsngatek.GatewayConfig
import net.farsystem.mqttsngatek.model.MQTTSNClient
import net.farsystem.mqttsngatek.mqtt.MQTTClient
import net.farsystem.mqttsngatek.mqtt.MQTTClientFactory

class InMemoryMQTTClientRepository(
    private val gatewayConfig: GatewayConfig,
    private val mqttClientFactory: MQTTClientFactory,
) : MQTTClientRepository {

    private val mutex = Mutex()

    private val mqttClients = mutableMapOf<MQTTSNClient, MQTTClient>()
    override suspend fun get(snClient: MQTTSNClient): MQTTClient? = mqttClients[snClient]

    override suspend fun getOrCreate(snClient: MQTTSNClient): MQTTClient = mutex.withLock {
        mqttClients[snClient] ?: mqttClientFactory.getClient(
            snClient.clientId,
            gatewayConfig.broker(),
            gatewayConfig.brokerPort()
        ).also {
            mqttClients[snClient] = it
        }
    }

    override suspend fun remove(snClient: MQTTSNClient) = mutex.withLock {
        mqttClients.remove(snClient)
        Unit
    }

    override suspend fun removeAll() = mutex.withLock {
        mqttClients.clear()
    }
}