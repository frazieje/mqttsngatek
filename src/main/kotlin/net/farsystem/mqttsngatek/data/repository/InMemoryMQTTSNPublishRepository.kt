package net.farsystem.mqttsngatek.data.repository

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.farsystem.mqttsngatek.MQTTSNPublish
import net.farsystem.mqttsngatek.model.MQTTSNClient

class InMemoryMQTTSNPublishRepository : MQTTSNPublishRepository {

    private val pubMutex = Mutex()

    private val publishesByClient = mutableMapOf<MQTTSNClient, MutableMap<Int, MQTTSNPublish>>()

    override suspend fun putPendingPublish(mqttsnClient: MQTTSNClient, registrationMessageId: Int, mqttsnPublish: MQTTSNPublish) {
        pubMutex.withLock {
            publishesByClient[mqttsnClient]?.get(registrationMessageId) ?: run {
                publishesByClient[mqttsnClient]?.let {
                    it[registrationMessageId] = mqttsnPublish
                } ?: run {
                    publishesByClient[mqttsnClient] = mutableMapOf(Pair(registrationMessageId, mqttsnPublish))
                }
            }
        }
    }

    override suspend fun getPendingPublish(mqttsnClient: MQTTSNClient, registrationMessageId: Int) = pubMutex.withLock {
        publishesByClient[mqttsnClient]?.get(registrationMessageId)
    }
}