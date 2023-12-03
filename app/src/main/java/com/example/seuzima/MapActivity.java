package com.example.seuzima;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapSdk;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.util.FusedLocationSource;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    private FusedLocationSource locationSource;
    private NaverMap naverMap;
    private static Double user_lat;
    private static Double user_lon;
    private static final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        NaverMapSdk.getInstance(this).setClient(
                new NaverMapSdk.NaverCloudPlatformClient("83ejnr111i"));
        getNoParkingData();

        if (hasLocationPermissions()) {
            // 권한이 이미 허용된 경우 지도 초기화

            Log.d(TAG, "위치 허용 코드: "+LOCATION_PERMISSION_REQUEST_CODE);
            initMap();

        } else {
            // 권한 요청
            // 위치 권한 요청
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
        locationSource =
                new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);
    }

    private boolean hasLocationPermissions() {
        for (String permission : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void initMap() {
        // 지도 초기화 코드 작성
        //지도 객체 생성하기
        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.map_fragment);
        if (mapFragment==null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map_fragment, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,  @NonNull int[] grantResults) {
        if (locationSource.onRequestPermissionsResult(
                requestCode, permissions, grantResults)) {
            if (!locationSource.isActivated()) { // 권한 거부됨
                naverMap.setLocationTrackingMode(LocationTrackingMode.None);
            }
            return;
        }
        super.onRequestPermissionsResult(
                requestCode, permissions, grantResults);
    }

    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
//        naverMap.setLocationSource(locationSource);
        set_user_location(naverMap);

    }
    private void set_user_location(NaverMap navermap) {
        navermap.setLocationSource(locationSource);

        navermap.setLocationTrackingMode(LocationTrackingMode.Follow);
        navermap.addOnLocationChangeListener(location -> {
            user_lat = location.getLatitude();
            user_lon = location.getLongitude();
        });

    }

    public void search(View view) {
        Intent intent_searching = new Intent(MapActivity.this, Search.class);
        startActivity(intent_searching);
    }


    public static ArrayList<noParkingZone_API> noParkingZone_ApiDataList;
    public void getNoParkingData(){
        new Thread(){
            @Override
            public void run(){
                // 쿼리 작성하기
                String api_key = getString(R.string.api_key);
                String pageNo = "1";
                String dataCount = "10";
                String queryUrl = "https://apis.data.go.kr/6300000/openapi2022/vstpCCTV/getvstpCCTV?serviceKey="+api_key+
                        "&pageNo="+pageNo+"&numOfRows="+dataCount;

                try {
                    // 데이터 받아오기
                    URL url = new URL(queryUrl);

                    InputStream is = url.openStream();
                    InputStreamReader isr = new InputStreamReader(is);
                    BufferedReader reader = new BufferedReader(isr);

                    StringBuffer buffer = new StringBuffer();
                    String line = reader.readLine();
                    while (line != null) {
                        buffer.append(line + "\n");
                        line = reader.readLine();
                    }

                    // 데이터 파싱하기
                    String jsonString = buffer.toString();
                    JSONObject jsonObject = new JSONObject(jsonString);
                    JSONObject response = jsonObject.getJSONObject("response");
                    JSONObject body = response.getJSONObject("body");
                    JSONArray items = body.getJSONArray("items");
                    JSONObject data = items.getJSONObject(0);

//                    Log.d("noParking_DATA: ", items.toString());

                    noParkingZone_ApiDataList = new ArrayList<noParkingZone_API>();
                    for (int i=0; i<data.length();i++){
                        String name = data.getString("manageNo");
                        String addr = data.getString("lnmAdres");
                        String lon = data.getString("crdntX");
                        String lat = data.getString("crdntY");
                        noParkingZone_ApiDataList.add(new noParkingZone_API(
                                name, addr, lat, lon
                        ));
                    }

                    Log.d("noParking_DATA: ", noParkingZone_ApiDataList.toString());

                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void show_noParkingZone(View view) {

    }
}

