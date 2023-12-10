package com.example.seuzima;

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

public class ParkingZone_API extends AsyncTask<Void, Void, String> {

    public static String name; // 명칭
    public static String addr; // 명칭
    public static Double lon; // 경도
    public static Double lat; // 위도
    public static int totalQty; // 주차 총면적수


    //유료 주차장 변수들
    public static String[] paid_name = new String[719]; // 명칭
    public static String[] paid_addr= new String[719]; // 주소
    public static Double[] paid_lon= new Double[719]; // 경도
    public static Double[] paid_lat= new Double[719]; // 위도
    public static int[] paid_totalQty = new int[719]; // 주차 총면적수
    public static int[] paid_baseTime= new int[719]; // 주차 기본시간
    public static int[] paid_baseRate = new int[719]; // 주차 기본요금
    public static int[] paid_addTime= new int[719]; // 추가 단위시간
    public static int[] paid_addRate = new int[719]; // 추가 단위요금
    public static String[] paid_weekdayOpenTime= new String[719]; // 평일 운영 시작 시간
    public static String[] paid_weekdayCloseTime= new String[719]; // 평일 운영 종료 시간
    public static String[] paid_satOpenTime= new String[719]; // 토요일 운영 시작 시간
    public static String[] paid_satCloseTime= new String[719]; // 토요일 운영 종료 시간
    public static String[] paid_holidayOpenTime= new String[719]; // 공휴일 운영 시작 시간
    public static String[] paid_holidayCloseTime= new String[719]; // 공휴일 운영 종료 시간

    //무료 주차장
    public static String[] free_name = new String[719]; // 명칭
    public static String[] free_addr= new String[719]; // 주소
    public static Double[] free_lon= new Double[719]; // 경도
    public static Double[] free_lat= new Double[719]; // 위도
    public static int[] free_totalQty = new int[719]; // 주차 총면적수
    public static int[] free_baseTime= new int[719]; // 주차 기본시간
    public static int[] free_baseRate = new int[719]; // 주차 기본요금
    public static int[] free_addTime= new int[719]; // 추가 단위시간
    public static int[] free_addRate = new int[719]; // 추가 단위요금
    public static String[] free_weekdayOpenTime= new String[719]; // 평일 운영 시작 시간
    public static String[] free_weekdayCloseTime= new String[719]; // 평일 운영 종료 시간
    public static String[] free_satOpenTime= new String[719]; // 토요일 운영 시작 시간
    public static String[] free_satCloseTime= new String[719]; // 토요일 운영 종료 시간
    public static String[] free_holidayOpenTime= new String[719]; // 공휴일 운영 시작 시간
    public static String[] free_holidayCloseTime= new String[719]; // 공휴일 운영 종료 시간
    static int free = 0;
    static int paid = 0;

    private String url;
    private int n;

    public ParkingZone_API(String url, int n) {

        this.n = n;
        this.url = url;
//        free = free+50*(n-1);
//        paid = paid+50*(n-1);
    }

    @Override
    protected String doInBackground(Void... params) {


        Log.d("start:","start");

        DocumentBuilderFactory dbFactoty = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;
        try {
            dBuilder = dbFactoty.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        Document doc = null;
        try {
            doc = dBuilder.parse(url);
        } catch (IOException | SAXException e) {
            e.printStackTrace();
        }

        // root tag
        doc.getDocumentElement().normalize();
        System.out.println("Root element: " + doc.getDocumentElement().getNodeName()); // Root element: result

        // 파싱할 tag
        NodeList nList = doc.getElementsByTagName("item");

        for(int temp = 0; temp < nList.getLength(); temp++){
            Node nNode = nList.item(temp);
            if(nNode.getNodeType() == Node.ELEMENT_NODE){

                Element eElement = (Element) nNode;
                String type = getTagValue("type", eElement);

                if (type.equals("무료")) {
                    free_name[free] = getTagValue("name", eElement);
                    free_lat[free] = Double.valueOf(getTagValue("lat", eElement));
                    free_lon[free] = Double.valueOf(getTagValue("lon", eElement));
                    free_addr[free] = getTagValue("address", eElement);
                    free_totalQty[free] = Integer.parseInt(getTagValue("totalQty", eElement));

                    free++;
                } else {
                    paid_name[paid] = getTagValue("name", eElement);
                    paid_lat[paid] = Double.valueOf(getTagValue("lat", eElement));
                    paid_lon[paid] = Double.valueOf(getTagValue("lon", eElement));
                    paid_addr[paid] = getTagValue("address", eElement);
                    paid_totalQty[paid] = Integer.parseInt(getTagValue("totalQty", eElement));

                    paid++;
                }
            }
        }

        return null;
    }

    @Override
    protected void onPostExecute(String str) {
        super.onPostExecute(str);
    }

    private String getTagValue(String tag, Element eElement) {
        NodeList nlList = eElement.getElementsByTagName(tag).item(0).getChildNodes();
        Node nValue = (Node) nlList.item(0);
        if(nValue == null)
            return null;
        return nValue.getNodeValue();
    }
}


