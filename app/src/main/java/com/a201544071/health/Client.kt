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

open class OrderList : RealmObject() {
    @PrimaryKey var no:Int?=null
    var id:String?=null
    var count:Int?=0
    var price:Double? =0.0
    var address: String?=null
}