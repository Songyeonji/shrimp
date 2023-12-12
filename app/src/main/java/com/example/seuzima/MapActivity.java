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
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapFragment;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapSdk;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    // 위치 정보 사용 권한 허락했는지 확인할 때 비교하는 변수 (권한을 허락했을 때 1000을 반환하는 모양..)
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;

    // 사용자 위치정보 저장하는 변수
    private FusedLocationSource locationSource;
    private NaverMap naverMap;

    // 사용자 위치 위경도 저장하는 변수
    private static Double user_lat;
    private static Double user_lon;

    // Manifest에서 설정된 권한 정보 가져오기
    private static final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    // 불법주정차 단속 위치 마커 리스트
    private List<Marker> noParking_markerList = new ArrayList<>();

    // 유료주차장 마커 리스트
    private List<Marker> paid_markerList = new ArrayList<>();

    // 무료주차장 마커 리스트
    private List<Marker> free_markerList = new ArrayList<>();

    // 불법주정차, 유/무료 주차장에 대한 마커가 찍혔는지 안찍혔는지 확인하고 구분하기 위한 변수들
    private Boolean noParking = Boolean.FALSE;
    private Boolean paidParking = Boolean.FALSE;
    private Boolean freeParking = Boolean.FALSE;

    private FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference nopz = mDatabase.getReference("noParkingZone");
    private DatabaseReference freepz = mDatabase.getReference("freeParkingZone");
    private DatabaseReference paidpz = mDatabase.getReference("paidParkingZone");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        NaverMapSdk.getInstance(this).setClient(
                new NaverMapSdk.NaverCloudPlatformClient("83ejnr111i"));

        noParkingZone_API.getNoParkingData(nopz);
        ParkingZone_API.getParkingData(freepz, paidpz);

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

    // 사용자가 이전에 해당 앱에 위치 정보를 이용하는 것에 동의했는지 확인하는 함수
    // 사용자가 이전에 해당 앱에 위치 정보를 이용하는 것에 동의했다면 true,
    // 그렇지 않으면 false 반환
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
        // 지도 초기화 코드 작성 (이것도 네이버지도 불러올 때 필수로 있어야하는 함수)
        //지도 객체 생성하기
        FragmentManager fm = getSupportFragmentManager();
        MapFragment mapFragment = (MapFragment)fm.findFragmentById(R.id.map_fragment);
        if (mapFragment==null) {
            mapFragment = MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map_fragment, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);


    }

    // 사용자에게 위치 정보를 가져오는 권한 물어보는 함수.
    // 이전에 해당 앱에 대한 위치 권한을 동의한 사용자에게는 권한 다시 물어보지 X.
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

    // 지도가 출력되기 전 준비할 때 실행되는 함수
    // (무조건 네이버 지도를 불러올 때 필수로 있어야함)
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
        set_user_location(naverMap);

    }

    // 사용자 위치 실시간으로 받아와서 지도에 표시하는 함수
    private void set_user_location(NaverMap navermap) {
        navermap.setLocationSource(locationSource);

        navermap.setLocationTrackingMode(LocationTrackingMode.Follow);
        navermap.addOnLocationChangeListener(location -> {
            user_lat = location.getLatitude();
            user_lon = location.getLongitude();
        });

    }

    // 검색창 클릭하면 검색페이지로 이동하는 함수
    public void search(View view) {
        Intent intent_searching = new Intent(MapActivity.this, Search.class);
        startActivity(intent_searching);
    }

    // 주차 금지 구역 데이터 부르기
    public void loadNoParkingZone(){
        nopz.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    noParkingZone noPZ = snapshot.getValue(noParkingZone.class);
                    noParking_markerList.add(createMarker(naverMap, noPZ.lat, noPZ.lon, "no"));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("MapActivity", "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    // 주차 금지 구역 마커 찍는 구간
    public void show_noParkingZone(View view) {
        if (noParking.equals(Boolean.FALSE)) {
            loadNoParkingZone();
            noParking = Boolean.TRUE;
        } else {
            hideMarkers(noParking_markerList);
            noParking_markerList.clear();
            noParking = Boolean.FALSE;
        }
    }

    // 무료주차장 데이터 부르기
    public void loadFreeParkingZone(){
        freepz.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    freeParkingZone freePZ = snapshot.getValue(freeParkingZone.class);
                    free_markerList.add(createMarker(naverMap, freePZ.lat, freePZ.lon, "free"));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("MapActivity", "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    // 무료 주차장 마커 표시 함수
    public void show_freeParkingZone(View view) {
        if (freeParking.equals(Boolean.FALSE)) {
            loadFreeParkingZone();
            freeParking = Boolean.TRUE;
        } else {
            hideMarkers(free_markerList);
            free_markerList.clear();
            freeParking = Boolean.FALSE;
        }
    }

    // 유료주차장 데이터 부르기
    public void loadPaidParkingZone(){
        paidpz.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    paidParkingZone paidPZ = snapshot.getValue(paidParkingZone.class);
                    paid_markerList.add(createMarker(naverMap, paidPZ.lat, paidPZ.lon, "paid"));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w("MapActivity", "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    // 유료 주차장 마커 표시 함수
    public void show_paidParkingZone(View view) {
        if (paidParking.equals(Boolean.FALSE)) {
            loadPaidParkingZone();
            paidParking = Boolean.TRUE;
        } else {
            hideMarkers(paid_markerList);
            paid_markerList.clear();
            paidParking = Boolean.FALSE;
        }
    }

    // 지도에 다중 marker 생성해서 출력하는 함수
    private Marker createMarker(NaverMap naverMap, double latitude, double longitude, String name) {
        // 마커를 생성하고 위치를 설정하여 지도에 추가합니다.
        Marker marker = new Marker();
        marker.setPosition(new LatLng(latitude, longitude));
        if (name.equals("no")) {
            marker.setIconTintColor(Color.RED);
        } else if (name.equals("free")) {
            marker.setIconTintColor(Color.BLUE);
        } else {
            marker.setIconTintColor(Color.YELLOW);
        }

        marker.setMap(naverMap);

        return marker;
    }

    // 지도에 표시된 marker 지우는 함수
    private void hideMarkers(List<Marker> markerList) {
        // markerList에 있는 모든 마커를 지도에서 제거합니다.
        for (Marker marker : markerList) {
            marker.setMap(null);
        }
    }
}

