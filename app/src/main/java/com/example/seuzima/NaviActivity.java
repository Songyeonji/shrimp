package com.example.seuzima;

import static android.content.ContentValues.TAG;
import static com.example.seuzima.MapFragment.LOCATION_PERMISSION_REQUEST_CODE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

public class NaviActivity extends AppCompatActivity {

    private MapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navi);

        addFragment();
    }

    private void addFragment() {
        // FragmentManager를 통해 Fragment의 트랜잭션 시작
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // FrameLayout에 추가할 Fragment 생성
        mapFragment = new MapFragment();

        // Fragment를 FrameLayout에 추가
        fragmentTransaction.replace(R.id.map_layout,mapFragment);

        // 트랜잭션 완료
        fragmentTransaction.commit();
    }

    public void check_location() {
        if (hasLocationPermissions()) {
            // 권한이 이미 허용된 경우 지도 초기화

            Log.d(TAG, "위치 허용 코드: "+LOCATION_PERMISSION_REQUEST_CODE);
            MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map_layout);
            mapFragment.initMap();

        } else {
            // 권한 요청
            // 위치 권한 요청
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }
    // 사용자가 이전에 해당 앱에 위치 정보를 이용하는 것에 동의했는지 확인하는 함수
    // 사용자가 이전에 해당 앱에 위치 정보를 이용하는 것에 동의했다면 true,
    // 그렇지 않으면 false 반환
    private boolean hasLocationPermissions() {
        for (String permission : MainActivity.PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}