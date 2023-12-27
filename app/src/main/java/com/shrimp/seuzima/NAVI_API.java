package com.shrimp.seuzima;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.overlay.PathOverlay;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NAVI_API extends AsyncTask<Void, Void, String> {
    public static Double startLat = MapFragment.user_lat;
    public static Double startLon = MapFragment.user_lon;
    public static Double destLat;
    public static Double destLon;
    public static String start = "내 위치";
    public static String dest;
    public static JSONArray pathArray;
    public static JSONArray guideArray;
    int num;
    public static PathOverlay path;
    public static ArrayList<JSONArray> guide_points;
    public static int duration;
    public static int distance;

    public NAVI_API(String start, String dest, Double startLat, Double startLon, Double destLat, Double destLon) {
        this.start = start;
        this.dest = dest;
        this.startLat = startLat;
        this.startLon = startLon;
        this.destLat = destLat;
        this.destLon = destLon;
    }
    @Override
    protected String doInBackground(Void... voids) {
        OkHttpClient client = new OkHttpClient();

        String url = "https://naveropenapi.apigw.ntruss.com/map-direction-15/v1/driving?start="+startLon+","+startLat+"&goal="+destLon+","+destLat+"&option=trafast";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("X-NCP-APIGW-API-KEY-ID", BuildConfig.NAVER_MAP_API_KEY)
                .addHeader("X-NCP-APIGW-API-KEY", BuildConfig.NAVER_MAP_API_CLIENT_KEY)
                .build();

        try {
            Response response = client.newCall(request).execute();
            String responseJson = response.body().string();
            return responseJson;
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("ResponseErr:", e.getMessage());
            return null;
        }
    }

    @Override
    protected void onPostExecute(String responseJson) {
        if (responseJson != null) {
            read_json(responseJson);
        } else {
            Log.e("Error", "Network request failed");
        }
    }


    public void read_json(String json) {
        //json 자료 가져오기
        try {
            //배열로된 자료를 가져올때
            JSONObject Object = new JSONObject(json).getJSONObject("route").getJSONArray("trafast").getJSONObject(0);//배열의 이름
            pathArray = Object.getJSONArray("path");
            guideArray = Object.getJSONArray("guide");
            guide_points = new ArrayList<JSONArray>();
            Log.d("Object", Object.toString());

            path = new PathOverlay();

            List<LatLng> coords = new ArrayList<>();

            int n=0;
            for (int i = 0; i<pathArray.length(); i++) {
                LatLng latLng = new LatLng(pathArray.getJSONArray(i).getDouble(1), pathArray.getJSONArray(i).getDouble(0));
                coords.add(i,latLng);
                if (guideArray.getJSONObject(n).getInt("pointIndex")==i) {
                    n++;
                    guide_points.add(pathArray.getJSONArray(i));
                }
            }

            // 출발지 -> 목적지 걸리는 시간
            duration = Object.getJSONObject("summary").getInt("duration")/1000/60;
            // 출발지 -> 목적지 거리
            distance = Object.getJSONObject("summary").getInt("distance")/100;

            ((MainActivity) MainActivity.context).set_preview_content(start, dest);

            /*Log.d("dur:", String.valueOf(duration));
            Log.d("dis:", String.valueOf(distance));*/

            // 네비게이션 루트 지도 위에 그리기
            path.setCoords(coords);
            path.setOutlineWidth(0);
            path.setWidth(25);
            path.setColor(Color.argb(50, 90, 156, 242));
            path.setMap(MapFragment.naverMap);




        }  catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
