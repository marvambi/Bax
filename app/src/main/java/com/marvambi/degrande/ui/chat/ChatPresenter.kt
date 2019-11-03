package com.marvambi.degrande.ui.chat

import com.google.gson.Gson
import com.marvambi.degrande.common.BaseMvpPresenter
import com.marvambi.degrande.common.BaseMvpView
import com.marvambi.degrande.common.RxPresenter
import com.marvambi.degrande.datas.Author
import com.marvambi.degrande.datas.Message
import com.marvambi.degrande.messaging.MqttManager
import com.marvambi.mqttchat.extensions.onMain
import io.reactivex.Single
import org.eclipse.paho.client.mqttv3.*
import org.joda.time.DateTime


class ChatPresenter<MvpView : BaseMvpView> : RxPresenter(), BaseMvpPresenter<MvpView> {


    private lateinit var view: ChatMvpView
    private lateinit var mqttManager: MqttManager

    private val gson = Gson()

    override fun attachView(view: MvpView) {
        this.view = view as ChatMvpView
    }


    override fun destroy() {
        dispose()
        mqttManager.disconnect()
    }

    fun connect(author: Author, topic: String) {
        add(Single.create<Unit> {
            mqttManager = MqttManager(author, topic, actionOnSubscribed())

            when {
                mqttManager.connect() -> it.onSuccess(Unit)
                else -> it.onError(IllegalAccessException("Fail to connect"))
            }
        }.onMain().subscribe({ view.onSuccessConnect() }, { view.onErrorConnect() }))
    }

    private fun actionOnSubscribed(): (String, MqttMessage) -> Unit {
        return { _, mqttMessage ->
            add(Single.create<Message> {
                val message: Message = gson.fromJson(mqttMessage.toString(), Message::class.java)

                it.onSuccess(message)
            }.onMain().subscribe(view::onReceiveMessage, Throwable::printStackTrace))
        }
    }

    fun sendMessage(input: CharSequence?) {
        add(Single.create<String> {
            val messageid = (1..12340).shuffled().first()
            val creationtime = DateTime.now().millis
            val message = Message(author = mqttManager.author, text = input.toString(), id = messageid.toString(), createdAt = creationtime)
            it.onSuccess(gson.toJson(message))
        }.onMain().subscribe({
            mqttManager.publish(it)
        }, {}))
    }

}