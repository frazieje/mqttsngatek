package net.farsystem.mqttsngatek.data.repository

import net.farsystem.mqttsngatek.MQTTSNPublish
import net.farsystem.mqttsngatek.model.MQTTSNClient

interface MQTTSNPublishRepository {
    suspend fun putPendingPublish(mqttsnClient: MQTTSNClient, registrationMessageId: Int, mqttsnPublish: MQTTSNPublish)
    suspend fun getPendingPublish(mqttsnClient: MQTTSNClient, registrationMessageId: Int): MQTTSNPublish?
}