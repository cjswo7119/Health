package com.a201544071.health

import android.Manifest
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.android.synthetic.main.activity_map_result.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import java.io.BufferedReader
import java.io.File
import java.io.FileReader


class MapResult : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mMap: GoogleMap
    private val REQUEST_WRITE_EXTERNAL_STORAGE=1000
    private val polylineOptions=PolylineOptions().width(5f).color(Color.BLUE)


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            REQUEST_WRITE_EXTERNAL_STORAGE->{  //외부저장장치 권한확인
                if(grantResults.isNotEmpty()&&grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    toast("권한 허용")
                }else {
                    toast("권한 거부 됨")
                }
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)  //화면 꺼지지 않게 하기
        requestedOrientation= ActivityInfo.SCREEN_ORIENTATION_PORTRAIT  //화면 세로모드 고정
        setContentView(R.layout.activity_map_result)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                alert("파일 정보를 얻으려면 외부 저장소 권한이 필수로 필요합니다","권한이 필요한 이유"){
                    yesButton { ActivityCompat.requestPermissions(this@MapResult,
                        arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        REQUEST_WRITE_EXTERNAL_STORAGE)
                    }
                    noButton {  }
                }.show()
            }else{
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    REQUEST_WRITE_EXTERNAL_STORAGE)
            }
        }

        if (intent.hasExtra("filename")) {  //인텐트로 전달된 값이 있을때에만 화면에 파일 경로 출력.
            RecordTitle.text = intent.getStringExtra("filename")
            //"filename"라는 이름의 인텐트에 저장된 값이 있다면
            // textView의 내용을 "filename"인텐트에 담긴 값으로 변환
        } else {
            Toast.makeText(this, "전달된 파일이 없습니다", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        var strline:String?  //파일을 한 줄단위로 읽어 해당 위도와 경도 정보를 읽는 문자열 변수
        val file: File = File(intent.getStringExtra("filename"))
        //인텐트로 전달 받은 해당 액티비티에서 전달받은 인텐트의 데이터를 사용하기 위해 getStringExtra 수행.
        //해당 인텐트에는 파일의 경로명이 저장 되어 있기 때문에 File객체로 생성하여 해당 경로명의 문자열 전달.

        try {
            var bufr: BufferedReader = BufferedReader(FileReader(file))  //위도,경도를 한줄 단위로 읽어 파일로 읽는BufferedWriter객체 생성.
            while(bufr.readLine()!=null) {  //해당라인 이 null일 때까지 반복문 수행
                strline= bufr.readLine()  //파일 내용을 읽으며 각 라인의 위도,경도 정보를 strline에 전달.
                var latlng:List<String> = strline.split(",")  //,(컴마)를 기준으로 latlng(문자열 배열) 전달
                var latLng = LatLng(latlng[0].toDouble(),latlng[1].toDouble())  //해당 위도와 경도 위치를 파악했다면 그에 해당하는 좌표를 LatLng객체에 저장.
                polylineOptions.add(latLng)  //좌표 정보를 지닌 LatLng를 화면에 표시하기 위하여 선을 그리기 위해 좌표 추가.
                mMap.addPolyline(polylineOptions)  //해당 좌표 지도에 표시하며 선 그리기
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,17f))  //현위치에 따라 지도 움직이기
            }
            bufr.close()  //파일 모두 읽은 후 BufferedReader체 닫기.
        }catch(e:Exception){  //io 입출력 중 발생하는 예외처리는 콘솔에 에러 출력
            e.printStackTrace()
        }
    }
}