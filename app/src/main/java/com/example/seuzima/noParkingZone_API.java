package com.example.seuzima;

public class noParkingZone_API {
    public static String STRING_NAME = "NAME";
    public static String STRING_ADDR = "ADDR";
    public static String STRING_LNG = "LNG";
    public static String STRING_LAT = "LAT";

    String name; // 명칭
    String addr; // 주소
    String LON; // 경도
    String LAT; // 위도

    public noParkingZone_API(String d_name, String d_addr, String d_lon, String d_lat) {
        this.name = d_name;
        this.addr = d_addr;
        this.LAT = d_lat;
        this.LON = d_lon;
    }

    @Override
    public String toString() {
        return "noParkingZone_ApiData{" +
                "name='" + name + '\'' +
                ", addr='" + addr + '\'' +
                "lat='" + LAT + '\'' +
                ", lon='" + LON + '\'' +
                '}';
    }
}
