package net.farsystem.mqttsngatek.data.repository

import net.farsystem.mqttsngatek.model.MQTTSNClient
import net.farsystem.mqttsngatek.model.MQTTSNTopic

interface MQTTSNTopicRepository {

    suspend fun getOrCreateTopic(client: MQTTSNClient, topic: String): MQTTSNTopic

    suspend fun getTopic(id: Int): MQTTSNTopic?

    suspend fun getAllTopics(client: MQTTSNClient): List<MQTTSNTopic>

    suspend fun removeTopic(client: MQTTSNClient, topic: MQTTSNTopic)

    suspend fun removeAllTopics(client: MQTTSNClient)

}