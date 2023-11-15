package net.farsystem.mqttsngatek.data.repository

import net.farsystem.mqttsngatek.MQTTSNConnect
import net.farsystem.mqttsngatek.MQTTSNWillTopic
import net.farsystem.mqttsngatek.model.MQTTSNClient

interface MQTTSNWillRepository {
    suspend fun putPendingConnect(mqttsnClient: MQTTSNClient, connect: MQTTSNConnect)
    suspend fun getPendingConnect(mqttsnClient: MQTTSNClient): MQTTSNConnect?
    suspend fun putPendingWillTopic(mqttsnClient: MQTTSNClient, topic: MQTTSNWillTopic)
    suspend fun getPendingWillTopic(mqttsnClient: MQTTSNClient): MQTTSNWillTopic?
}