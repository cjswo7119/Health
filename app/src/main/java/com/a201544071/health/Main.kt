package com.a201544071.health

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity

class Main : AppCompatActivity() {
    val realm= Realm.getDefaultInstance()  //realm디비 연동을 위한 인스턴스 객체 얻기

    private fun Login(id:String):Boolean{
    //로그인 시 매개변수로 입력받은 아이디를 조회. 해당 유저의 데이터와 동일한지 확인하기 위한 함수.
        try {
            realm.beginTransaction()  //트랜잭션 시작.
            val selectUser = realm.where<Client>().equalTo("id", id).findFirst()!!
            //Client 테이블의 기본키인 id를 통해 테이블에서 id를 조회 후 첫번째 데이터(튜플)를 반환.
            //realm객체의 findFirst()메서드는 해당하는 컬럼의 첫번째 데이터(튜플) 반환.
            //기본키는 테이블에서 중복을 허용하지 않는 유일한 값이기 때문에 테이블에는 하나의 데이터(튜플)만 가져와도 문제없음.
            if(Login_ID.text.toString().equals(selectUser.id)){
            //입력받은 아이디를 조회 후 결과가 있다면 화면에 입력한 아이디와 일치. 즉, 테이블에 해당 아이디가 존재.
            //만일 입력받은 아이디와 조회한 결과가 없다면 테이블에 해당 아이디가 존재한지 않으므로 로그인 실패.
                if(Login_PW.text.toString().equals(selectUser.password)){  //아이디를 검사 후 비밀번호 검사.
                    realm.commitTransaction()  //트랜잭션 닫기.
                    return true
                }else LoginErr.text="비밀번호가 일치하지 않습니다."
                realm.commitTransaction()  //트랜잭션 닫기.
            }
            return false
        }catch(e:Exception){
            realm.commitTransaction()  //트랜잭션 닫기.
            LoginErr.text="아이디를 확인하여 주십시요."
            return false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_Join.setOnClickListener {//회원가입 클릭 리스너
            startActivity<Join>()  //회원가입 액티비티 이동
        }
        btn_Login.setOnClickListener {
            if(Login(Login_ID.text.toString())){
                var nextIntent= Intent(this, Home::class.java)
                //다른 액티비티(MapResult) 전환시 인텐트를 통해 해당 유저의 아이디를 전달할 인텐트 객체 생성.
                val putExtra = nextIntent.putExtra("loginuser",Login_ID.text.toString())
                //인텐트 객체의 outExtra클래스를 통해 해당 데이터의 title을 지정(loginuser)하여 사용
                //인텐트의 title을 호출 시(getStringExtra:문자열 데이터) 인텐트에 담긴 데이터 읽기
                //사용자가 입력한 아이디는 다음 사용할 기능들에 공통적으로 필요한 데이터이므로 인텐트에 담기
                startActivity(nextIntent)  //위에서 인텐트를 전달하기로한 MapResult 액티비티를 실행.
            }
        }
    }
}
