package com.a201544071.health

import java.io.IOException
import java.sql.Connection
import java.sql.DriverManager
import java.sql.ResultSet
import java.sql.Statement

class DB_MAN {
    internal var strDriver = "oracle.jdbc.driver.OracleDriver"
    //String strURL="jdbc:oracle:thin:@10.40.41.55:1521";
    internal var strURL = "jdbc:oracle:thin:@172.30.1.4:1521"
    internal var strUser = "INHATC"
    internal var strPWD = "inha1958"

    internal lateinit var DB_con: Connection  //db연결
    internal lateinit var DB_stmt: Statement //db상태
    internal var DB_rs: ResultSet? = null  //db실행 결과

    @Throws(IOException::class)
    fun dbOpen() {
        try {
            Class.forName(strDriver)
            DB_con = DriverManager.getConnection(strURL, strUser, strPWD)
            DB_stmt = DB_con.createStatement()
        } catch (e: Exception) {
            println("SQLException : " + e.message)
        }

    }

    @Throws(IOException::class)
    fun dbClose() {
        try {
            DB_stmt.close()
            DB_con.close()
        } catch (e: Exception) {
            println("SQLException : " + e.message)
        }

    }
}
