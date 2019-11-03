package com.marvambi.degrande.common

import com.marvambi.degrande.datas.Message

interface BaseMvpView {
    fun initPresenter()
    fun update(message: Message)

}