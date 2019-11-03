package com.marvambi.degrande.datas

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import org.joda.time.DateTime

@RealmClass
open class MessageHistory(): RealmObject() {
    @PrimaryKey
    var id: String = ""
    var author: String = ""
    var topic: String = ""
    var txtMsg: String = ""
    var createAt: Long = DateTime.now().millis
}

