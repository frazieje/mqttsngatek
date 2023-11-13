package net.farsystem.mqttsngatek.data.repository

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.farsystem.mqttsngatek.MQTTSNPublish
import net.farsystem.mqttsngatek.MQTTSNWillTopic
import net.farsystem.mqttsngatek.model.MQTTSNClient

class InMemoryMQTTSNWillRepository : MQTTSNWillRepository {

    private val willMutex = Mutex()
    private val willsByClient = mutableMapOf<MQTTSNClient, MQTTSNPublish>()
    private val pendingWillTopics = mutableMapOf<MQTTSNClient, MQTTSNWillTopic>()
    override suspend fun putPendingWillTopic(mqttsnClient: MQTTSNClient, topic: MQTTSNWillTopic) = willMutex.withLock {
        pendingWillTopics[mqttsnClient] = topic
    }
    override suspend fun getPendingWillTopic(mqttsnClient: MQTTSNClient): MQTTSNWillTopic? = pendingWillTopics[mqttsnClient]
    override suspend fun getWill(mqttsnClient: MQTTSNClient): MQTTSNPublish? = willsByClient[mqttsnClient]
    override suspend fun putWill(mqttsnClient: MQTTSNClient, will: MQTTSNPublish) = willMutex.withLock {
        pendingWillTopics.remove(mqttsnClient)
        willsByClient[mqttsnClient] = will
    }
    override suspend fun removeWill(mqttsnClient: MQTTSNClient) {
        willMutex.withLock {
            willsByClient.remove(mqttsnClient)
            pendingWillTopics.remove(mqttsnClient)
        }
    }
}