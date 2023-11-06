package net.farsystem.mqttsngatek

import org.eclipse.paho.client.mqttv3.MqttPingSender
import org.eclipse.paho.client.mqttv3.internal.ClientComms

/**
 * No-op ping sender that simply consumes and disables the automatic keep-alive behaviors of the default Paho MQTT
 * async client implementation. All keep-alive pings should come directly from connected MQTT-SN clients and should be
 * forwarded to the MQTT broker.
 */
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