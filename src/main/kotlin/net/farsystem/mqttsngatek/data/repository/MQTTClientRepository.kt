package net.farsystem.mqttsngatek.data.repository

import net.farsystem.mqttsngatek.model.MQTTSNClient
import net.farsystem.mqttsngatek.mqtt.MQTTClient

interface MQTTClientRepository {
    suspend fun getOrCreate(snClient: MQTTSNClient): MQTTClient
    suspend fun remove(snClient: MQTTSNClient)
    suspend fun removeAll()
}