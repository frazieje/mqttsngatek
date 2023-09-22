package net.farsystem.mqttsngatek.data.repository

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import net.farsystem.mqttsngatek.model.MQTTSNClient
import net.farsystem.mqttsngatek.model.MQTTSNTopic
import org.slf4j.LoggerFactory

class InMemoryMQTTSNTopicRepository(
    predefinedTopics: Map<String, Int>
) : MQTTSNTopicRepository {

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val mutex = Mutex()

    private val topicsByClient = mutableMapOf<MQTTSNClient, MutableMap<String, MQTTSNTopic>>()

    private val predefinedTopicsByTopic = predefinedTopics.mapValues { MQTTSNTopic(it.key, it.value, true) }

    private val predefinedTopicsById = predefinedTopics.entries.associateBy(
        { it.value },
        { MQTTSNTopic(it.key, it.value, true) }
    )

    private var lastId: UShort = 1u

    override suspend fun getOrCreateTopic(client: MQTTSNClient, topic: String): MQTTSNTopic = mutex.withLock {
        topicsByClient[client]?.get(topic) ?: run {
            val newTopic = MQTTSNTopic(topic, (++lastId).toInt())
            topicsByClient[client] = mutableMapOf(Pair(topic, newTopic))
            newTopic
        }
    }

    override suspend fun getPredefinedTopic(id: Int): MQTTSNTopic? = predefinedTopicsById[id]

    override suspend fun getPredefinedTopic(topic: String): MQTTSNTopic? = predefinedTopicsByTopic[topic]

    override suspend fun getAllTopics(client: MQTTSNClient): List<MQTTSNTopic> =
        topicsByClient[client]?.values?.toList() ?: emptyList()

    override suspend fun removeTopic(client: MQTTSNClient, topic: MQTTSNTopic) {
        mutex.withLock {
            topicsByClient[client]?.remove(topic.topic)
        }
    }

    override suspend fun removeAllTopics(client: MQTTSNClient) {
        mutex.withLock {
            topicsByClient[client]?.clear()
        }
    }
}