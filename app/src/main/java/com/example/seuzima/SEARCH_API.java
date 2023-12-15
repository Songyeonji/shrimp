package com.example.seuzima;

// 네이버 검색 API 예제 - 블로그 검색
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


public class SEARCH_API {

    private static String name;
    private static String addr;
    private static int x;
    private static int y;
    private static String link;
    private static String category;
    private static String tel;


    public static void main(String args) throws JSONException {
        String clientId = "_mGhuJl_G59um5hr3hqz"; //애플리케이션 클라이언트 아이디
        String clientSecret = "pZgnKmwxPB"; //애플리케이션 클라이언트 시크릿


        String text = null;
        try {
            text = URLEncoder.encode(args, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("검색어 인코딩 실패",e);
        }


        String apiURL = "https://openapi.naver.com/v1/search/local.json?query=" + text + "&display=10&start=1&sort=random";    // JSON 결과
        //String apiURL = "https://openapi.naver.com/v1/search/blog.xml?query="+ text; // XML 결과


        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-Naver-Client-Id", clientId);
        requestHeaders.put("X-Naver-Client-Secret", clientSecret);
        String responseBody = get(apiURL,requestHeaders);

        JSONObject jsonObject = new JSONObject(responseBody);
//                        JSONObject response = jsonObject.getJSONObject("response");
//                        JSONObject body = response.getJSONObject("body");
        int total = jsonObject.getInt("total");
        JSONArray items = jsonObject.getJSONArray("items");

        Log.d("totla:", String.valueOf(total));
        for (int i=0; i<items.length();i++) {
            JSONObject data = items.getJSONObject(i);
            name = data.getString("title");
            addr = data.getString("address");
            x = data.getInt("mapx");
            y = data.getInt("mapy");
            link = data.getString("link");
            category = data.getString("category");
            tel = data.getString("telephone");

            name = name.replace("<b>", "");
            name = name.replace("</b>", " ");

            ((Search) Search.context).searched(name, addr, x, y);


        }

        Log.d("searching:", responseBody);
    }


    private static String get(String apiUrl, Map<String, String> requestHeaders){
        HttpURLConnection con = connect(apiUrl);
        try {
            con.setRequestMethod("GET");
            for(Map.Entry<String, String> header :requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }


            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                return readBody(con.getInputStream());
            } else { // 오류 발생
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }


    private static HttpURLConnection connect(String apiUrl){
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection)url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }


    private static String readBody(InputStream body){
        InputStreamReader streamReader = new InputStreamReader(body);


        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();


            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }


            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는 데 실패했습니다.", e);
        }
    }
}
