package com.shrimp.seuzima;

import static android.speech.tts.TextToSpeech.ERROR;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationRequest;
import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraAnimation;
import com.naver.maps.map.CameraPosition;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.LocationTrackingMode;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.UiSettings;
import com.naver.maps.map.overlay.LocationOverlay;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.OverlayImage;
import com.naver.maps.map.overlay.PathOverlay;
import com.naver.maps.map.util.FusedLocationSource;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


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
    private TextToSpeech textToSpeech;
    private String activity;
    private Context contexts;

    private Location next_guide_location;


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

        contexts = inflaters.getContext();
        if (inflaters.getContext() instanceof MainActivity) {
            ((MainActivity) getActivity()).check_location();
        } else {
            ((NaviActivity) getActivity()).check_location();
        }



        locationSource =
                new FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE);


        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        // context를 Activity로 캐스팅하여 MainActivity인지 SubActivity인지 확인
        if (context instanceof MainActivity) {
            // MainActivity일 때의 처리
            activity = "main";
            Log.d("Fragment", "Attached to MainActivity");
        } else if (context instanceof NaviActivity) {
            // SubActivity일 때의 처리
            activity = "navi";

            Log.d("Fragment", "Attached to SubActivity");
        }
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
        if (contexts instanceof MainActivity) {
            Log.d("activity:", "mainactivity");
            ((MainActivity) MainActivity.context).set_init();
        } else if (activity.equals("navi")) {
            Log.d("activity:", "naviactivity");
            set_navigation_map(naverMap);
            try {
                navigation();
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        UiSettings uiSettings = naverMap.getUiSettings();
        uiSettings.setLogoClickEnabled(false);
    }

    // 사용자 위치 실시간으로 받아와서 지도에 표시하는 함수
    public void set_user_location(NaverMap navermap) {
        navermap.setLocationSource(locationSource);

        navermap.setLocationTrackingMode(LocationTrackingMode.Follow);
        navermap.addOnLocationChangeListener(location -> {
            user_lat = location.getLatitude();
            user_lon = location.getLongitude();
        });

        Log.d("user:", String.valueOf(user_lat));

    }

    // 지도에 표시된 marker 지우는 함수
    public void hideMarkers(List<Marker> markerList) {
        // markerList에 있는 모든 마커를 지도에서 제거합니다.
        for (Marker marker : markerList) {
            marker.setMap(null);
        }
    }

    public void set_navigation_map(NaverMap naverMap) {
        LocationOverlay locationOverlay = naverMap.getLocationOverlay();
        locationOverlay.setVisible(true);
        naverMap.setLocationSource(locationSource);
        naverMap.setLocationTrackingMode(LocationTrackingMode.Face);
        naverMap.addOnLocationChangeListener(location -> {
            Double lat = location.getLatitude();
            Double lon = location.getLongitude();

            locationOverlay.setIcon(OverlayImage.fromResource(R.drawable.navi_loc));
            locationOverlay.setPosition(new LatLng(lat, lon));
            locationOverlay.setIconWidth(200);
            locationOverlay.setIconHeight(200);
            float bearing = location.getBearing();
            if (bearing<=180) {
                bearing +=180;
            } else {
                bearing -=180;
            }
            CameraPosition cameraPosition = new CameraPosition(
                    new LatLng(lat, lon), // 대상 지점
                    17.5, // 줌 레벨
                    0,
                    location.getBearing()
            );
            CameraUpdate cameraUpdate = CameraUpdate.toCameraPosition(cameraPosition).animate(CameraAnimation.Easing);
            naverMap.moveCamera(cameraUpdate);

        });
        NAVI_API.path.setMap(naverMap);
    }

    // 주행모드 네비게이션 기능 함수
    public void navigation() throws JSONException {
        textToSpeech = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (i!=ERROR) {
                    textToSpeech.setLanguage(Locale.KOREAN);
                    textToSpeech.speak("경로 안내를 시작합니다.", TextToSpeech.QUEUE_FLUSH, null);
                }

            }
        });

        JSONArray guideArray = NAVI_API.guideArray;
        ArrayList<JSONArray> guidePoint = NAVI_API.guide_points;
//        path = new PathOverlay();


        final int[] n = {0};

        naverMap.addOnLocationChangeListener(new NaverMap.OnLocationChangeListener() {
            @Override
            public void onLocationChange(@NonNull Location location) {
                try {
                    Location guide_location = new Location(""){{setLatitude(guidePoint.get(n[0]).getDouble(1));
                        setLongitude(guidePoint.get(n[0]).getDouble(0));}};
                    float distance = location.distanceTo(guide_location);
                    float next_distance;
                    String next_guide="";

                    String guide = NAVI_API.guideArray.getJSONObject(n[0]).getString("instructions").toString();
                    if (guideArray.length()>n[0]+1) {
                        next_guide_location = new Location(""){{setLatitude(guidePoint.get(n[0]+1).getDouble(1));
                            setLongitude(guidePoint.get(n[0]+1).getDouble(0));}};
                        next_distance = guide_location.distanceTo(next_guide_location);
                        next_guide = NAVI_API.guideArray.getJSONObject(n[0]+1).getString("instructions").toString();
                    } else {
                        next_distance = 0.0f;
                    }
                    set_guide(guide, distance, next_guide, next_distance);


                    if (distance <= 320.0f && distance >280.0f) {
                        Log.d("Instruction:", guide);
                        // Toast.makeText(CameraActivity.this, guide+"하세요", Toast.LENGTH_SHORT).show();
                        if (guide.contains("시 방향")) {
                            if (textToSpeech.isSpeaking()==false) {
                                textToSpeech.speak("약 300미터 앞에서 " + guide+"으로 이동하세요.", TextToSpeech.QUEUE_FLUSH, null);
                            }
                        } else if(guide.contains("톨게이트")) {
                            if (textToSpeech.isSpeaking()==false) {
                                textToSpeech.speak("약 300미터 앞에서 " + guide+"로 진입하세요.", TextToSpeech.QUEUE_FLUSH, null);
                            }
                        } else if(guide.contains("U턴")) {
                            if (textToSpeech.isSpeaking() == false) {
                                textToSpeech.speak("약 300미터 앞에서 " + guide + "하세요.", TextToSpeech.QUEUE_FLUSH, null);
                            }
                        } else if(guide.contains("옆길")) {
                            if (textToSpeech.isSpeaking()==false) {
                                textToSpeech.speak("약 300미터 앞에서 " + guide+"로 이동하세요.", TextToSpeech.QUEUE_FLUSH, null);
                            }
                        } else if(guide.contains("목적지")) {

                        }else {
                            if (textToSpeech.isSpeaking()==false) {
                                textToSpeech.speak("약 300미터 앞에서 " + guide+"하세요.", TextToSpeech.QUEUE_FLUSH, null);
                            }
                        }

                    }
                    else if (distance <= 100.0f && distance >95.0f) {
                        if (guide.contains("좌회전")||(guide.contains("왼쪽") && guide.contains("시 방향"))) {
                            //guide_img.setImageResource(R.drawable.turn_left);
                            if (textToSpeech.isSpeaking()==false) {
                                if (guide.contains("왼쪽")) {
                                    textToSpeech.speak("100미터 앞에서 "+guide+"으로 좌회전 하세요.", TextToSpeech.QUEUE_FLUSH, null);
                                } else {
                                    textToSpeech.speak("100미터 앞에서 "+guide+" 하세요.", TextToSpeech.QUEUE_FLUSH, null);
                                }
                            }

                        } else if (guide.contains("우회전")||(guide.contains("오른쪽") && guide.contains("시 방향"))) {
                /*guide_img.setImageResource(R.drawable.turn_right);

                Animation blinkAnimation = AnimationUtils.loadAnimation(CameraActivity.this, R.anim.blink_anim);
                guide_img.startAnimation(blinkAnimation);*/
                            if (textToSpeech.isSpeaking()==false) {
                                if (guide.contains("오른쪽")) {
                                    textToSpeech.speak("100미터 앞에서 "+guide+"으로 우회전 하세요.", TextToSpeech.QUEUE_FLUSH, null);
                                } else {
                                    textToSpeech.speak("100미터 앞에서 "+guide+" 하세요.", TextToSpeech.QUEUE_FLUSH, null);
                                }
                            }
                        } else if(guide.contains("톨게이트")) {
                            if (textToSpeech.isSpeaking()==false) {
                                textToSpeech.speak("약 300미터 앞에서 " + guide+"로 진입하세요.", TextToSpeech.QUEUE_FLUSH, null);
                            }
                        } else if(guide.contains("U턴")) {
                            if (textToSpeech.isSpeaking() == false) {
                                textToSpeech.speak("약 100미터 앞에서 " + guide + "하세요.", TextToSpeech.QUEUE_FLUSH, null);
                            }
                        } else if (guide.contains("목적지")) {
                            if (textToSpeech.isSpeaking()==false) {
                                textToSpeech.speak("100미터 앞에" + guide + "가 있습니다.", TextToSpeech.QUEUE_FLUSH, null);
                            }
                        }


                    }else if (distance <= 40.0f && distance>25.0f) {
                        if (guideArray.length()>n[0]+1) {
                            n[0]++;
                        }
                    }else if (distance <= 20.0f && distance>15.0f) {
                        if (guide.equals("목적지")) {
                            Toast.makeText(getActivity(), "목적지에 도착하였습니다. 안내를 종료합니다.", Toast.LENGTH_SHORT).show();
                            textToSpeech.speak("목적지에 도착하였습니다. 안내를 종료합니다.", TextToSpeech.QUEUE_FLUSH, null);
                            Intent intent = new Intent(getActivity(), MainActivity.class);
                            startActivity(intent);

                        }
                    }
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void set_guide(String guide, float distance, String next_guide, float next_distance) {
        TextView textView_distance = getActivity().findViewById(R.id.dist_text);
        TextView textView_next_distance = getActivity().findViewById(R.id.second_dist_text);
        ImageView imageView_guide = getActivity().findViewById(R.id.guide_image);
        ImageView imageView_next_guide = getActivity().findViewById(R.id.second_guide_image);
        LinearLayout next_guide_layout = getActivity().findViewById(R.id.second_guide_layout);

        TextView textView_guide = getActivity().findViewById(R.id.guide_text);
        TextView textView_next_guide = getActivity().findViewById(R.id.second_guide_text);


        if (distance>=1000) {
            Double km = Double.valueOf(Math.round(distance/1000*100)/100.0);
            textView_distance.setText(String.valueOf(km)+"km");
        } else {
            int m = (int) distance;
            textView_distance.setText(String.valueOf(m)+"m");
        }
        if (guide.contains("우회전")||guide.contains("오른쪽")) {
            imageView_guide.setImageResource(R.drawable.turn_right_24);
            textView_guide.setText("우회전");
        } else if (guide.contains("좌회전")||guide.contains("왼쪽")) {
            imageView_guide.setImageResource(R.drawable.turn_left);
            textView_guide.setText("좌회전");
        } else if (guide.contains("U턴")) {
            imageView_guide.setImageResource(R.drawable.u_turn);
            textView_guide.setText("U턴");
        } else if (guide.contains("직진")) {
            imageView_guide.setImageResource(R.drawable.straight);
            textView_guide.setText("직진");
        } else if (guide.contains("톨게이트")) {
            imageView_guide.setImageResource(R.drawable.car);
            textView_guide.setText("톨게이트");
        } else if (guide.contains("목적지")) {
            imageView_guide.setImageResource(R.drawable.location_icon);
            textView_guide.setText("목적지");
        }
        if (next_distance==0.0f) {
            next_guide_layout.setVisibility(View.GONE);
        } else {
            if (next_distance>=1000) {
                Double km = Double.valueOf(Math.round(next_distance/1000*100)/100.0);
                textView_next_distance.setText(String.valueOf(km)+"km");
            } else {
                int m = (int) next_distance;
                textView_next_distance.setText(String.valueOf(m)+"m");
            }
            if (next_guide.contains("우회전")||next_guide.contains("오른쪽")) {
                imageView_next_guide.setImageResource(R.drawable.turn_right_24);
                textView_next_guide.setText("우회전");
            } else if (next_guide.contains("좌회전")||next_guide.contains("왼쪽")) {
                imageView_next_guide.setImageResource(R.drawable.turn_left);
                textView_next_guide.setText("좌회전");
            } else if (next_guide.contains("U턴")) {
                imageView_next_guide.setImageResource(R.drawable.u_turn);
                textView_next_guide.setText("U턴");
            } else if (next_guide.contains("직진")) {
                imageView_next_guide.setImageResource(R.drawable.straight);
                textView_next_guide.setText("직진");
            } else if (next_guide.contains("톨게이트")) {
                imageView_next_guide.setImageResource(R.drawable.car);
                textView_next_guide.setText("톨게이트");
            } else if (next_guide.contains("목적지")) {
                imageView_next_guide.setImageResource(R.drawable.location_icon);
                textView_next_guide.setText("목적지");
            }
        }

    }
}