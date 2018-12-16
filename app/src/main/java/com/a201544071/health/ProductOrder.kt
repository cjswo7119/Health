package com.a201544071.health

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import io.realm.Realm
import kotlinx.android.synthetic.main.activity_product_order.*
import java.util.regex.Pattern


class ProductOrder : AppCompatActivity() {
    val realm= Realm.getDefaultInstance()
    var loginID:String?=null
    /*private fun insertOrder() {
        alert(
            "가격 :" + totPrice.text.toString().toLong() + "\n" + "주소 :" + txtAddress.text.toString() + "\n",
            "주문을 진행하시겠습니까?"
        ) {
            yesButton {
                realm.beginTransaction()  //트랜잭션 시작

                val newOrder = realm.createObject<Client>(nextNo())
                //자체 생성한 메서드(nextNo)를 통해 기본키인 주문번호 중 가장 큰값을 조회하여 데이터가 있을 경우 더하기 1을하여 데이터 추가.
                newOrder.id = loginID  //주문한 현재 로그인 된 사용자의 로그인 아이디 정보 전달
                newOrder.count = ProdCnt.text.toString().toInt()  //상품 주문한 갯수 전달
                newOrder.price = totPrice.text.toString().toLong()  //결제 금액을 전달.
                newOrder.address = txtAddress.text.toString() //입력한 주소를 전달.

                realm.commitTransaction()  //트랜잭션 종료 반영
                finish()  //해당 액티비티 닫기
                toast("확인")
            }
            noButton { toast("취소") }
        }.show()
    }

    private fun nextNo():Int{
    //상품 주문 시 기본키인 상품 주문번호를 조회하여 가장 높은 수를 찾은뒤 1을 더하여 다음주문 추가
        val maxNo=realm.where<Client>().max("no")
        //realm 객체의 max를 메서드를 통해 해당 컬럼의 가장 큰값을 반환.
        if(maxNo!=null){   //테이블에 값이 있을 경우
            return maxNo.toInt()+1  //가장 큰 주문번호에 더하기 1
        }
        return 1  //테이블에 데이터가 없을 경우 1 반환
    }*/
    fun isAddress(word: String): Boolean {  //주소검사 (한글, 숫자, 공백(뛰어쓰기, -(하이푼))
        return Pattern.matches("(^[가-힣0-9\\s-]*$)", word)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_order)
        /*var nextIntent= Intent(this, Home::class.java)
        //다른 액티비티(MapResult) 전환시 인텐트를 통해 해당 유저의 아이디를 전달할 인텐트 객체 생성.
        val putExtra = nextIntent.putExtra("loginuser",Loginuser.text.toString())
        //인텐트 객체의 outExtra클래스를 통해 해당 데이터의 title을 지정(loginuser)하여 사용
        //인텐트의 title을 호출 시(getStringExtra:문자열 데이터) 인텐트에 담긴 데이터 읽기
        //사용자가 입력한 아이디는 다음 사용할 기능들에 공통적으로 필요한 데이터이므로 인텐트에 담기
*/
        if (intent.hasExtra("loginuser")) {  //인텐트로 전달된 값이 있을때에만 화면에 파일 경로 출력.
            loginID= intent.getStringExtra("loginuser")
            //"loginuser"라는 이름의 인텐트에 저장된 값이 있다면
            // 화면에 로그인한 유저의 정보를 표시하는 Loginuser의 내용을 "filename"인텐트에 담긴 값 유저 ID로 변환
        } else {
            Toast.makeText(this, "전달된 아이디가 없습니다", Toast.LENGTH_SHORT).show()
        }

        val edit = findViewById(R.id.ProdCnt) as EditText
        edit.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {  //텍스트뷰가 변할때마다 실행되는 메서드
                if(ProdCnt.text.toString().length!=0){
                    totPrice.text= (ProdCnt.text.toString().toInt()*5500).toString()  //입력이 완료된 후 수량에 맞는 결재금액 출력.
                }
                else totPrice.text="0"
            }//입력 시 변화하는 메서드
            override fun afterTextChanged(arg0: Editable) {  //입력이 완료될 때 실행되는 메서드
                if(ProdCnt.text.toString().length!=0){
                    totPrice.text= (ProdCnt.text.toString().toInt()*5500).toString()  //입력이 완료된 후 수량에 맞는 결재금액 출력.
                }
                else totPrice.text="0"
            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {  //입력 전 변화하는 메서드
                if(ProdCnt.text.toString().length!=0){
                    totPrice.text= (ProdCnt.text.toString().toInt()*5500).toString()  //입력이 완료된 후 수량에 맞는 결재금액 출력.
                }
                else totPrice.text="0"
            }
        })

        OrderUser.setOnClickListener {
            if(ProdCnt.text.toString().length==0){
                txtOrdErr.text="구매수량을 확인하여 주시기 바랍니다."
                return@setOnClickListener
            }
            else if(ProdCnt.text.toString().toInt()>10){
                txtOrdErr.text="죄송합니다. 한번 최대 구매 수량은 10개입니다."
                return@setOnClickListener
            }
            //=======================================================================
            if(txtAddress.text.toString().length==0){  //주소가 입력되지 않은 경우
                txtOrdErr.text="주소를 입력받지 못했습니다."
                return@setOnClickListener
            }
            if(!isAddress(txtAddress.text.toString())){  //한글(완성형), 숫자, 뛰어쓰기, 하이푼 여부 확인.
                txtOrdErr.text="입력된 주소를 확인하여 주시기 바랍니다."
                return@setOnClickListener
            }
            //=======================================================================
            //insertOrder()
        }
    }
    override fun onDestroy(){
        super.onDestroy()
        realm.close()  //인스턴스 해제
    }
}
