package com.shrimp.seuzima;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class ParkingZone_API {

    public static String name; // 명칭
    public static String addr; // 명칭
    public static Double lon; // 경도
    public static Double lat; // 위도
    public static int totalQty; // 주차 총면적수


    private static int num = 1044;
    //유료 주차장 변수들
    public static String[] paid_name = new String[num]; // 명칭
    public static String[] paid_addr= new String[num]; // 주소
    public static Double[] paid_lon= new Double[num]; // 경도
    public static Double[] paid_lat= new Double[num]; // 위도
    public static int[] paid_totalQty = new int[num]; // 주차 총면적수
    public static int[] paid_baseTime= new int[num]; // 주차 기본시간
    public static int[] paid_baseRate = new int[num]; // 주차 기본요금
    public static int[] paid_addTime= new int[num]; // 추가 단위시간
    public static int[] paid_addRate = new int[num]; // 추가 단위요금
    public static String[] paid_weekdayOpenTime= new String[num]; // 평일 운영 시작 시간
    public static String[] paid_weekdayCloseTime= new String[num]; // 평일 운영 종료 시간
    public static String[] paid_satOpenTime= new String[num]; // 토요일 운영 시작 시간
    public static String[] paid_satCloseTime= new String[num]; // 토요일 운영 종료 시간
    public static String[] paid_holidayOpenTime= new String[num]; // 공휴일 운영 시작 시간
    public static String[] paid_holidayCloseTime= new String[num]; // 공휴일 운영 종료 시간

    //무료 주차장
    public static String[] free_name = new String[num]; // 명칭
    public static String[] free_addr= new String[num]; // 주소
    public static Double[] free_lon= new Double[num]; // 경도
    public static Double[] free_lat= new Double[num]; // 위도
    public static int[] free_totalQty = new int[num]; // 주차 총면적수
    public static int[] free_baseTime= new int[num]; // 주차 기본시간
    public static int[] free_baseRate = new int[num]; // 주차 기본요금
    public static int[] free_addTime= new int[num]; // 추가 단위시간
    public static int[] free_addRate = new int[num]; // 추가 단위요금
    public static String[] free_weekdayOpenTime= new String[num]; // 평일 운영 시작 시간
    public static String[] free_weekdayCloseTime= new String[num]; // 평일 운영 종료 시간
    public static String[] free_satOpenTime= new String[num]; // 토요일 운영 시작 시간
    public static String[] free_satCloseTime= new String[num]; // 토요일 운영 종료 시간
    public static String[] free_holidayOpenTime= new String[num]; // 공휴일 운영 시작 시간
    public static String[] free_holidayCloseTime= new String[num]; // 공휴일 운영 종료 시간
    static int free = 0;
    static int paid = 0;


    public static void getParkingData(){
        if (free_name[0]==null || paid_name[0]==null) {
            new Thread(){
                @Override
                public void run(){
                    // 쿼리 작성하기
                    String api_key = "UhsJNt7dpE1r5bSAnj7VjXZDFlcK8rpnEkZ%2BrYRhT0CaBNFs%2BcR9okv6jEEoiCYTSjRLSMZsSpXF%2FhdbVCihsw%3D%3D";
                    String pageNo = "1";
                    String dataCount = String.valueOf(num);
                    String queryUrl = "https://challenge.daejeon.go.kr/restapi/openapi/smart_on/parking/info?serviceKey="+api_key+
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
//                        JSONObject response = jsonObject.getJSONObject("response");
//                        JSONObject body = response.getJSONObject("body");
                        JSONArray items = jsonObject.getJSONArray("resultList");

                        for (int i=0; i<num;i++){
                            JSONObject data = items.getJSONObject(i);
                            if (data.getString("park_interval_free_yn").equals("N")) {
                                paid_name[paid] = data.getString("park_name");
                                paid_addr[paid] = data.getString("park_full_address");
                                paid_lat[paid] = data.getDouble("park_latitude");
                                paid_lon[paid] = data.getDouble("park_longitude");

                                //주차장 이용 시간
                                paid_weekdayOpenTime[paid] = data.getString("park_biz_opentime");
                                paid_weekdayCloseTime[paid] = data.getString("park_biz_closetime");
                                paid_satOpenTime[paid] = data.getString("park_sat_biz_opentime");
                                paid_satCloseTime[paid] = data.getString("park_sat_biz_closetime");
                                paid_holidayOpenTime[paid] = data.getString("park_sun_hol_opentime");
                                paid_holidayCloseTime[paid] = data.getString("park_sun_hol_closetime");

                                //주차장 요금
                                paid_baseRate[paid] = data.getInt("park_basic_interval_price");
                                paid_baseTime[paid] = data.getInt("park_basic_interval_minute");
                                paid_addRate[paid] = data.getInt("park_additional_interval_price");
                                paid_addTime[paid] = data.getInt("park_additional_interval_minute");
                                paid++;
                            } else if(data.getString("park_interval_free_yn").equals("Y")){
                                free_name[free] = data.getString("park_name");
                                free_addr[free] = data.getString("park_full_address");
                                free_lat[free] = data.getDouble("park_latitude");
                                free_lon[free] = data.getDouble("park_longitude");

                                //주차장 이용 시간
                                free_weekdayOpenTime[free] = data.getString("park_biz_opentime");
                                free_weekdayCloseTime[free] = data.getString("park_biz_closetime");
                                free_satOpenTime[free] = data.getString("park_sat_biz_opentime");
                                free_satCloseTime[free] = data.getString("park_sat_biz_closetime");
                                free_holidayOpenTime[free] = data.getString("park_sun_hol_opentime");
                                free_holidayCloseTime[free] = data.getString("park_sun_hol_closetime");
                                free++;
                            }

                        }
                        Log.d("ParkingAPI_DATA: ", paid_name[364]);
                        Log.d("freeParkingAPI_DATA: ", free_name[3]);


                    } catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();
        }

    }
}


