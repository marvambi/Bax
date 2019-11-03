package com.marvambi.degrande.db

import android.provider.BaseColumns

object DBContract {

    /* Inner class that defines the table contents */
    class MessageEntry : BaseColumns {
        companion object {
            val TABLE_NAME = "messages"
            val COLUMN_ID = "id"
            val COLUMN_AUTHOR = "author"
            val COLUMN_TOPIC = "topic"
            val COLUMN_TEXT = "text"
            val COLUMN_CREATEAT = "createAt"

        }
    }
}