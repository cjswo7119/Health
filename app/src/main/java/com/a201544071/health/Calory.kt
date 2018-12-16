package com.a201544071.health

import android.content.Context
import android.content.pm.ActivityInfo
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.method.ScrollingMovementMethod
import kotlinx.android.synthetic.main.activity_calory.*

class Calory : AppCompatActivity(),SensorEventListener {
    var footcount=0  //걸음수를 구하기 위한 변수
    var consumKcal=0.0F  //소모칼로리를 구한 연산 값을 저장하는 변수
    var speed:Float=0.0F  //장치가 움직인 값(가속도 공식)
    var x:Float=0.0F  //가속도센서에서 현재 전달 받은 x값
    var y:Float=0.0F  //가속도센서에서 현재 전달 받은 y값
    var z:Float=0.0F  //가속도센서에서 현재 전달 받은 z값
    var lastX:Float=0.0F  //지난번 가속도센서에서 전달 받은 x값
    var lastY:Float=0.0F  //지난번 가속도센서에서 전달 받은 y값
    var lastZ:Float=0.0F  //지난번 가속도센서에서 전달 받은 z값
    var lastTime:Long =0L  //가속도 연산 중 0.1초단위 단위시간을 파악하기 위한 변수
    val shake=800  //한걸음 표준치

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {  //센서 정밀도가 변경되면 호출
        //return
    }

    override fun onSensorChanged(event: SensorEvent?) {  //센서값이 변경되면 호출
        event?.let{
            //Log.d("Consumed Calrory","onSensorChanged: X:"+"${event.values[0]}, Y:${event.values[1]}, Z:${event.values[2]},date:${System.currentTimeMillis()},${Math.abs(x-lastX+y-lastY+z -lastZ)}")
            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {  //start버튼을 클릭하여 센서매니저를 통해 가속도 센서가 등록되었을 경우에만 센서값 받기.
                val currentTime=System.currentTimeMillis()  //현재시간 1/1000초 값
                val gabOfTime=currentTime-lastTime  //단위시간을 구하기 위한 지난 시간동안의 움직인 크기(순간 가속도)
                if (gabOfTime > 100) {  //단위시간(0.1초) 마다의 속도의 크기를 구하기 위한 조건문
                    lastTime=currentTime  //다음 0.1초뒤 센서값을 받기위해 현재시각을 lastTime에 전달.
                    x=event.values[0]  //가속도 센서 현재 x축 값
                    y=event.values[1]  //가속도 센서 현재 y축 값
                    z=event.values[2]  //가속도 센서 현재 z축 값

                    speed=Math.abs(x-lastX+y-lastY+z -lastZ)/gabOfTime*10000
                    //각 축이 받는 크기 x-laxtX 백터의 크기
                    //가속도 공식(뒤의 1000의 경우는 전달받은 가속도 크기 값이 안 움직였을 시 0.009, 처럼 값이 작기 때문에 눈에 익숙한 값을 구하기 위해 10000 곱하기 수행)
                    //값이 마이너스인 경우는 절대값으로 변환하여 양수인 shake와 비교.
                    if (speed > shake) {  //연산한 가속도 크기값이 한걸음 표준치보다 크다면 걸음수를 증가하고 소모칼로리 연산 수행.
                        ++footcount  //걸음수 증가
                        consumKcal=Math.round(footcount*0.03F * 100F) / 100.0F  //소모칼로리 연산(걸음수 * 0.03),소수점 3자리에서 반올림.
                        lbl_Kcal.setText(consumKcal.toString())  //소모칼로리 출력
                        lbl_FootCount.setText(footcount.toString())  //걸음수 출력
                    }

                    lastX=event.values[0]  //다음 이동한 크기를 구하기 위해 현재 받은 x축 값을 lastX에 저장.
                    lastY=event.values[1]  //다음 이동한 크기를 구하기 위해 현재 받은 y축 값을 lastY에 저장.
                    lastZ=event.values[2]  //다음 이동한 크기를 구하기 위해 현재 받은 z축 값을 lastZ에 저장.
                }
            }
        }
    }

    private val sensorManager by lazy{  //센서 매니저 참조
        getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }
    override fun onCreate(savedInstanceState: Bundle?) {  //센서의 감지를 등록
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calory)
        requestedOrientation= ActivityInfo.SCREEN_ORIENTATION_PORTRAIT  //세로모드 고정

        btn_Start.setOnClickListener {   //START버튼 클릭 리스너
            sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_UI)
            //가속도 센서 등록(표준 속도 갱신)
            footcount=0  //걸음수 초기화
            consumKcal=0.0F  //다시 연산하기위해 초기화
        }

        btn_Stop.setOnClickListener {  //STOP버튼 클릭 리스너
            sensorManager.unregisterListener(this)  //센서해제
        }

        btn_Save.setOnClickListener {
            txt_Record.setMovementMethod(ScrollingMovementMethod())  //텍스트뷰에 수직 스크롤바 추가.
            txt_Record.append(java.text.SimpleDateFormat("MM/dd hh:mm").format(java.util.Date())+"\t걸음수 :"+footcount.toString()+"\t소모칼로리 :"+consumKcal.toString()+"\n")
            //텍스트 뷰에 기록저장(월/일 시:분 걸음수 소모칼로리)
        }
    }
}
