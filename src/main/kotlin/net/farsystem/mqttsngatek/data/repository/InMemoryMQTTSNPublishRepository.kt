package net.farsystem.mqttsngatek.data.repository

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.farsystem.mqttsngatek.MQTTSNPublish
import net.farsystem.mqttsngatek.model.MQTTSNClient

class InMemoryMQTTSNPublishRepository : MQTTSNPublishRepository {

    private val mutex = Mutex()

    private val publishesByClient = mutableMapOf<MQTTSNClient, MutableMap<Int, MQTTSNPublish>>()

    override suspend fun put(mqttsnClient: MQTTSNClient, registrationMessageId: Int, mqttsnPublish: MQTTSNPublish) {
        mutex.withLock {
            publishesByClient[mqttsnClient]?.get(registrationMessageId) ?: run {
                publishesByClient[mqttsnClient]?.let {
                    it[registrationMessageId] = mqttsnPublish
                } ?: run {
                    publishesByClient[mqttsnClient] = mutableMapOf(Pair(registrationMessageId, mqttsnPublish))
                }
            }
        }
    }

    override suspend fun get(mqttsnClient: MQTTSNClient, registrationMessageId: Int) = mutex.withLock {
        publishesByClient[mqttsnClient]?.get(registrationMessageId)
    }
}