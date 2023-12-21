package com.example.seuzima;

import static android.content.ContentValues.TAG;
import static com.example.seuzima.MapFragment.LOCATION_PERMISSION_REQUEST_CODE;
import static com.example.seuzima.MapFragment.loc_marker;
import static com.example.seuzima.MapFragment.naverMap;

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
import android.widget.LinearLayout;

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
    private List<Marker> paid_markerList = new ArrayList<>();

    // 무료주차장 위치 api에서 데이터 받아와서 저장하는 변수들
    private String[] freeParkingZone_name = new String[719];
    private String[] freeParkingZone_addr = new String[719];
    private Double[] freeParkingZone_lat = new Double[719];
    private Double[] freeParkingZone_lon = new Double[719];
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

    // Manifest에서 설정된 권한 정보 가져오기
    private static final String[] PERMISSIONS = {
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        NaverMapSdk.getInstance(this).setClient(
                new NaverMapSdk.NaverCloudPlatformClient("83ejnr111i"));

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

    public void set_init() {
        Intent getintent = getIntent();
        String name = getintent.getStringExtra("loc_name");
        String addr = getintent.getStringExtra("loc_addr");
        int x = getintent.getIntExtra("loc_x",0);
        int y = getintent.getIntExtra("loc_y", 0);
        String category = getintent.getStringExtra("loc_category");
        String link = getintent.getStringExtra("loc_link");
        String tel = getintent.getStringExtra("loc_tel");

        if (x!=0 && y!=0) {
            Double lat = y/Math.pow(10,7);
            Double lon = x/Math.pow(10,7);

            mapFragment.location_marker(lat, lon);
            show_Bottom_Location(name, addr, lat, lon, link, tel, category);
        } else {
            mapFragment.set_user_location(naverMap);
        }
    }

    /*private set_map() {
        FrameLayout map_layout = findViewById(R.id.map_fragment);
        bottom_locationInform = new Bottom_LocationInform();
        bottom_locationInform.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.main_content, bottom_locationInform).commit();
    }*/

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
            Double lat = latLng.latitude;
            Double lon = latLng.longitude;
            before_marker=marker;
            marker.setPosition(new LatLng(lat, lon));
            String names = null;
            String addr = null;
            String category = null;
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
                names = noParkingZone_name[n];
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
                names = paidParkingZone_name[n];
                addr = paidParkingZone_addr[n];
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

                names = freeParkingZone_name[n];
                addr = freeParkingZone_addr[n];

            }
            marker.setMap(naverMap);
            show_Bottom_Location(names, addr, lat, lon,"","",category);
            return true;
        };
        marker.setOnClickListener(listener);

        return marker;
    }

    private void show_Bottom_Location(String name, String addr, Double lat, Double lon, String link, String tel, String category) {

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

        /*for (int i = 1; i<=2; i++) {
            String api_key = "5Q44AbprRae2DW%2FDurbwg83MQLdKuV9wx3jkkhdCcZNwYdEyIw43X8kzO2syrpPz%2FQ257YQOjs3RFF4OnA4QVQ%3D%3D";

            String pageNo = Integer.toString(i);
            String dataCount = "50";
            String queryUrl = "https://apis.data.go.kr/6300000/pis/parkinglotIF?serviceKey="+api_key+
                    "&numOfRows="+dataCount+"&pageNo="+pageNo;

            ParkingZone_API dust = new ParkingZone_API(queryUrl, i);
            dust.execute();
        }*/

        ParkingZone_API.getParkingData();

        freeParkingZone_name = ParkingZone_API.free_name;
        freeParkingZone_addr = ParkingZone_API.free_addr;
        freeParkingZone_lat = ParkingZone_API.free_lat;
        freeParkingZone_lon = ParkingZone_API.free_lon;

        paidParkingZone_name = ParkingZone_API.paid_name;
        paidParkingZone_addr = ParkingZone_API.paid_addr;
        paidParkingZone_lat = ParkingZone_API.paid_lat;
        paidParkingZone_lon = ParkingZone_API.paid_lon;

    }

    // 주차 금지 구역 마커 찍는 구간
    public void show_noParkingZone(View view) {
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
    public void show_searchingLayout() {
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

    // 유료 주차장 마커 표시 함수
    public void show_paidParkingZone(View view) {
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
        for (String permission : PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(this, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    public void set_up(View view) {
        if (loc_marker!=null) {
            loc_marker.setMap(null);
            ((MainActivity) MainActivity.context).show_searchingLayout();
        }

        mapFragment.set_user_location(naverMap);

    }

    public void set_preview_content(String start, String dest) {
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