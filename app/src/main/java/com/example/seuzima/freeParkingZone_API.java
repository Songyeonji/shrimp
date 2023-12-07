package com.example.seuzima;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class freeParkingZone_API {


    public static String[] name = new String[365]; // 명칭
    public static String[] addr= new String[365]; // 주소
    public static Double[] lon= new Double[365]; // 경도
    public static Double[] lat= new Double[365]; // 위도
    int n = 0;


    public static void getNoParkingData(){
        if (name[0]==null) {
            new Thread(){
                @Override
                public void run(){
                    // 쿼리 작성하기
                    String api_key = "5Q44AbprRae2DW%2FDurbwg83MQLdKuV9wx3jkkhdCcZNwYdEyIw43X8kzO2syrpPz%2FQ257YQOjs3RFF4OnA4QVQ%3D%3D";
                    String pageNo = "1";
                    String dataCount = "365";
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

                        for (int i=0; i<365;i++){
                            JSONObject data = items.getJSONObject(i);
                            name[i] = data.getString("manageNo");
                            addr[i] = data.getString("lnmAdres");
                            lon[i] = Double.valueOf(data.getString("crdntX"));
                            lat[i] = Double.valueOf(data.getString("crdntY"));

                        }
                        Log.d("noParkingAPI_DATA: ", name[364]);


                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();
        }

    }

}
