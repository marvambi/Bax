package com.marvambi.degrande.ui.main

import com.marvambi.degrande.common.BaseMvpView
import com.marvambi.degrande.datas.ChatRoom


interface MainMvpView : BaseMvpView {

    fun onLoadChatRooms(chatRooms: List<ChatRoom>)
}