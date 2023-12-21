package com.example.seuzima;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.util.FusedLocationSource;

import java.util.List;


public class MapFragment extends Fragment implements OnMapReadyCallback {

    private MapView mapView;

    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;


    // 사용자 위치정보 저장하는 변수
    public FusedLocationSource locationSource;
    public static NaverMap naverMap;

    // 사용자 위치 위경도 저장하는 변수
    public static Double user_lat;
    public static Double user_lon;

    public static Context context;
    public static Marker loc_marker;
    private LayoutInflater inflater;
    private ViewGroup container;
    private Bundle savedInstanceState;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflaters, ViewGroup containers,
                             Bundle savedInstanceStates) {
        // Inflate the layout for this fragment

        inflater = inflaters;
        container = containers;
        savedInstanceState = savedInstanceStates;

        ((MainActivity) getActivity()).check_location();

        locationSource =
                new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);


        return inflater.inflate(R.layout.fragment_map, container, false);
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
    public void location_marker(Double lat, Double lon) {
        loc_marker = new Marker();
        loc_marker.setPosition(new LatLng(lat, lon));
        loc_marker.setIconTintColor(Color.BLUE);
        loc_marker.setMap(naverMap);
        Log.d("lat/lon: ", lat.toString()+" | "+lon.toString());
        CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(lat, lon));
        naverMap.moveCamera(cameraUpdate);
    }

    public void initMap() {
        // 지도 초기화 코드 작성 (이것도 네이버지도 불러올 때 필수로 있어야하는 함수)
        //지도 객체 생성하기
        FragmentManager fm = getFragmentManager();
        com.naver.maps.map.MapFragment mapFragment = (com.naver.maps.map.MapFragment)fm.findFragmentById(R.id.map_fragment);
        if (mapFragment==null) {
            mapFragment = com.naver.maps.map.MapFragment.newInstance();
            fm.beginTransaction().add(R.id.map_fragment, mapFragment).commit();
        }
        mapFragment.getMapAsync(this);

    }



    // 지도가 출력되기 전 준비할 때 실행되는 함수
    // (무조건 네이버 지도를 불러올 때 필수로 있어야함)
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
//        naverMap.setLocationSource(locationSource);
        ((MainActivity) MainActivity.context).set_init();


    }

    // 사용자 위치 실시간으로 받아와서 지도에 표시하는 함수
    public void set_user_location(NaverMap navermap) {
        navermap.setLocationSource(locationSource);

        navermap.setLocationTrackingMode(LocationTrackingMode.Follow);
        navermap.addOnLocationChangeListener(location -> {
            user_lat = location.getLatitude();
            user_lon = location.getLongitude();
        });

    }

    // 지도에 표시된 marker 지우는 함수
    public void hideMarkers(List<Marker> markerList) {
        // markerList에 있는 모든 마커를 지도에서 제거합니다.
        for (Marker marker : markerList) {
            marker.setMap(null);
        }
    }
}