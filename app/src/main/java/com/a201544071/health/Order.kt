package com.a201544071.health

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

class Order : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        /*var realmResult=realm.where<OrderList>()
            .findAll()
            .sort("data", Sort.DESCENDING)
        val adapter=list_Order(realmResult)
        list_Order.adapter=adapter
        */
    }
}
