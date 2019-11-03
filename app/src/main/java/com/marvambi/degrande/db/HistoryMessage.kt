package com.marvambi.degrande.db

import com.marvambi.degrande.datas.Author
import org.joda.time.DateTime

class HistoryMessage {
    var id: String = ""
    var author: Author = Author()
    var topic: String = ""
    var text: String = ""
    var createAt: Long = DateTime.now().millis

    constructor(id: String, author: Author, topic: String, text: String, createAt: Long) {
        this.id = id
        this.author = author
        this.topic = topic
        this.text = text
        this.createAt = createAt

    }
}