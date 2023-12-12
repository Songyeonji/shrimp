package com.example.seuzima;

public class paidParkingZone {
    public String name;
    public String addr;
    public double lat;
    public double lon;
    public paidParkingZone(){
        //DataSnapshot.getValue(paidParkingZone.class) 호출에 필요한 기본 생성자
    }
    public paidParkingZone(String name, String addr, double lat, double lon){
        this.name = name;
        this.addr = addr;
        this.lat = lat;
        this.lon = lon;
    }
}
