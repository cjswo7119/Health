package com.a201544071.health

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Client (
    @PrimaryKey var id:String?=null,
    var password:String?=null,
    var name:String?=null,
    var birth:String?=null,
    var gender:String?=null,
    var blood:String?=null,
    var height:Double=0.0,
    var weight:Double=0.0
): RealmObject(){
}