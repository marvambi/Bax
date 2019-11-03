package com.marvambi.degrande.ui.main

import com.marvambi.degrande.common.BaseMvpPresenter
import com.marvambi.degrande.common.BaseMvpView
import com.marvambi.degrande.datas.ChatRoom


class MainPresenter<MvpView : BaseMvpView> : BaseMvpPresenter<MvpView> {

    companion object {
        val defaultChatRooms = listOf(
                ChatRoom(name = "Kitchen"),
                ChatRoom(name = "Laundry"),
                ChatRoom(name = "Maintenance"),
                ChatRoom(name = "Security")
        )
    }

    private lateinit var view: MainMvpView

    override fun attachView(view: MvpView) {
        this.view = view as MainMvpView
    }

    override fun destroy() {}

    fun loadChatRooms(): Unit = view.onLoadChatRooms(defaultChatRooms)

}