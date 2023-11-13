package net.farsystem.mqttsngatek.data.repository

import net.farsystem.mqttsngatek.MQTTSNPublish
import net.farsystem.mqttsngatek.MQTTSNWillTopic
import net.farsystem.mqttsngatek.model.MQTTSNClient

interface MQTTSNWillRepository {
    suspend fun putPendingWillTopic(mqttsnClient: MQTTSNClient, topic: MQTTSNWillTopic)
    suspend fun getPendingWillTopic(mqttsnClient: MQTTSNClient): MQTTSNWillTopic?
    suspend fun getWill(mqttsnClient: MQTTSNClient): MQTTSNPublish?
    suspend fun putWill(mqttsnClient: MQTTSNClient, will: MQTTSNPublish)
    suspend fun removeWill(mqttsnClient: MQTTSNClient)
}