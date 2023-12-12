package com.example.seuzima;

import com.google.firebase.database.DatabaseReference;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class noParkingZone_API {

    public static void getNoParkingData(DatabaseReference nopz){
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
                    noParkingZone noPZ;

                    for (int i=0; i<365;i++){
                        JSONObject data = items.getJSONObject(i);
                        noPZ = new noParkingZone(data.getString("manageNo"), data.getString("lnmAdres"), Double.valueOf(data.getString("crdntX")), Double.valueOf(data.getString("crdntY")));
                        nopz.push().setValue(noPZ);
                    }

                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
