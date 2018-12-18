package com.a201544071.health

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.widget.Spinner
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_join.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class Join : AppCompatActivity() {
    val realm= Realm.getDefaultInstance()

    fun isKor(word: String): Boolean {  //성명:한글체크
        return Pattern.matches("(^[가-힣]*$)", word)
    }
    fun isLower(word: String): Boolean {  //ID,PW:소문자
        return Pattern.matches("(^[a-z]*$)", word)
    }
    fun isUpper(word: String): Boolean {  //PW:소문자&숫자
        return Pattern.matches("(^[A-Z]*$)", word)
    }
    fun isNumber(word: String): Boolean {  //ID,PW:숫자
        return Pattern.matches("(^[0-9]*$)", word)
    }
    fun isSymbol(word: String): Boolean {  //PW:숫자
        return Pattern.matches("(^[~`!@#$%^&*()_+-={}:;<>,.?/|\"\'\\\\\\[\\]|]*$)", word)  //\는 앞에\\\, [,]는 \\
    }
    private fun insertClient(date:String,gender:String,blood:String,height:Double,weight:Double,security:Char,hei_message:String,wei_message:String){

        alert("보안등급 :"+security.toString()+"\n"+hei_message+"\n"+wei_message,"회원가입을 진행하시겠습니까?") {
            yesButton {
                try {
                    realm.beginTransaction()  //트랜잭션 시작
                    val newItem = realm.createObject<Client>(txt_ID.text.toString())

                    newItem.password = txt_PW.text.toString()
                    newItem.name = txt_Name.text.toString()
                    newItem.birth = date
                    newItem.gender = gender
                    newItem.blood = blood
                    newItem.height = height
                    newItem.weight = weight

                    realm.commitTransaction()
                    finish()  //해당 액티비티 닫기
                    toast("확인")
                }catch(e:Exception){
                    System.out.print("false")
                }
            }
            noButton { toast("취소") }
        }.show()

    }

    private fun isClient(id:String):Boolean{
        try {
            realm.beginTransaction()  //디비 작업 시작
            val selectUser = realm.where<Client>().equalTo("id", id).findFirst()!!
            //Client 테이블의 기본키인 id를 통해 테이블에서 id를 조회 후 첫번째 데이터(튜플)를 반환.
            //realm객체의 findFirst()메서드는 해당하는 컬럼의 첫번째 데이터(튜플) 반환.
            //기본키는 테이블에서 중복을 허용하지 않는 유일한 값이기 때문에 테이블에는 하나의 데이터(튜플)만 존재해야함.
            if(txt_ID.text.toString().equals(selectUser.id)){
            //입력받은 아이디를 조회 후 결과가 있다면 화면에 입력한 아이디와 일치. 즉, 테이블에 해당 아이디가 존재.
            //만일 조회한 결과가 없다면 테이블에 해당 아이디가 존재한지 않으므로 중복확인 결과 통과.
            realm.commitTransaction()  //디비 작업 종료
            return false  //아이디 조회결과 중복시 false
            }else return true  //겹치지 않을시 true
        }catch(e:Exception){
            realm.commitTransaction()
            //예기치 않은 종료(findFirst로 해당 아이디가 없을시 에러)는 예외처리를 통해 디비작업 중단.
            return true  //테이블에 해당 아이디가 없으므로 true
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Realm.init(this)
        setContentView(R.layout.activity_join)

        var userid:String="" //중복확인으로 확인된 아이디인지 판별하기 위한 검사용 문자열 변수
        val tz = TimeZone.getTimeZone("Asia/Seoul")  //서울 시간 파싱
        val gc = GregorianCalendar(tz)  //파싱된 정보를 캘린더에 담기
        var now_year=gc.get(GregorianCalendar.YEAR).toString()  //캘린더 정보 중 현재 년도의 정보를 담는 문자열 타입의 now_year생성.
        val year = ArrayList<String>()  //년을 담는 배열 리스트
        for(i in now_year.toInt() downTo now_year.toInt()-120) {  //현재부터 120년 전까지 반복
            year.add(i.toString()+"년")
        }
        val month = ArrayList<String>()  //월을 담는 배열 리스트
        for(i in 1 .. 12) {
            if(i<10) month.add("0" + i.toString() + "월")
            else month.add(i.toString() + "월")
        }
        val day = ArrayList<String>()  //일을 담는 배열 리스트
        for(i in 1 .. 31) {
            if(i<10) day.add("0"+i.toString()+"일")
            else day.add(i.toString()+"일")
        }

        val spnBlood= findViewById<Spinner>(R.id.spn_Blood)
        val adapter= ArrayAdapter.createFromResource(this, R.array.bloodList, android.R.layout.simple_spinner_item)
        spnBlood.adapter=adapter  //리스트에 어댑터 적용

        val arrayAdapter_Year=ArrayAdapter(this,android.R.layout.simple_spinner_item, year)
        //배열리스트의 각 인덱스 값을 스피너에 전달해주기 위해 어댑터를 선언.
        arrayAdapter_Year.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        //스피너의 항목목록을 스피너에 표시해주기 위해 스피너 레이아웃 지정.
        spn_Year.adapter=arrayAdapter_Year  //어댑터에 담긴 년도정보를 스피너에 전달

        val arrayAdapter_Month=ArrayAdapter(this,android.R.layout.simple_spinner_item, month)
        arrayAdapter_Month.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spn_Month.adapter=arrayAdapter_Month

        val arrayAdapter_Day=ArrayAdapter(this,android.R.layout.simple_spinner_item, day)
        arrayAdapter_Day.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spn_Day.adapter=arrayAdapter_Day

        btn_Check.setOnClickListener {  //중복확인 클릭 리스너
            if(isClient(txt_ID.text.toString())){  //아이디 검사
                userid=txt_ID.text.toString()
                //아이디 검사 후 회원가입 누르기 전 아이디를 수정하는 것을 대비한 검사통과 아이디 저장.
                msg_error.text = "사용가능합니다."
                return@setOnClickListener
            }
            else {
                msg_error.text = "ID가 중복입니다."
                return@setOnClickListener
            }
        }

        btnJoin.setOnClickListener {  //회원가입 클릭 리스너
            if(txt_ID.text.toString().length<5){  //ID 5자 미만 입력 시
                if(txt_ID.text.toString().length==0){  //ID 공란(입력데이터없음)일 시
                    msg_error.text = "ID를 입력받지 못했습니다."
                    return@setOnClickListener
                }
                msg_error.text = "ID는 5자 이상 15 미만 입력입니다."
                return@setOnClickListener
            }
            if((isLower(txt_ID.text.toString()) || isNumber(txt_ID.text.toString()))){  //소문자 또는 숫자로만 이루어진 경우
                msg_error.text = "ID는 소문자&숫자 조합 입력입니다."
                return@setOnClickListener
            }
            if(!userid.equals(txt_ID.text.toString())){
                msg_error.text = "중복검사를 진행해주십시요."
                return@setOnClickListener
            }
//--------------------------------------------------------------------------------------------------------------
            var security='무'
            var PW_bit=0  //2진수의 보안등급 합
            var PW_low=0  //소문자 포함시 8
            var PW_num=0  //숫자 포함시 4
            var PW_upp=0  //대문자 포함시 2
            var PW_sym=0  //특수문자 포함시 1
            var array_word = txt_PW.text.toString().split("".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray() //배열에 한글자씩 저장하기
            if(txt_PW.text.toString().length<10){  //입력한 비밀번호가 10자 미만인 경우
                if(txt_PW.text.toString().length==0){  //10자 미만 입력중 공란(비어있음)인 경우
                    msg_error.text = "PW를 입력받지 못했습니다."
                    return@setOnClickListener
                }
                msg_error.text = "PW는 10자 이상 20자 미만 입력입니다."
                return@setOnClickListener
            }
            for (i in array_word.indices) { //출력
                if(isLower(array_word[i])){  //소문자 포함여부 검사
                    PW_low=8
                }
                else if(isNumber(array_word[i])){  //숫자 포함여부 검사
                    PW_num=4
                }
                else if(isUpper(array_word[i])){  //대문자 포함여부 검사
                    PW_upp=2
                }
                else if(isSymbol(array_word[i])){  //특수문자 포함여부 검사
                    PW_sym=1
                }
            }
            PW_bit=PW_low+PW_num+PW_upp+PW_sym  //비트연산의 합을 통한 보안등급 결정.
            if(PW_bit==15){  //소문자, 숫자, 대문자, 특수문자 포함인 경우
                security='상'
            }
            else if(PW_bit>=13 && PW_bit<=14){  //소문자, 숫자를 포함하며 대문자 또는 특수문자를 포함한 경우
                security='중'
            }
            else if(PW_bit==12){  //소문자, 숫자만을 포함한 경우
                security='하'
            }
            else if(PW_bit<12){  //소문자또는 숫자가 포함되지 않은 경우
                msg_error.text = "PW는 소문자&숫자 조합 입력이 필수입니다."
                return@setOnClickListener
            }
//-----------------------------------------------------------------------------------------------------------------
            if(!(txt_PW.text.toString().equals(txt_PWcheck.text.toString()))){
                msg_error.text = "PW 불일치"
                txt_PWcheck.setText("")
                return@setOnClickListener
            }
//-----------------------------------------------------------------------------------------------------------------
            var abc=txt_Name.text.toString()

            if(abc.length<2){  //입력한 성명이 2자 미만인 경우
                if(abc.toString().length==0){  //2자 미만 입력중 공란(비어있음)인 경우
                    msg_error.text = "성명을 입력받지 못했습니다."
                    return@setOnClickListener
                }
                msg_error.text = "성명은 2자 이상 20자 미만 입력입니다."
                return@setOnClickListener
            }
            if(!isKor(abc)) {  //한글(완성형)을 입력받지 못했을 때
                msg_error.text = "성명은 한글로만 입력 가능합니다"
                return@setOnClickListener
            }
//------------------------------------------------------------------------------------------------------------------
            val date=spn_Year.selectedItem.toString().substring(0,4)+spn_Month.selectedItem.toString().substring(0,2)+spn_Day.selectedItem.toString().substring(0,2)

            val sdf = SimpleDateFormat("yyyyMMdd")  //값을 비교하기 위해 위의 선언한 date의 형식과 같게 맞추기 위해 포맷형식(yyyymmdd)지정.
            val cld = Calendar.getInstance()  //현재 날짜와 시간정보를 가진 Calender객체 cld생성.
            val strToday = sdf.format(cld.time)  //현재날짜정보(cld)를 위에서 지정한 포맷형식(yyyymmdd)으로 문자열 strToday생성.

            if(date.toInt()>strToday.toInt()) {  //현재 날짜보다 미래인 경우 에러메세지 출력.
                msg_error.text = "미래에서 오셨군요.^^;"
                return@setOnClickListener
            }
//-------------------------------------------------------------------------------------------------------------------
            var gender :String  //성별의 정보를 담는 문자형의 변수 gender 생성.
            if(r_Man.isChecked) gender="M"  //남자를 선택한 경우 gender에 'M' 저장.
            else if(r_Female.isChecked) gender="F"  //여자를 선택한 경우 gender에 'F' 저장.
            else {  //아무것도 선택되지 않았을 시 에러메세지 출력 후 리턴.
                msg_error.text = "성별은 필수 입력입니다."
                return@setOnClickListener
            }
            /*
            button.setOnClickListener { view ->
                when (radioGroup.checkedRadioButtonId) {
                    R.id.r_Man ->
                        gender = 'M'
                    R.id.r_Female ->
                        gender = 'F'
                }
            }*/
//-------------------------------------------------------------------------
            val blood : String  //혈액형을 담는 문자열 타입의 변수 blood 생성.
            blood=spn_Blood.selectedItem.toString()  //스피너에서 선택된 혈액형을 위에서 선언한 blood에 값 전달.
//--------------------------------------------------------------------------
            var hei_message:String=""
            var wei_message:String=""
            var height=0.0
            var weight=0.0
            if ( txt_Height.text.toString().length == 0 ) {
                msg_error.text = "키를 입력받지 못했습니다."
                return@setOnClickListener
            }else {
                height = txt_Height.text.toString().toDouble()
                height=Math.round(height * 10) / 10.0  //소수점 한자리 반올림
                if(height<40) hei_message = "헬스 앱을 통해 함께 쑥쑥 커봐요!"
                if(height>180) hei_message = "함께 같은 공기를 마시고 싶군요!"
            }
//----------------------------------------------------------------------------------
            if ( txt_Weight.text.toString().length == 0 ) {
                msg_error.text = "체중을 입력받지 못했습니다."
                return@setOnClickListener
            }else {
                weight = txt_Weight.text.toString().toDouble()
                weight=Math.round(weight * 10) / 10.0  //소수점 한자리 반올림
                if(weight<30) wei_message = "헬스 앱을 통해 함께 추억을 쌓아보아요!"
                if(weight>180) wei_message = "한계극복! 함께 뛰는 헬스앱이 되겠습니다!"
            }
            insertClient(date,gender,blood,height,weight,security,hei_message,wei_message)
        }
    }
    override fun onDestroy(){
        super.onDestroy()
        realm.close()  //인스턴스 해제
    }
}