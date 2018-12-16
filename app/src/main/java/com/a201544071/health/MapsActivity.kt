package com.a201544071.health

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.WindowManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.android.synthetic.main.activity_maps.*
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.toast
import org.jetbrains.anko.yesButton
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*


class  MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    private lateinit var fusedLocationProviderClient:FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback:MyLocationCallBack
    private val REQUEST_ACCESS_FINE_LOCATION=1000
    private val REQUEST_WRITE_EXTERNAL_STORAGE=500
    private val polylineOptions=PolylineOptions().width(5f).color(Color.BLUE)
    private var filetitle:String?=null
    private var fileload:Boolean=false
    private fun permissionCheck(cancel: ()->Unit,ok: ()->Unit){  //manifest임포트. LOCATION의 위험권한을 요청.
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){  //권한 확인
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.ACCESS_FINE_LOCATION)){
                //권한이 허용되지 않았을 경우이며 이전에 이미 권한을 거부되었을 때
                cancel()  //취소
            }else{
                ActivityCompat.requestPermissions(this,arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),REQUEST_ACCESS_FINE_LOCATION)
            }
        }else{
            ok()
        }
    }
    private fun showPermissionInfoDialog(){  //권한 요청시 확인 후 위치 정보 전달 받음
        alert("현재 위치 정보를 얻으려면 위치 권한이 필요합니다","권한이 필요한 이유"){
            yesButton{
                ActivityCompat.requestPermissions(this@MapsActivity,arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),REQUEST_ACCESS_FINE_LOCATION)
            }
            noButton{}
        }.show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            REQUEST_ACCESS_FINE_LOCATION->{  //위치권한
                if((grantResults.isNotEmpty() && grantResults[0]==PackageManager.PERMISSION_GRANTED)){
                    addLocationListener()
                }else{
                    toast("권한 거부 됨")
                }
                return
            }
            REQUEST_WRITE_EXTERNAL_STORAGE->{  //외부저장장치 권한
                if(grantResults.isNotEmpty()&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
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
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        locationInit()
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                alert("파일 정보를 얻으려면 외부 저장소 권한이 필수로 필요합니다","권한이 필요한 이유"){
                    yesButton { ActivityCompat.requestPermissions(this@MapsActivity,
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
        btn_Start.setOnClickListener {
            //start버튼 클릭 시 시간정보를 넣어 파일명을 전역변수에 담아 위치 갱신 시 해당 파일을 찾기 위한 파일 생성.
            if(fileload) {  //앞에서 이미 start버튼을 클릭 시 파일이 도중 새로 작성을 막기위한 검사
                toast("이미 수행 중 입니다")  //화면에 에러메시지 출력
                return@setOnClickListener
            }
            mMap.clear()  //새로운 입출력 데이터를 받을 시 현 화면에 이미 작성이 완료된 마커와 폴리라인 지우기
            fileload=true  //파일 작성 시작

            val sdf = SimpleDateFormat("MM_dd_hh_mm")  //파일의 기록한 날짜를 기입하기 위한 날짜 포맷(생성될 파일명)
            val cld = Calendar.getInstance()  //현재 날짜와 시간정보를 가진 Calender객체 cld생성.
            val date = sdf.format(cld.time)  //현재날짜정보(cld)를 위에서 지정한 포맷형식(yyyymmdd)으로 문자열 strToday생성.

            val SaveFolderPath = "/mnt/sdcard/foot"  //생성할 폴더명
            val dir:File = File(SaveFolderPath)  //폴더명을 검색하여 존재하지 않다면 폴더 경로 지정
            if (!dir.exists()) {  //폴더가 존재하는지 확인
                dir.mkdirs()  //없다면 디렉터리 생성(mkdirs()는 하위 폴더까지 생성, mkdir()는 해당 폴더만 생성)
            }
            toast("기록 시작")  //결과 출력
            this.filetitle=SaveFolderPath+"/"+date+".txt"
            //폴더경로에 생성할 파일명과 확장자를 추가하여 전역변수 filetitle(파일명)에 전달
            val file:File = File(filetitle)
            //위 파일명의 경로에 파일 생성
            try {
                var bufwr:BufferedWriter = BufferedWriter(FileWriter(file))  //위도,경도를 한줄 단위로 읽어 파일로 작성하는BufferedWriter객체 생성.
                bufwr.close()  //파일 생성 후 BufferedWriter객체 닫기.
            }catch(e:Exception){  //io 입출력 중 발생하는 예외처리는 콘솔에 에러 출력
                e.printStackTrace()
            }
        }
        btn_Finish.setOnClickListener {
            fileload=false  //기록 작성을 중지
            filetitle=""  //파일 작성이 완료가 된 후 시간정보를 담았던 전역변수 파일명을 초기화
            toast("기록 종료")  //결과 출력
        }
    }
    private fun locationInit(){
        fusedLocationProviderClient= FusedLocationProviderClient(this)
        locationCallback=MyLocationCallBack()
        locationRequest=LocationRequest()

        locationRequest.priority=LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval=10000  //10초간격 위치 갱신
        locationRequest.fastestInterval=5000
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }
    override fun onResume(){
        super.onResume()

        permissionCheck(cancel={
            showPermissionInfoDialog()
        },ok={
            addLocationListener()
        })
    }
    @SuppressLint("MissingPermission")
    private fun addLocationListener(){  //위치 정보 갱신시마다 호출
        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback,null)
    }
    inner class MyLocationCallBack:LocationCallback(){  //위치 갱신 시마다 호출
        override fun onLocationResult(locationResult: LocationResult?){
            super.onLocationResult(locationResult)

            val location=locationResult?.lastLocation

            location?.run{
                mMap.clear()  //이전에 전송한 위치정보를 표시한 마커를 지우기 위한 mMap비우기.
                val latLng: LatLng =LatLng(latitude,longitude)  //위도, 경도정보를 받아 현위치 정보 저장
                if(fileload){  //start버튼을 통해 작성할 파일이 존재했을때만 파일 작성, 그렇지 않을 경우 현위치만 표시
                    val file = File(filetitle)  //start버튼 클릭시 생성했던 전역변수 파일명으로 파일 재작성.
                    try {
                        var bufwr = BufferedWriter(FileWriter(file,true))
                        //생성했던 파일에 갱신 시마다 해당 파일에 위도와 경도를 기록하도록 이어쓰기.
                        var str:String = latitude.toString()+","+longitude.toString()  //파일에 작성될 위도와 경도 기록을 작성
                        bufwr.write(str)  //작성된 위도, 경도 기록을 파일에 작성.
                        bufwr.newLine()  //위도, 경도를 작성 후 다음 행으로 커서 이동.
                        bufwr.close()  //입력이 완료된 BufferedWriter객체 닫기
                        polylineOptions.add(latLng)  //현위치를 받아 이전 위치와 선을 연결하기 위해 좌표정보를 polyline에 추가.
                        mMap.addPolyline(polylineOptions)  //입력받은 현위치를 통해 이전에 위치와 polyline으로 선 연결
                    }catch(e:Exception){  //io 입출력 중 발생하는 예외처리는 콘솔에 에러 출력
                        e.printStackTrace()
                    }
                }
                mMap.addMarker(MarkerOptions().position(latLng).title("Health"))  //지도에 마커로 현위치 표시
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,17f))  //현위치에 따라 지도 움직이기
                Log.d("MapsActivity","위도:$latitude, 경도:$longitude")  //콘솔에 위도와 경도 출력

            }
        }
    }

    override fun onPause() {  //다른 액티비티로 넘어갈 시
        super.onPause()
        removeLocationListener()  //위치요청 삭제 메서드 호출
    }
    private fun removeLocationListener(){  //위치요청 삭제
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }
}
