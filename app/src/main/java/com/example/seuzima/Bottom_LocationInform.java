package com.example.seuzima;


import static com.example.seuzima.MapActivity.user_lat;
import static com.example.seuzima.MapActivity.user_lon;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.overlay.PathOverlay;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Bottom_LocationInform extends Fragment {

    Double latitude;
    Double longitude;
    boolean exist = false;
    String email;
//    Bottom_Favorite bottomFavorite;
    String title;
    String addr;
    View layout;
    public static JSONArray pathArray;
    public static JSONArray guideArray;
    int num;
    public static PathOverlay path;
    public static ArrayList<JSONArray> guide_points;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootview = inflater.inflate(R.layout.fragment_location_inform, container, false);

        FrameLayout bttnDest = rootview.findViewById(R.id.bttn_dest);
        bttnDest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                set_destPoint();
            }
        });

        FrameLayout bttnStart = rootview.findViewById(R.id.bttn_start);
        bttnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                set_startPoint();
            }
        });

//        ImageView bttnDelete = rootview.findViewById(R.id.delete_bttn);
       /* bttnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MapActivity mapActivity = (MapActivity) getActivity();
                mapActivity.show_searchingLayout();
            }
        });*/

        return rootview;
    }



    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView loc_title = view.findViewById(R.id.loc_title);
        TextView loc_addr = view.findViewById(R.id.loc_addr);


        // mapview에서 받아온 위치 정보(이름, 주소, 위경도) 가져와서 문자열 및 double 변수에 저장
        title = this.getArguments().getString("loc_name");
        addr = this.getArguments().getString("loc_addr");
        latitude = this.getArguments().getDouble("loc_lat");
        longitude = this.getArguments().getDouble("loc_lon");
        //"loc_lon", longitude

        // 장소 이름, 주소는 각각 textview에 저장
        loc_title.setText(title);
        loc_addr.setText(addr);
    }

    // '도착' 버튼 클릭 시 MainActivity로 이동 + 장소 이름, 주소, 위경도 정보 같이 intent
    public void set_destPoint() {
        NAVI_API dust = new NAVI_API(user_lat, user_lon, latitude, longitude);
        dust.execute();
    }

    public void set_startPoint() {

    }
}