package com.marvambi.degrande.messaging

interface UIUpdateInterface {

    fun resetUIWithConnection(status: Boolean)
    fun updateStatusViewWith(status: String)
    fun update(message: String)
}