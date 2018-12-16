package com.a201544071.health

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_home.*

class Home : AppCompatActivity() {

    private var log:String?=null
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        if (intent.hasExtra("loginuser")) {  //인텐트로 전달된 값이 있을때에만 화면에 파일 경로 출력.
            log=intent.getStringExtra("loginuser")
            Loginuser.text = intent.getStringExtra("loginuser")
            //"loginuser"라는 이름의 인텐트에 저장된 값이 있다면
            // 화면에 로그인한 유저의 정보를 표시하는 Loginuser의 내용을 "filename"인텐트에 담긴 값 유저 ID로 변환
        } else {
            Toast.makeText(this, "전달된 아이디가 없습니다", Toast.LENGTH_SHORT).show()
        }

        btnMaps.setOnClickListener {
            var nextIntent= Intent(this, MapsActivity::class.java)
            //다른 액티비티(MapResult) 전환시 인텐트를 통해 해당 유저의 아이디를 전달할 인텐트 객체 생성.
            val putExtra = nextIntent.putExtra("loginuser",Loginuser.text.toString())
            //인텐트 객체의 outExtra클래스를 통해 해당 데이터의 title을 지정(loginuser)하여 사용
            //인텐트의 title을 호출 시(getStringExtra:문자열 데이터) 인텐트에 담긴 데이터 읽기
            //사용자가 입력한 아이디는 다음 사용할 기능들에 공통적으로 필요한 데이터이므로 인텐트에 담기
            startActivity(nextIntent)  //위에서 인텐트를 전달하기로한 로드맵 액티비티를 실행.
        }
        btnCal.setOnClickListener {
            var nextIntent= Intent(this, Calory::class.java)
            //다른 액티비티(MapResult) 전환시 인텐트를 통해 해당 유저의 아이디를 전달할 인텐트 객체 생성.
            val putExtra = nextIntent.putExtra("loginuser",Loginuser.text.toString())
            //인텐트 객체의 outExtra클래스를 통해 해당 데이터의 title을 지정(loginuser)하여 사용
            //인텐트의 title을 호출 시(getStringExtra:문자열 데이터) 인텐트에 담긴 데이터 읽기
            //사용자가 입력한 아이디는 다음 사용할 기능들에 공통적으로 필요한 데이터이므로 인텐트에 담기
            startActivity(nextIntent)  //위에서 인텐트를 전달하기로한 로드맵 액티비티를 실행.
        }
        btnMapRecord.setOnClickListener {
            var nextIntent= Intent(this, MapRecord::class.java)
            //다른 액티비티(MapResult) 전환시 인텐트를 통해 해당 유저의 아이디를 전달할 인텐트 객체 생성.
            val putExtra = nextIntent.putExtra("loginuser",Loginuser.text.toString())
            //인텐트 객체의 outExtra클래스를 통해 해당 데이터의 title을 지정(loginuser)하여 사용
            //인텐트의 title을 호출 시(getStringExtra:문자열 데이터) 인텐트에 담긴 데이터 읽기
            //사용자가 입력한 아이디는 다음 사용할 기능들에 공통적으로 필요한 데이터이므로 인텐트에 담기
            startActivity(nextIntent)  //위에서 인텐트를 전달하기로한 로드맵 액티비티를 실행.
        }
        btn_Order.setOnClickListener {
            var nextIntent= Intent(this, ProductOrder::class.java)
            //다른 액티비티(MapResult) 전환시 인텐트를 통해 해당 유저의 아이디를 전달할 인텐트 객체 생성.
            val putExtra = nextIntent.putExtra("loginuser",Loginuser.text.toString())
            //인텐트 객체의 outExtra클래스를 통해 해당 데이터의 title을 지정(loginuser)하여 사용
            //인텐트의 title을 호출 시(getStringExtra:문자열 데이터) 인텐트에 담긴 데이터 읽기
            //사용자가 입력한 아이디는 다음 사용할 기능들에 공통적으로 필요한 데이터이므로 인텐트에 담기
            startActivity(nextIntent)  //위에서 인텐트를 전달하기로한 상품 주문 액티비티를 실행.
        }

    }
}
