package com.marvambi.degrande.ui.main

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.marvambi.degrande.R
import com.marvambi.degrande.common.BaseActivity
import com.marvambi.degrande.datas.ChatRoom
import com.marvambi.degrande.datas.Message
import com.marvambi.degrande.ui.chat.ChatActivity
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity(), MainMvpView {
    override fun update(message: Message) {
        //
    }

    private lateinit var presenter: MainPresenter<MainMvpView>
    private lateinit var chatRoomAdapter: ChatRoomAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Realm.init(this)

        initLayout()
    }

    override fun initPresenter() {
        presenter = MainPresenter()
        presenter.attachView(this)
    }

    override fun onLoadChatRooms(chatRooms: List<ChatRoom>) = chatRoomAdapter.addItems(chatRooms)

    private fun initLayout() {
        chatRoomAdapter = ChatRoomAdapter {
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("title", it.name)
            startActivity(intent)
        }

        rvChatRooms.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvChatRooms.adapter = chatRoomAdapter

        presenter.loadChatRooms()
    }

    override fun onPause() {
        //d
        Log.w("MainActivity", "Pausing Main Activity")
        super.onPause()
    }


}
