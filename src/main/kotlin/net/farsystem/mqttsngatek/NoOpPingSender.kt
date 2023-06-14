package net.farsystem.mqttsngatek

import org.eclipse.paho.client.mqttv3.MqttPingSender
import org.eclipse.paho.client.mqttv3.internal.ClientComms

class NoOpPingSender : MqttPingSender {
    override fun init(comms: ClientComms?) {
        //nothing
    }

    override fun start() {
        //do nothing
    }

    override fun stop() {
        //do nothing
    }

    override fun schedule(delayInMilliseconds: Long) {
        //do nothing
    }
}