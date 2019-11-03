package com.marvambi.degrande.db

import com.marvambi.degrande.datas.Author

class MessageModel (val id: String, val author: Author, val topic: String, val text: String, val createAt: Long)