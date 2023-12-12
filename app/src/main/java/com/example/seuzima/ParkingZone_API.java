package com.example.seuzima;

import com.google.firebase.database.DatabaseReference;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public class ParkingZone_API  {
    public static void getParkingData(DatabaseReference freepz, DatabaseReference paidpz){
        new Thread(){
            @Override
            public void run(){
                for(int pn = 1; pn <= 15; pn++) {
                    // 쿼리 작성하기
                    String api_key = "5Q44AbprRae2DW%2FDurbwg83MQLdKuV9wx3jkkhdCcZNwYdEyIw43X8kzO2syrpPz%2FQ257YQOjs3RFF4OnA4QVQ%3D%3D";
                    String pageNo = Integer.toString(pn);
                    String dataCount;
                    if (pn < 15){
                        dataCount = "50";
                    }else{
                        dataCount = "19";
                    }
                    String queryUrl = "https://apis.data.go.kr/6300000/pis/parkinglotIF?serviceKey="+api_key+
                            "&numOfRows="+dataCount+"&pageNo="+pageNo;

                    try {
                        // 데이터 받아오기
                        URL url = new URL(queryUrl);
                        InputStream is = url.openStream();

                        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                        XmlPullParser xpp = factory.newPullParser();
                        xpp.setInput(new InputStreamReader(is, "UTF-8"));

                        String tag;
                        int eventType = xpp.getEventType();
                        xpp.next();

                        freeParkingZone freePZ;
                        paidParkingZone paidPZ;
                        String name = null;
                        Double lat = null;
                        Double lon = null;
                        String addr = null;
                        String type = null;

                        while (eventType != XmlPullParser.END_DOCUMENT) {
                            tag = xpp.getName();
                            switch (eventType) {
                                case XmlPullParser.START_TAG:
                                    if (tag.equals("name")) {
                                        xpp.next();
                                        name = xpp.getText();
                                    } else if (tag.equals("lat")) {
                                        xpp.next();
                                        lat = Double.parseDouble(xpp.getText());
                                    } else if (tag.equals("lon")) {
                                        xpp.next();
                                        lon = Double.parseDouble(xpp.getText());
                                    } else if (tag.equals("address")) {
                                        xpp.next();
                                        addr = xpp.getText();
                                    } else if (tag.equals("type")) {
                                        xpp.next();
                                        type = xpp.getText();
                                    }
                                    break;
                                case XmlPullParser.END_TAG:
                                    if (tag.equals("item")){
                                        if (type.equals("무료")) {
                                            freePZ = new freeParkingZone(name, addr, lat, lon);
                                            freepz.push().setValue(freePZ);
                                        } else if (type.equals("유료")) {
                                            paidPZ = new paidParkingZone(name, addr, lat, lon);
                                            paidpz.push().setValue(paidPZ);
                                        }
                                    }
                                    break;
                            }
                            eventType = xpp.next();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

    }
}