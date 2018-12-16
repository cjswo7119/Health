package com.a201544071.health

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.widget.ListView
import kotlinx.android.synthetic.main.activity_map_record.*
import java.io.File


class MapRecord : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_record)

        val path = File("/mnt/sdcard/foot")  //폴더경로를 저장한 변수
        if (path.exists() == false) {  //폴더가 존재하지 않다면 리턴하여 빠져나오기
            return
        }
        val files = path.listFiles()  //해당 폴더의 파일을 들을 FIle타입의 배열에 전달.

        val arrayList = ArrayList<String>()
        //안드로이드의 리스트뷰에 값을 전달하기위해서는 어댑터를 사용한다
        // 이 때 어댑터에 정보를 전달하는 수단으로 배열 형태로 자료를 전달
        //배열, 문자열 배열, 리스트 주로 사용

        for (i in files!!.indices) {  //전달받은 파일 수만큼 반복
            arrayList.add(files[i].name)  //파일명을 배열리스트에 전달.
        }

        val arrayAdt = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1, arrayList
        )  //arrayList의 데이터(파일명)를 받는 어댑터 생성.

        var listview = findViewById<ListView>(R.id.FootList)  //
        listview.setAdapter(arrayAdt)  //폴더경로 파일명을 담은 어댑터를 리스트뷰에 전달.

        FootList.setOnItemClickListener { adapterView, view, i, l ->  //리스트뷰 해당 맵기록 선택시
            var nextIntent= Intent(this, MapResult::class.java)
            //다른 액티비티(MapResult) 전환시 인텐트를 통해 파일 경로명 데이터를 전달할 인텐트 객체 생성.
            val putExtra = nextIntent.putExtra("filename",files[i].toString())
            //인텐트 객체의 outExtra클래스를 통해 해당 데이터의 title을 지정하여 사용할 인텐트의 title을 호출 시(getStringExtra:문자열 데이터) 인텐트에 담긴 데이터 읽기
            //사용자가 선택한 해당 파일의 경로(문자열)를 인텐트에 담기
            startActivity(nextIntent)  //위에서 인텐트를 전달하기로한 MapResult 액티비티를 실행.
        }
    }
}