package com.marvambi.degrande.ui.chat

import com.marvambi.degrande.common.BaseMvpView
import com.marvambi.degrande.datas.Message
import com.stfalcon.chatkit.messages.MessageInput
import com.stfalcon.chatkit.messages.MessagesListAdapter

interface ChatMvpView :
        BaseMvpView,
        MessageInput.InputListener,
        MessagesListAdapter.OnMessageViewClickListener<Message> {

    fun onReceiveMessage(message: Message)
    fun onSuccessConnect()
    fun onErrorConnect()
}
