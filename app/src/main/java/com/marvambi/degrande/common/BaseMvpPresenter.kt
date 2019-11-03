package com.marvambi.degrande.common

interface BaseMvpPresenter<T : BaseMvpView> {

    fun attachView(view: T)

    fun destroy()
}