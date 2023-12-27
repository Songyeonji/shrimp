package com.shrimp.seuzima;

import static android.content.ContentValues.TAG;
import static com.shrimp.seuzima.MapFragment.LOCATION_PERMISSION_REQUEST_CODE;
import static com.shrimp.seuzima.MapFragment.loc_marker;
import static com.shrimp.seuzima.MapFragment.naverMap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.NaverMapSdk;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.overlay.OverlayImage;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // 불법주정차 단속 위치 api에서 데이터 받아와서 저장하는 변수들
    private String[] noParkingZone_name = new String[365];
    private String[] noParkingZone_addr = new String[365];
    private Double[] noParkingZone_lat = new Double[365];
    private Double[] noParkingZone_lon = new Double[365];

    // 불법주정차 단속 위치 마커 리스트
    private List<Marker> noParking_markerList = new ArrayList<>();

    // 유료주차장 위치 api에서 데이터 받아와서 저장하는 변수들
    private String[] paidParkingZone_name = new String[719];
    private String[] paidParkingZone_addr = new String[719];
    private Double[] paidParkingZone_lat = new Double[719];
    private Double[] paidParkingZone_lon = new Double[719];
    private String[] paidParkingZone_weekOpen = new String[719];
    private String[] paidParkingZone_weekClose = new String[719];
    private String[] paidParkingZone_satOpen = new String[719];
    private String[] paidParkingZone_satClose = new String[719];
    private String[] paidParkingZone_holiOpen = new String[719];
    private String[] paidParkingZone_holiClose = new String[719];
    private int[] paidParkingZone_baseTime = new int[719];
    private int[] paidParkingZone_baseRate = new int[719];
    private int[] paidParkingZone_addTime = new int[719];
    private int[] paidParkingZone_addRate = new int[719];
    private List<Marker> paid_markerList = new ArrayList<>();

    // 무료주차장 위치 api에서 데이터 받아와서 저장하는 변수들
    private String[] freeParkingZone_name = new String[719];
    private String[] freeParkingZone_addr = new String[719];
    private Double[] freeParkingZone_lat = new Double[719];
    private Double[] freeParkingZone_lon = new Double[719];
    private String[] freeParkingZone_weekOpen = new String[719];
    private String[] freeParkingZone_weekClose = new String[719];
    private String[] freeParkingZone_satOpen = new String[719];
    private String[] freeParkingZone_satClose = new String[719];
    private String[] freeParkingZone_holiOpen = new String[719];
    private String[] freeParkingZone_holiClose = new String[719];
    private List<Marker> free_markerList = new ArrayList<>();

    // 불법주정차, 유/무료 주차장에 대한 마커가 찍혔는지 안찍혔는지 확인하고 구분하기 위한 변수들
    private Boolean noParking = Boolean.FALSE;
    private Boolean paidParking = Boolean.FALSE;
    private Boolean freeParking = Boolean.FALSE;

    public static Context context;
    private Context map_context;
    private Bottom_LocationInform bottom_locationInform;

    private Marker before_marker;
    private MapFragment mapFragment;

    private LocationFullFragment locationFullFragment;

    private String name;
    private String addr;
    private Double lat;
    private Double lon;
    private String category;
    private String link;
    private String tel;
    private String weekOpen;
    private String weekClose;
    private String satOpen;
    private String satClose;
    private String holiOpen;
    private String holiClose;
    private String baseRate;
    private String addRate;
    private String baseTime;
    private String addTime;

    // Manifest에서 설정된 권한 정보 가져오기
    public static final String[] PERMISSIONS = {
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NaverMapSdk.getInstance(this).setClient(
                new NaverMapSdk.NaverCloudPlatformClient(BuildConfig.NAVER_MAP_API_KEY));

        context = this;


        getNoParkingData();
        getParkingData();

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



    // Activity 시작할 때 지도 초기화 설정
    public void set_init() {
        Intent getintent = getIntent();
        name = getintent.getStringExtra("loc_name");
        addr = getintent.getStringExtra("loc_addr");
        int x = getintent.getIntExtra("loc_x",0);
        int y = getintent.getIntExtra("loc_y", 0);
        category = getintent.getStringExtra("loc_category");
        link = getintent.getStringExtra("loc_link");
        tel = getintent.getStringExtra("loc_tel");

        if (x!=0 && y!=0) {
            lat = y/Math.pow(10,7);
            lon = x/Math.pow(10,7);

            mapFragment.location_marker(lat, lon);
            show_Bottom_Location(name, addr, lat, lon, link, tel, "", "", "", "", "", "", "", "", category);
        } else {
            mapFragment.set_user_location(naverMap);
        }
    }


    // 지도에 다중 marker 생성해서 출력하는 함수
    public Marker createMarker(NaverMap naverMap, double latitude, double longitude, String name) {
        // 마커를 생성하고 위치를 설정하여 지도에 추가합니다.
        Marker marker = new Marker();
        marker.setPosition(new LatLng(latitude, longitude));
        if (name.equals("no")) {
            marker.setTag("noParking");
            marker.setIcon(OverlayImage.fromResource(R.drawable.nopark_icon));
//            marker.setIconTintColor(Color.RED);
        } else if (name.equals("free")) {
            marker.setTag("free");
            marker.setIcon(OverlayImage.fromResource(R.drawable.free_icon));
        } else {
            marker.setTag("paid");
            marker.setIcon(OverlayImage.fromResource(R.drawable.dallar_icon));
        }

        marker.setWidth(80);
        marker.setHeight(80);

        marker.setMap(naverMap);

        Overlay.OnClickListener listener = overlay -> {
            if (before_marker!=null) {
                String tag = (String) before_marker.getTag();
                Log.d("tag:", tag);

                if (tag.equals("noParking")) {
                    before_marker.setIcon(OverlayImage.fromResource(R.drawable.nopark_icon));
                    before_marker.setMap(naverMap);

                } else if (tag.equals("paid")) {
                    before_marker.setIcon(OverlayImage.fromResource(R.drawable.dallar_icon));
                    before_marker.setMap(naverMap);
                } else {
                    before_marker.setIcon(OverlayImage.fromResource(R.drawable.free_icon));
                    before_marker.setMap(naverMap);
                }

                before_marker.setWidth(80);
                before_marker.setHeight(80);
                before_marker.setMap(naverMap);

            }
            Marker markers = (Marker) overlay;

            LatLng latLng = markers.getPosition();
            lat = latLng.latitude;
            lon = latLng.longitude;
            before_marker=marker;
            marker.setPosition(new LatLng(lat, lon));

            if (marker.getTag().equals("noParking")) {
                category = "주정차 금지구역";
                Log.d("clikc ltlng:", lat.toString()+" | "+lon.toString());
                marker.setIcon(OverlayImage.fromResource(R.drawable.selected_nopark_icon));
                marker.setWidth(100);
                marker.setHeight(130);
                int n = 0;

                while (!(noParkingZone_lat[n].equals(lat) && noParkingZone_lon[n].equals(lon))) {
                    if (noParkingZone_lat[n].equals(lat) && noParkingZone_lon[n].equals(lon)) {

                        break;
                    }
                    n++;
                }
                this.name = noParkingZone_name[n];
                addr = noParkingZone_addr[n];
            } else if (marker.getTag().equals("paid")) {
                category = "유료 주차장";
                int n = 0;
                marker.setIcon(OverlayImage.fromResource(R.drawable.selected_dallar_icon));
                marker.setWidth(100);
                marker.setHeight(130);
                while (!(paidParkingZone_lat[n].equals(lat) && paidParkingZone_lon[n].equals(lon))) {
                    if (paidParkingZone_lat[n].equals(lat) && paidParkingZone_lon[n].equals(lon)) {

                        break;
                    }
                    n++;
                }
                this.name = paidParkingZone_name[n];
                addr = paidParkingZone_addr[n];
                if (paidParkingZone_weekOpen[n] == "null"){
                    weekOpen = "-";
                }else{
                    weekOpen = paidParkingZone_weekOpen[n];
                }
                if (paidParkingZone_weekClose[n] == "null"){
                    weekClose = "-";
                }else{
                    weekClose = paidParkingZone_weekClose[n];
                }
                if (paidParkingZone_satOpen[n] == "null"){
                    satOpen = "-";
                }else{
                    satOpen = paidParkingZone_satOpen[n];
                }
                if (paidParkingZone_satClose[n] == "null"){
                    satClose = "-";
                }else{
                    satClose = paidParkingZone_satClose[n];
                }
                if (paidParkingZone_holiOpen[n] == "null"){
                    holiOpen = "-";
                }else{
                    holiOpen = paidParkingZone_holiOpen[n];
                }
                if (paidParkingZone_holiClose[n] == "null"){
                    holiClose = "-";
                }else{
                    holiClose = paidParkingZone_holiClose[n];
                }
                baseRate = paidParkingZone_baseRate[n] + "원";
                baseTime = paidParkingZone_baseTime[n] + "분";
                addRate = paidParkingZone_addRate[n] + "원";
                addTime = paidParkingZone_addTime[n] + "분";

            } else {
                category = "무료 주차장";
                int n = 0;
                marker.setIcon(OverlayImage.fromResource(R.drawable.selected_free_icon));
                marker.setWidth(100);
                marker.setHeight(130);
                while (!(freeParkingZone_lat[n].equals(lat) && freeParkingZone_lon[n].equals(lon))) {
                    if (freeParkingZone_lat[n].equals(lat) && freeParkingZone_lon[n].equals(lon)) {

                        break;
                    }
                    n++;
                }

                this.name = freeParkingZone_name[n];
                addr = freeParkingZone_addr[n];
                if (freeParkingZone_weekOpen[n] == "null"){
                    weekOpen = "-";
                }else{
                    weekOpen = freeParkingZone_weekOpen[n];
                }
                if (freeParkingZone_weekClose[n] == "null"){
                    weekClose = "-";
                }else{
                    weekClose = freeParkingZone_weekClose[n];
                }
                if (freeParkingZone_satOpen[n] == "null"){
                    satOpen = "-";
                }else{
                    satOpen = freeParkingZone_satOpen[n];
                }
                if (freeParkingZone_satClose[n] == "null"){
                    satClose = "-";
                }else{
                    satClose = freeParkingZone_satClose[n];
                }
                if (freeParkingZone_holiOpen[n] == "null"){
                    holiOpen = "-";
                }else{
                    holiOpen = freeParkingZone_holiOpen[n];
                }
                if (freeParkingZone_holiClose[n] == "null"){
                    holiClose = "-";
                }else{
                    holiClose = freeParkingZone_holiClose[n];
                }
                baseTime = "-";
                baseRate = "무료";
                addTime = "-";
                addRate = "무료";

            }
            marker.setMap(naverMap);
            show_Bottom_Location(this.name, addr, lat, lon,weekOpen, weekClose, satOpen, satClose, holiOpen, holiClose, baseRate, addRate, baseTime, addTime, category);
            return true;
        };
        marker.setOnClickListener(listener);

        return marker;
    }

    // 장소 상세 페이지가 하단에 나타나도록 하는 함수
    private void show_Bottom_Location(String name, String addr, Double lat, Double lon, String weekOpen, String weekClose, String satOpen, String satClose, String holiOpen, String holiClose, String baseRate, String addRate,String baseTime, String addTime, String category) {

        LinearLayout search_layout = findViewById(R.id.search_layout);
        search_layout.setVisibility(View.GONE);
        findViewById(R.id.main_content).setVisibility(View.VISIBLE);

        Bundle bundle = new Bundle();
        bundle.putString("loc_name", name);
        bundle.putString("loc_addr", addr);
        bundle.putDouble("loc_lat", lat);
        bundle.putDouble("loc_lon", lon);
        bundle.putString("loc_link", link);
        bundle.putString("loc_tel", tel);
        bundle.putString("week_open", weekOpen);
        bundle.putString("week_close", weekClose);

        bundle.putString("sat_open", satOpen);
        bundle.putString("sat_close", satClose);

        bundle.putString("holi_open", holiOpen);
        bundle.putString("holi_close", holiClose);
        bundle.putString("baseRate", baseRate);
        bundle.putString("baseTime", baseTime);
        bundle.putString("addRate", addRate);
        bundle.putString("addTime", addTime);

        bundle.putString("loc_category", category);

        bottom_locationInform = new Bottom_LocationInform();
        bottom_locationInform.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.main_content, bottom_locationInform).commit();

    }

    // 검색창 클릭하면 검색페이지로 이동하는 함수
    public void search(View view) {
        Intent intent_searching = new Intent(MainActivity.this, Search.class);
        startActivity(intent_searching);
    }

    // noParkingZone_API.java에서 함수를 실행시켜 api를 받아오고 주차금지 구역의 이름, 주소, 위경도 등을
    // 배열 변수에 저장하도록 함. api를 처음 실행할 때 한번만 사용해서 데이터를 가져와서 저장하고
    // 이후에는 배열 변수에 저장된 데이터를 사용하는 방식으로 하기 위함.
    private void getNoParkingData() {

        noParkingZone_API.getNoParkingData();
        noParkingZone_name = noParkingZone_API.name;
        noParkingZone_addr = noParkingZone_API.addr;
        noParkingZone_lat = noParkingZone_API.lat;
        noParkingZone_lon = noParkingZone_API.lon;

    }



    // 주차장 api에서 데이터 가져오기 (가져오는데 시간이 많이 소요됨..ㅠㅠ)
    private void getParkingData() {
        // 주차장 api는 한 페이지에 데이터 50개만 출력이 가능하기 때문에
        // 반복문을 사용해서 한 페이지에 50개씩, 총 15번을 반복해서 데이터를 가져와야함.
        // 그래서 가져오는데 시간이 많이 걸림..
        // 총 데이터 = 719개..

        ParkingZone_API.getParkingData();

        freeParkingZone_name = ParkingZone_API.free_name;
        freeParkingZone_addr = ParkingZone_API.free_addr;
        freeParkingZone_lat = ParkingZone_API.free_lat;
        freeParkingZone_lon = ParkingZone_API.free_lon;
        freeParkingZone_weekOpen = ParkingZone_API.free_weekdayOpenTime;
        freeParkingZone_weekClose = ParkingZone_API.free_weekdayCloseTime;
        freeParkingZone_satOpen = ParkingZone_API.free_satOpenTime;
        freeParkingZone_satClose = ParkingZone_API.free_satCloseTime;
        freeParkingZone_holiOpen = ParkingZone_API.free_holidayOpenTime;
        freeParkingZone_holiClose = ParkingZone_API.free_holidayCloseTime;

        paidParkingZone_name = ParkingZone_API.paid_name;
        paidParkingZone_addr = ParkingZone_API.paid_addr;
        paidParkingZone_lat = ParkingZone_API.paid_lat;
        paidParkingZone_lon = ParkingZone_API.paid_lon;
        paidParkingZone_weekOpen = ParkingZone_API.paid_weekdayOpenTime;
        paidParkingZone_weekClose = ParkingZone_API.paid_weekdayCloseTime;
        paidParkingZone_satOpen = ParkingZone_API.paid_satOpenTime;
        paidParkingZone_satClose = ParkingZone_API.paid_satCloseTime;
        paidParkingZone_holiOpen = ParkingZone_API.paid_holidayOpenTime;
        paidParkingZone_holiClose = ParkingZone_API.paid_holidayCloseTime;
        paidParkingZone_baseTime = ParkingZone_API.paid_baseTime;
        paidParkingZone_baseRate = ParkingZone_API.paid_baseRate;
        paidParkingZone_addTime = ParkingZone_API.paid_addTime;
        paidParkingZone_addRate = ParkingZone_API.paid_addRate;

    }

    // 주차 금지 구역 마커 찍는 구간
    public void show_noParkingZone(View view) {
        if (noParkingZone_name[0]==null) {
            Toast.makeText(this, "데이터를 가져오는 중입니다. 잠시만 기다려주세요.", Toast.LENGTH_SHORT).show();
        } else {
            Log.d("lat: ", noParkingZone_lat[364].toString());
            if (noParking.equals(Boolean.FALSE)) {
                for (int i = 0; i<365; i++) {
                    noParking_markerList.add(createMarker(naverMap, noParkingZone_lat[i], noParkingZone_lon[i], "no"));
                    noParking = Boolean.TRUE;
                }
            } else {
                for (int i = 0; i<365; i++) {
                    mapFragment.hideMarkers(noParking_markerList);
                    noParking = Boolean.FALSE;
                }
            }
        }

    }
    
    // 하단에 검색 페이지 나타나도록 하기 (원래 처음에 있었던 '어디로 가세요' 페이지 나타나도록 하기)
    public void show_searchingLayout() {
        if (findViewById(R.id.home_content).getVisibility()==View.GONE) {
            findViewById(R.id.home_content).setVisibility(View.VISIBLE);
            findViewById(R.id.full_view).setVisibility(View.GONE);
        }

        findViewById(R.id.search_layout).setVisibility(View.VISIBLE);
        // FragmentTransaction 시작
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

// bottom_locationInform Fragment를 제거
        transaction.remove(bottom_locationInform);

// 변경된 FragmentTransaction을 반영
        transaction.commit();
    }

    // 무료 주차장 마커 표시 함수
    public void show_freeParkingZone(View view) {
        if (freeParkingZone_name[0]==null) {
            Toast.makeText(this, "데이터를 가져오는 중입니다. 잠시만 기다려주세요.", Toast.LENGTH_SHORT).show();
        } else {
            if (freeParking.equals(Boolean.FALSE)) {
                int i = 0;
                while (freeParkingZone_lat[i]!=null) {
                    free_markerList.add(createMarker(naverMap, freeParkingZone_lat[i], freeParkingZone_lon[i], "free"));
                    freeParking = Boolean.TRUE;
                    i++;
                }
            } else {
                int i = 0;
                while (freeParkingZone_lat[i]!=null) {
                    mapFragment.hideMarkers(free_markerList);
                    freeParking = Boolean.FALSE;
                    i++;
                }
            }
        }

    }

    // 유료 주차장 마커 표시 함수
    public void show_paidParkingZone(View view) {
        if (paidParkingZone_name[0]==null) {
            Toast.makeText(this, "데이터를 가져오는 중입니다. 잠시만 기다려주세요.", Toast.LENGTH_SHORT).show();
        } else {
            if (paidParking.equals(Boolean.FALSE)) {
                int i = 0;
                while (paidParkingZone_lat[i]!=null) {
                    paid_markerList.add(createMarker(naverMap, paidParkingZone_lat[i], paidParkingZone_lon[i], "paid"));
                    paidParking = Boolean.TRUE;
                    i++;
                }
            } else {
                int i = 0;
                while (paidParkingZone_lat[i]!=null) {
                    mapFragment.hideMarkers(paid_markerList);
                    paidParking = Boolean.FALSE;
                    i++;
                }
            }
        }

    }

    // 위치 추적 허용 확인
    public void check_location() {
        if (hasLocationPermissions()) {
            // 권한이 이미 허용된 경우 지도 초기화

            Log.d(TAG, "위치 허용 코드: "+LOCATION_PERMISSION_REQUEST_CODE);


        } else {
            // 권한 요청
            // 위치 권한 요청
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
            set_init();
        }
        MapFragment mapFragment = (MapFragment) getSupportFragmentManager().findFragmentById(R.id.map_layout);
        mapFragment.initMap();
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

    // gps 버튼 클릭시 사용자 위치 추적 다시 시작 & 화면 초기화
    public void set_up(View view) {
        if (loc_marker!=null) {
            loc_marker.setMap(null);
            show_searchingLayout();
        }

        mapFragment.set_user_location(naverMap);

    }

    // 예상 경로 미리보기 화면 -> 이전 홈화면으로 변환하는 함수
    public void before_home(View view) {
        NAVI_API.path.setMap(null);
        findViewById(R.id.preview_content).setVisibility(View.GONE);
        findViewById(R.id.home_content).setVisibility(View.VISIBLE);
    }
    
    // 장소 상세 페이지 나타나기 함수 (주차장만 해당)
    public void show_full_view(View view) {
        findViewById(R.id.home_content).setVisibility(View.GONE);
        FrameLayout full_view = findViewById(R.id.full_view);
        full_view.setVisibility(View.VISIBLE);

        Bundle bundle = new Bundle();
        bundle.putString("loc_name", name);
        bundle.putString("loc_addr", addr);
        bundle.putDouble("loc_lat", lat);
        bundle.putDouble("loc_lon", lon);
        bundle.putString("loc_link", link);
        bundle.putString("loc_tel", tel);
        bundle.putString("week_open", weekOpen);
        bundle.putString("week_close", weekClose);

        bundle.putString("sat_open", satOpen);
        bundle.putString("sat_close", satClose);

        bundle.putString("holi_open", holiOpen);
        bundle.putString("holi_close", holiClose);
        bundle.putString("baseRate", baseRate);
        bundle.putString("baseTime", baseTime);
        bundle.putString("addRate", addRate);
        bundle.putString("addTime", addTime);
        bundle.putString("loc_category", category);

        locationFullFragment = new LocationFullFragment();
        locationFullFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.full_view, locationFullFragment).commit();
    }

    // 경로 안내 미리보기 화면 띄우기
    public void set_preview_content(String start, String dest) {
        if (findViewById(R.id.full_view).getVisibility()==View.VISIBLE) {
            findViewById(R.id.full_view).setVisibility(View.GONE);
        }
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // FrameLayout에 추가할 Fragment 생성
        PreviewFragment previewFragment = new PreviewFragment();

        // Fragment를 FrameLayout에 추가
        fragmentTransaction.replace(R.id.preview_content, previewFragment);

        // 트랜잭션 완료
        fragmentTransaction.commit();
        findViewById(R.id.preview_content).setVisibility(View.VISIBLE);
        findViewById(R.id.home_content).setVisibility(View.GONE);

        Bundle bundle = new Bundle();
        bundle.putString("start", start);
        bundle.putString("dest", dest);

        previewFragment.setArguments(bundle);

        mapFragment.hideMarkers(noParking_markerList);
        mapFragment.hideMarkers(paid_markerList);
        mapFragment.hideMarkers(free_markerList);


    }
}