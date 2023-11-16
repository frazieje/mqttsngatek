package net.farsystem.mqttsngatek.data.repository

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.farsystem.mqttsngatek.MQTTSNConnect
import net.farsystem.mqttsngatek.MQTTSNPublish
import net.farsystem.mqttsngatek.MQTTSNWillTopic
import net.farsystem.mqttsngatek.model.MQTTSNClient

class InMemoryMQTTSNWillRepository : MQTTSNWillRepository {

    private val willMutex = Mutex()
    private val pendingWillTopics = mutableMapOf<MQTTSNClient, MQTTSNWillTopic>()
    private val pendingConnects = mutableMapOf<MQTTSNClient, MQTTSNConnect>()
    override suspend fun putPendingConnect(mqttsnClient: MQTTSNClient, connect: MQTTSNConnect) = willMutex.withLock {
        pendingConnects[mqttsnClient] = connect
    }
    override suspend fun getPendingConnect(mqttsnClient: MQTTSNClient): MQTTSNConnect? = pendingConnects[mqttsnClient]
    override suspend fun putPendingWillTopic(mqttsnClient: MQTTSNClient, topic: MQTTSNWillTopic) = willMutex.withLock {
        pendingWillTopics[mqttsnClient] = topic
    }
    override suspend fun getPendingWillTopic(mqttsnClient: MQTTSNClient): MQTTSNWillTopic? = pendingWillTopics[mqttsnClient]
}