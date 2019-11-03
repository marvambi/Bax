package com.marvambi.degrande.datas

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import io.realm.annotations.RealmClass
import org.joda.time.DateTime


// I Define the Messages model class by extending RealmObject
@RealmClass
open class Messages: RealmObject() {
    @PrimaryKey
    var id: String = ""
    var author: String = ""
    var topic: String = ""
    var txtMsg: String = ""
    var createAt: Long = DateTime.now().millis
}