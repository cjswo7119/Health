package com.a201544071.health;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;



public class test {

    public static boolean getType(String word) {
        return Pattern.matches("(^[a-z가-힣0-9]*$)", word);
    }

    public static boolean getsym(String word) {
        return Pattern.matches("(^[a-zA-Z\\\\\\[\\]|]*$)", word);
    }

    public static void main(String[] args) {
        String abc = "원혁qed";
        if (getType(abc)) {
            System.out.println("true");
        } else System.out.println("false");

        /*String word = "POWER"; //스트링 문자 POWER
        String[] array_word; // 스트링을 담을 배열


        array_word = word.split(""); //배열에 한글자씩 저장하기

        for (int i = 0; i < array_word.length; i++) { //출력
            System.out.println(array_word[i]);
        }*/

        //public void alert(View v) {

/*
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("알림!!");
        alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "확인 버튼이 눌렸습니다",Toast.LENGTH_SHORT).show();
            }
        });
        alert.setIcon(R.drawable.ic_launcher);
        alert.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "취소 버튼이 눌렸습니다",Toast.LENGTH_SHORT).show();
            }
        });
        alert.setMessage("안녕하십니까? 알림 예제소스 학습중 입니다");
        alert.show();
        */
        //}

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");  //값을 비교하기 위해 위의 선언한 date의 형식과 같게 맞추기 위해 포맷형식(yyyymmdd)지정.
        Calendar cld = Calendar.getInstance();  //현재 날짜와 시간정보를 가진 Calender객체 cld생성.
        String strToday = sdf.format(cld.getTime());  //현재날짜정보(cld)를 위에서 지정한 포맷형식(yyyymmdd)으로 문자열 strToday생성.

        try {
        } catch (Exception e) {
            System.out.println("SQLException : " + e.getMessage());
        }
        /*FootList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });*/
        //String[] paramArray = queryString.split(",");
        String[] strData=new String[7];
        //String[] array = str.split("#");
    }

    public Boolean abc(){
        return false;
    }

}