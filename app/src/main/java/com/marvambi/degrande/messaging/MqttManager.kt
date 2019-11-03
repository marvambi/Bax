package com.marvambi.degrande.messaging

import com.marvambi.degrande.common.EndPoint.ENDPOINT_MQTT_BROKER
import com.marvambi.degrande.datas.Author
import com.marvambi.degrande.ui.chat.ChatActivity
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.MqttConnectOptions
import org.eclipse.paho.client.mqttv3.MqttMessage
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence
import java.security.Security


class MqttManager(
        val author: Author,
        private val topic: String,
        private val actionOnSubscribed: (String, MqttMessage) -> Unit
) {

    companion object {
        private const val QOS_LEVEL = 2
        private const val INTERVAL_KEEP_ALIVE = 1000 * 60 * 60 * 24
        // CREDENTIALS : Set cloud/local broker access for organisation
        private const val ACCOUNT_USER_NAME = "chbfqjyv"
        private const val ACCOUNT_PASSWORD = "lue2boO9Srb9"
    }

    private val mqttClient: MqttClient by lazy {
        MqttClient(ENDPOINT_MQTT_BROKER, author.id +"/" + author.name, MemoryPersistence())
    }

    private val connectOptions: MqttConnectOptions by lazy {
        MqttConnectOptions().apply {
            isCleanSession = false
            isAutomaticReconnect = true
            userName = ACCOUNT_USER_NAME
            password = ACCOUNT_PASSWORD.toCharArray()
        }
    }

    tailrec fun connect(): Boolean {
        return if (mqttClient.isConnected) {
            subscribeOnTopic()

            true
        } else {
            mqttClient.connect(connectOptions)

            connect()
        }
    }

    fun disconnect() = if (mqttClient.isConnected) mqttClient.disconnect() else Unit


    fun publish(message: String) = mqttClient.publish(topic, message.toByteArray(), QOS_LEVEL, true)


    private fun subscribeOnTopic() = mqttClient.subscribe(topic, QOS_LEVEL, actionOnSubscribed)

}
