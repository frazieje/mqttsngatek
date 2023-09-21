package net.farsystem.mqttsngatek.data.repository

import kotlinx.coroutines.sync.Mutex
import net.farsystem.mqttsngatek.model.MQTTSNClient
import net.farsystem.mqttsngatek.model.MQTTSNTopic
import org.slf4j.LoggerFactory

class InMemoryMQTTSNTopicRepository(
    private val predefinedTopics: Map<String, Int>
) : MQTTSNTopicRepository {

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val mutex = Mutex()

    private val topicsByClient = mutableMapOf<MQTTSNClient, MQTTSNTopic>()

    override suspend fun getOrCreateTopic(client: MQTTSNClient, topic: String): MQTTSNTopic {
        TODO("Not yet implemented")
    }

    override suspend fun getTopic(id: Int): MQTTSNTopic? {
        TODO("Not yet implemented")
    }

    override suspend fun getAllTopics(client: MQTTSNClient): List<MQTTSNTopic> {
        TODO("Not yet implemented")
    }

    override suspend fun removeTopic(client: MQTTSNClient, topic: MQTTSNTopic) {
        TODO("Not yet implemented")
    }

    override suspend fun removeAllTopics(client: MQTTSNClient) {
        TODO("Not yet implemented")
    }
}