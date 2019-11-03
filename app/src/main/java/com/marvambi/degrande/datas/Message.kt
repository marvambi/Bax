package com.marvambi.degrande.datas

import com.stfalcon.chatkit.commons.models.IMessage
import org.joda.time.DateTime
import java.util.*

data class Message(
        private var id: String = "",
        private var author: Author,
        private var topic: String = "",
        private var text: String = "",
        private var createdAt: Long = DateTime.now().millis


) : IMessage {
    override fun getId() = id
    override fun getUser() = author

    override fun getText() = text

    override fun getCreatedAt() = Date(createdAt)

    fun getTopic() = topic


}
