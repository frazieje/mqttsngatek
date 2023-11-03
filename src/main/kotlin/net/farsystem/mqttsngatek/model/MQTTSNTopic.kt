package net.farsystem.mqttsngatek.model

import net.farsystem.mqttsngatek.MQTTSNTopicType

data class MQTTSNTopic(val type: MQTTSNTopicType, val topic: String, val id: Int? = 0)
